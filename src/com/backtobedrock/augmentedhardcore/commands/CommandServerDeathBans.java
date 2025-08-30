package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.guis.GuiServerDeathBans;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.command.CommandSender;
import java.util.logging.Level;

public class CommandServerDeathBans extends AbstractCommand {
    public CommandServerDeathBans(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.SERVERDEATHBANS;

        if (!this.hasPermission(command)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }


        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> PlayerUtils.openInventory(this.sender, new GuiServerDeathBans(this.sender, serverData)), this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error executing server death bans command.", ex);
            return null;
        });
    }
}
