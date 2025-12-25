package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TimeCommand implements BotCommand {

    @Override
    public String getCommand() {
        return "time";
    }

    @Override
    public String getDescription() {
        return "当前服务器时间";
    }

    @Override
    public String execute(String args) {
        return "🕒 服务器时间: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

