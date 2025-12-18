package com.example.chatroom.service;

import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.entity.Message;

/**
 * Domain event published after a chat message (and its receipts) commit successfully.
 */
public record MessageSavedEvent(Message message, ChatMessageDto dto) {}

