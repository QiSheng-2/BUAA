package com.example.chatroom.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @Pattern(regexp = "[A-Za-z][A-Za-z0-9_]{3,19}", message = "Username must start with a letter, and contain only letters, digits, or underscores (4-20 chars)")
    private String username;

    @Size(min = 6, max = 16, message = "Password must be 6-16 characters")
    private String password;

    // Getters & Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

