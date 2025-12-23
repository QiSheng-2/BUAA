package com.example.chatroom.repository;

import com.example.chatroom.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    // 按需添加查询方法
    // 查询某个房间的历史消息（分页）
    List<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId);

    // 查询最近N条消息
    List<ChatMessage> findTop50ByRoomIdOrderByCreatedAtDesc(String roomId);

    // 删除某个房间的旧消息（可选：清理历史）
    void deleteByRoomIdAndCreatedAtBefore(String roomId, LocalDateTime date);

    List<ChatMessage> findTop50ByRoomIdOrderByCreatedAtAsc(String roomId);

    List<ChatMessage> findTop1ByRoomIdOrderByCreatedAtDesc(String roomId);
}