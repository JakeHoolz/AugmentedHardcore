package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.guis.AbstractGui;
import com.backtobedrock.augmentedhardcore.guis.GuiRevive;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.command.CommandSender;
import java.util.logging.Level;

public class CommandRevive extends AbstractCommand {
    public CommandRevive(CommandSender cs, String[] args) {
        super(cs, args);
    }

    @Override
    public void run() {
        Command command = Command.REVIVE;

        if (!this.hasPermission(command)) {
            return;
        }

        if (!this.isPlayer()) {
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
            if (!bool) {
                return;
            }

            this.plugin.getPlayerRepository().getByPlayer(this.sender).thenAcceptAsync(playerData -> {
                if (!playerData.checkRevivePermissionsReviver(this.target, this.sender)) {
                    return;
                }

                AbstractGui gui = new GuiRevive(playerData, this.target);
                PlayerUtils.openInventory(this.sender, gui);
            }, this.plugin.getExecutor()).exceptionally(ex -> {
                this.plugin.getLogger().log(Level.SEVERE, "Error executing revive command.", ex);
                return null;
            });
        }, this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error executing revive command.", ex);
            return null;
        });
    }
}
