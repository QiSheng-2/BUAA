package com.example.chatroom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Component
public class MessageBroadcastListener {

    private static final Logger log = LoggerFactory.getLogger(MessageBroadcastListener.class);

    private final SimpMessagingTemplate messagingTemplate;

    public MessageBroadcastListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @EventListener
    public void handleMessageSaved(MessageSavedEvent event) {
        var saved = event.message();
        var dto = event.dto();

        // Build a payload expected by the front-end
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", saved.getContent());
        payload.put("contentType", saved.getContentType());
        payload.put("senderName", saved.getSenderName());
        payload.put("senderId", saved.getSenderId());
        payload.put("type", saved.getType());
        payload.put("targetId", saved.getTargetId());

        if (saved.getCreatedAt() != null) {
            payload.put("timestamp", saved.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        } else {
            payload.put("timestamp", System.currentTimeMillis());
        }

        String topic = "/topic/room." + dto.getTargetId();
        log.info("Broadcasting saved message id={} to {} payload={}", saved.getId(), topic, payload);

        // 发送消息到指定房间
        messagingTemplate.convertAndSend(topic, payload);
    }
}