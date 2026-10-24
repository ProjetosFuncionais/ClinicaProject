package br.edu.ifsp.clinica.dto;

import br.edu.ifsp.clinica.model.Consulta;
import br.edu.ifsp.clinica.model.StatusConsulta;

import java.time.LocalDateTime;

public record ConsultaResponse(
        Long id,
        LocalDateTime dataHora,
        StatusConsulta status,
        String observacoes,
        MedicoResumo medico,
        PacienteResumo paciente) {

    public record MedicoResumo(Long id, String nome, String sala) {}

    public record PacienteResumo(Long id, String nome) {}

    public static ConsultaResponse from(Consulta c) {
        return new ConsultaResponse(
                c.getId(), c.getDataHora(), c.getStatus(), c.getObservacoes(),
                new MedicoResumo(c.getMedico().getId(), c.getMedico().getNome(), c.getMedico().getSala()),
                new PacienteResumo(c.getPaciente().getId(), c.getPaciente().getNome()));
    }
}
