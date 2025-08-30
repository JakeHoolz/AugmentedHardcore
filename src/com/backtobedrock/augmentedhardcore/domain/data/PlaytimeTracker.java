package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.entity.Player;

/**
 * Handles playtime based progression for lives, max-health and revive cooldown.
 */
public class PlaytimeTracker {
    private final AugmentedHardcore plugin;
    private final PlayerData data;

    private long timeTillNextRevive;
    private long timeTillNextLifePart;
    private long timeTillNextMaxHealth;

    public PlaytimeTracker(AugmentedHardcore plugin, PlayerData data,
                           long timeTillNextRevive, long timeTillNextLifePart, long timeTillNextMaxHealth) {
        this.plugin = plugin;
        this.data = data;
        setTimeTillNextRevive(timeTillNextRevive);
        setTimeTillNextLifePart(timeTillNextLifePart);
        setTimeTillNextMaxHealth(timeTillNextMaxHealth);
    }

    public long getTimeTillNextRevive() {
        return timeTillNextRevive;
    }

    public void setTimeTillNextRevive(long timeTillNextRevive) {
        this.timeTillNextRevive = Math.max(0, Math.min(timeTillNextRevive,
                plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives()));
    }

    public long getTimeTillNextLifePart() {
        return timeTillNextLifePart;
    }

    public void setTimeTillNextLifePart(long timeTillNextLifePart) {
        this.timeTillNextLifePart = Math.max(0, Math.min(timeTillNextLifePart,
                plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart()));
    }

    public long getTimeTillNextMaxHealth() {
        return timeTillNextMaxHealth;
    }

    public void setTimeTillNextMaxHealth(long timeTillNextMaxHealth) {
        this.timeTillNextMaxHealth = Math.max(0, Math.min(timeTillNextMaxHealth,
                plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart()));
    }

    public void decreaseTimeTillNextLifePart(int amount, Player player) {
        if (data.isSpectatorBanned()) return;
        if (!plugin.getConfigurations().getLivesAndLifePartsConfiguration().isUseLifeParts()) return;
        if (!plugin.getConfigurations().getLivesAndLifePartsConfiguration().isGetLifePartsByPlaytime()) return;
        if (player.hasPermission(Permission.BYPASS_GAINLIFEPARTS_PLAYTIME.getPermissionString())) return;
        if (plugin.getConfigurations().getLivesAndLifePartsConfiguration()
                .getDisableGainingLifePartsInWorlds()
                .contains(player.getWorld().getName().toLowerCase())) return;
        if (data.getLifeManager().getLifeParts() >= plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLifeParts()) return;

        long decreased = getTimeTillNextLifePart() - amount;
        if (decreased <= 0) {
            data.gainLifeParts(1, player);
            setTimeTillNextLifePart(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart() - Math.abs(decreased));
        } else {
            setTimeTillNextLifePart(decreased);
        }
    }

    public void decreaseTimeTillNextMaxHealth(int amount, Player player) {
        if (data.isSpectatorBanned()) return;
        if (!plugin.getConfigurations().getMaxHealthConfiguration().isUseMaxHealth()) return;
        if (!plugin.getConfigurations().getMaxHealthConfiguration().isGetMaxHealthByPlaytime()) return;
        if (player.hasPermission(Permission.BYPASS_GAINMAXHEALTH_PLAYTIME.getPermissionString())) return;
        if (plugin.getConfigurations().getMaxHealthConfiguration()
                .getDisableGainingMaxHealthInWorlds()
                .contains(player.getWorld().getName().toLowerCase())) return;
        double value = PlayerUtils.getMaxHealth(player);
        if (value >= plugin.getConfigurations().getMaxHealthConfiguration().getMaxHealth()) return;

        long decreased = getTimeTillNextMaxHealth() - amount;
        if (decreased <= 0) {
            data.gainMaxHealth(1D, player);
            setTimeTillNextMaxHealth(plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart() - Math.abs(decreased));
        } else {
            setTimeTillNextMaxHealth(decreased);
        }
    }

    public void decreaseTimeTillNextRevive(int amount) {
        if (data.isSpectatorBanned()) return;
        if (!plugin.getConfigurations().getReviveConfiguration().isUseRevive()) return;
        if (timeTillNextRevive == 0) return;

        setTimeTillNextRevive(getTimeTillNextRevive() - amount);
    }
}
