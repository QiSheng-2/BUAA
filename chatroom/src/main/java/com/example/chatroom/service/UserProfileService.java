package com.example.chatroom.service;

import com.example.chatroom.dto.UpdateProfileRequest;
import com.example.chatroom.entity.User;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    public User updateProfile(String username, UpdateProfileRequest request) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        // avatar 由上传接口单独处理（避免大文件走 JSON）
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getPublicProfile(String username) {
        return userRepository.findByUsername(username)
            .map(u -> {
                u.setPassword(null); // 隐藏敏感字段
                return u;
            })
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
