package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.entity.Player;

public class ClickActionCloseInventory extends AbstractClickAction {
    public ClickActionCloseInventory(AugmentedHardcore plugin) {
        super(plugin);
    }

    @Override
    public void execute(Player player) {
        player.closeInventory();
    }
}
