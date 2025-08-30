package com.backtobedrock.augmentedhardcore.domain;

import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.Test;
import com.zaxxer.hikari.HikariDataSource;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.sql.Connection;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseTest {

    @Test
    void testGetConnection() throws Exception {
        Connection mockConn = mock(Connection.class);
        try (MockedConstruction<HikariDataSource> mocked = Mockito.mockConstruction(HikariDataSource.class,
                (mock, context) -> when(mock.getConnection()).thenReturn(mockConn))) {
            Database db = new Database("localhost", "3306", "testdb", "user", "pass");
            Connection conn = db.getConnection();
            assertSame(mockConn, conn);
        }
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
