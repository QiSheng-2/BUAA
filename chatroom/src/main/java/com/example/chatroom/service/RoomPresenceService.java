package com.example.chatroom.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomPresenceService {

    // roomId -> Set of userIds
    private final Map<String, Set<String>> roomMembers = new ConcurrentHashMap<>();

    public void addUserToRoom(String roomId, Long userId) {
        roomMembers.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(String.valueOf(userId));
    }

    public void removeUserFromRoom(String roomId, Long userId) {
        Set<String> members = roomMembers.get(roomId);
        if (members != null) {
            members.remove(String.valueOf(userId));
            if (members.isEmpty()) {
                roomMembers.remove(roomId);
            }
        }
    }

    public java.util.List<String> removeUserFromAllRooms(Long userId) {
        String userIdStr = String.valueOf(userId);
        java.util.List<String> joinedRooms = new java.util.ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : roomMembers.entrySet()) {
            if (entry.getValue().remove(userIdStr)) {
                joinedRooms.add(entry.getKey());
            }
        }
        // Clean up empty rooms
        roomMembers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return joinedRooms;
    }

    public Set<String> getMembersInRoom(String roomId) {
        return roomMembers.getOrDefault(roomId, Set.of());
    }
}
