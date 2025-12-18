/*
package com.example.chatroom.service.bot;

import org.springframework.stereotype.Component;

//@Component
public class CalcCommand implements BotCommand {

    //@Override
    public String getCommand() {
        return "calc";
    }

    //@Override
    public String getDescription() {
        return "Simple calculator (e.g., 'calc 1 + 1')";
    }

    //@Override
    public String execute(String args) {
        if (args == null || args.trim().isEmpty()) {
            return "🧮 Please provide an expression (e.g., 1 + 1)";
        }

        try {
            // Very simple parser for two operands and an operator
            String[] parts = args.trim().split("\\s+");
            if (parts.length != 3) {
                return "🧮 Invalid format. Try 'number operator number' (e.g., 5 * 3)";
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
                    if (num2 == 0) return "🧮 Cannot divide by zero!";
                    result = num1 / num2;
                    break;
                default:
                    return "🧮 Unsupported operator: " + op + ". Use +, -, *, or /";
            }

            // Format result to remove trailing zeros
            String formatted = String.format("%.10f", result).replaceAll("0*$", "").replaceAll("\\.$", "");
            return "🧮 Result: " + formatted;
        } catch (NumberFormatException e) {
            return "🧮 Invalid number format. Try 'calc 1 + 1'";
        } catch (Exception e) {
            return "🧮 Calculation error: " + e.getMessage();
        }
    }
}
*/