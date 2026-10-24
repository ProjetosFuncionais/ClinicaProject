package br.edu.ifsp.clinica.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ConsultaRequest(
        @NotNull(message = "é obrigatório")
        LocalDateTime dataHora,
        @NotNull(message = "é obrigatório")
        Long medicoId,
        @NotNull(message = "é obrigatório")
        Long pacienteId,
        @Size(max = 500, message = "deve ter no máximo 500 caracteres")
        String observacoes) {
}
