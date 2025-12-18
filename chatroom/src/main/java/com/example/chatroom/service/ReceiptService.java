/*
package com.example.chatroom.service;

import com.example.chatroom.repository.MessageReceiptRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

//@Service
public class ReceiptService {

    private final MessageReceiptRepository receiptRepository;

    public ReceiptService(MessageReceiptRepository receiptRepository) {
        this.receiptRepository = receiptRepository;
    }

    //@Transactional
    public void markRoomAsRead(String roomId, String userId) {
        // 批量更新：将该房间中该用户的所有未读消息标记为已读
        receiptRepository.markAsReadByRoomAndUser(roomId, userId);
    }

    *//**
     * 定期清理 30 天前的已读回执，防止 message_receipts 表无限膨胀。
     *//*
    //@Scheduled(cron = "0 0 3 * * *") // 每天凌晨 3 点
    //@Transactional
    public void purgeOldReadReceipts() {
        LocalDateTime threshold = LocalDateTime.now().minusDays(30);
        int removed = receiptRepository.deleteReadReceiptsOlderThan(threshold);
        // Optional: log removed count using logger if available
    }
}
*/