package com.example.chatroom.controller;

import com.example.chatroom.dto.UpdateProfileRequest;
import com.example.chatroom.entity.User;
import com.example.chatroom.service.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserProfileService userProfileService;

    public UserController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getCurrentUserProfile(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String username = userDetails.getUsername();
        User user = userProfileService.getPublicProfile(username);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/profile")
    public ResponseEntity<User> updateProfile(
            @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String username = userDetails.getUsername();
        User updated = userProfileService.updateProfile(username, request);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> getProfile(@PathVariable String username) {
        return ResponseEntity.ok(userProfileService.getPublicProfile(username));
    }
}