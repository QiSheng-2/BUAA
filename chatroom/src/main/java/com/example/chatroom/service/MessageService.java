package com.example.chatroom.service;

import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.entity.*;
import com.example.chatroom.repository.ChatMessageRepository;
import com.example.chatroom.repository.MessageReceiptRepository;
import com.example.chatroom.repository.MessageRepository;
import com.example.chatroom.repository.UserRoomStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository repository;
    private final MessageReceiptRepository receiptRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    private ChatMessageRepository messageRepository;

    @Autowired
    private UserRoomStatusRepository userRoomStatusRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public MessageService(MessageRepository repository,
                          MessageReceiptRepository receiptRepository,
                          SimpMessagingTemplate messagingTemplate,
                          ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.receiptRepository = receiptRepository;
        this.messagingTemplate = messagingTemplate;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Message saveMessage(ChatMessageDto dto) {
        Message m = new Message();
        m.setContent(dto.getContent());
        m.setType(dto.getType());
        m.setContentType(dto.getContentType());
        m.setSenderId(dto.getSenderId());
        m.setSenderName(dto.getSenderName());
        m.setTargetId(dto.getTargetId());
        m.setSearchableContent(cleanText(dto.getContent()));
        Message saved = repository.save(m);

        // 2. 创建"未读"回执（群聊：所有成员；私聊：对方）
        List<String> receiverIds = getReceivers(dto.getTargetId(), dto.getSenderId());
        for (String userId : receiverIds) {
            MessageReceipt receipt = new MessageReceipt();
            receipt.setMessageId(saved.getId());
            receipt.setUserId(userId);
            receipt.setRead(false);
            receiptRepository.save(receipt);
        }

        // 确保始终发布事件，即使在事务外也能正常工作
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    eventPublisher.publishEvent(new MessageSavedEvent(saved, dto));
                }
            });
        } else {
            // 如果不在事务中，直接发布事件
            eventPublisher.publishEvent(new MessageSavedEvent(saved, dto));
        }

        ChatMessage message = new ChatMessage();
        message.setRoomId(dto.getTargetId()); // 房间ID
        message.setSenderId(dto.getSenderId()); // 发送者ID
        message.setSenderName(dto.getSenderName()); // 发送者名称
        message.setContent(dto.getContent()); // 内容
        message.setContentType(dto.getContentType()); // 内容类型
        message.setType(dto.getType()); // 消息类型
        message.setCreatedAt(LocalDateTime.now()); // 创建时间

        messageRepository.save(message);
        //System.out.println("消息已保存到数据库: " + dto.getContent());


        return saved;
    }

    private String cleanText(String content) {
        if (content == null) return "";
        return content.replaceAll("<[^>]*>", "").toLowerCase();
    }

    private List<String> getReceivers(String targetId, String senderId) {
        // Simplified logic:
        // If targetId looks like private_A_B, extract the other user.
        // If it's a group room, ideally we query room members.
        // For now, we might just return empty or rely on PresenceService if possible,
        // but PresenceService only knows online users.
        // Assuming we don't have a full RoomMemberService yet, we'll implement a basic check.

        if (targetId != null && targetId.startsWith("private_")) {
            // 私聊：解析出对方 ID
            try {
                String[] parts = targetId.replace("private_", "").split("_");
                if (parts.length >= 2) {
                    String id1 = parts[0];
                    String id2 = parts[1];
                    String other = id1.equals(senderId) ? id2 : id1;
                    return Collections.singletonList(other);
                }
            } catch (Exception e) {
                // ignore
            }
        }
        // For group chat, without a RoomMember repository, we can't easily know all offline members.
        // We will skip creating receipts for offline group members in this simplified version,
        // or just return empty list if we can't determine members.
        // In a real app, inject RoomMemberRepository here.
        return Collections.emptyList();
    }

    public Page<Message> findByTargetPaged(String targetId, Pageable pageable) {
        return repository.findByTargetIdOrderByCreatedAtAsc(targetId, pageable);
    }

    // New: get messages for targetId in descending order (latest first), paged
    public Page<Message> getMessagesDesc(String targetId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repository.findMessagesByTargetIdDesc(targetId, pageable);
    }

    public void sendMessage(ChatMessageDto dto) {
        // persist
        saveMessage(dto);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getHistoryMessages(String roomId, int limit) {
        List<ChatMessage> messages = messageRepository.findTop50ByRoomIdOrderByCreatedAtDesc(roomId);

        return messages.stream().map(msg -> {
            ChatMessageDto dto = new ChatMessageDto();
            dto.setContent(msg.getContent());
            dto.setContentType(msg.getContentType());
            dto.setType(msg.getType());
            dto.setSenderId(msg.getSenderId());
            dto.setSenderName(msg.getSenderName());
            dto.setTargetId(msg.getRoomId());
            dto.setTimestamp(msg.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    public void markAsRead(String userId, String roomId) {
        UserRoomStatusId id = new UserRoomStatusId(userId, roomId);
        UserRoomStatus status = userRoomStatusRepository.findById(id).orElse(null);

        if (status != null) {
            status.setUnreadCount(0);
            status.setLastReadAt(LocalDateTime.now());

            // 设置最后阅读的消息ID
            List<ChatMessage> latestMessages = chatMessageRepository.findTop1ByRoomIdOrderByCreatedAtDesc(roomId);
            if (!latestMessages.isEmpty()) {
                status.setLastReadMessageId(latestMessages.get(0).getId());
            }

            userRoomStatusRepository.save(status);
        }
    }

    // 获取用户的所有私聊未读状态
    public List<UserRoomStatus> getPrivateChatUnreadStatus(String userId) {
        return userRoomStatusRepository.findPrivateChatStatusByUserId(userId);
    }

    // 新消息到达时，增加接收者的未读计数
    @Transactional
    public void incrementUnreadCount(String receiverId, String roomId) {
        userRoomStatusRepository.incrementUnreadCount(receiverId, roomId);
    }

    // 确保用户房间状态存在（在创建房间时调用）
    public void ensureUserRoomStatus(String userId, String roomId) {
        UserRoomStatusId id = new UserRoomStatusId(userId, roomId);
        if (!userRoomStatusRepository.existsById(id)) {
            UserRoomStatus status = new UserRoomStatus();
            status.setUserId(userId);
            status.setRoomId(roomId);
            status.setUnreadCount(0);
            status.setLastReadAt(LocalDateTime.now());
            userRoomStatusRepository.save(status);
        }
    }

    @Transactional
    public void saveMessageAndIncrementUnread(com.example.chatroom.dto.ChatMessage message) {
        if (message.getRoomId() != null && message.getRoomId().startsWith("private_")) {
            String roomId = message.getRoomId();
            String senderId = message.getFrom();

            String[] parts = roomId.split("_");
            String receiverId = parts[1].equals(senderId) ? parts[2] : parts[1];

            ensureUserRoomStatus(receiverId, roomId);

            userRoomStatusRepository.incrementUnreadCount(receiverId, roomId);

            //System.out.println("未读计数+1: 发送者=" + senderId +receiverId+ ", 房间=" + roomId);
        }
    }
}