package com.backtobedrock.augmentedhardcore.mappers.player;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.mappers.AbstractMapper;
import com.backtobedrock.augmentedhardcore.mappers.ban.MySQLBanMapper;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.javatuples.Pair;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MySQLPlayerMapper extends AbstractMapper implements IPlayerMapper {
    private static MySQLPlayerMapper instance;

    public static synchronized MySQLPlayerMapper getInstance(AugmentedHardcore plugin) {
        if (instance == null) {
            instance = new MySQLPlayerMapper(plugin);
        }
        return instance;
    }

    private MySQLPlayerMapper(AugmentedHardcore plugin) {
        super(plugin);
    }

    @Override
    public void insertPlayerDataAsync(PlayerData playerData) {
        CompletableFuture.runAsync(() -> this.updatePlayerData(playerData), this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, String.format("Could not insert PlayerData for %s.", playerData.getPlayer().getName()), ex);
            return null;
        });
    }

    @Override
    public void insertPlayerDataSync(PlayerData playerData) {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.updatePlayerData(playerData));
    }

    @Override
    public CompletableFuture<PlayerData> getByPlayer(OfflinePlayer player) {
        return CompletableFuture.supplyAsync(() -> this.getPlayerData(player), this.plugin.getExecutor());
    }

    private PlayerData getPlayerData(OfflinePlayer player) {
        String sql = "SELECT * "
                + "FROM ah_ban AS b "
                + "RIGHT OUTER JOIN ah_player as p ON p.player_uuid = b.player_uuid "
                + "WHERE p.player_uuid = ?;";

        try (Connection connection = this.database.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            NavigableMap<Integer, Ban> deathBans = new TreeMap<>();
            PlayerData playerData = null;
            while (resultSet.next()) {
                if (playerData == null) {
                    playerData = new PlayerData(
                            this.plugin,
                            player,
                            resultSet.getString("last_known_ip"),
                            resultSet.getTimestamp("last_death") == null ? LocalDateTime.now() : resultSet.getTimestamp("last_death").toLocalDateTime(),
                            resultSet.getInt("lives"),
                            resultSet.getInt("life_parts"),
                            resultSet.getBoolean("spectator_banned"),
                            resultSet.getLong("time_till_next_revive"),
                            resultSet.getLong("time_till_next_life_part"),
                            resultSet.getLong("time_till_next_max_health"),
                            deathBans
                    );
                }
                Pair<Integer, Ban> banPair = MySQLBanMapper.getInstance(this.plugin).getBanFromResultSetSync(resultSet);
                if (banPair != null) {
                    deathBans.put(banPair.getValue0(), banPair.getValue1());
                }
            }
            return playerData;
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, String.format("Could not get PlayerData for %s.", player.getName()), e);
        }
        return null;
    }

    @Override
    public PlayerData getByPlayerSync(OfflinePlayer player) {
        return this.getPlayerData(player);
    }

    @Override
    public void updatePlayerData(PlayerData playerData) {
        if (this.plugin.isStopping()) {
            this.updatePlayerDataSync(playerData);
        } else {
            CompletableFuture.runAsync(() -> this.updatePlayerDataSync(playerData), this.plugin.getExecutor()).exceptionally(ex -> {
                this.plugin.getLogger().log(Level.SEVERE, String.format("Could not update PlayerData for %s asynchronously.", playerData.getPlayer().getName()), ex);
                return null;
            });
        }
    }

    // Previously named updatePlayerDateSync; renamed for accuracy.
    private void updatePlayerDataSync(PlayerData playerData) {
        String sql = "INSERT INTO ah_player (`player_uuid`, `last_known_name`, `last_known_ip`, `last_death`, `lives`, `life_parts`, `spectator_banned`, `time_till_next_revive`, `time_till_next_life_part`, `time_till_next_max_health`)"
                + "VALUES(?,?,?,?,?,?,?,?,?,?)"
                + "ON DUPLICATE KEY UPDATE "
                + "`last_known_name` = ?,"
                + "`last_known_ip` = ?,"
                + "`last_death` = ?,"
                + "`lives` = ?,"
                + "`life_parts` = ?,"
                + "`spectator_banned` = ?,"
                + "`time_till_next_revive` = ?,"
                + "`time_till_next_life_part` = ?,"
                + "`time_till_next_max_health` = ?;";

        try (Connection connection = this.database.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, playerData.getPlayer().getUniqueId().toString());
            preparedStatement.setString(2, playerData.getPlayer().getName());
            preparedStatement.setString(3, playerData.getLastKnownIp());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(playerData.getLastDeath()));
            preparedStatement.setInt(5, playerData.getLifeManager().getLives());
            preparedStatement.setInt(6, playerData.getLifeManager().getLifeParts());
            preparedStatement.setBoolean(7, playerData.isSpectatorBanned());
            preparedStatement.setLong(8, playerData.getPlaytimeTracker().getTimeTillNextRevive());
            preparedStatement.setLong(9, playerData.getPlaytimeTracker().getTimeTillNextLifePart());
            preparedStatement.setLong(10, playerData.getPlaytimeTracker().getTimeTillNextMaxHealth());
            preparedStatement.setString(11, playerData.getPlayer().getName());
            preparedStatement.setString(12, playerData.getLastKnownIp());
            preparedStatement.setTimestamp(13, Timestamp.valueOf(playerData.getLastDeath()));
            preparedStatement.setInt(14, playerData.getLifeManager().getLives());
            preparedStatement.setInt(15, playerData.getLifeManager().getLifeParts());
            preparedStatement.setBoolean(16, playerData.isSpectatorBanned());
            preparedStatement.setLong(17, playerData.getPlaytimeTracker().getTimeTillNextRevive());
            preparedStatement.setLong(18, playerData.getPlaytimeTracker().getTimeTillNextLifePart());
            preparedStatement.setLong(19, playerData.getPlaytimeTracker().getTimeTillNextMaxHealth());
            preparedStatement.execute();
        } catch (SQLException e) {
            this.plugin.getLogger().log(Level.SEVERE, String.format("Could not update PlayerData for %s.", playerData.getPlayer().getName()), e);
        }
    }

    @Override
    public void deletePlayerData(OfflinePlayer player) {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM ah_player " +
                    "WHERE `player_uuid` = ?;";

            try (Connection connection = this.database.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, player.getUniqueId().toString());
                preparedStatement.execute();
            } catch (SQLException e) {
                this.plugin.getLogger().log(Level.SEVERE, String.format("Could not delete PlayerData for %s.", player.getName()), e);
            }
        }, this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, String.format("Could not delete PlayerData for %s asynchronously.", player.getName()), ex);
            return null;
        });
    }
}
