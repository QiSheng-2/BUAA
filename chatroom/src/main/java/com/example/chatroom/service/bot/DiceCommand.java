package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;
import java.util.Random;

@Component
public class DiceCommand implements BotCommand {

    private final Random random = new Random();

    @Override
    public String getCommand() {
        return "dice";
    }

    @Override
    public String getDescription() {
        return "掷骰子 (1-6) 或自定义范围 (例如: 'dice 20')";
    }

    @Override
    public String execute(String args) {
        int max = 6;
        if (args != null && !args.trim().isEmpty()) {
            try {
                max = Integer.parseInt(args.trim());
                if (max < 1) max = 6;
            } catch (NumberFormatException e) {
                // ignore
            }
        }
        int result = random.nextInt(max) + 1;
        return "🎲 你掷出了 " + result + " (1-" + max + ")";
    }
}

