package com.example.chatroom.websocket;

import com.example.chatroom.entity.ChatRoom;
//import com.example.chatroom.service.RoomService;//
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class RoomAccessHandshakeInterceptor implements HandshakeInterceptor {

    //private final RoomService roomService;

    public RoomAccessHandshakeInterceptor(/*RoomService roomService*/) {
        //this.roomService = roomService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (!(request instanceof ServletServerHttpRequest servletRequest)) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }
        HttpServletRequest httpReq = servletRequest.getServletRequest();
        String path = httpReq.getRequestURI(); // expect /ws/chat/{roomId}
        String[] parts = path.split("/");
        String roomIdStr = null;
        for (int i = 0; i < parts.length; i++) {
            if ("chat".equals(parts[i]) && i + 1 < parts.length) {
                roomIdStr = parts[i+1];
                break;
            }
        }
        if (roomIdStr == null) {
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        /*
        ChatRoom room = roomService.findByRoomId(roomIdStr);
        if (room == null) { response.setStatusCode(HttpStatus.NOT_FOUND); return false; }

        // user check: require principal present (login)
        if (httpReq.getUserPrincipal() == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        */

        // attach roomId for later use
        attributes.put("roomId", roomIdStr);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) { }
}