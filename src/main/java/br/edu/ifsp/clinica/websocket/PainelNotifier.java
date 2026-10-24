package br.edu.ifsp.clinica.websocket;

import br.edu.ifsp.clinica.dto.ConsultaResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;

/** Converte eventos de consulta em JSON e envia para os painéis conectados. */
@Component
public class PainelNotifier {

    private final PainelWebSocketHandler handler;
    private final ObjectMapper mapper;

    public PainelNotifier(PainelWebSocketHandler handler, ObjectMapper mapper) {
        this.handler = handler;
        this.mapper = mapper;
    }

    public void notificar(String tipo, ConsultaResponse consulta) {
        String json;
        try {
            json = mapper.writeValueAsString(new PainelEvento(tipo, LocalDateTime.now(), consulta));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Erro ao gerar JSON do evento", e);
        }
        // Só envia depois que a alteração foi salva no banco (commit)
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    handler.broadcast(json);
                }
            });
        } else {
            handler.broadcast(json);
        }
    }
}
