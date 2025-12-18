package com.example.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PresenceUpdate {
    private String userId;
    private String status; // ONLINE, BUSY, AWAY
}

