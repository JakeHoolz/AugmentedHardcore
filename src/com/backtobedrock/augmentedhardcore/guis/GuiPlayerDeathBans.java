package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.Ban;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import com.backtobedrock.augmentedhardcore.utilities.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.javatuples.Pair;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GuiPlayerDeathBans extends AbstractDeathBansGui {
    private final PlayerData playerData;

    public GuiPlayerDeathBans(AugmentedHardcore plugin, PlayerData playerData) {
        super(plugin, String.format("%s Death Bans", playerData.getPlayer().getName()), playerData.getBanManager().getBanCount());
        playerData.getBanManager().getBans().forEach((key, value) -> this.bans.put(new Pair<>(playerData.getPlayer(), key), value));
        this.playerData = playerData;
        this.initialize();
    }

    @Override
    public void setDataIcon(boolean update) {
        this.setIcon(4, new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(InventoryUtils.createPlayerSkull(this.plugin.getConfigurations().getGuisConfiguration().getPlayerDisplay().getName(), this.plugin.getConfigurations().getGuisConfiguration().getPlayerDisplay().getLore(), this.playerData.getPlayer()), this.getPlaceholders()), Collections.emptyList()), update);
    }

    private Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        OfflinePlayer player = this.playerData.getPlayer();
        placeholders.put("player", player.getName());
        placeholders.put("total_deaths", player.getPlayer() == null ? "-" : Integer.toString(player.getPlayer().getStatistic(Statistic.DEATHS)));
        placeholders.put("total_death_bans", Integer.toString(this.playerData.getBanManager().getBanCount()));
        Ban lastBan = this.playerData.getBanManager().getLastDeathBan();
        placeholders.put("last_ban_time_long", lastBan == null ? "-" : MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(lastBan.getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS), TimePattern.LONG));
        placeholders.put("last_ban_time_short", lastBan == null ? "-" : MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(lastBan.getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS), TimePattern.SHORT));
        placeholders.put("last_ban_time_digital", lastBan == null ? "-" : MessageUtils.getTimeFromTicks(MessageUtils.timeUnitToTicks(ChronoUnit.SECONDS.between(lastBan.getStartDate(), LocalDateTime.now()), TimeUnit.SECONDS), TimePattern.DIGITAL));
        placeholders.put("last_death_time_long", MessageUtils.getTimeFromTicks(MessageUtils.timeBetweenDatesToTicks(LocalDateTime.now(), playerData.getLastDeath()), TimePattern.LONG));
        placeholders.put("last_death_time_short", MessageUtils.getTimeFromTicks(MessageUtils.timeBetweenDatesToTicks(LocalDateTime.now(), playerData.getLastDeath()), TimePattern.SHORT));
        placeholders.put("last_death_time_digital", MessageUtils.getTimeFromTicks(MessageUtils.timeBetweenDatesToTicks(LocalDateTime.now(), playerData.getLastDeath()), TimePattern.DIGITAL));
        return placeholders;
    }

    @Override
    public Icon getIcon(OfflinePlayer player, Map<String, String> placeholders) {
        return new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getItem(), placeholders), Collections.emptyList());
    }
}
