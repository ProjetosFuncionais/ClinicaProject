package br.edu.ifsp.clinica.service;

import br.edu.ifsp.clinica.dto.ConsultaResponse;
import br.edu.ifsp.clinica.dto.EspecialidadeResponse;
import br.edu.ifsp.clinica.dto.MedicoRequest;
import br.edu.ifsp.clinica.dto.MedicoResponse;
import br.edu.ifsp.clinica.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.clinica.exception.RegraNegocioException;
import br.edu.ifsp.clinica.model.Especialidade;
import br.edu.ifsp.clinica.model.Medico;
import br.edu.ifsp.clinica.repository.ConsultaRepository;
import br.edu.ifsp.clinica.repository.MedicoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class MedicoService {

    private final MedicoRepository repository;
    private final ConsultaRepository consultaRepository;
    private final EspecialidadeService especialidadeService;

    public MedicoService(MedicoRepository repository, ConsultaRepository consultaRepository,
                         EspecialidadeService especialidadeService) {
        this.repository = repository;
        this.consultaRepository = consultaRepository;
        this.especialidadeService = especialidadeService;
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> listar() {
        return repository.findAll(Sort.by("nome")).stream().map(MedicoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public MedicoResponse buscar(Long id) {
        return MedicoResponse.from(buscarEntidade(id));
    }

    @Transactional
    public MedicoResponse criar(MedicoRequest req) {
        if (repository.existsByCrmIgnoreCase(req.crm().trim())) {
            throw new RegraNegocioException("Já existe um médico com o CRM '" + req.crm().trim() + "'");
        }
        Medico m = new Medico();
        preencher(m, req);
        return MedicoResponse.from(repository.save(m));
    }

    @Transactional
    public MedicoResponse atualizar(Long id, MedicoRequest req) {
        Medico m = buscarEntidade(id);
        if (repository.existsByCrmIgnoreCaseAndIdNot(req.crm().trim(), id)) {
            throw new RegraNegocioException("Já existe um médico com o CRM '" + req.crm().trim() + "'");
        }
        preencher(m, req);
        return MedicoResponse.from(m);
    }

    @Transactional
    public void excluir(Long id) {
        Medico m = buscarEntidade(id);
        if (consultaRepository.existsByMedicoId(id)) {
            throw new RegraNegocioException("Não é possível excluir: o médico possui consultas vinculadas");
        }
        repository.delete(m);
    }

    // ---- Relacionamento Muitos-para-Muitos (Medico <-> Especialidade) ----

    @Transactional(readOnly = true)
    public List<EspecialidadeResponse> listarEspecialidades(Long id) {
        return buscarEntidade(id).getEspecialidades().stream()
                .map(EspecialidadeResponse::from)
                .sorted(Comparator.comparing(EspecialidadeResponse::nome))
                .toList();
    }

    @Transactional
    public MedicoResponse adicionarEspecialidade(Long id, Long especialidadeId) {
        Medico m = buscarEntidade(id);
        m.getEspecialidades().add(especialidadeService.buscarEntidade(especialidadeId));
        return MedicoResponse.from(m);
    }

    @Transactional
    public void removerEspecialidade(Long id, Long especialidadeId) {
        Medico m = buscarEntidade(id);
        Especialidade e = especialidadeService.buscarEntidade(especialidadeId);
        if (!m.getEspecialidades().remove(e)) {
            throw new RegraNegocioException("O médico não possui a especialidade '" + e.getNome() + "'");
        }
    }

    // ---- Relacionamento Um-para-Muitos (Medico -> Consulta) ----

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarConsultas(Long id) {
        return buscarEntidade(id).getConsultas().stream()
                .sorted(Comparator.comparing(c -> c.getDataHora()))
                .map(ConsultaResponse::from)
                .toList();
    }

    Medico buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Médico", id));
    }

    private void preencher(Medico m, MedicoRequest req) {
        m.setNome(req.nome().trim());
        m.setCrm(req.crm().trim().toUpperCase());
        m.setEmail(req.email());
        m.setTelefone(req.telefone());
        m.setSala(req.sala());
        if (req.especialidadeIds() != null) {
            m.getEspecialidades().clear();
            req.especialidadeIds().forEach(espId ->
                    m.getEspecialidades().add(especialidadeService.buscarEntidade(espId)));
        }
    }
}
