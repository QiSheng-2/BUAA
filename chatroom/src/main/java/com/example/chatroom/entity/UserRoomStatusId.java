package com.example.chatroom.entity;

import java.io.Serializable;

public class UserRoomStatusId implements Serializable {
    private String userId;
    private String roomId;

    public UserRoomStatusId() {}

    public UserRoomStatusId(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    // 必须重写 equals 和 hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserRoomStatusId that = (UserRoomStatusId) o;
        return userId.equals(that.userId) && roomId.equals(that.roomId);
    }

    @Override
    public int hashCode() {
        return userId.hashCode() + roomId.hashCode();
    }
}