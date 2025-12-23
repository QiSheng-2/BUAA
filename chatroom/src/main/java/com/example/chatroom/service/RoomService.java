package com.example.chatroom.service;

import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.entity.ChatRoom;
import com.example.chatroom.repository.RoomMemberRepository;
import com.example.chatroom.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.example.chatroom.entity.ChatRoom;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    private RoomMemberRepository roomMemberRepository;

    @Autowired
    private MessageService messageService;

    public RoomService(RoomRepository roomRepository, @Lazy SimpMessagingTemplate messagingTemplate) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    //初始化默认聊天大厅
    @PostConstruct
    public void initDefaultRoom() {
        // 检查是否存在默认聊天大厅，如果不存在则创建
        ChatRoom defaultRoom = roomRepository.findByRoomId("room_general");
        if (defaultRoom == null) {
            defaultRoom = new ChatRoom();
            defaultRoom.setRoomId("room_general");
            defaultRoom.setName("General Chat");
            defaultRoom.setType("PUBLIC");
            defaultRoom.setCreatorId("system");
            defaultRoom.setAdminIds(Set.of("system"));
            roomRepository.save(defaultRoom);
            System.out.println("默认聊天大厅已创建: room_general");
        } else {
            System.out.println("默认聊天大厅已存在: room_general");
        }
    }

    public List<ChatRoom> listPublicRooms() {
        return roomRepository.findByType("PUBLIC");
    }

    @Transactional
    public ChatRoom createRoom(String name, String creatorId, String type, String password) {
        String roomId = "room_" + UUID.randomUUID().toString().replace("-", "");
        ChatRoom room = new ChatRoom(roomId, name, type, creatorId);
        room.setAdminIds(Set.of(creatorId)); // 创建者自动为管理员
        // TODO: handle password and private groups
        return roomRepository.save(room);
    }

    public ChatRoom findById(Long id) {
        return roomRepository.findById(id).orElse(null);
    }

    public ChatRoom findByRoomId(String roomId) {
        return roomRepository.findByRoomId(roomId);
    }

    // Find or create a direct 1:1 room between two users. RoomId format: private_min_max
    public ChatRoom findOrCreateDirectRoom(String userIdA, String userIdB) {
        if (userIdA == null || userIdB == null) return null;
        if (userIdA.equals(userIdB)) {
            // self chat -> normalize to a personal room
            String roomId = "private_" + userIdA + "_" + userIdA;
            ChatRoom existing = roomRepository.findByRoomId(roomId);
            if (existing != null) return existing;
            ChatRoom room = new ChatRoom(roomId, null, "DIRECT", userIdA);
            return roomRepository.save(room);
        }
        // normalized ordering to avoid duplicate rooms
        String min = userIdA.compareTo(userIdB) < 0 ? userIdA : userIdB;
        String max = userIdA.compareTo(userIdB) < 0 ? userIdB : userIdA;
        String roomId = "private_" + min + "_" + max;

        ChatRoom existing = roomRepository.findByRoomId(roomId);
        if (existing != null) return existing;

        ChatRoom room = new ChatRoom(roomId, null, "DIRECT", min);
        return roomRepository.save(room);
    }

    public void updateAnnouncement(String roomId, String newAnnouncement, String updaterId) {
        ChatRoom room = roomRepository.findByRoomId(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        if (!room.getAdminIds().contains(updaterId) && !room.getCreatorId().equals(updaterId)) {
            throw new RuntimeException("Only admins can update announcement");
        }
        room.setAnnouncement(newAnnouncement);
        roomRepository.save(room);
    }

    public void removeMember(String roomId, String targetUserId, String operatorId) {
        ChatRoom room = roomRepository.findByRoomId(roomId);
        if (room == null) throw new RuntimeException("Room not found");

        if (!room.getAdminIds().contains(operatorId) && !room.getCreatorId().equals(operatorId)) {
            throw new RuntimeException("Only admins can kick members");
        }
        // 从 room_members 表删除
        roomMemberRepository.deleteByRoomIdAndUserId(roomId, targetUserId);

        // WebSocket 通知被踢用户
        messagingTemplate.convertAndSendToUser(targetUserId, "/queue/kicked", roomId);
    }

    @Transactional(readOnly = true)
    public String getAnnouncement(String roomId) {
        ChatRoom room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return "";
        }
        return room.getAnnouncement() != null ? room.getAnnouncement() : "";
    }

    @Transactional
    public void updateAnnouncement(String roomId, String announcement) {
        ChatRoom room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            throw new RuntimeException("房间不存在: " + roomId);
        }

        room.setAnnouncement(announcement);
        roomRepository.save(room);

        if (messageService != null) {
            String messageContent = "📢 系统公告: " + announcement;
            messageService.sendMessage(new ChatMessageDto(messageContent,"SYSTEM","System","System",roomId,System.currentTimeMillis(),"TEXT"));
            //System.out.println("公告消息已发送到房间 " + roomId);
        }
    }
}