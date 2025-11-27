package com.example.chatroom.config;

import com.example.chatroom.service.MessageBroadcastService;
import com.example.chatroom.service.RedisMessagePublisher;
import com.example.chatroom.websocket.ChatEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
public class WebSocketConfig {

    @Bean
    public ServerEndpointExporter serverEndpointExporter(MessageBroadcastService messageBroadcastService,
                                                         RedisMessagePublisher redisMessagePublisher) {
        // Wire Spring beans into the static setters of ChatEndpoint
        ChatEndpoint.setMessageBroadcastService(messageBroadcastService);
        ChatEndpoint.setRedisMessagePublisher(redisMessagePublisher);
        return new ServerEndpointExporter();
    }
}

