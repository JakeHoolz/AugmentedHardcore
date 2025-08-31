package com.backtobedrock.augmentedhardcore.utilities;

import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class MessageUtils {
    public static final DateTimeFormatter SHORT_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yy',' HH:mm z").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter MEDIUM_FORMATTER = DateTimeFormatter.ofPattern("MMM dd yyyy',' HH:mm z").withZone(ZoneId.systemDefault());
    public static final DateTimeFormatter LONG_FORMATTER = DateTimeFormatter.ofPattern("EEEE MMM dd yyyy 'at' HH:mm:ss z").withZone(ZoneId.systemDefault());

    // Time constants
    public static final long TICKS_PER_SECOND = 20L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long MINUTES_PER_HOUR = 60L;
    public static final long HOURS_PER_DAY = 24L;
    public static final long TICKS_PER_MINUTE = TICKS_PER_SECOND * SECONDS_PER_MINUTE;
    public static final long TICKS_PER_HOUR = TICKS_PER_MINUTE * MINUTES_PER_HOUR;
    public static final long TICKS_PER_DAY = TICKS_PER_HOUR * HOURS_PER_DAY;

    public static long timeUnitToTicks(long time, TimeUnit unit) {
        return unit.toSeconds(time) * TICKS_PER_SECOND;
    }

    public static long timeBetweenDatesToTicks(LocalDateTime date1, LocalDateTime date2) {
        return Math.abs(ChronoUnit.SECONDS.between(date1, date2)) * TICKS_PER_SECOND;
    }

    public static String getTimeFromTicks(long amount, TimePattern pattern) {
        StringBuilder sb = new StringBuilder();

        long d = amount / TICKS_PER_DAY;
        long h = amount % TICKS_PER_DAY / TICKS_PER_HOUR;
        long m = amount % TICKS_PER_HOUR / TICKS_PER_MINUTE;
        long s = amount % TICKS_PER_MINUTE / TICKS_PER_SECOND;

        switch (pattern) {
            case LONG:
                if (d > 0) {
                    sb.append(d).append(d == 1 ? " day" : " days");
                }
                if (h > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(h).append(h == 1 ? " hour" : " hours");
                }
                if (m > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(m).append(m == 1 ? " minute" : " minutes");
                }
                if (s > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(s).append(s == 1 ? " second" : " seconds");
                }
                break;
            case SHORT:
                if (d > 0) {
                    sb.append(d).append("d");
                }
                if (h > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(h).append("h");
                }
                if (m > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(m).append("m");
                }
                if (s > 0) {
                    if (!sb.toString().isEmpty()) {
                        sb.append(", ");
                    }
                    sb.append(s).append("s");
                }
                break;
            case DIGITAL:
                sb.append(d > 9 ? d : "0" + d).append(":").append(h > 9 ? h : "0" + h).append(":").append(m > 9 ? m : "0" + m).append(":").append(s > 9 ? s : "0" + s);
                break;
        }

        return sb.toString();
    }

    public static String replacePlaceholders(String string, Map<String, String> placeholders) {
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            string = string.replaceAll("%" + entry.getKey().toLowerCase() + "%", entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static ItemStack replaceItemNamePlacholders(ItemStack item, Map<String, String> placeholders) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setDisplayName(replacePlaceholders(itemMeta.getDisplayName(), placeholders));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static ItemStack replaceItemLorePlacholders(ItemStack item, Map<String, String> placeholders) {
        ItemMeta itemMeta = item.getItemMeta();
        if (itemMeta != null && itemMeta.getLore() != null) {
            itemMeta.setLore(itemMeta.getLore().stream().map(e -> replacePlaceholders(e, placeholders)).collect(Collectors.toList()));
            item.setItemMeta(itemMeta);
        }
        return item;
    }

    public static ItemStack replaceItemNameAndLorePlaceholders(ItemStack item, Map<String, String> placeholders) {
        return replaceItemNamePlacholders(replaceItemLorePlacholders(item, placeholders), placeholders);
    }
}
