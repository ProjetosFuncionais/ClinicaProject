package br.edu.ifsp.clinica.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.ConcurrentWebSocketSessionDecorator;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servidor WebSocket do painel de chamadas (ws://localhost:8080/ws/painel).
 * Guarda as conexões abertas e envia a mesma mensagem para todas (broadcast).
 */
@Component
public class PainelWebSocketHandler extends TextWebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(PainelWebSocketHandler.class);

    // Decorator torna o envio seguro quando várias threads enviam ao mesmo tempo
    private final Map<String, WebSocketSession> sessoes = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessoes.put(session.getId(), new ConcurrentWebSocketSessionDecorator(session, 5000, 64 * 1024));
        log.info("Painel conectado: {} (total: {})", session.getId(), sessoes.size());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessoes.remove(session.getId());
        log.info("Painel desconectado: {} (total: {})", session.getId(), sessoes.size());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        // Mensagem "ping" do cliente para checar a conexão
        if ("ping".equalsIgnoreCase(message.getPayload().trim())) {
            sessoes.getOrDefault(session.getId(), session).sendMessage(new TextMessage("pong"));
        }
    }

    public void broadcast(String json) {
        TextMessage msg = new TextMessage(json);
        sessoes.values().forEach(s -> {
            try {
                if (s.isOpen()) s.sendMessage(msg);
            } catch (IOException | IllegalStateException e) {
                log.warn("Falha ao enviar para {}: {}", s.getId(), e.getMessage());
            }
        });
    }
}
