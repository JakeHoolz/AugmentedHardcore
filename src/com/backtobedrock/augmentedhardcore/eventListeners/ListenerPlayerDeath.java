package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.logging.Level;

public class ListenerPlayerDeath extends AbstractEventListener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void PlayerDeathListener(PlayerDeathEvent event) {
        Player player = event.getEntity();

        this.plugin.getPlayerRepository().getByPlayer(player)
                .thenAcceptAsync(playerData -> playerData.onDeath(event, player), this.plugin.getExecutor())
                .exceptionallyAsync(ex -> {
                    this.plugin.getLogger().log(Level.SEVERE, "Error handling player death.", ex);
                    return null;
                }, this.plugin.getExecutor());
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
