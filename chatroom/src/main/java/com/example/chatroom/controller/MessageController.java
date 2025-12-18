/*
package com.example.chatroom.controller;

import com.example.chatroom.entity.Message;
import com.example.chatroom.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

//@RestController
//@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // existing: /api/rooms/{targetId}/messages
    //@GetMapping("/rooms/{targetId}/messages")
    public ResponseEntity<Page<Message>> getMessagesByRoom(@PathVariable String targetId, Pageable pageable) {
        Page<Message> page = messageService.findByTargetPaged(targetId, pageable);
        return ResponseEntity.ok(page);
    }

    // new history endpoint: /api/messages/{roomId}?page=0&size=50
    //@GetMapping("/messages/{roomId}")
    public ResponseEntity<Page<Message>> getHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        Page<Message> messages = messageService.getMessagesDesc(roomId, page, size);
        return ResponseEntity.ok(messages);
    }
}
*/