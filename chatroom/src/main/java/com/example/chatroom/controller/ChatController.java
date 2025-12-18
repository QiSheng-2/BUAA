package com.example.chatroom.controller;

import com.example.chatroom.dto.ChatMessage;
import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.service.MessageService;
import com.example.chatroom.service.RoomPresenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private MessageService messageService;

    @Autowired
    private RoomPresenceService roomPresenceService;

    // 统一的消息处理方法
    @MessageMapping("/chat.message")
    public void handleMessage(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.debug("Received /chat.message payload={} sessionAttributes={}", message, headerAccessor.getSessionAttributes());

        String senderId = null;
        String senderName = null;

        if (headerAccessor.getSessionAttributes() != null) {
            Object sid = headerAccessor.getSessionAttributes().get("userId");
            if (sid != null) senderId = String.valueOf(sid);
            Object sname = headerAccessor.getSessionAttributes().get("username");
            if (sname != null) senderName = String.valueOf(sname);
        }

        // Fallback: try Principal (may be set by security or handshake)
        Principal principal = headerAccessor.getUser();
        if ((senderId == null || senderId.isEmpty()) && principal != null) {
            senderId = principal.getName();
        }

        // Fallback: if still missing, derive from username or generate a stable placeholder
        if ((senderId == null || senderId.isEmpty())) {
            if (senderName != null && !senderName.isEmpty()) {
                senderId = String.valueOf(Math.abs(senderName.hashCode()));
            } else {
                senderName = (senderName == null) ? "Anonymous" : senderName;
                senderId = "ANON-" + Math.abs((senderName + System.currentTimeMillis()).hashCode());
            }
        }

        // Ensure senderName is set
        if (senderName == null) {
            senderName = (principal != null ? principal.getName() : "Anonymous");
        }

        message.setFrom(senderName);
        message.setTimestamp(System.currentTimeMillis());

        ChatMessageDto dto = new ChatMessageDto();
        dto.setContent(message.getContent());
        dto.setType(message.getType()); // GROUP or PRIVATE
        dto.setContentType(message.getContentType());
        dto.setSenderId(senderId);
        dto.setSenderName(senderName);
        // 修复：正确设置targetId为roomId，如果没有roomId则使用默认的room_general
        String targetId = (message.getRoomId() != null) ? String.valueOf(message.getRoomId()) : "room_general";
        dto.setTargetId(targetId);
        // 如果timestamp为空则设置当前时间
        dto.setTimestamp(message.getTimestamp() > 0 ? message.getTimestamp() : System.currentTimeMillis());

        log.info("User {} ({}) sending to {}: {}", dto.getSenderName(), dto.getSenderId(), dto.getTargetId(), dto.getContent());

        messageService.sendMessage(dto);
    }

    // 客户端发送一条 join 消息以触发 presence
    @MessageMapping("/chat.join")
    public void join(ChatMessage message, SimpMessageHeaderAccessor headerAccessor) {
        log.info("Received /chat.join payload={} sessionAttributes={}", message, headerAccessor.getSessionAttributes());

        String username = message.getFrom();
        String roomId = (message.getRoomId() != null) ? String.valueOf(message.getRoomId()) : "room_general";
        if (username == null) username = "Anonymous";

        // ensure session attributes exist
        if (headerAccessor.getSessionAttributes() != null) {
            String userId = String.valueOf(Math.abs(username.hashCode() & 0x7fffffff));
            headerAccessor.getSessionAttributes().put("username", username);
            headerAccessor.getSessionAttributes().put("userId", userId);

            // register into room presence map so presence listener can use it if subscribe happens after join
            try {
                long uid = Long.parseLong(userId);
                roomPresenceService.addUserToRoom(roomId, uid);
                log.info("Registered userId={} username={} into room={}", uid, username, roomId);
            } catch (NumberFormatException ignore) {
            }
        }
    }
}