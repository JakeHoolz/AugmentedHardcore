package com.backtobedrock.augmentedhardcore.guis.clickActions;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.entity.Player;

public abstract class AbstractClickAction {

    protected final AugmentedHardcore plugin;

    public AbstractClickAction(AugmentedHardcore plugin) {
        this.plugin = plugin;
    }

    public abstract void execute(Player player);
}
