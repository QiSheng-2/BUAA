package com.example.chatroom.dto;

import java.time.LocalDateTime;

public class ChatMessageDto {
    private String content;
    private String type; // GROUP or PRIVATE
    private String contentType; // TEXT, IMAGE, FILE
    private String senderId;
    private String senderName;
    private String targetId; // roomId or receiverId
    private Long timestamp;

    public ChatMessageDto() {}

    public ChatMessageDto(String content, String type, String senderId, String senderName, String targetId, Long timestamp) {
        this.content = content;
        this.type = type;
        this.senderId = senderId;
        this.senderName = senderName;
        this.targetId = targetId;
        this.timestamp = timestamp;
    }

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
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}
