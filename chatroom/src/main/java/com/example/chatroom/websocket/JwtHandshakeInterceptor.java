package com.example.chatroom.websocket;

import com.example.chatroom.service.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger log = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtService jwtService;

    // Default true; set to false in application.yml or via -D to disable JWT requirement for local testing
    @Value("${app.websocket.require-jwt:true}")
    private boolean requireJwt;

    public JwtHandshakeInterceptor(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public boolean beforeHandshake(org.springframework.http.server.ServerHttpRequest request,
                                   org.springframework.http.server.ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception {
        String query = request.getURI().getQuery();
        log.debug("WebSocket handshake request URI: {}", request.getURI());

        if (!requireJwt) {
            // In dev mode, populate a deterministic dev username and id to avoid null sender issues
            String devUsername = "devuser" + (ThreadLocalRandom.current().nextInt(1000, 10000));
            String devUserId = String.valueOf(Math.abs(devUsername.hashCode()));
            attributes.put("username", devUsername);
            attributes.put("userId", devUserId);
            log.info("JWT handshake requirement disabled (app.websocket.require-jwt=false). Assigning dev username '{}' and userId '{}'.", devUsername, devUserId);
            return true;
        }

        if (query == null || !query.contains("token=")) {
            log.warn("WebSocket handshake missing token query: {}", query);
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        String token = query.split("token=")[1].split("&")[0];
        try {
            String username = jwtService.extractUsername(token);
            if (jwtService.isTokenValid(token, username)) {
                attributes.put("username", username); // 传递给后续处理器
                return true;
            }
        } catch (Exception e) {
            log.warn("JWT validation failed during WebSocket handshake", e);
        }
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    @Override
    public void afterHandshake(org.springframework.http.server.ServerHttpRequest request,
                               org.springframework.http.server.ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // cleanup if needed
    }
}
