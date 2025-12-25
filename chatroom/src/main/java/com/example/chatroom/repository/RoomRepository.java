package com.example.chatroom.repository;

import com.example.chatroom.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<ChatRoom, Long> {
    List<ChatRoom> findByType(String type);
    ChatRoom findByRoomId(String roomId);
    
    @Query("SELECT r FROM ChatRoom r WHERE :userId IN ELEMENTS(r.adminIds)")
    List<ChatRoom> findRoomsByAdminId(@Param("userId") String userId);
    
    List<ChatRoom> findByCreatorId(String creatorId);
}