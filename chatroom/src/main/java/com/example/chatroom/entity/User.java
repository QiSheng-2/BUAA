package com.example.chatroom.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String username;

    @Column(nullable = false)
    private String password; // BCrypt encrypted

    @Column(nullable = false)
    private String role; // "USER" or "ADMIN"

    private String nickname;      // 昵称（可改）
    private String avatar;        // 头像 URL
    private String bio;           // 个人简介
    private String status;        // "ONLINE", "OFFLINE", "BUSY", "AWAY"
    private Long lastActive;      // 最后活跃时间戳
}
