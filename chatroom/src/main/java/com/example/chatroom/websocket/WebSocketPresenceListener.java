package com.example.chatroom.websocket;

import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.dto.PresenceMessage;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.RoomPresenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

import java.util.List;

@Component
public class WebSocketPresenceListener {

    @Autowired
    private RoomPresenceService presenceService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    // When client subscribes to a room topic, we add them
    @EventListener
    public void handleSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        // look for destination like /topic/room.{roomId} or /topic/chat?roomId=
        String dest = sha.getDestination();
        if (dest == null) return;

        String roomId = extractRoomId(dest, sha.getNativeHeader("roomId"));
        Long userId = extractUserId(sha.getUser());
        if (userId == null && sha.getSessionAttributes() != null) {
            Object sid = sha.getSessionAttributes().get("userId");
            if (sid != null) {
                try { userId = Long.parseLong(String.valueOf(sid)); } catch (Exception ignored) {}
            }
        }
        if (roomId != null && userId != null) {
            presenceService.addUserToRoom(roomId, userId);
            PresenceMessage pm = new PresenceMessage("JOIN", String.valueOf(userId), null, "/avatars/default.png", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/room." + roomId, pm);
        }
    }

    // When client unsubscribes
    @EventListener
    public void handleUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        String dest = sha.getDestination();
        if (dest == null) return;
        String roomId = extractRoomId(dest, sha.getNativeHeader("roomId"));
        Long userId = extractUserId(sha.getUser());
        if (userId == null && sha.getSessionAttributes() != null) {
            Object sid = sha.getSessionAttributes().get("userId");
            if (sid != null) {
                try { userId = Long.parseLong(String.valueOf(sid)); } catch (Exception ignored) {}
            }
        }
        if (roomId != null && userId != null) {
            presenceService.removeUserFromRoom(roomId, userId);
            PresenceMessage pm = new PresenceMessage("LEAVE", String.valueOf(userId), null, "/avatars/default.png", System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/room." + roomId, pm);
        }
    }

    // When session disconnects, remove from all rooms
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        Long userId = extractUserId(sha.getUser());
        String username = null;

        if (sha.getSessionAttributes() != null) {
            if (userId == null) {
                Object sid = sha.getSessionAttributes().get("userId");
                if (sid != null) {
                    try { userId = Long.parseLong(String.valueOf(sid)); } catch (Exception ignored) {}
                }
            }
            Object uname = sha.getSessionAttributes().get("username");
            if (uname != null) username = String.valueOf(uname);
        }

        if (userId != null) {
            List<String> rooms = presenceService.removeUserFromAllRooms(userId);
            if (username != null) {
                for (String roomId : rooms) {
                    sendLeaveMessage(roomId, username);
                }
            }
        }
    }

    private void sendLeaveMessage(String roomId, String username) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setContent(username + " 离开了聊天室");
        dto.setType("SYSTEM");
        dto.setContentType("TEXT");
        dto.setSenderId("System");
        dto.setSenderName("System");
        dto.setTargetId(roomId);
        dto.setTimestamp(System.currentTimeMillis());

        messageService.sendMessage(dto);
    }

    private String extractRoomId(String destination, List<String> roomIdHeader) {
        // try common patterns
        if (destination.startsWith("/topic/room.")) {
            return destination.substring("/topic/room.".length());
        }
        if (destination.startsWith("/topic/chat")) {
            // maybe header
            if (roomIdHeader != null && !roomIdHeader.isEmpty()) return roomIdHeader.get(0);
        }
        // fallback: parse query
        if (destination.contains("roomId=")) {
            String[] parts = destination.split("roomId=");
            return parts[1].split("&")[0];
        }
        return null;
    }

    private Long extractUserId(java.security.Principal principal) {
        if (principal == null) return null;
        try {
            return Long.parseLong(principal.getName());
        } catch (Exception e) {
            return null;
        }
    }
}
