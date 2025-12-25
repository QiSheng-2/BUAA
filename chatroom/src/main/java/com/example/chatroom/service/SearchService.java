
package com.example.chatroom.service;

import com.example.chatroom.entity.Message;
import com.example.chatroom.repository.MessageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SearchService {

    private final MessageRepository messageRepository;

    public SearchService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Page<Message> searchInRoom(String roomId, String keyword, int page, int size, String currentUserId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty();
        }

        // 安全：只允许搜索当前用户有权限的房间（此处简化）
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return messageRepository.findByTargetIdAndSearchableContentContainingIgnoreCase(roomId, keyword.trim(), pageable);
    }
}