package com.backtobedrock.augmentedhardcore.mappers.ban;

import com.backtobedrock.augmentedhardcore.domain.BanEntry;
import org.bukkit.Server;

import java.util.UUID;

public interface IBanMapper {
    void insertBan(Server server, UUID uuid, BanEntry ban);

    void updateBan(Server server, UUID uuid, BanEntry ban);

    void deleteBan(UUID uuid, Integer id);
}
