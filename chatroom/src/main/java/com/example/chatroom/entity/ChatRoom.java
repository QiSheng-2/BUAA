package com.example.chatroom.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false, unique = true)
    private String roomId; // e.g. room_123 or private_min_max

    @Column(nullable = true)
    private String name;

    @Column(nullable = false)
    private String type; // PUBLIC / PRIVATE_GROUP / DIRECT

    @Column(name = "creator_id")
    private String creatorId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private String announcement; // 房间公告

    // ✅ 新增：管理员列表（JSON 字符串 or 关联表）
    @ElementCollection
    @CollectionTable(name = "room_admins", joinColumns = @JoinColumn(name = "room_id"))
    @Column(name = "admin_id")
    private java.util.Set<String> adminIds = new java.util.HashSet<>();

    public ChatRoom() {}

    public ChatRoom(String roomId, String name, String type, String creatorId) {
        this.roomId = roomId;
        this.name = name;
        this.type = type;
        this.creatorId = creatorId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCreatorId() { return creatorId; }
    public void setCreatorId(String creatorId) { this.creatorId = creatorId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }
    public java.util.Set<String> getAdminIds() { return adminIds; }
    public void setAdminIds(java.util.Set<String> adminIds) { this.adminIds = adminIds; }
}
