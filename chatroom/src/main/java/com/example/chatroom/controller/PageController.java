package com.example.chatroom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/chat"})
    public String index() {
        // Forward to static index.html
        return "forward:/index.html";
    }
}

