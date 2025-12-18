package com.example.chatroom.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nickname;
    private String bio;
}

