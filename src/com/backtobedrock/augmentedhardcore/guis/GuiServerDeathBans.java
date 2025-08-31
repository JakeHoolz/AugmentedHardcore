package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.ServerData;
import com.backtobedrock.augmentedhardcore.domain.enums.Permission;
import com.backtobedrock.augmentedhardcore.guis.clickActions.AbstractClickAction;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionOpenUnDeathBanGui;
import com.backtobedrock.augmentedhardcore.utilities.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.javatuples.Pair;

import java.util.*;

public class GuiServerDeathBans extends AbstractDeathBansGui {
    private final ServerData serverData;
    private final Player player;

    public GuiServerDeathBans(AugmentedHardcore plugin, Player player, ServerData serverData) {
        super(plugin, "Currently Ongoing Death Bans", serverData.getTotalOngoingBans());
        serverData.getOngoingBans().forEach((key, value) -> this.bans.put(new Pair<>(Bukkit.getOfflinePlayer(key), value.getBan().getValue0()), value.getBan().getValue1()));
        this.serverData = serverData;
        this.player = player;
        this.initialize();
    }

    @Override
    public void setDataIcon(boolean update) {
        Icon icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(this.plugin.getConfigurations().getGuisConfiguration().getServerDisplay().getItem(), this.getPlaceholders()), Collections.emptyList());
        this.setIcon(4, icon, update);
    }

    private Map<String, String> getPlaceholders() {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("total_death_bans", Integer.toString(this.serverData.getTotalDeathBans()));
        placeholders.put("total_ongoing_death_bans", Integer.toString(this.serverData.getTotalOngoingBans()));
        return placeholders;
    }

    @Override
    public Icon getIcon(OfflinePlayer player, Map<String, String> placeholders) {
        List<AbstractClickAction> clickActions = new ArrayList<>();
        ItemStack item = InventoryUtils.createPlayerSkull(String.format("%s - %s", player.getName(), this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getName()), this.plugin.getConfigurations().getGuisConfiguration().getBanDisplay().getLore(), player);
        if (this.player.hasPermission(Permission.UNDEATHBAN.getPermissionString())) {
            clickActions.add(new ClickActionOpenUnDeathBanGui(this.plugin, player, item));
        }
        return new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(item, placeholders), clickActions);
    }
}
