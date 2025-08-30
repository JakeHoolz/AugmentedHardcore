package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.logging.Level;

public class ListenerPlayerJoin extends AbstractEventListener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(e -> e.onJoin(player)).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error handling player join.", ex);
            return null;
        });

        if (player.isOp() && this.plugin.getUpdateChecker().isOutdated()) {
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () -> player.sendMessage(String.format("§eA new version (§f%s§e) of §f%s§e is available on Spigot.org. Your current version is §f%s§e.", this.plugin.getUpdateChecker().getNewestVersion(), this.plugin.getDescription().getName(), this.plugin.getDescription().getVersion())), 5L);
        }
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
