package com.example.chatroom.controller;

import com.example.chatroom.dto.AuthResponse;
import com.example.chatroom.dto.LoginRequest;
import com.example.chatroom.dto.RegisterRequest;
import com.example.chatroom.entity.User;
import com.example.chatroom.service.JwtService;
import com.example.chatroom.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok("Register success");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        // 使用 AuthenticationManager 进行认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 认证成功后生成 Token
        User user = userService.findByUsername(request.getUsername());
        String token = jwtService.generateToken(user.getUsername());
        return ResponseEntity.ok(new AuthResponse(token, user.getUsername(), user.getRole()));
    }
}

