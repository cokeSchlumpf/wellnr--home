package com.wellnr.home.framework.scenes.environment;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import lombok.AllArgsConstructor;

import javax.xml.stream.Location;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimeZone;

@AllArgsConstructor(staticName = "apply")
public class Daylight {

    private final Location location;

    private final TimeZone timeZone;

    private final SunriseSunsetCalculator sunriseSunsetCalculator;

    public LocalDateTime getNextSunrise() {
        var sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(Calendar.getInstance(timeZone)))
    }

    LocalTime getNextSunriseTime();

    LocalDateTime getLastSunrise();

    LocalTime getLAstSunriseTime();

    LocalDateTime getNextSunset();

    LocalTime getNextSunsetTime();

    LocalDateTime getLastSunset();

    LocalTime getLastSunsetTime();

    boolean isDay();

    boolean isNight();

    boolean isNightAndBefore(LocalTime time);

    boolean isNightAndAfter(LocalTime time);

    boolean isDayAndBefore(LocalTime time);

    boolean isDayAndAfter(LocalTime time);

    private LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(
                calendar.toInstant(), this.timeZone.toZoneId()
        );
    }

}
