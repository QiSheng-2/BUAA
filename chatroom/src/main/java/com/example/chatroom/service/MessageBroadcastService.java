package com.example.chatroom.service;

import com.example.chatroom.websocket.ChatEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Service
public class MessageBroadcastService {

    // A thread-safe set to store all active WebSocket sessions
    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private RedisMessagePublisher publisher;

    // Publish message to Redis for cross-instance communication
    public void publishToRedis(String message) {
        publisher.publish(message);
    }

    // Broadcast message to all WebSocket clients on the current instance
    public void broadcastLocally(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (IOException e) {
                        // Log error or handle it
                    }
                }
            }
        }
    }

    // Register a new WebSocket session
    public void registerSession(Session session) {
        sessions.add(session);
    }

    // Remove a closed WebSocket session
    public void removeSession(Session session) {
        sessions.remove(session);
    }
}