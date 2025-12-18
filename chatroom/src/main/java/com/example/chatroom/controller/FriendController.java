/*
package com.example.chatroom.controller;

import com.example.chatroom.entity.User;
import com.example.chatroom.service.FriendService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RestController
//@RequestMapping("/api/friends")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    //@PostMapping("/request/{targetId}")
    public ResponseEntity<Void> sendRequest(
            @PathVariable String targetId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        friendService.sendFriendRequest(userDetails.getUsername(), targetId);
        return ResponseEntity.ok().build();
    }

    //@PostMapping("/accept/{friendId}")
    public ResponseEntity<Void> acceptRequest(
            @PathVariable String friendId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        friendService.acceptRequest(userDetails.getUsername(), friendId);
        return ResponseEntity.ok().build();
    }

    //@GetMapping
    public ResponseEntity<List<User>> getFriends(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(friendService.getFriends(userDetails.getUsername()));
    }
}
*/