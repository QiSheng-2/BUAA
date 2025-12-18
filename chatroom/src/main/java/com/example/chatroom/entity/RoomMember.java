package com.example.chatroom.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "room_members")
public class RoomMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id", nullable = false)
    private String roomId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "role")
    private String role; // MEMBER / ADMIN

    public RoomMember() {}

    public RoomMember(String roomId, String userId, String role) {
        this.roomId = roomId;
        this.userId = userId;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}

