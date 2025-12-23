package com.example.chatroom.controller;

import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.entity.ChatRoom;
import com.example.chatroom.entity.Message;
import com.example.chatroom.entity.UserRoomStatus;
import com.example.chatroom.repository.ChatRoomRepository;
import com.example.chatroom.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;
    private final ChatRoomRepository chatRoomRepository;

    public MessageController(MessageService messageService,
                             ChatRoomRepository chatRoomRepository) {
        this.messageService = messageService;
        this.chatRoomRepository = chatRoomRepository;
    }

    // existing: /api/rooms/{targetId}/messages
    @GetMapping("/rooms/{targetId}/messages")
    public ResponseEntity<Page<Message>> getMessagesByRoom(@PathVariable String targetId, Pageable pageable) {
        Page<Message> page = messageService.findByTargetPaged(targetId, pageable);
        return ResponseEntity.ok(page);
    }

    // new history endpoint: /api/messages/{roomId}?page=0&size=50
    @GetMapping("/rooms/{roomId}/history")
    public ResponseEntity<List<ChatMessageDto>> getHistory(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "50") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<ChatMessageDto> history = messageService.getHistoryMessages(roomId, limit);
        return ResponseEntity.ok(history);
    }

    @GetMapping("/rooms/private")
    public ResponseEntity<Map<String, Object>> getOrCreatePrivateRoom(
            @RequestParam String with,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        String currentUser = userDetails.getUsername();
        String[] users = {currentUser, with};
        Arrays.sort(users);
        String roomId = "private_" + users[0] + "_" + users[1];

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByRoomId(roomId);

        Map<String, Object> response = new HashMap<>();
        response.put("roomId", roomId);
        response.put("type", "PRIVATE");

        if (existingRoom.isPresent()) {
            response.put("name", existingRoom.get().getName());
            response.put("status", "existing");
        } else {
            ChatRoom newRoom = new ChatRoom(roomId, null, "PRIVATE", null);
            chatRoomRepository.save(newRoom);
            response.put("name", null);
            response.put("status", "created");
        }

        // ✅ 确保两个用户的状态记录都存在
        messageService.ensureUserRoomStatus(currentUser, roomId);
        messageService.ensureUserRoomStatus(with, roomId);

        return ResponseEntity.ok(response);
    }

    // ✅ 新增：标记已读
    @PostMapping("/rooms/{roomId}/mark-read")
    public ResponseEntity<Void> markRoomAsRead(
            @PathVariable String roomId,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        messageService.markAsRead(userDetails.getUsername(), roomId);
        return ResponseEntity.ok().build();
    }

    // ✅ 新增：获取好友未读状态
    @GetMapping("/friends/unread-status")
    public ResponseEntity<List<Map<String, Object>>> getFriendsWithUnreadStatus(
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<UserRoomStatus> statuses = messageService.getPrivateChatUnreadStatus(userDetails.getUsername());
        List<Map<String, Object>> result = new ArrayList<>();

        for (UserRoomStatus status : statuses) {
            String roomId = status.getRoomId();
            String[] parts = roomId.split("_");
            String friendUsername = parts[1].equals(userDetails.getUsername()) ? parts[2] : parts[1];

            Map<String, Object> friendData = new HashMap<>();
            friendData.put("username", friendUsername);
            friendData.put("unreadCount", status.getUnreadCount());
            friendData.put("roomId", roomId);

            result.add(friendData);
        }

        return ResponseEntity.ok(result);
    }
}