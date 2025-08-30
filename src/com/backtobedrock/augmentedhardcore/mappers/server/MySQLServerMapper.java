package com.backtobedrock.augmentedhardcore.mappers.server;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.mappers.AbstractMapper;
import com.backtobedrock.augmentedhardcore.mappers.ban.MySQLBanMapper;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.javatuples.Pair;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class MySQLServerMapper extends AbstractMapper implements IServerMapper {

    private final MySQLBanMapper banMapper;

    public MySQLServerMapper(AugmentedHardcore plugin) {
        super(plugin);
        this.banMapper = new MySQLBanMapper(plugin);
    }

    @Override
    public void insertServerDataAsync(ServerData serverData) {
        CompletableFuture.runAsync(() -> this.updateServerData(serverData), this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not insert server data asynchronously.", ex);
            return null;
        });
    }

    @Override
    public void insertServerDataSync(ServerData serverData) {
        Bukkit.getScheduler().runTask(this.plugin, () -> this.updateServerData(serverData));
    }

    @Override
    public CompletableFuture<ServerData> getServerData(Server server) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * "
                    + "FROM ah_ban AS b "
                    + "RIGHT OUTER JOIN ah_server as s ON b.server_ip = s.server_ip AND b.server_port = s.server_port "
                    + "WHERE s.server_ip = ? AND s.server_port = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, InetAddress.getLocalHost().getHostAddress());
                preparedStatement.setInt(2, server.getPort());
                ResultSet resultSet = preparedStatement.executeQuery();
                Map<UUID, Pair<Integer, Ban>> deathBans = new HashMap<>();
                int totalDeathBans = 0;
                while (resultSet.next()) {
                    totalDeathBans = resultSet.getInt("total_death_bans");
                    String uuidString = resultSet.getString("player_uuid");
                    if (uuidString != null && !uuidString.isEmpty()) {
                        Pair<Integer, Ban> banPair = this.banMapper.getBanFromResultSetSync(resultSet);
                        if (banPair != null) {
                            deathBans.put(UUID.fromString(uuidString), banPair);
                        }
                    }
                }
                return new ServerData(totalDeathBans, deathBans);
            } catch (SQLException | UnknownHostException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not load server data.", e);
            }
            return null;
        }, this.plugin.getExecutor());
    }

    @Override
    public void updateServerData(ServerData data) {
        CompletableFuture.runAsync(() -> {
            String sql = "INSERT INTO ah_server (`server_ip`, `server_port`, `total_death_bans`)"
                    + "VALUES(?, ?, ?)"
                    + "ON DUPLICATE KEY UPDATE `total_death_bans` = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, InetAddress.getLocalHost().getHostAddress());
                preparedStatement.setInt(2, this.plugin.getServer().getPort());
                preparedStatement.setInt(3, data.getTotalDeathBans());
                preparedStatement.setInt(4, data.getTotalDeathBans());
                preparedStatement.execute();
            } catch (SQLException | UnknownHostException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not update server data.", e);
                return;
            }
            data.getOngoingBans().forEach((key, value) -> this.banMapper.updateBan(this.plugin.getServer(), key, value.getBan()));
        }, this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not update server data asynchronously.", ex);
            return null;
        });
    }

    @Override
    public void deleteServerData() {
        CompletableFuture.runAsync(() -> {
            String sql = "DELETE FROM ah_server " +
                    "WHERE server_ip = ? AND server_port = ?;";

            try (Connection connection = this.database.getDataSource().getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, InetAddress.getLocalHost().getHostAddress());
                preparedStatement.setInt(2, this.plugin.getServer().getPort());
                preparedStatement.execute();
            } catch (SQLException | UnknownHostException e) {
                this.plugin.getLogger().log(Level.SEVERE, "Could not delete server data.", e);
            }
        }, this.plugin.getExecutor()).exceptionally(ex -> {
            this.plugin.getLogger().log(Level.SEVERE, "Could not delete server data asynchronously.", ex);
            return null;
        });
    }

    @Override
    public void deleteBanFromServerData(UUID uuid, Pair<Integer, Ban> ban) {
        this.banMapper.updateBan(null, uuid, ban);
    }
}
