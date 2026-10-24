package br.edu.ifsp.clinica.websocket;

import br.edu.ifsp.clinica.dto.ConsultaResponse;

import java.time.LocalDateTime;

/**
 * Mensagem enviada pelo WebSocket para o painel de chamadas.
 * tipo = "CHAMADA" (paciente chamado para o consultório) ou "STATUS" (outra mudança de status).
 */
public record PainelEvento(String tipo, LocalDateTime momento, ConsultaResponse consulta) {
}
