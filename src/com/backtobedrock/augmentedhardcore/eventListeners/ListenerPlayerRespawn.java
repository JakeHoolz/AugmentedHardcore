package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.logging.Level;

public class ListenerPlayerRespawn extends AbstractEventListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(e -> e.onRespawn(player)).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error handling player respawn.", ex);
            return null;
        });
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
