/*
package com.example.chatroom.controller;

import com.example.chatroom.entity.ChatRoom;
import com.example.chatroom.service.RoomService;
import com.example.chatroom.dto.CreateRoomRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

//@RestController
//@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    //@GetMapping
    public ResponseEntity<List<ChatRoom>> listRooms() {
        return ResponseEntity.ok(roomService.listPublicRooms());
    }

    //@PostMapping
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest createRequest, @AuthenticationPrincipal Principal principal) {
        // require login
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = principal.getName();
        ChatRoom room = roomService.createRoom(createRequest.getName(), userId, createRequest.isPrivate() ? "PRIVATE_GROUP" : "PUBLIC", createRequest.getPassword());
        return ResponseEntity.ok(room);
    }

    //@GetMapping("/private")
    public ResponseEntity<ChatRoom> getOrCreatePrivateRoom(@RequestParam("with") String otherUserId, @AuthenticationPrincipal Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        String myId = principal.getName();
        ChatRoom room = roomService.findOrCreateDirectRoom(myId, otherUserId);
        return ResponseEntity.ok(room);
    }

    //@PostMapping("/{roomId}/announcement")
    public ResponseEntity<Void> updateAnnouncement(
            @PathVariable String roomId,
            @RequestBody String announcement,
            @AuthenticationPrincipal Principal principal
    ) {
        roomService.updateAnnouncement(roomId, announcement, principal.getName());
        return ResponseEntity.ok().build();
    }

    //@DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity<Void> kickMember(
            @PathVariable String roomId,
            @PathVariable String userId,
            @AuthenticationPrincipal Principal principal
    ) {
        roomService.removeMember(roomId, userId, principal.getName());
        return ResponseEntity.ok().build();
    }
}
*/