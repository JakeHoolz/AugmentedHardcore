package com.backtobedrock.augmentedhardcore.utilities;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import org.bukkit.command.CommandSender;
import java.util.OptionalInt;

public class CommandUtils {
    public static Command getCommand(String command) {
        try {
            return Command.valueOf(command.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static OptionalInt getPositiveNumberFromString(CommandSender sender, String number) {
        try {
            int convertedNumber = Integer.parseInt(number);
            if (convertedNumber < 1) {
                sender.sendMessage(String.format("§c%s is not a valid number between 1 and %d.", number, Integer.MAX_VALUE));
                return OptionalInt.empty();
            }
            return OptionalInt.of(convertedNumber);
        } catch (NumberFormatException e) {
            sender.sendMessage(String.format("§c%s is not a valid number between 1 and %d.", number, Integer.MAX_VALUE));
            return OptionalInt.empty();
        }
    }
}
