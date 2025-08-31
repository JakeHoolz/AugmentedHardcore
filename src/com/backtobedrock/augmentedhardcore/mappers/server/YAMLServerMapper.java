package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.BanEntry;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class YAMLServerMapper implements IServerMapper {
    private final AugmentedHardcore plugin;

    public YAMLServerMapper(AugmentedHardcore plugin) {
        this.plugin = plugin;
    }

    private void insertServerData(ServerData data) {
        FileConfiguration config = this.getConfig();
        data.serialize().forEach(config::set);
        this.saveConfig(config);
    }

    @Override
    public void insertServerDataAsync(ServerData data) {
        CompletableFuture.runAsync(() -> this.insertServerData(data), this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not insert server data.", ex);
            return null;
        });
    }

    @Override
    public void insertServerDataSync(ServerData data) {
        this.insertServerData(data);
    }

    @Override
    public CompletableFuture<ServerData> getServerData(Server server) {
        return CompletableFuture.supplyAsync(() -> ServerData.deserialize(getConfig()), this.plugin.getExecutor());
    }

    @Override
    public CompletableFuture<Void> updateServerData(ServerData data) {
        if (this.plugin.isStopping()) {
            this.insertServerDataSync(data);
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> this.insertServerData(data), this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not insert server data.", ex);
            return null;
        });
    }

    @Override
    public void deleteServerData() {
        CompletableFuture.runAsync(() -> {
            File file = this.getFile();
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }, this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not delete server data file.", ex);
            return null;
        });
    }

    @Override
    public void deleteBanFromServerData(UUID uuid, BanEntry ban) {
        CompletableFuture.runAsync(() -> {
            FileConfiguration config = this.getConfig();
            config.set("OngoingBans." + uuid, null);
            this.saveConfig(config);
        }, this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not delete ban from server data.", ex);
            return null;
        });
    }

    private void saveConfig(FileConfiguration config) {
        try {
            config.save(this.getFile());
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot save to {0}", this.getFile().getName());
        }
    }

    private FileConfiguration getConfig() {
        return YamlConfiguration.loadConfiguration(this.getFile());
    }

    private File getFile() {
        File file = new File(this.plugin.getDataFolder() + "/server.yml");
        if (!file.exists()) {
            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Cannot create server data file.");
            }
        }
        return file;
    }
}
