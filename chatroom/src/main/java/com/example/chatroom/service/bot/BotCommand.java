package com.example.chatroom.service.bot;

public interface BotCommand {
    String getCommand();
    String getDescription();
    String execute(String args);
}

