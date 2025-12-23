package com.example.chatroom.controller;

import com.example.chatroom.entity.ChatRoom;
import com.example.chatroom.repository.RoomRepository;
import com.example.chatroom.service.RoomService;
import com.example.chatroom.dto.CreateRoomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<List<ChatRoom>> listRooms() {
        return ResponseEntity.ok(roomService.listPublicRooms());
    }

    @PostMapping
    public ResponseEntity<ChatRoom> createRoom(@RequestBody CreateRoomRequest createRequest, @AuthenticationPrincipal Principal principal) {
        // require login
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        String userId = principal.getName();
        ChatRoom room = roomService.createRoom(createRequest.getName(), userId, createRequest.isPrivate() ? "PRIVATE_GROUP" : "PUBLIC", createRequest.getPassword());
        return ResponseEntity.ok(room);
    }

    //更新房间公告
    @PostMapping("/{roomId}/announcement")
    public ResponseEntity<Map<String, String>> updateAnnouncement(
            @PathVariable String roomId,
            @RequestBody Map<String, String> body,  // 接收 JSON
            @AuthenticationPrincipal UserDetails userDetails) {

        Map<String, String> response = new HashMap<>();

        try {
            String announcement = body.get("announcement");
            if (announcement == null || announcement.trim().isEmpty()) {
                response.put("error", "公告内容不能为空");
                return ResponseEntity.badRequest().body(response);
            }

            // 验证用户权限
            // boolean hasPermission = roomService.checkPermission(roomId, userDetails.getUsername());

            roomService.updateAnnouncement(roomId, announcement);
            response.put("message", "公告已更新");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 获取房间公告
    @GetMapping("/{roomId}/announcement")
    public ResponseEntity<Map<String, String>> getAnnouncement(@PathVariable String roomId) {
        Map<String, String> response = new HashMap<>();
        try {
            String announcement = roomService.getAnnouncement(roomId);
            response.put("announcement", announcement);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{roomId}/members/{userId}")
    public ResponseEntity<Void> kickMember(
            @PathVariable String roomId,
            @PathVariable String userId,
            @AuthenticationPrincipal Principal principal
    ) {
        roomService.removeMember(roomId, userId, principal.getName());
        return ResponseEntity.ok().build();
    }
}