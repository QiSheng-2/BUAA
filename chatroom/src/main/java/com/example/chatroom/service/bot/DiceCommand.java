/*
package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;
import java.util.Random;

//@Component
public class DiceCommand implements BotCommand {

    private final Random random = new Random();

    //@Override
    public String getCommand() {
        return "dice";
    }

    //@Override
    public String getDescription() {
        return "Roll a die (1-6) or custom range (e.g., 'dice 20')";
    }

    //@Override
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
        return "🎲 You rolled a " + result + " (1-" + max + ")";
    }
}
*/