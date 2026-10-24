package br.edu.ifsp.clinica.service;

import br.edu.ifsp.clinica.dto.ConsultaRequest;
import br.edu.ifsp.clinica.dto.ConsultaResponse;
import br.edu.ifsp.clinica.exception.RecursoNaoEncontradoException;
import br.edu.ifsp.clinica.exception.RegraNegocioException;
import br.edu.ifsp.clinica.model.Consulta;
import br.edu.ifsp.clinica.model.StatusConsulta;
import br.edu.ifsp.clinica.repository.ConsultaRepository;
import br.edu.ifsp.clinica.websocket.PainelNotifier;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class ConsultaService {

    private static final Set<StatusConsulta> FINALIZADOS = Set.of(StatusConsulta.CONCLUIDA, StatusConsulta.CANCELADA);

    private final ConsultaRepository repository;
    private final MedicoService medicoService;
    private final PacienteService pacienteService;
    private final PainelNotifier painel;

    public ConsultaService(ConsultaRepository repository, MedicoService medicoService,
                           PacienteService pacienteService, PainelNotifier painel) {
        this.repository = repository;
        this.medicoService = medicoService;
        this.pacienteService = pacienteService;
        this.painel = painel;
    }

    /** Lista consultas com filtros opcionais por data, médico, paciente e status. */
    @Transactional(readOnly = true)
    public List<ConsultaResponse> listar(LocalDate data, Long medicoId, Long pacienteId, StatusConsulta status) {
        Specification<Consulta> spec = (root, query, cb) -> cb.conjunction();
        if (data != null) {
            spec = spec.and((root, q, cb) -> cb.between(root.get("dataHora"),
                    data.atStartOfDay(), data.plusDays(1).atStartOfDay().minusNanos(1)));
        }
        if (medicoId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("medico").get("id"), medicoId));
        }
        if (pacienteId != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("paciente").get("id"), pacienteId));
        }
        if (status != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("status"), status));
        }
        return repository.findAll(spec, Sort.by("dataHora")).stream().map(ConsultaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscar(Long id) {
        return ConsultaResponse.from(buscarEntidade(id));
    }

    @Transactional
    public ConsultaResponse criar(ConsultaRequest req) {
        Consulta c = new Consulta();
        preencher(c, req, -1L);
        c.setStatus(StatusConsulta.AGENDADA);
        return ConsultaResponse.from(repository.save(c));
    }

    @Transactional
    public ConsultaResponse atualizar(Long id, ConsultaRequest req) {
        Consulta c = buscarEntidade(id);
        if (FINALIZADOS.contains(c.getStatus())) {
            throw new RegraNegocioException("Consulta " + c.getStatus() + " não pode ser alterada");
        }
        preencher(c, req, id);
        return ConsultaResponse.from(c);
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(buscarEntidade(id));
    }

    /** Altera o status da consulta e avisa o painel em tempo real (WebSocket). */
    @Transactional
    public ConsultaResponse alterarStatus(Long id, StatusConsulta novoStatus) {
        Consulta c = buscarEntidade(id);
        if (FINALIZADOS.contains(c.getStatus())) {
            throw new RegraNegocioException("Consulta já está " + c.getStatus() + " e não pode mudar de status");
        }
        c.setStatus(novoStatus);
        ConsultaResponse resp = ConsultaResponse.from(c);
        painel.notificar(novoStatus == StatusConsulta.CHAMADA ? "CHAMADA" : "STATUS", resp);
        return resp;
    }

    /** Chama o paciente para o consultório: muda o status para CHAMADA e envia para o painel. */
    @Transactional
    public ConsultaResponse chamar(Long id) {
        Consulta c = buscarEntidade(id);
        if (c.getStatus() != StatusConsulta.AGENDADA && c.getStatus() != StatusConsulta.CHAMADA) {
            throw new RegraNegocioException("Só é possível chamar pacientes de consultas AGENDADAS (status atual: "
                    + c.getStatus() + ")");
        }
        return alterarStatus(id, StatusConsulta.CHAMADA);
    }

    private Consulta buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Consulta", id));
    }

    private void preencher(Consulta c, ConsultaRequest req, Long idAtual) {
        var medico = medicoService.buscarEntidade(req.medicoId());
        var paciente = pacienteService.buscarEntidade(req.pacienteId());

        if (repository.existsByMedicoIdAndDataHoraAndStatusNotAndIdNot(
                medico.getId(), req.dataHora(), StatusConsulta.CANCELADA, idAtual)) {
            throw new RegraNegocioException("O médico já possui uma consulta agendada neste horário");
        }
        if (repository.existsByPacienteIdAndDataHoraAndStatusNotAndIdNot(
                paciente.getId(), req.dataHora(), StatusConsulta.CANCELADA, idAtual)) {
            throw new RegraNegocioException("O paciente já possui uma consulta agendada neste horário");
        }

        c.setDataHora(req.dataHora());
        c.setMedico(medico);
        c.setPaciente(paciente);
        c.setObservacoes(req.observacoes());
    }
}
