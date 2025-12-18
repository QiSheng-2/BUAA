package com.example.chatroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        // Forward to static index.html
        return "forward:/index.html";
    }

    @GetMapping("/chat")
    public String chat() {
        // Forward to static chat.html
        return "forward:/chat.html";
    }
}
