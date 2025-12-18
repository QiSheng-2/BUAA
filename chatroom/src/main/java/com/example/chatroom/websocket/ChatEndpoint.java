package com.example.chatroom.websocket;

import com.example.chatroom.service.MessageBroadcastService;
import com.example.chatroom.service.RedisMessagePublisher;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Simple WebSocket chat endpoint.
 *
 * Note: to allow @ServerEndpoint to use Spring-managed beans, you'll typically
 * add a custom configurator to fetch beans from the application context.
 * For simplicity, this example assumes MessageBroadcastService and
 * RedisMessagePublisher are obtained via a static SpringContextHolder or
 * similar mechanism, which you can adjust as needed.
 */
@ServerEndpoint("/ws/chat")
@Component
public class ChatEndpoint {

    private static final Logger log = LoggerFactory.getLogger(ChatEndpoint.class);

    // These would be wired via a static accessor to Spring beans or a custom Configurator.
    private static MessageBroadcastService messageBroadcastService;
    private static RedisMessagePublisher redisMessagePublisher;

    public static void setMessageBroadcastService(MessageBroadcastService service) {
        messageBroadcastService = service;
    }

    public static void setRedisMessagePublisher(RedisMessagePublisher publisher) {
        redisMessagePublisher = publisher;
    }

    @OnOpen
    public void onOpen(Session session) {
        log.info("WebSocket opened: {}", session.getId());
        if (messageBroadcastService != null) {
            messageBroadcastService.registerSession(session);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("Received message from {}: {}", session.getId(), message);
        if (redisMessagePublisher != null) {
            redisMessagePublisher.publish(message);
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        log.info("WebSocket closed: {}, reason: {}", session.getId(), reason);
        if (messageBroadcastService != null) {
            messageBroadcastService.removeSession(session);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("WebSocket error on session " + (session != null ? session.getId() : "n/a"), throwable);
    }
}
