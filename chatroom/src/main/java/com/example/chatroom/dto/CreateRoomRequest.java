package com.example.chatroom.dto;

public class CreateRoomRequest {
    private String name;
    private boolean isPrivate;
    private String password;

    public CreateRoomRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean aPrivate) { isPrivate = aPrivate; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

