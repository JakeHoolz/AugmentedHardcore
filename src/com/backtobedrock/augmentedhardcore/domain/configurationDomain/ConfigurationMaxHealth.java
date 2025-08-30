package com.backtobedrock.augmentedhardcore.domain.configurationDomain;

import com.backtobedrock.augmentedhardcore.utilities.ConfigUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.EnumMap;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;

public class ConfigurationMaxHealth {
    private final boolean useMaxHealth;
    private final double maxHealth;
    private final double minHealth;
    private final double maxHealthAfterBan;
    private final double maxHealthDecreasePerDeath;
    private final boolean maxHealthIncreaseOnKill;
    private final EnumMap<EntityType, Double> maxHealthIncreasePerKill;
    private final boolean getMaxHealthByPlaytime;
    private final int playtimePerHalfHeart;
    private final List<String> disableLosingMaxHealthInWorlds;
    private final List<String> disableGainingMaxHealthInWorlds;

    public ConfigurationMaxHealth(boolean useMaxHealth, double maxHealth, double minHealth, double maxHealthAfterBan, double maxHealthDecreasePerDeath, boolean maxHealthIncreaseOnKill, EnumMap<EntityType, Double> maxHealthIncreasePerKill, boolean getMaxHealthByPlaytime, int playtimePerHalfHeart, List<String> disableLosingMaxHealthInWorlds, List<String> disableGainingMaxHealthInWorlds) {
        this.useMaxHealth = useMaxHealth;
        this.maxHealth = maxHealth;
        this.minHealth = minHealth;
        this.maxHealthAfterBan = maxHealthAfterBan;
        this.maxHealthDecreasePerDeath = maxHealthDecreasePerDeath;
        this.maxHealthIncreaseOnKill = maxHealthIncreaseOnKill;
        this.maxHealthIncreasePerKill = maxHealthIncreasePerKill;
        this.getMaxHealthByPlaytime = getMaxHealthByPlaytime;
        this.playtimePerHalfHeart = playtimePerHalfHeart;
        this.disableLosingMaxHealthInWorlds = disableLosingMaxHealthInWorlds;
        this.disableGainingMaxHealthInWorlds = disableGainingMaxHealthInWorlds;
    }

    public static ConfigurationMaxHealth deserialize(ConfigurationSection section) {
        boolean cUseMaxHealth = section.getBoolean("UseMaxHealth", true);
        OptionalDouble cMaxHealth = ConfigUtils.checkMinMax("MaxHealth", section.getDouble("MaxHealth", 20), 1, Double.MAX_VALUE);
        OptionalDouble cMinHealth = ConfigUtils.checkMinMax("MinHealth", section.getDouble("MinHealth", 6), 1, Double.MAX_VALUE);
        OptionalDouble cMaxHealthAfterBan = section.getDouble("MaxHealthAfterBan", 20) == -1 ? OptionalDouble.of(-1) : ConfigUtils.checkMinMax("MaxHealthAfterBan", section.getDouble("MaxHealthAfterBan", 20), -1, Double.MAX_VALUE);
        OptionalDouble cMaxHealthDecreasePerDeath = ConfigUtils.checkMinMax("MaxHealthDecreasePerDeath", section.getDouble("MaxHealthDecreasePerDeath", 2), 1, Double.MAX_VALUE);
        boolean cMaxHealthIncreaseOnKill = section.getBoolean("MaxHealthIncreaseOnKill", true);
        EnumMap<EntityType, Double> cMaxHealthIncreasePerKill = new EnumMap<>(EntityType.class);
        boolean cGetMaxHealthByPlaytime = section.getBoolean("GetMaxHealthByPlaytime", false);
        OptionalInt cPlaytimePerHalfHeart = ConfigUtils.checkMinMax("PlaytimePerHalfHeart", section.getInt("PlaytimePerHalfHeart", 30), 1, Integer.MAX_VALUE);
        List<String> cDisableLosingMaxHealthInWorlds = section.getStringList("DisableLosingMaxHealthInWorlds").stream().map(String::toLowerCase).toList();
        List<String> cDisableGainingMaxHealthInWorlds = section.getStringList("DisableGainingMaxHealthInWorlds").stream().map(String::toLowerCase).toList();

        //cMaxHealthIncreasePerKill
        ConfigurationSection maxHealthIncreasePerKillSection = section.getConfigurationSection("MaxHealthIncreasePerKill");
        if (maxHealthIncreasePerKillSection != null) {
            maxHealthIncreasePerKillSection.getKeys(false).forEach(e -> {
                EntityType type = ConfigUtils.getLivingEntityType("MaxHealthIncreasePerKill", e);
                if (type != null) {
                    OptionalDouble amount = ConfigUtils.checkMinMax("MaxHealthIncreasePerKill." + e, maxHealthIncreasePerKillSection.getDouble(e, 0), 0, Integer.MAX_VALUE);
                    amount.ifPresent(a -> cMaxHealthIncreasePerKill.put(type, a));
                }
            });
        }

        if (cMaxHealth.isEmpty() || cMinHealth.isEmpty() || cMaxHealthAfterBan.isEmpty() || cMaxHealthDecreasePerDeath.isEmpty() || cPlaytimePerHalfHeart.isEmpty()) {
            return null;
        }

        double vMaxHealth = cMaxHealth.getAsDouble();
        double vMinHealth = cMinHealth.getAsDouble();
        double vMaxHealthAfterBan = cMaxHealthAfterBan.getAsDouble();
        double vMaxHealthDecreasePerDeath = cMaxHealthDecreasePerDeath.getAsDouble();
        int vPlaytimePerHalfHeart = cPlaytimePerHalfHeart.getAsInt();

        return new ConfigurationMaxHealth(
                cUseMaxHealth,
                vMaxHealth,
                vMinHealth,
                vMaxHealthAfterBan,
                vMaxHealthDecreasePerDeath,
                cMaxHealthIncreaseOnKill,
                cMaxHealthIncreasePerKill,
                cGetMaxHealthByPlaytime,
                vPlaytimePerHalfHeart * 1200,
                cDisableLosingMaxHealthInWorlds,
                cDisableGainingMaxHealthInWorlds
        );
    }

    public boolean isUseMaxHealth() {
        return useMaxHealth;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public double getMinHealth() {
        return minHealth;
    }

    public double getMaxHealthAfterBan() {
        return maxHealthAfterBan;
    }

    public double getMaxHealthDecreasePerDeath() {
        return maxHealthDecreasePerDeath;
    }

    public boolean isMaxHealthIncreaseOnKill() {
        return maxHealthIncreaseOnKill;
    }

    public EnumMap<EntityType, Double> getMaxHealthIncreasePerKill() {
        return maxHealthIncreasePerKill;
    }

    public List<String> getDisableLosingMaxHealthInWorlds() {
        return disableLosingMaxHealthInWorlds;
    }

    public List<String> getDisableGainingMaxHealthInWorlds() {
        return disableGainingMaxHealthInWorlds;
    }

    public boolean isGetMaxHealthByPlaytime() {
        return getMaxHealthByPlaytime;
    }

    public int getPlaytimePerHalfHeart() {
        return playtimePerHalfHeart;
    }
}
