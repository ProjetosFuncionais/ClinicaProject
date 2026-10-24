package br.edu.ifsp.clinica.dto;

import br.edu.ifsp.clinica.model.StatusConsulta;
import jakarta.validation.constraints.NotNull;

public record StatusRequest(
        @NotNull(message = "é obrigatório (AGENDADA, CHAMADA, EM_ATENDIMENTO, CONCLUIDA ou CANCELADA)")
        StatusConsulta status) {
}
