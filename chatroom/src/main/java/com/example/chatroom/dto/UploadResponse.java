package com.example.chatroom.dto;

import lombok.Data;

@Data
public class UploadResponse {
    private String url;
    private String fileName;
    private String fileType;
    private long size;

    public UploadResponse(String url, String fileName, String fileType, long size) {
        this.url = url;
        this.fileName = fileName;
        this.fileType = fileType;
        this.size = size;
    }
}

