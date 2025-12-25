package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;

@Component
public class CalcCommand implements BotCommand {

    @Override
    public String getCommand() {
        return "calc";
    }

    @Override
    public String getDescription() {
        return "简易计算器 (例如: 'calc 1 + 1')";
    }

    @Override
    public String execute(String args) {
        if (args == null || args.trim().isEmpty()) {
            return "🧮 请提供一个表达式 (例如: 1 + 1)";
        }

        try {
            // Very simple parser for two operands and an operator
            String[] parts = args.trim().split("\\s+");
            if (parts.length != 3) {
                return "🧮 格式无效。请尝试 '数字 运算符 数字' (例如: 5 * 3)";
            }

            double num1 = Double.parseDouble(parts[0]);
            String op = parts[1];
            double num2 = Double.parseDouble(parts[2]);
            double result = 0;

            switch (op) {
                case "+": result = num1 + num2; break;
                case "-": result = num1 - num2; break;
                case "*": result = num1 * num2; break;
                case "/":
                    if (num2 == 0) return "🧮 不能除以零！";
                    result = num1 / num2;
                    break;
                default:
                    return "🧮 不支持的运算符: " + op + "。请使用 +, -, *, 或 /";
            }

            // Format result to remove trailing zeros
            String formatted = String.format("%.10f", result).replaceAll("0*$", "").replaceAll("\\.$", "");
            return "🧮 结果: " + formatted;
        } catch (NumberFormatException e) {
            return "🧮 无效的数字格式。请尝试 'calc 1 + 1'";
        } catch (Exception e) {
            return "🧮 计算错误: " + e.getMessage();
        }
    }
}

