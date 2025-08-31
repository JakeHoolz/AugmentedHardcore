package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.guis.GuiUnDeathBan;
import com.backtobedrock.augmentedhardcore.utilities.PlayerUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClickActionOpenUnDeathBanGui extends AbstractClickAction {
    private final OfflinePlayer target;
    private final ItemStack item;

    public ClickActionOpenUnDeathBanGui(AugmentedHardcore plugin, OfflinePlayer target, ItemStack item) {
        super(plugin);
        this.target = target;
        this.item = item;
    }

    @Override
    public void execute(Player player) {
        PlayerUtils.openInventory(player, new GuiUnDeathBan(this.plugin, this.target, this.item));
    }
}
