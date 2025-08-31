package com.backtobedrock.augmentedhardcore.repositories;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.StorageType;
import com.backtobedrock.augmentedhardcore.mappers.server.IServerMapper;
import com.backtobedrock.augmentedhardcore.mappers.server.MySQLServerMapper;
import com.backtobedrock.augmentedhardcore.mappers.server.YAMLServerMapper;
import org.bukkit.Server;
import org.javatuples.Pair;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ServerRepository {
    private final AugmentedHardcore plugin;

    private IServerMapper mapper;

    //server cache
    private ServerData serverData = null;

    public ServerRepository(AugmentedHardcore plugin) {
        this.plugin = plugin;
        this.initializeMapper();
        this.getServerData(this.plugin.getServer())
                .thenAcceptAsync(serverData -> this.plugin.getLogger().log(Level.INFO, String.format("Loaded %d ongoing death %s.", serverData.getTotalOngoingBans(), serverData.getTotalOngoingBans() != 1 ? "bans" : "ban")), this.plugin.getExecutor())
                .exceptionally(e -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not load server data asynchronously.", e);
            return null;
        });
    }

    public void onReload() {
        this.initializeMapper();
    }

    private void initializeMapper() {
        this.mapper = new YAMLServerMapper(this.plugin);
        if (this.plugin.getConfigurations().getDataConfiguration().getStorageType() == StorageType.MYSQL) {
            this.mapper = MySQLServerMapper.getInstance(this.plugin);
        } else {
            this.mapper = new YAMLServerMapper(this.plugin);
        }
    }

    public CompletableFuture<ServerData> getServerData(Server server) {
        if (this.serverData == null) {
            return this.mapper.getServerData(server)
                    .thenApplyAsync(this::getFromDataAndCache, this.plugin.getExecutor());
        } else {
            return CompletableFuture.supplyAsync(this::getFromCache, this.plugin.getExecutor());
        }
    }

    public ServerData getServerDataSync() {
        return this.serverData;
    }

    private ServerData getFromDataAndCache(ServerData serverData) {
        if (serverData == null) {
            serverData = new ServerData(this.plugin);
            this.mapper.insertServerDataAsync(serverData);
        }
        this.serverData = serverData;
        return this.serverData;
    }

    private ServerData getFromCache() {
        return this.serverData;
    }

    public void updateServerData(ServerData data) {
        this.mapper.updateServerData(data);
    }

    public void removeBanFromServerData(UUID uuid, Pair<Integer, Ban> banPair) {
        this.mapper.deleteBanFromServerData(uuid, banPair);
    }
}
