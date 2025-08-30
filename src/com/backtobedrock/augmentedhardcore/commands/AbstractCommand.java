package com.backtobedrock.augmentedhardcore.commands;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.enums.Command;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

public abstract class AbstractCommand {

    protected final AugmentedHardcore plugin;
    protected final CommandSender cs;
    protected final String[] args;
    protected Player sender = null;
    protected OfflinePlayer target = null;

    public AbstractCommand(CommandSender cs, String[] args) {
        this.plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        this.cs = cs;
        this.args = args;
    }

    public abstract void run();

    protected boolean hasPermission(Command command) {
        return this.hasPermission(command.getPermission());
    }

    protected boolean hasPermission(Permission permission) {
        if (!this.cs.hasPermission(permission.getPermissionString())) {
            this.cs.sendMessage(this.plugin.getMessages().getNoPermissionError());
            return false;
        }
        return true;
    }

    protected boolean isPlayer() {
        if (!(this.cs instanceof Player)) {
            this.cs.sendMessage(this.plugin.getMessages().getRequireOnlinePlayerError());
            return false;
        }
        this.sender = (Player) cs;
        return true;
    }

    protected boolean hasCorrectAmountOfArguments(Command command) {
        if (this.args.length < command.getMinimumArguments() || this.args.length > command.getMaximumArguments()) {
            this.sendUsageMessage(command);
            return false;
        }
        return true;
    }

    protected Player isTargetOnline() {
        if (this.target == null) {
            return null;
        }

        if (this.target.getPlayer() == null) {
            this.cs.sendMessage(this.plugin.getMessages().getTargetNotOnlineError(this.target.getName()));
        }

        return this.target.getPlayer();
    }

    protected CompletableFuture<Boolean> hasPlayedBefore(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            @SuppressWarnings("deprecation") OfflinePlayer player = this.plugin.getServer().getOfflinePlayer(playerName);
            if (!player.hasPlayedBefore()) {
                this.cs.sendMessage(this.plugin.getMessages().getTargetNotPlayedBeforeError(player.getName()));
                return false;
            }
            this.target = player;
            return true;
        }, this.plugin.getExecutor());
    }

    public void sendUsageMessage(Command command) {
        this.cs.sendMessage(this.plugin.getMessages().getCommandUsageHeader(), command.getFancyVersion(), this.plugin.getMessages().getCommandUsageFooter());
    }
}
