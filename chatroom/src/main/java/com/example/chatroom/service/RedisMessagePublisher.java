package com.example.chatroom.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@Service
public class RedisMessagePublisher {

    private static final Logger log = LoggerFactory.getLogger(RedisMessagePublisher.class);

    private RedisTemplate<String, String> redisTemplate;
    private ChannelTopic chatTopic;

    @Autowired
    public RedisMessagePublisher(RedisTemplate<String, String> redisTemplate, ChannelTopic chatTopic) {
        this.redisTemplate = redisTemplate;
        this.chatTopic = chatTopic;
    }

    public void publish(String message) {
        try {
            redisTemplate.convertAndSend(chatTopic.getTopic(), message);
        } catch (Exception e) {
            log.error("Failed to publish message to Redis", e);
        }
    }
}
