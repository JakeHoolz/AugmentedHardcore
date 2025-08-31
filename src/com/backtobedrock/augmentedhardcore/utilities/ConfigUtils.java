package com.backtobedrock.augmentedhardcore.utilities;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.domain.enums.*;
import org.bukkit.BanList;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.logging.Level;

public class ConfigUtils {
    public static OptionalInt checkMin(String id, int value, int min) {
        if (value >= min) {
            return OptionalInt.of(value);
        } else {
            sendErrorMessage(String.format("%s: value cannot be lower than %d, your value is: %d", id, min, value));
            return OptionalInt.empty();
        }
    }

    public static OptionalInt checkMinMaxNoNotification(int value, int min, int max) {
        if (value >= min && value <= max) {
            return OptionalInt.of(value);
        } else {
            return OptionalInt.empty();
        }
    }

    public static OptionalDouble checkMin(String id, double value, double min) {
        if (value >= min) {
            return OptionalDouble.of(value);
        } else {
            sendErrorMessage(String.format("%s: value cannot be lower than %f, your value is: %f", id, min, value));
            return OptionalDouble.empty();
        }
    }

    public static OptionalDouble checkMinMax(String id, double value, double min, double max) {
        if (value >= min && value <= max) {
            return OptionalDouble.of(value);
        } else {
            sendErrorMessage(String.format("%s: value cannot be lower than %f and higher than %f, your value is: %f", id, min, max, value));
            return OptionalDouble.empty();
        }
    }

    public static OptionalInt checkMinMax(String id, int value, int min, int max) {
        if (value >= min && value <= max) {
            return OptionalInt.of(value);
        } else {
            sendErrorMessage(String.format("%s: value cannot be lower than %d and higher than %d, your value is: %d", id, min, max, value));
            return OptionalInt.empty();
        }
    }

    public static StorageType getStorageType(String id, String storageType) {
        try {
            if (storageType != null) {
                return StorageType.valueOf(storageType.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing storage type.", id, storageType));
        }
        return null;
    }

    public static Material getMaterial(String id, String material) {
        try {
            if (material != null) {
                return Material.valueOf(material.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing material.", id, material));
        }
        return null;
    }

    public static GrowthType getGrowthType(String id, String growthType, GrowthType type) {
        try {
            if (growthType != null) {
                return GrowthType.valueOf(growthType.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing growth type, default value will be used: %s.", id, growthType, type));
        }
        return type;
    }

    public static BanTimeType getBanTimeType(String id, String banTimeType, BanTimeType type) {
        try {
            if (banTimeType != null) {
                return BanTimeType.valueOf(banTimeType.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing ban time type, default value will be used: %s.", id, banTimeType, type));
        }
        return type;
    }

    public static BanList.Type getBanType(String id, String banType, BanList.Type type) {
        try {
            if (banType != null) {
                return BanList.Type.valueOf(banType.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing ban type, default value will be used: %s.", id, banType, type));
        }
        return type;
    }

    public static BarStyle getBarStyle(String id, String barStyle, BarStyle type) {
        try {
            if (barStyle != null) {
                return BarStyle.valueOf(barStyle.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing bar type, default value will be used: %s.", id, barStyle, type));
        }
        return type;
    }

    public static BarColor getBarColor(String id, String barColor, BarColor type) {
        try {
            if (barColor != null) {
                return BarColor.valueOf(barColor.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing bar color, default value will be used: %s.", id, barColor, type));
        }
        return type;
    }

    public static NotificationType getNotifcationType(String id, String notificationType) {
        try {
            if (notificationType != null) {
                return NotificationType.valueOf(notificationType.toUpperCase());
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not an existing notification type.", id, notificationType));
        }
        return null;
    }

    public static DamageCause getDamageCause(String damageCause, DamageCause defaultValue) {
        try {
            if (damageCause != null) {
                return DamageCause.valueOf(damageCause.toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {
        }
        return defaultValue;
    }

    public static DamageCause getDamageCause(String damageCause) {
        return getDamageCause(damageCause, null);
    }

    public static DamageCauseType getDamageCauseType(String damageCauseType, DamageCauseType defaultValue) {
        try {
            if (damageCauseType != null) {
                return DamageCauseType.valueOf(damageCauseType.toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {
        }
        return defaultValue;
    }

    public static DamageCauseType getDamageCauseType(String damageCauseType) {
        return getDamageCauseType(damageCauseType, null);
    }

    public static EntityType getEntityType(String entityType) {
        try {
            if (entityType != null) {
                return EntityType.valueOf(entityType.toUpperCase());
            }
        } catch (IllegalArgumentException ignored) {
        }
        return null;
    }

    public static EntityType getLivingEntityType(String id, String livingEntityType) {
        try {
            if (livingEntityType != null) {
                EntityType type = EntityType.valueOf(livingEntityType.toUpperCase());
                if (type.isAlive()) {
                    return type;
                }
            }
        } catch (IllegalArgumentException e) {
            sendErrorMessage(String.format("%s: %s is not a living entity.", id, livingEntityType));
        }
        return null;
    }

    private static void sendErrorMessage(String message) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        plugin.getLogger().log(Level.SEVERE, message);
    }

    private static void sendWarningMessage(String message) {
        AugmentedHardcore plugin = JavaPlugin.getPlugin(AugmentedHardcore.class);
        plugin.getLogger().log(Level.WARNING, message);
    }
}
