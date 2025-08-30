package com.backtobedrock.augmentedhardcore.eventListeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.logging.Level;

public class ListenerEntityDeath extends AbstractEventListener {

    @EventHandler
    public void onEntityKill(EntityDeathEvent event) {
        if (!(event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
            return;
        }

        EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getEntity().getLastDamageCause();
        if (!(entityDamageByEntityEvent.getDamager() instanceof Player)) {
            return;
        }

        Player player = (Player) entityDamageByEntityEvent.getDamager();

        this.plugin.getPlayerRepository().getByPlayer(player).thenAcceptAsync(playerData -> playerData.onEntityKill(event.getEntity().getType(), player)).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Error handling entity death.", ex);
            return null;
        });
    }

    @Override
    public boolean isEnabled() {
        return ((this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts() && this.plugin.getConfigurations().getLivesAndLifePartsConfiguration().isLifePartsOnKill()) || (this.plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth() && this.plugin.getConfigurations().getMaxHealthConfiguration().isMaxHealthIncreaseOnKill()));
    }
}
