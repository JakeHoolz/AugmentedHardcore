package com.backtobedrock.augmentedhardcore.domain.data;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.backtobedrock.augmentedhardcore.configs.Configurations;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.ConfigurationLivesAndLifeParts;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.ConfigurationMaxHealth;
import com.backtobedrock.augmentedhardcore.domain.configurationDomain.ConfigurationRevive;
import org.bukkit.OfflinePlayer;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlayerDataTest {

    @Test
    void testSerializeIncludesCoreFields() {
        AugmentedHardcore plugin = mock(AugmentedHardcore.class);
        Configurations configs = mock(Configurations.class);
        ConfigurationLivesAndLifeParts livesConfig = mock(ConfigurationLivesAndLifeParts.class);
        ConfigurationMaxHealth maxHealthConfig = mock(ConfigurationMaxHealth.class);
        ConfigurationRevive reviveConfig = mock(ConfigurationRevive.class);

        when(plugin.getConfigurations()).thenReturn(configs);
        when(configs.getLivesAndLifePartsConfiguration()).thenReturn(livesConfig);
        when(livesConfig.getPlaytimePerLifePart()).thenReturn(1000);
        when(configs.getMaxHealthConfiguration()).thenReturn(maxHealthConfig);
        when(maxHealthConfig.getPlaytimePerHalfHeart()).thenReturn(2000);
        when(configs.getReviveConfiguration()).thenReturn(reviveConfig);
        when(reviveConfig.getTimeBetweenRevives()).thenReturn(5000);

        OfflinePlayer offline = mock(OfflinePlayer.class);
        when(offline.getPlayer()).thenReturn(null);
        when(offline.getUniqueId()).thenReturn(UUID.randomUUID());
        when(offline.getName()).thenReturn("Steve");

        PlayerData data = new PlayerData(
                plugin,
                offline,
                "127.0.0.1",
                LocalDateTime.of(2023,1,1,0,0),
                3,
                5,
                true,
                1200,
                400,
                800,
                new TreeMap<>());

        Map<String, Object> serialized = data.serialize();
        assertEquals(3, serialized.get("Lives"));
        assertEquals(5, serialized.get("LifeParts"));
        assertEquals("127.0.0.1", serialized.get("LastKnownIp"));
        assertEquals(true, serialized.get("SpectatorBanned"));
    }

    private AugmentedHardcore mockPluginWithConfigs(int livesAtStart, int lifePartsAtStart, int maxLives) {
        AugmentedHardcore plugin = mock(AugmentedHardcore.class, RETURNS_DEEP_STUBS);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("test"));
        when(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLivesAtStart()).thenReturn(livesAtStart);
        when(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getLifePartsAtStart()).thenReturn(lifePartsAtStart);
        when(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getMaxLives()).thenReturn(maxLives);
        when(plugin.getConfigurations().getLivesAndLifePartsConfiguration().getPlaytimePerLifePart()).thenReturn(0);
        when(plugin.getConfigurations().getMaxHealthConfiguration().getPlaytimePerHalfHeart()).thenReturn(0);
        when(plugin.getConfigurations().getReviveConfiguration().isReviveOnFirstJoin()).thenReturn(true);
        when(plugin.getConfigurations().getReviveConfiguration().getTimeBetweenRevives()).thenReturn(0L);
        return plugin;
    }

    @Test
    void testInitializationUsesConfigValues() {
        AugmentedHardcore plugin = mockPluginWithConfigs(5, 2, 10);
        OfflinePlayer player = mock(OfflinePlayer.class);
        PlayerData data = new PlayerData(plugin, player);
        assertEquals(5, data.getLives());
        assertEquals(2, data.getLifeParts());
    }

    @Test
    void testSetLivesClampedToBounds() {
        AugmentedHardcore plugin = mockPluginWithConfigs(3, 0, 10);
        OfflinePlayer player = mock(OfflinePlayer.class);
        PlayerData data = new PlayerData(plugin, player);

        data.setLives(20);
        assertEquals(10, data.getLives());

        data.setLives(-5);
        assertEquals(0, data.getLives());
    }
}
