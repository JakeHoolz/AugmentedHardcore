package com.backtobedrock.augmentedhardcore.utilities;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;

public class InventoryUtils {
    public static ItemStack createItem(Material material, String displayName, List<String> lore, int amount, boolean glowing) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta im = item.getItemMeta();
        if (im != null) {
            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            if (displayName != null) {
                im.setDisplayName(displayName);
            }
            if (lore != null) {
                im.setLore(lore);
            } else {
                im.setLore(Collections.emptyList());
            }
            if (glowing) {
                im.addEnchant(Enchantment.INFINITY, 1, true);
                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            item.setItemMeta(im);
        }
        return item;
    }

    public static ItemStack createPlayerSkull(String displayName, List<String> lore, OfflinePlayer player) {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta sm = (SkullMeta) item.getItemMeta();
        if (sm != null) {
            sm.setOwningPlayer(player);
            if (displayName != null) {
                sm.setDisplayName(displayName);
            }
            if (lore != null) {
                sm.setLore(lore);
            } else {
                sm.setLore(Collections.emptyList());
            }
            item.setItemMeta(sm);
        }
        return item;
    }

    public static ItemStack createPotion(String displayName, List<String> lore) {
        ItemStack item = new ItemStack(Material.POTION);
        PotionMeta pm = (PotionMeta) item.getItemMeta();
        if (pm != null) {
            pm.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, 3600, 1), true);
            if (displayName != null) {
                pm.setDisplayName(displayName);
            }
            if (lore != null) {
                pm.setLore(lore);
            } else {
                pm.setLore(Collections.emptyList());
            }
            item.setItemMeta(pm);
        }
        return item;
    }
}
