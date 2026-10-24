package br.edu.ifsp.clinica.service;

import br.edu.ifsp.clinica.dto.ConsultaResponse;
import br.edu.ifsp.clinica.dto.PacienteRequest;
import br.edu.ifsp.clinica.dto.PacienteResponse;
import br.edu.ifsp.clinica.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.clinica.exception.RegraNegocioException;
import br.edu.ifsp.clinica.model.Paciente;
import br.edu.ifsp.clinica.repository.ConsultaRepository;
import br.edu.ifsp.clinica.repository.PacienteRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class PacienteService {

    private final PacienteRepository repository;
    private final ConsultaRepository consultaRepository;

    public PacienteService(PacienteRepository repository, ConsultaRepository consultaRepository) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> listar() {
        return repository.findAll(Sort.by("nome")).stream().map(PacienteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscar(Long id) {
        return PacienteResponse.from(buscarEntidade(id));
    }

    @Transactional
    public PacienteResponse criar(PacienteRequest req) {
        String cpf = somenteDigitos(req.cpf());
        if (repository.existsByCpf(cpf)) {
            throw new RegraNegocioException("Já existe um paciente com o CPF informado");
        }
        Paciente p = new Paciente();
        preencher(p, req);
        return PacienteResponse.from(repository.save(p));
    }

    @Transactional
    public PacienteResponse atualizar(Long id, PacienteRequest req) {
        Paciente p = buscarEntidade(id);
        if (repository.existsByCpfAndIdNot(somenteDigitos(req.cpf()), id)) {
            throw new RegraNegocioException("Já existe um paciente com o CPF informado");
        }
        preencher(p, req);
        return PacienteResponse.from(p);
    }

    @Transactional
    public void excluir(Long id) {
        Paciente p = buscarEntidade(id);
        if (consultaRepository.existsByPacienteId(id)) {
            throw new RegraNegocioException("Não é possível excluir: o paciente possui consultas vinculadas");
        }
        repository.delete(p);
    }

    // ---- Relacionamento Um-para-Muitos (Paciente -> Consulta) ----

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarConsultas(Long id) {
        return buscarEntidade(id).getConsultas().stream()
                .sorted(Comparator.comparing(c -> c.getDataHora()))
                .map(ConsultaResponse::from)
                .toList();
    }

    Paciente buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente", id));
    }

    private void preencher(Paciente p, PacienteRequest req) {
        p.setNome(req.nome().trim());
        p.setCpf(somenteDigitos(req.cpf()));
        p.setDataNascimento(req.dataNascimento());
        p.setTelefone(req.telefone());
        p.setEmail(req.email());
    }

    private static String somenteDigitos(String cpf) {
        return cpf.replaceAll("\\D", "");
    }
}
