package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseTest {

    @Test
    void testGetDataSourceConfiguration() {
        Database db = new Database("localhost", "3306", "testdb", "user", "pass");
        HikariDataSource ds = db.getDataSource();
        assertEquals("jdbc:mysql://localhost:3306/testdb", ds.getJdbcUrl());
        assertEquals("user", ds.getUsername());
        assertEquals("pass", ds.getPassword());
        assertEquals("true", ds.getDataSourceProperties().getProperty("autoReconnect"));
    }

    @Test
    void testDeserialize() {
        ConfigurationSection section = mock(ConfigurationSection.class);
        when(section.getString("Hostname")).thenReturn("localhost");
        when(section.getString("Port", "3306")).thenReturn("3306");
        when(section.getString("Database")).thenReturn("db");
        when(section.getString("Username")).thenReturn("user");
        when(section.getString("Password")).thenReturn("pwd");

        AugmentedHardcore plugin = mock(AugmentedHardcore.class);
        when(plugin.getLogger()).thenReturn(Logger.getLogger("test"));
        try (MockedStatic<JavaPlugin> mocked = Mockito.mockStatic(JavaPlugin.class)) {
            mocked.when(() -> JavaPlugin.getPlugin(AugmentedHardcore.class)).thenReturn(plugin);
            Database db = Database.deserialize(section);
            assertNotNull(db);
            assertEquals("localhost", db.getHostname());
            assertEquals("db", db.getDatabaseName());
        }
    }
}
