package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.data.PlayerData;
import org.bukkit.entity.Player;

public class ClickActionConfirmRevive extends AbstractClickAction {
    private final PlayerData reviverData;
    private final PlayerData revivingData;

    public ClickActionConfirmRevive(AugmentedHardcore plugin, PlayerData reviverData, PlayerData revivingData) {
        super(plugin);
        this.reviverData = reviverData;
        this.revivingData = revivingData;
    }

    @Override
    public void execute(Player player) {
        this.reviverData.onReviving(this.revivingData, player);
        player.closeInventory();
    }
}
