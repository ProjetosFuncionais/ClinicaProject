package br.edu.ifsp.clinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PacienteRequest(
        @NotBlank(message = "é obrigatório") @Size(max = 120, message = "deve ter no máximo 120 caracteres")
        String nome,
        @NotBlank(message = "é obrigatório")
        @Pattern(regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}-?\\d{2}", message = "deve estar no formato 000.000.000-00")
        String cpf,
        @Past(message = "deve ser uma data no passado")
        LocalDate dataNascimento,
        @Size(max = 20, message = "deve ter no máximo 20 caracteres")
        String telefone,
        @Email(message = "deve ser um e-mail válido")
        String email) {
}
