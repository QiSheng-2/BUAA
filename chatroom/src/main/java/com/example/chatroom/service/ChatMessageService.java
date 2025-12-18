package com.example.chatroom.service;

import com.example.chatroom.entity.ChatMessage;
import com.example.chatroom.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatMessageService {

    private final ChatMessageRepository repository;

    @Autowired
    public ChatMessageService(ChatMessageRepository repository) {
        this.repository = repository;
    }

    public ChatMessage saveRawMessage(String content) {
        ChatMessage m = new ChatMessage(content, LocalDateTime.now());
        return repository.save(m);
    }

    public void saveMessage(com.example.chatroom.dto.ChatMessage messageDto) {
        ChatMessage m = new ChatMessage(messageDto.getContent(), LocalDateTime.now());
        // Note: Entity currently doesn't support 'from' field, so we ignore it for now.
        repository.save(m);
    }
}
