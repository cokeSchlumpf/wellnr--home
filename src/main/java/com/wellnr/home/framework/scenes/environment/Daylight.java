package com.wellnr.home.framework.scenes.environment;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor(staticName = "apply", access = AccessLevel.PRIVATE)
public class Daylight {

    /**
     * No. of minutes which will be added to the official sunset time.
     * Daylight will start at the moment when sunset time is adjustedSunriseMinutes behind.
     */
    private final long adjustedSunriseMinutes;

    /**
     * No. of minutes which will be subtracted from the official sunset time.
     * Daylight will end at the moment where official sunset is adjustedSunsetMinutes ahead.
     */
    private final long adjustSunsetMinutes;

    /**
     * The time zone to use for date/ time calculations.
     */
    private final TimeZone timeZone;

    /**
     * The current date and time this instance operates on.
     */
    private final LocalDateTime now;

    /**
     * The sunrise/ sunset calculator to use.
     */
    private final SunriseSunsetCalculator sunriseSunsetCalculator;

    /**
     * The cache to use for storing the next sunrise and next sunset.
     */
    private final Cache<String, LocalDateTime> cache;

    /**
     * Creates a new instance.
     *
     * @param adjustedSunriseMinutes See Daylight#adjustedSunriseMinutes.
     * @param adjustSunsetMinutes See Daylight#adjustSunsetMinutes.
     * @param timeZone See Daylight#timeZone.
     * @param location The location to calculate the sunrise/ sunset for.
     * @return A new instance.
     */
    public static Daylight apply(long adjustedSunriseMinutes, long adjustSunsetMinutes, TimeZone timeZone, Location location) {
        var now = LocalDateTime.now(timeZone.toZoneId());
        return apply(adjustedSunriseMinutes, adjustSunsetMinutes, timeZone, location, now);
    }

    /**
     * Creates a new instance.
     *
     * @param adjustedSunriseMinutes See Daylight#adjustedSunriseMinutes.
     * @param adjustSunsetMinutes See Daylight#adjustSunsetMinutes.
     * @param timeZone See Daylight#timeZone.
     * @param location The location to calculate the sunrise/ sunset for.
     * @param now The current date and time this instance operates on.
     * @return A new instance.
     */
    public static Daylight apply(long adjustedSunriseMinutes, long adjustSunsetMinutes, TimeZone timeZone, Location location, LocalDateTime now) {
        var sunriseSunsetCalculator = new SunriseSunsetCalculator(location, timeZone);
        var cache = Caffeine.newBuilder()
                .expireAfterWrite(24, TimeUnit.HOURS)
                .maximumSize(100)
                .<String, LocalDateTime>build();

        return new Daylight(
                adjustedSunriseMinutes, adjustSunsetMinutes, timeZone, now, sunriseSunsetCalculator, cache
        );
    }


    public LocalDateTime getNextSunrise() {
        return cache.get("next-sunrise", k -> {
            var calendar = createCalendar();
            var sunrise = getSunrise(calendar);

            if (now.isAfter(sunrise)) {
                calendar.add(Calendar.DATE, 1);
                return getSunrise(calendar);
            } else {
                return sunrise;
            }
        });
    }

    public LocalTime getNextSunriseTime() {
        return getNextSunrise().toLocalTime();
    }

    public LocalDateTime getLastSunrise() {
        return cache.get("last-sunrise", k -> {
            var calendar = createCalendar();
            var sunrise = getSunrise(calendar);

            if (now.isBefore(sunrise)) {
                calendar.add(Calendar.DATE, -1);
                return getSunrise(calendar);
            } else {
                return sunrise;
            }
        });
    }

    public LocalTime getLAstSunriseTime() {
        return getLastSunrise().toLocalTime();
    }

    public LocalDateTime getNextSunset() {
        return cache.get("next-sunset", k -> {
            var calendar = createCalendar();
            var sunset = getSunset(calendar);

            if (now.isAfter(sunset)) {
                calendar.add(Calendar.DATE, 1);
                return getSunset(calendar);
            } else {
                return sunset;
            }
        });
    }

    public LocalTime getNextSunsetTime() {
        return getNextSunset().toLocalTime();
    }

    public LocalDateTime getLastSunset() {
        return cache.get("last-sunset", k -> {
            var calendar = createCalendar();
            var sunset = getSunset(calendar);

            if (now.isBefore(sunset)) {
                calendar.add(Calendar.DATE, -1);
                return getSunset(calendar);
            } else {
                return sunset;
            }
        });
    }

    public LocalTime getLastSunsetTime() {
        return getLastSunset().toLocalTime();
    }

    public boolean isDay() {
        var nextSunset = getNextSunset();
        var nextSunrise = getNextSunrise();

        return nextSunset.isBefore(nextSunrise);
    }

    public boolean isNight() {
        return !isDay();
    }

    public boolean isNightAndBefore(LocalTime time) {
        return isNight() && now.toLocalTime().isBefore(time);
    }

    public boolean isNightAndAfter(LocalTime time) {
        return isNight() && now.toLocalTime().isAfter(time);
    }

    public boolean isDayAndBefore(LocalTime time) {
        return isDay() && now.toLocalTime().isBefore(time);
    }

    public boolean isDayAndAfter(LocalTime time) {
        return isDay() && now.toLocalTime().isAfter(time);
    }

    private LocalDateTime getSunset(Calendar calendar) {
        return toLocalDateTime(
                sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar)
        ).minus(
                Duration.ofMinutes(adjustSunsetMinutes)
        );
    }

    private LocalDateTime getSunrise(Calendar calendar) {
        return toLocalDateTime(
                sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar)
        ).plus(
                Duration.ofMinutes(adjustedSunriseMinutes)
        );
    }

    private Calendar createCalendar() {
        var calendar = Calendar.getInstance(timeZone);
        calendar.setTime(Date.from(now.atZone(this.timeZone.toZoneId()).toInstant()));

        return calendar;
    }

    private LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(
                calendar.toInstant(), this.timeZone.toZoneId()
        );
    }

}
