package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class JokeCommand implements BotCommand {

    private final String[] JOKES = {
        "为什么 Java 程序员戴眼镜？因为他们看不清 C#。",
        "世界上有 10 种人：懂二进制的和不懂二进制的。",
        "一个 SQL 查询走进一家酒吧，走到两张桌子前问：'我可以加入你们吗？'",
        "换一个灯泡需要多少个程序员？一个都不用，那是硬件问题。",
        "叩叩。谁在那里？递归。递归谁？叩叩...",
        "调试：从干草堆里找针。"
    };

    private final Random random = new Random();

    @Override
    public String getCommand() {
        return "joke";
    }

    @Override
    public String getDescription() {
        return "随机编程笑话";
    }

    @Override
    public String execute(String args) {
        return "😂 " + JOKES[random.nextInt(JOKES.length)];
    }
}

