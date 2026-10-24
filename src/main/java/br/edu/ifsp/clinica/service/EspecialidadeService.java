package br.edu.ifsp.clinica.service;

import br.edu.ifsp.clinica.dto.EspecialidadeRequest;
import br.edu.ifsp.clinica.dto.EspecialidadeResponse;
import br.edu.ifsp.clinica.dto.MedicoResponse;
import br.edu.ifsp.clinica.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.clinica.exception.RegraNegocioException;
import br.edu.ifsp.clinica.model.Especialidade;
import br.edu.ifsp.clinica.model.Medico;
import br.edu.ifsp.clinica.repository.EspecialidadeRepository;
import br.edu.ifsp.clinica.repository.MedicoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class EspecialidadeService {

    private final EspecialidadeRepository repository;
    private final MedicoRepository medicoRepository;

    public EspecialidadeService(EspecialidadeRepository repository, MedicoRepository medicoRepository) {
        this.repository = repository;
        this.medicoRepository = medicoRepository;
    }

    @Transactional(readOnly = true)
    public List<EspecialidadeResponse> listar() {
        return repository.findAll(Sort.by("nome")).stream().map(EspecialidadeResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EspecialidadeResponse buscar(Long id) {
        return EspecialidadeResponse.from(buscarEntidade(id));
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> listarMedicos(Long id) {
        buscarEntidade(id);
        return medicoRepository.findDistinctByEspecialidadesId(id, Sort.by("nome")).stream()
                .map(MedicoResponse::from).toList();
    }

    @Transactional
    public EspecialidadeResponse criar(EspecialidadeRequest req) {
        if (repository.existsByNomeIgnoreCase(req.nome().trim())) {
            throw new RegraNegocioException("Já existe uma especialidade com o nome '" + req.nome().trim() + "'");
        }
        Especialidade e = new Especialidade();
        preencher(e, req);
        return EspecialidadeResponse.from(repository.save(e));
    }

    @Transactional
    public EspecialidadeResponse atualizar(Long id, EspecialidadeRequest req) {
        Especialidade e = buscarEntidade(id);
        if (repository.existsByNomeIgnoreCaseAndIdNot(req.nome().trim(), id)) {
            throw new RegraNegocioException("Já existe uma especialidade com o nome '" + req.nome().trim() + "'");
        }
        preencher(e, req);
        return EspecialidadeResponse.from(e);
    }

    @Transactional
    public void excluir(Long id) {
        Especialidade e = buscarEntidade(id);
        // Remove o vínculo com os médicos antes de excluir (Medico é o dono do relacionamento N:N)
        for (Medico m : new ArrayList<>(e.getMedicos())) {
            m.getEspecialidades().remove(e);
        }
        repository.delete(e);
    }

    Especialidade buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Especialidade", id));
    }

    private void preencher(Especialidade e, EspecialidadeRequest req) {
        e.setNome(req.nome().trim());
        e.setDescricao(req.descricao());
    }
}
