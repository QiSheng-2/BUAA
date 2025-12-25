package com.example.chatroom.repository;

import com.example.chatroom.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    boolean existsByUserIdAndFriendId(String userId, String friendId);
    Optional<Friendship> findByUserIdAndFriendIdAndStatus(String userId, String friendId, Friendship.Status status);

    @Query("SELECT f.friendId FROM Friendship f WHERE f.userId = :userId AND f.status = 'ACCEPTED'")
    List<String> findAcceptedFriends(@Param("userId") String userId);
    
    @Query("SELECT f.userId FROM Friendship f WHERE f.friendId = :userId AND f.status = 'ACCEPTED'")
    List<String> findFriendIdsByUserId(@Param("userId") String userId);
    
    List<Friendship> findByUserIdAndStatus(String userId, Friendship.Status status);

    List<Friendship> findByFriendIdAndStatus(String friendId, Friendship.Status status);

    boolean existsByUserIdAndFriendIdAndStatus(String userId, String friendId, Friendship.Status status);
}