package com.backtobedrock.augmentedhardcore.runnables;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.backtobedrock.augmentedhardcore.AugmentedHardcore;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

public class UpdateCheckerTest {

    private ServerMock server;
    private AugmentedHardcore plugin;

    @BeforeEach
    void setUp() {
        this.server = MockBukkit.mock();
        this.plugin = MockBukkit.load(AugmentedHardcore.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void testUpdateCheckerCancelledOnDisable() throws Exception {
        Field field = AugmentedHardcore.class.getDeclaredField("updateChecker");
        field.setAccessible(true);
        UpdateChecker checker = (UpdateChecker) field.get(this.plugin);
        int taskId = checker.getTaskId();

        Assertions.assertTrue(this.server.getScheduler().isQueued(taskId));

        this.plugin.onDisable();

        Assertions.assertTrue(checker.isCancelled());
        Assertions.assertFalse(this.server.getScheduler().isQueued(taskId));
    }
}
