package com.example.chatroom.dto;

public class ChatMessage {
    private String from;
    private String content;
    private long timestamp;

    // new fields
    private String type; // CHAT, JOIN, LEAVE, IMAGE, SYSTEM
    private String contentType; // TEXT, IMAGE, FILE
    private String roomId;
    private String senderName;
    private String senderId;

    public String getFrom() {
        return from;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getRoomId() { return roomId; }
    public void setRoomId(String roomId) { this.roomId = roomId; }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderId() {
        return senderId;
    }
}
