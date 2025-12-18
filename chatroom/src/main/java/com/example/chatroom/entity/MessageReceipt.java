package com.example.chatroom.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "message_receipts",
    uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"})
)
public class MessageReceipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private boolean isRead = false;

    @CreationTimestamp
    private LocalDateTime readAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}

