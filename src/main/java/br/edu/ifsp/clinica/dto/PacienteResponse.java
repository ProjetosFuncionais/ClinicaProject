package br.edu.ifsp.clinica.dto;

import br.edu.ifsp.clinica.model.Paciente;

import java.time.LocalDate;

public record PacienteResponse(
        Long id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String telefone,
        String email) {

    public static PacienteResponse from(Paciente p) {
        return new PacienteResponse(p.getId(), p.getNome(), formatarCpf(p.getCpf()),
                p.getDataNascimento(), p.getTelefone(), p.getEmail());
    }

    private static String formatarCpf(String cpf) {
        if (cpf == null || cpf.length() != 11) return cpf;
        return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
    }
}
