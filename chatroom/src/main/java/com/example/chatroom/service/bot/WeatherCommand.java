package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;

@Component
public class WeatherCommand implements BotCommand {

    @Override
    public String getCommand() {
        return "weather";
    }

    @Override
    public String getDescription() {
        return "显示天气 (模拟)";
    }

    @Override
    public String execute(String args) {
        return "🌤️  北京晴，25°C。(这是一个演示机器人。请集成天气 API 以获取真实数据。)";
    }
}

