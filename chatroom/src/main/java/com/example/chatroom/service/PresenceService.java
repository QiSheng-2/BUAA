package com.example.chatroom.service;

import com.example.chatroom.dto.PresenceMessage;
import com.example.chatroom.dto.PresenceUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class PresenceService {

    private final Map<String, PresenceMessage> onlineUsers = new ConcurrentHashMap<>();
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    @Lazy
    private FriendService friendService;

    public PresenceService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void userJoined(String userId, String username) {
        String avatar = "/avatars/default.png";
        long now = System.currentTimeMillis();

        PresenceMessage presence = new PresenceMessage("JOIN", userId, username, avatar, now);
        onlineUsers.put(userId, presence);

        // Broadcast to everyone (legacy)
        messagingTemplate.convertAndSend("/topic/presence", presence);

        // Update status to ONLINE
        updateUserStatus(userId, "ONLINE");
    }

    public void userLeft(String userId) {
        PresenceMessage presence = onlineUsers.remove(userId);
        if (presence != null) {
            presence.setType("LEAVE");
            presence.setLastActive(System.currentTimeMillis());
            messagingTemplate.convertAndSend("/topic/presence", presence);

            updateUserStatus(userId, "OFFLINE");
        }
    }

    public void sendOnlineListToUser(String destination) {
        var list = onlineUsers.values().stream()
                .map(p -> new PresenceMessage("ONLINE_LIST", p.getUserId(), p.getUsername(), p.getAvatar(), p.getLastActive()))
                .collect(Collectors.toList());
        messagingTemplate.convertAndSend(destination, list);
    }

    public void updateUserStatus(String userId, String status) {
        // Update internal state if needed
        if (onlineUsers.containsKey(userId)) {
            // onlineUsers.get(userId).setStatus(status); // If PresenceMessage had status
        }

        // 只广播给该用户的好友
        if (friendService != null) {
            try {
                // Assuming userId is username here as per FriendService assumption
                // But wait, userId in PresenceService is usually the ID or username?
                // In ChatController: userId = String.valueOf(username.hashCode() & 0x7fffffff);
                // This is inconsistent. Let's assume userId passed here is the username for FriendService compatibility.
                // If userId is the numeric ID string, we need to map it.
                // For now, let's assume the "userId" passed to userJoined is actually the username or we can look it up.
                // In ChatController.join: userId is generated from hash if null.
                // Let's try to use username if available.
                // But userJoined takes userId and username.
                // Let's use the username for friend lookup
                String username = onlineUsers.get(userId) != null ? onlineUsers.get(userId).getUsername() : null;

                if (username != null) {
                    List<String> friendIds = friendService.getFriendIds(username);
                    for (String friendId : friendIds) {
                        messagingTemplate.convertAndSendToUser(
                            friendId, "/queue/presence",
                            new PresenceUpdate(username, status)
                        );
                    }
                }
            } catch (Exception e) {
                // ignore
            }
        }
    }
}