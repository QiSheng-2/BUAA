package com.example.chatroom.dto;

public class ChatMessage {
    private String from;
    private String content;
    private long timestamp;

    // new fields
    private String type; // CHAT, JOIN, LEAVE, IMAGE, SYSTEM
    private String contentType; // TEXT, IMAGE, FILE
    private Long roomId;

    public String getFrom() {
        return from;
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

    public Long getRoomId() { return roomId; }
    public void setRoomId(Long roomId) { this.roomId = roomId; }
}
