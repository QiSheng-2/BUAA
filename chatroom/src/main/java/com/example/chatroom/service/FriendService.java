/*
package com.example.chatroom.service;

import com.example.chatroom.entity.Friendship;
import com.example.chatroom.entity.User;
import com.example.chatroom.repository.FriendshipRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//@Service
//@Transactional
public class FriendService {

    //@Autowired
    private FriendshipRepository friendshipRepo;

    //@Autowired
    private UserRepository userRepository;

    public void sendFriendRequest(String requesterUsername, String targetUsername) {
        if (requesterUsername.equals(targetUsername)) {
            throw new IllegalArgumentException("Cannot add yourself");
        }
        // 检查是否已存在请求
        if (friendshipRepo.existsByUserIdAndFriendId(requesterUsername, targetUsername) ||
            friendshipRepo.existsByUserIdAndFriendId(targetUsername, requesterUsername)) {
            throw new IllegalStateException("Friend request already exists");
        }

        Friendship request = new Friendship();
        request.setUserId(requesterUsername);
        request.setFriendId(targetUsername);
        request.setStatus(Friendship.Status.PENDING);
        friendshipRepo.save(request);
    }

    public void acceptRequest(String username, String friendUsername) {
        Friendship request = findPendingRequest(friendUsername, username); // friend -> me
        request.setStatus(Friendship.Status.ACCEPTED);
        friendshipRepo.save(request);

        // 自动创建反向 ACCEPTED 记录（对称好友）
        Friendship reverse = new Friendship();
        reverse.setUserId(username);
        reverse.setFriendId(friendUsername);
        reverse.setStatus(Friendship.Status.ACCEPTED);
        friendshipRepo.save(reverse);
    }

    public List<User> getFriends(String username) {
        List<String> friendUsernames = friendshipRepo.findAcceptedFriends(username);
        if (friendUsernames == null || friendUsernames.isEmpty()) return List.of();
        return userRepository.findAllByUsernameIn(friendUsernames);
    }

    public List<String> getFriendIds(String username) {
        return friendshipRepo.findAcceptedFriends(username);
    }

    private Friendship findPendingRequest(String fromUsername, String toUsername) {
        return friendshipRepo.findByUserIdAndFriendIdAndStatus(fromUsername, toUsername, Friendship.Status.PENDING)
            .orElseThrow(() -> new IllegalStateException("No pending request"));
    }

    public boolean isFriend(String userA, String userB) {
        List<String> accepted = friendshipRepo.findAcceptedFriends(userA);
        return accepted != null && accepted.contains(userB);
    }
}
*/