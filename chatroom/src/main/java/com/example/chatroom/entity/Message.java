package com.example.chatroom.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false)
    private String type; // TEXT or IMAGE or CHAT etc.

    @Column(name = "content_type")
    private String contentType; // TEXT, IMAGE, FILE

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "target_id", nullable = false)
    private String targetId; // roomId or receiverId

    @Column(columnDefinition = "TEXT")
    private String searchableContent; // 存纯文本，去除 HTML/表情等

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Message() {}

    public Message(String content, String type, String senderId, String senderName, String targetId) {
        this.content = content;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.targetId = targetId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getTargetId() { return targetId; }
    public void setTargetId(String targetId) { this.targetId = targetId; }

    // Alias for roomId to match user request
    public String getRoomId() { return targetId; }
    public void setRoomId(String roomId) { this.targetId = roomId; }

    public String getSearchableContent() { return searchableContent; }
    public void setSearchableContent(String searchableContent) { this.searchableContent = searchableContent; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
