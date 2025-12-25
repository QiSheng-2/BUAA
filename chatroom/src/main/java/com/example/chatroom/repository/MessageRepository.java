package com.example.chatroom.repository;

import com.example.chatroom.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {
    Page<Message> findByTargetIdOrderByCreatedAtAsc(String targetId, Pageable pageable);

    @Query("SELECT m FROM Message m WHERE m.targetId = :targetId ORDER BY m.createdAt DESC")
    Page<Message> findMessagesByTargetIdDesc(@Param("targetId") String targetId, Pageable pageable);

    Page<Message> findByTargetIdAndSearchableContentContainingIgnoreCase(
        String targetId, String keyword, Pageable pageable
    );

    @Query("SELECT COUNT(m) FROM Message m " +
           "LEFT JOIN MessageReceipt r ON m.id = r.messageId AND r.userId = :userId " +
           "WHERE m.targetId = :roomId AND r.isRead = false AND m.senderId != :userId")
    long countUnreadMessages(@Param("roomId") String roomId, @Param("userId") String userId);

    @Query("SELECT m FROM Message m WHERE m.targetId = :targetId AND m.createdAt BETWEEN :startTime AND :endTime ORDER BY m.createdAt ASC")
    Page<Message> findByTargetIdAndCreatedAtBetweenOrderByCreatedAtAsc(
        @Param("targetId") String targetId, 
        @Param("startTime") java.time.LocalDateTime startTime, 
        @Param("endTime") java.time.LocalDateTime endTime, 
        Pageable pageable);
}