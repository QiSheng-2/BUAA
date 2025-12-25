package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;

@Component
public class EchoCommand implements BotCommand {

    @Override
    public String getCommand() {
        return "echo";
    }

    @Override
    public String getDescription() {
        return "复读你的消息";
    }

    @Override
    public String execute(String args) {
        return "🦜 " + (args != null ? args : "");
    }
}

