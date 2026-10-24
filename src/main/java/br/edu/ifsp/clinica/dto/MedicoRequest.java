package br.edu.ifsp.clinica.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record MedicoRequest(
        @NotBlank(message = "é obrigatório") @Size(max = 120, message = "deve ter no máximo 120 caracteres")
        String nome,
        @NotBlank(message = "é obrigatório") @Size(max = 20, message = "deve ter no máximo 20 caracteres")
        String crm,
        @Email(message = "deve ser um e-mail válido")
        String email,
        @Size(max = 20, message = "deve ter no máximo 20 caracteres")
        String telefone,
        @Size(max = 20, message = "deve ter no máximo 20 caracteres")
        String sala,
        // IDs das especialidades do médico (relacionamento Muitos-para-Muitos)
        Set<Long> especialidadeIds) {
}
