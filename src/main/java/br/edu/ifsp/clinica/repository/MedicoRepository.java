package br.edu.ifsp.clinica.repository;

import br.edu.ifsp.clinica.model.Medico;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicoRepository extends JpaRepository<Medico, Long> {

    @EntityGraph(attributePaths = "especialidades")
    List<Medico> findAll(Sort sort);

    @EntityGraph(attributePaths = "especialidades")
    List<Medico> findDistinctByEspecialidadesId(Long especialidadeId, Sort sort);

    boolean existsByCrmIgnoreCase(String crm);

    boolean existsByCrmIgnoreCaseAndIdNot(String crm, Long id);
}
