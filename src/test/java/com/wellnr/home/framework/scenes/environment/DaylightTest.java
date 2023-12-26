package com.wellnr.home.framework.scenes.environment;

import com.luckycatlabs.sunrisesunset.dto.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.TimeZone;

class DaylightTest {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");
    private static final Location LOCATION = new Location(48.8588416, 2.2943506);

    private static final LocalDateTime NOW = LocalDateTime.of(2023, 2, 10, 12, 0);
    private static final Daylight DAYLIGHT = Daylight.apply(10, 20, UTC, LOCATION, NOW);

    @Test
    public void testGetNextSunrise() {
        var nextSunrise = DAYLIGHT.getNextSunrise();
        Assertions.assertEquals(NOW.plus(Duration.ofDays(1)).getDayOfMonth(), nextSunrise.getDayOfMonth());
    }

    @Test
    public void testGetLastSunrise() {
        var lastSunriseTime = DAYLIGHT.getLastSunrise();
        Assertions.assertEquals(NOW.getDayOfMonth(), lastSunriseTime.getDayOfMonth());
    }

    @Test
    public void testGetDay()  {
        Assertions.assertTrue(DAYLIGHT.isDay());
    }

    @Test
    public void testGetNextSunset() {
        var nextSunset = DAYLIGHT.getNextSunset();
        Assertions.assertEquals(NOW.getDayOfMonth(), nextSunset.getDayOfMonth());
    }

    @Test
    public void testGetLastSunset() {
        var lastSunsetTime = DAYLIGHT.getLastSunset();
        Assertions.assertEquals(NOW.minus(Duration.ofDays(1)).getDayOfMonth(), lastSunsetTime.getDayOfMonth());
    }

}