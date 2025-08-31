package com.backtobedrock.augmentedhardcore.runnables;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class ClearCache extends BukkitRunnable {

    private final AugmentedHardcore plugin;
    private final OfflinePlayer player;

    public ClearCache(AugmentedHardcore plugin, OfflinePlayer player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        if (!this.player.isOnline()) {
            this.plugin.getPlayerRepository().removeFromPlayerCache(this.player);
        }
    }
}
