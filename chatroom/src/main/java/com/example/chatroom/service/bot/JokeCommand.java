/*
package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;
import java.util.Random;

//@Component
public class JokeCommand implements BotCommand {

    private final String[] JOKES = {
        "Why do Java programmers wear glasses? Because they don't C#.",
        "There are 10 types of people in the world: those who understand binary, and those who don't.",
        "A SQL query walks into a bar, walks up to two tables and asks, 'Can I join you?'",
        "How many programmers does it take to change a light bulb? None, that's a hardware problem.",
        "Knock, knock. Who's there? Recursion. Recursion who? Knock, knock...",
        "Debugging: Removing the needles from the haystack."
    };

    private final Random random = new Random();

    //@Override
    public String getCommand() {
        return "joke";
    }

    //@Override
    public String getDescription() {
        return "Random programming joke";
    }

    //@Override
    public String execute(String args) {
        return "😂 " + JOKES[random.nextInt(JOKES.length)];
    }
}
*/