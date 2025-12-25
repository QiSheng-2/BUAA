package com.example.chatroom.repository;

import com.example.chatroom.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);

    List<User> findAllByUsernameIn(List<String> usernames);
    
    @Query("SELECT u.username FROM User u WHERE u.id IN :ids")
    List<String> findUsernamesByIds(@Param("ids") List<Long> ids);
}