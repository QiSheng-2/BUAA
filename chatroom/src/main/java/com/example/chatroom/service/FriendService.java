package com.example.chatroom.service;

import com.example.chatroom.dto.FriendRequestDto;
import com.example.chatroom.entity.Friendship;
import com.example.chatroom.entity.User;
import com.example.chatroom.repository.FriendshipRepository;
import com.example.chatroom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendService {

    @Autowired
    private FriendshipRepository friendshipRepo;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void sendFriendRequest(String requesterUsername, String targetUsername) {
        if (requesterUsername.equals(targetUsername)) {
            throw new IllegalArgumentException("cannot_add_self");
        }

        if (!userRepository.existsByUsername(targetUsername)) {
            throw new IllegalArgumentException("error_user_not_found");
        }

        boolean isAlreadyFriend = friendshipRepo.existsByUserIdAndFriendIdAndStatus(
                requesterUsername,
                targetUsername,
                Friendship.Status.ACCEPTED
        ) || friendshipRepo.existsByUserIdAndFriendIdAndStatus(
                targetUsername,
                requesterUsername,
                Friendship.Status.ACCEPTED
        );

        if (isAlreadyFriend) {
            throw new IllegalStateException("error_friend_already");
        }

        if (friendshipRepo.existsByUserIdAndFriendId(requesterUsername, targetUsername) ||
                friendshipRepo.existsByUserIdAndFriendId(targetUsername, requesterUsername)) {
            throw new IllegalStateException("error_duplicate");
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

    @Transactional(readOnly = true)
    public List<FriendRequestDto> getPendingRequests(String username) {
        //System.out.println("=== 查询待处理的好友申请 ===");
        //System.out.println("目标用户名: " + username);

        List<Friendship> pendingRequests = friendshipRepo.findByFriendIdAndStatus(
                username,
                Friendship.Status.PENDING
        );

        //System.out.println("数据库查询结果数量: " + pendingRequests.size());

        List<FriendRequestDto> result = pendingRequests.stream()
                .map(request -> {
                    System.out.println("处理申请记录: userId=" + request.getUserId() +
                            ", friendId=" + request.getFriendId() +
                            ", status=" + request.getStatus());

                    User fromUser = userRepository.findByUsername(request.getUserId())
                            .orElse(null);

                    if (fromUser == null) {
                        //System.out.println("找不到请求者: " + request.getUserId());
                        return null; // 跳过这条记录
                    }

                    System.out.println("找到请求者: " + fromUser.getUsername() +
                            ", nickname: " + fromUser.getNickname());

                    // 创建 DTO
                    return new FriendRequestDto(
                            fromUser.getUsername(),
                            fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getUsername(),
                            (fromUser.getNickname() != null ? fromUser.getNickname() : fromUser.getUsername()) +
                                    " 请求添加你为好友"
                    );
                })
                .filter(dto -> dto != null) // 过滤掉 null 值
                .collect(Collectors.toList());

        //System.out.println("最终返回的 DTO 数量: " + result.size());
        //System.out.println("=== 查询完成 ===");

        return result;
    }

    public boolean hasPendingRequests(String username) {
        return !friendshipRepo.findByUserIdAndFriendIdAndStatus(
                null,
                username,
                Friendship.Status.PENDING
        ).isEmpty();
    }

    public void rejectRequest(String username, String friendUsername) {
        // 查找待处理的申请（对方发给我的）
        Friendship request = friendshipRepo.findByUserIdAndFriendIdAndStatus(
                friendUsername,  // 发送者
                username,        // 接收者
                Friendship.Status.PENDING
        ).orElseThrow(() -> new IllegalStateException("没有找到待处理的好友申请"));

        // 删除申请记录（或更新为 REJECTED）
        friendshipRepo.delete(request);
        // 或者：request.setStatus(Friendship.Status.REJECTED); friendshipRepo.save(request);
    }
}