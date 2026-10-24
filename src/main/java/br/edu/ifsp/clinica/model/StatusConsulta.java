package br.edu.ifsp.clinica.model;

import java.util.Set;

public enum StatusConsulta {
    AGENDADA,
    CHAMADA,
    EM_ATENDIMENTO,
    CONCLUIDA,
    CANCELADA;

    /** Transições permitidas a partir deste status (diagrama de estados no documento de especificação). */
    public Set<StatusConsulta> proximosPermitidos() {
        return switch (this) {
            case AGENDADA -> Set.of(CHAMADA, EM_ATENDIMENTO, CANCELADA);
            // CHAMADA -> AGENDADA: paciente não compareceu e volta para a fila
            case CHAMADA -> Set.of(CHAMADA, AGENDADA, EM_ATENDIMENTO, CANCELADA);
            case EM_ATENDIMENTO -> Set.of(CONCLUIDA);
            case CONCLUIDA, CANCELADA -> Set.of();
        };
    }

    public boolean podeMudarPara(StatusConsulta destino) {
        return proximosPermitidos().contains(destino);
    }

    public boolean isFinal() {
        return this == CONCLUIDA || this == CANCELADA;
    }
}
