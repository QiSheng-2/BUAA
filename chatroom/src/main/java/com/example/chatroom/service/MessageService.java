package com.example.chatroom.service;

import com.example.chatroom.dto.ChatMessageDto;
import com.example.chatroom.entity.Message;
import com.example.chatroom.entity.MessageReceipt;
import com.example.chatroom.repository.MessageReceiptRepository;
import com.example.chatroom.repository.MessageRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository repository;
    private final MessageReceiptRepository receiptRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ApplicationEventPublisher eventPublisher;

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
}