package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.javatuples.Pair;

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
        CompletableFuture.runAsync(() -> this.insertServerData(data)).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void insertServerDataSync(ServerData data) {
        this.insertServerData(data);
    }

    @Override
    public CompletableFuture<ServerData> getServerData(Server server) {
        return CompletableFuture.supplyAsync(() -> ServerData.deserialize(getConfig()));
    }

    @Override
    public void updateServerData(ServerData data) {
        if (this.plugin.isStopping()) {
            this.insertServerDataSync(data);
        } else {
            this.insertServerDataAsync(data);
        }
    }

    @Override
    public void deleteServerData() {
        CompletableFuture.runAsync(() -> {
            File file = this.getFile();
            if (file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }).exceptionally(ex -> {
            ex.printStackTrace();
            return null;
        });
    }

    @Override
    public void deleteBanFromServerData(UUID uuid, Pair<Integer, Ban> ban) {
        CompletableFuture.runAsync(() -> {
            FileConfiguration config = this.getConfig();
            config.set("OngoingBans." + uuid, null);
            this.saveConfig(config);
        }).exceptionally(ex -> {
            ex.printStackTrace();
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
