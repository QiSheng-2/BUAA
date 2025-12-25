package com.example.chatroom.service;

import com.example.chatroom.service.bot.BotCommand;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BotService {

    private final Map<String, BotCommand> commandMap = new HashMap<>();

    public BotService(List<BotCommand> commands) {
        for (BotCommand cmd : commands) {
            commandMap.put(cmd.getCommand().toLowerCase(), cmd);
        }
    }

    public String getReply(String query) {
        if (query == null) return "我没听清。";

        String trimmed = query.trim();
        String lower = trimmed.toLowerCase();

        if (lower.isEmpty() || lower.equals("help")) {
            return getHelpMessage();
        }

        // Find command
        String[] parts = trimmed.split("\\s+", 2);
        String cmdKey = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";

        BotCommand command = commandMap.get(cmdKey);
        if (command != null) {
            return command.execute(args);
        }

        // Fallback for partial matches if needed, or just default
        for (Map.Entry<String, BotCommand> entry : commandMap.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue().execute(args);
            }
        }

        return "🤔 我不明白 '" + query + "' 是什么意思。试试输入 '@Bot help'。";
    }

    private String getHelpMessage() {
        String commandsHelp = commandMap.values().stream()
                .map(cmd -> "• @Bot " + cmd.getCommand() + " : " + cmd.getDescription())
                .collect(Collectors.joining("\n"));

        return """
               🤖 我是聊天机器人！支持的指令：
               %s
               • @Bot help : 显示此帮助信息""".formatted(commandsHelp);
    }
}
