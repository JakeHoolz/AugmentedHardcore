package com.backtobedrock.augmentedhardcore.utilities;

import com.backtobedrock.augmentedhardcore.domain.enums.TimePattern;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Unit tests for {@link MessageUtils#getTimeFromTicks(long, TimePattern)} to ensure
 * the various {@link TimePattern} formats handle edge cases correctly.
 */
public class MessageUtilsTest {

    @Test
    void testGetTimeFromTicksZero() {
        assertEquals("", MessageUtils.getTimeFromTicks(0, TimePattern.LONG));
        assertEquals("", MessageUtils.getTimeFromTicks(0, TimePattern.SHORT));
        assertEquals("00:00:00:00", MessageUtils.getTimeFromTicks(0, TimePattern.DIGITAL));
    }

    @Test
    void testGetTimeFromTicksMixedValues() {
        long ticks = MessageUtils.TICKS_PER_DAY
                + 2 * MessageUtils.TICKS_PER_HOUR
                + 3 * MessageUtils.TICKS_PER_MINUTE
                + 4 * MessageUtils.TICKS_PER_SECOND;

        assertEquals("1 day, 2 hours, 3 minutes, 4 seconds",
                MessageUtils.getTimeFromTicks(ticks, TimePattern.LONG));
        assertEquals("1d, 2h, 3m, 4s",
                MessageUtils.getTimeFromTicks(ticks, TimePattern.SHORT));
        assertEquals("01:02:03:04",
                MessageUtils.getTimeFromTicks(ticks, TimePattern.DIGITAL));
    }

    @Test
    void testDigitalPadding() {
        long ticks = 3 * MessageUtils.TICKS_PER_MINUTE + 5 * MessageUtils.TICKS_PER_SECOND;
        assertEquals("00:00:03:05",
                MessageUtils.getTimeFromTicks(ticks, TimePattern.DIGITAL));
    }
}

