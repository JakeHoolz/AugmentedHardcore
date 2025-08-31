package com.backtobedrock.augmentedhardcore.guis;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import com.backtobedrock.augmentedhardcore.guis.clickActions.ClickActionConfirmRevive;
import com.backtobedrock.augmentedhardcore.utilities.InventoryUtils;
import com.backtobedrock.augmentedhardcore.utilities.MessageUtils;
import org.bukkit.OfflinePlayer;

import java.util.Collections;
import java.util.Map;
import java.util.logging.Level;

public class GuiRevive extends AbstractConfirmationGui {
    private final PlayerData reviverData;
    private final OfflinePlayer reviving;
    private PlayerData revivingData;

    public GuiRevive(AugmentedHardcore plugin, PlayerData reviverData, OfflinePlayer reviving) {
        super(plugin, String.format("Reviving %s", reviving.getName()));
        this.reviverData = reviverData;
        this.reviving = reviving;
        this.plugin.getPlayerRepository().getByPlayer(this.reviving)
                .thenAcceptAsync(playerData -> {
                    this.revivingData = playerData;
                    this.updateInfo(true);
                    this.updateConfirmation(true);
                }, this.plugin.getExecutor())
                .exceptionallyAsync(ex -> {
                    this.plugin.getLogger().log(Level.SEVERE, "Error loading revive GUI.", ex);
                    return null;
                }, this.plugin.getExecutor());
        this.initialize();
    }

    @Override
    protected void initialize() {
        this.updateInfo(false);
        this.updateConfirmation(false);
        super.initialize();
    }

    public void updateInfo(boolean update) {
        Icon icon;
        if (this.revivingData != null) {
            Map<String, String> placeholders = Map.of(
                    "player", revivingData.getPlayer().getName(),
                    "lives_number", Integer.toString(revivingData.getLives())
            );
            icon = new Icon(MessageUtils.replaceItemNameAndLorePlaceholders(InventoryUtils.createPlayerSkull(this.plugin.getConfigurations().getGuisConfiguration().getRevivingDisplay().getName(), this.plugin.getConfigurations().getGuisConfiguration().getRevivingDisplay().getLore(), this.reviving), placeholders), Collections.emptyList());
        } else {
            icon = new Icon(this.plugin.getConfigurations().getGuisConfiguration().getLoadingDisplay().getItem(), Collections.emptyList());
        }
        super.updateInfo(icon, update);
    }

    public void updateConfirmation(boolean update) {
        super.updateConfirmation(Collections.singletonList(new ClickActionConfirmRevive(this.plugin, this.reviverData, this.revivingData)), update);
    }
}
