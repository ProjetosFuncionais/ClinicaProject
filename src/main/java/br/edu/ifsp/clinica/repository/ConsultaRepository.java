package br.edu.ifsp.clinica.repository;

import br.edu.ifsp.clinica.model.Consulta;
import br.edu.ifsp.clinica.model.StatusConsulta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long>, JpaSpecificationExecutor<Consulta> {

    @EntityGraph(attributePaths = {"medico", "paciente"})
    List<Consulta> findAll(Specification<Consulta> spec, Sort sort);

    boolean existsByMedicoId(Long medicoId);

    boolean existsByPacienteId(Long pacienteId);

    // Usados para impedir dois agendamentos no mesmo horário (ignorando consultas canceladas)
    boolean existsByMedicoIdAndDataHoraAndStatusNotAndIdNot(
            Long medicoId, LocalDateTime dataHora, StatusConsulta status, Long id);

    boolean existsByPacienteIdAndDataHoraAndStatusNotAndIdNot(
            Long pacienteId, LocalDateTime dataHora, StatusConsulta status, Long id);
}
