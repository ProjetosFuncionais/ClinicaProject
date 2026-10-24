package br.edu.ifsp.clinica.config;

import br.edu.ifsp.clinica.websocket.PainelWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final PainelWebSocketHandler painelHandler;

    public WebSocketConfig(PainelWebSocketHandler painelHandler) {
        this.painelHandler = painelHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(painelHandler, "/ws/painel").setAllowedOrigins("*");
    }
}
