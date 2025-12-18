package com.example.chatroom.dto;

public class PresenceMessage {
    private String type;        // "JOIN" 或 "LEAVE" or "ONLINE_LIST"
    private String userId;
    private String username;
    private String avatar;      // URL or Base64
    private long lastActive;    // timestamp ms

    public PresenceMessage() {}

    public PresenceMessage(String type, String userId, String username, String avatar, long lastActive) {
        this.type = type;
        this.userId = userId;
        this.username = username;
        this.avatar = avatar;
        this.lastActive = lastActive;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public long getLastActive() { return lastActive; }
    public void setLastActive(long lastActive) { this.lastActive = lastActive; }
}
