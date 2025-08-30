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
}
