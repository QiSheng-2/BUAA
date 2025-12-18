package com.example.chatroom.repository;

import com.example.chatroom.entity.MessageReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MessageReceiptRepository extends JpaRepository<MessageReceipt, Long> {

    @Modifying
    @Query("UPDATE MessageReceipt r SET r.isRead = true, r.readAt = CURRENT_TIMESTAMP " +
           "WHERE r.messageId IN (SELECT m.id FROM Message m WHERE m.targetId = :roomId) " +
           "AND r.userId = :userId AND r.isRead = false")
    void markAsReadByRoomAndUser(@Param("roomId") String roomId, @Param("userId") String userId);

    @Modifying
    @Query("DELETE FROM MessageReceipt r WHERE r.isRead = true AND r.readAt < :threshold")
    int deleteReadReceiptsOlderThan(@Param("threshold") LocalDateTime threshold);
    
    List<MessageReceipt> findByUserIdAndIsReadFalse(String userId);
    
    List<MessageReceipt> findByMessageIdInAndUserId(List<Long> messageIds, String userId);
    
    @Query("SELECT r FROM MessageReceipt r WHERE r.messageId IN (SELECT m.id FROM Message m WHERE m.targetId = :roomId) AND r.userId = :userId")
    List<MessageReceipt> findByRoomIdAndUserId(@Param("roomId") String roomId, @Param("userId") String userId);
}