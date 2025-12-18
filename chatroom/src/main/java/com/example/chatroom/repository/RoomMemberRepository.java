package com.example.chatroom.repository;

import com.example.chatroom.entity.RoomMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {
    @Transactional
    void deleteByRoomIdAndUserId(String roomId, String userId);
    
    List<RoomMember> findByRoomId(String roomId);
    
    List<RoomMember> findByUserId(String userId);
    
    @Query("SELECT rm.userId FROM RoomMember rm WHERE rm.roomId = :roomId")
    List<String> findUserIdsByRoomId(@Param("roomId") String roomId);
}