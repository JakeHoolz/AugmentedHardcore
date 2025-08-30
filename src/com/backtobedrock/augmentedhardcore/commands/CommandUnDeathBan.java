package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.logging.Level;

public class CommandUnDeathBan extends AbstractCommand {
    public CommandUnDeathBan(CommandSender cs, String[] args) {
        super(cs, args);
    }

    public CommandUnDeathBan(Player player, OfflinePlayer target) {
        super(player, new String[]{});
        this.target = target;
    }

    @Override
    public void run() {
        Command command = Command.UNDEATHBAN;

        if (!this.hasPermission(command)) {
            return;
        }

        //Needed for unbanning through GUI.
        if (this.target != null) {
            this.unDeathBan();
            return;
        }

        if (!this.hasCorrectAmountOfArguments(command)) {
            return;
        }

        this.hasPlayedBefore(this.args[0]).thenAcceptAsync(bool -> {
            if (!bool) {
                return;
            }

            this.unDeathBan();
        }).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error executing un-death-ban command.", ex);
            return null;
        });
    }

    private void unDeathBan() {
        this.plugin.getServerRepository().getServerData(this.plugin.getServer()).thenAcceptAsync(serverData -> {
            if (serverData.unDeathBan(this.target.getUniqueId())) {
                this.cs.sendMessage(this.plugin.getMessages().getCommandUndeathBan(this.target.getName()));
            } else {
                this.cs.sendMessage(this.plugin.getMessages().getTargetNotBannedByPluginError(this.target.getName(), this.plugin.getDescription().getName()));
            }
        }).exceptionally(e -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error executing un-death-ban command.", e);
            return null;
        });
    }
}
