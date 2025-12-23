package com.example.chatroom.controller;

import com.example.chatroom.dto.FriendRequestDto;
import com.example.chatroom.entity.User;
import com.example.chatroom.service.FriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @PostMapping("/request/{targetId}")
    public ResponseEntity<Map<String, String>> sendRequest(
            @PathVariable String targetId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            friendService.sendFriendRequest(userDetails.getUsername(), targetId);
            response.put("message", "好友申请已发送");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalStateException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("error", "服务器错误: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/accept/{friendId}")
    public ResponseEntity<Map<String, String>> acceptRequest(
            @PathVariable String friendId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            friendService.acceptRequest(userDetails.getUsername(), friendId);
            response.put("message", "已添加为好友");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriends(userDetails.getUsername()));
    }

    @GetMapping("/pending")
    public ResponseEntity<List<FriendRequestDto>> getPendingRequests(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<FriendRequestDto> requests = friendService.getPendingRequests(userDetails.getUsername());
        return ResponseEntity.ok(requests);
    }

    // ✅ 新增：拒绝好友请求
    @PostMapping("/reject/{friendId}")
    public ResponseEntity<Map<String, String>> rejectRequest(
            @PathVariable String friendId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            friendService.rejectRequest(userDetails.getUsername(), friendId);
            response.put("message", "已拒绝好友申请");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}