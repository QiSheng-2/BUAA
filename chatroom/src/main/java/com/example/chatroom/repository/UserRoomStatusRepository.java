package com.example.chatroom.repository;

import com.example.chatroom.entity.UserRoomStatus;
import com.example.chatroom.entity.UserRoomStatusId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserRoomStatusRepository extends JpaRepository<UserRoomStatus, UserRoomStatusId> {

    List<UserRoomStatus> findByUserId(String userId);

    @Query("SELECT urs FROM UserRoomStatus urs WHERE urs.userId = :userId AND urs.roomId LIKE 'private_%'")
    List<UserRoomStatus> findPrivateChatStatusByUserId(@Param("userId") String userId);

    @Modifying
    @Transactional
    @Query("UPDATE UserRoomStatus urs SET urs.unreadCount = urs.unreadCount + 1 WHERE urs.userId = :userId AND urs.roomId = :roomId")
    void incrementUnreadCount(@Param("userId") String userId, @Param("roomId") String roomId);

    @Modifying
    @Transactional
    @Query("UPDATE UserRoomStatus urs SET urs.unreadCount = 0 WHERE urs.userId = :userId AND urs.roomId = :roomId")
    void resetUnreadCount(@Param("userId") String userId, @Param("roomId") String roomId);

    default void ensureUserRoomStatus(String userId, String roomId) {
        UserRoomStatusId id = new UserRoomStatusId(userId, roomId);
        if (!existsById(id)) {
            UserRoomStatus status = new UserRoomStatus();
            status.setUserId(userId);
            status.setRoomId(roomId);
            status.setUnreadCount(0);
            status.setLastReadAt(LocalDateTime.now());
            save(status);
        }
    }
}