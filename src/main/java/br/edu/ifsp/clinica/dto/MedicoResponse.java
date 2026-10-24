package br.edu.ifsp.clinica.dto;

import br.edu.ifsp.clinica.model.Medico;

import java.util.Comparator;
import java.util.List;

public record MedicoResponse(
        Long id,
        String nome,
        String crm,
        String email,
        String telefone,
        String sala,
        List<EspecialidadeResponse> especialidades) {

    public static MedicoResponse from(Medico m) {
        List<EspecialidadeResponse> esp = m.getEspecialidades().stream()
                .map(EspecialidadeResponse::from)
                .sorted(Comparator.comparing(EspecialidadeResponse::nome))
                .toList();
        return new MedicoResponse(m.getId(), m.getNome(), m.getCrm(), m.getEmail(),
                m.getTelefone(), m.getSala(), esp);
    }
}
