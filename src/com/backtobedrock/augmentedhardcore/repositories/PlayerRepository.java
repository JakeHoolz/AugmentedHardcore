package com.backtobedrock.augmentedhardcore.repositories;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.mappers.player.IPlayerMapper;
import com.backtobedrock.augmentedhardcore.mappers.player.MySQLPlayerMapper;
import com.backtobedrock.augmentedhardcore.mappers.player.YAMLPlayerMapper;
import com.backtobedrock.augmentedhardcore.runnables.ClearCache;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public class PlayerRepository {
    private final AugmentedHardcore plugin;

    //player cache
    private final Map<UUID, PlayerData> playerCache;
    private IPlayerMapper mapper;

    public PlayerRepository(AugmentedHardcore plugin) {
        this.plugin = plugin;
        this.playerCache = new ConcurrentHashMap<>();
        this.initializeMapper();
    }

    public void onReload() {
        this.initializeMapper();
        this.playerCache.forEach((key, value) -> {
            Player player = this.plugin.getServer().getPlayer(key);
            if (player != null) {
                value.onReload(player);
            }
        });
    }

    private void initializeMapper() {
        if (this.plugin.getConfigurations().getDataConfiguration().getStorageType() == StorageType.MYSQL) {
            this.mapper = new MySQLPlayerMapper(this.plugin);
        } else {
            this.mapper = new YAMLPlayerMapper(this.plugin);
        }
    }

    public CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            return this.mapper.getByPlayer(player)
                    .thenApplyAsync(playerData -> this.getFromDataAndCache(player, playerData), this.plugin.getExecutor());
        } else {
            return CompletableFuture.supplyAsync(() -> player, this.plugin.getExecutor())
                    .thenApplyAsync(this::getFromCache, this.plugin.getExecutor())
                    .exceptionally(ex -> {
                        this.plugin.getLogger().log(Level.SEVERE, String.format("Failed to retrieve PlayerData for %s from cache.", player.getName()), ex);
                        return null;
                    });
        }
    }

    public PlayerData getByPlayerSync(OfflinePlayer player) {
        if (!this.playerCache.containsKey(player.getUniqueId())) {
            return this.getFromDataAndCache(player, this.mapper.getByPlayerSync(player));
        } else {
            return this.getFromCache(player);
        }
    }

    private PlayerData getFromDataAndCache(OfflinePlayer player, PlayerData playerData) {
        if (playerData == null) {
            playerData = new PlayerData(this.plugin, player);
            if (player.hasPlayedBefore())
                this.mapper.insertPlayerDataAsync(playerData);
        }
        this.playerCache.put(player.getUniqueId(), playerData);

        if (!player.isOnline()) {
            new ClearCache(player).runTaskLater(this.plugin, 6000);
        }

        return this.getFromCache(player);
    }

    private PlayerData getFromCache(OfflinePlayer player) {
        return this.playerCache.get(player.getUniqueId());
    }

    public void updatePlayerData(PlayerData data) {
        this.mapper.updatePlayerData(data);
    }

    public void deletePlayerData(OfflinePlayer player) {
        this.mapper.deletePlayerData(player);
    }

    public void removeFromPlayerCache(OfflinePlayer player) {
        this.playerCache.remove(player.getUniqueId());
    }
}
