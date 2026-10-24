package br.edu.ifsp.clinica.dto;

import br.edu.ifsp.clinica.model.Especialidade;

public record EspecialidadeResponse(Long id, String nome, String descricao) {

    public static EspecialidadeResponse from(Especialidade e) {
        return new EspecialidadeResponse(e.getId(), e.getNome(), e.getDescricao());
    }
}
