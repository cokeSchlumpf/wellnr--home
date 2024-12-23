package com.wellnr.home.services;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.wellnr.common.Operators;
import com.wellnr.common.markup.Tuple3;
import com.wellnr.home.framework.EWeLinkSwitch;
import com.wellnr.home.framework.TasmatoPlug;
import com.wellnr.home.ports.weather.WeatherPort;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

@Slf4j
@Component
public class AutomaticLightningService {

    private static final double LATITUDE = 49.442009;

    private static final double LONGITUDE = 6.636030;

    private final List<TasmatoPlug> nightLights;

    private final List<TasmatoPlug> workLights;

    private final EWeLinkSwitch outsideSwitch;

    private final WeatherPort weatherPort;

    private final TimeZone timeZone;

    private final SunriseSunsetCalculator sunriseSunsetCalculator;

    Boolean nightLightsTurnedOn;

    Boolean workLightsTurnedOn;

    public AutomaticLightningService(IMqttClient client, EWeLinkSwitch outsideSwitch, WeatherPort weatherPort) {
        this.weatherPort = weatherPort;

        var galleryLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-001");
        var livingRoomLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-002");
        var entranceLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-003");
        var guestsNorthLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-004");
        var workWindowLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-005");
        var workWallLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-006");
        var guestsSouthLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-007");
        var unknownLights01 = new TasmatoPlug(client, "wellnr/home", "iot-plug-008");
        var pianoLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-009");
        var unknownLights02 = new TasmatoPlug(client, "wellnr/home", "iot-plug-010");
        var diningRoomLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-011");
        var unknownLights03 = new TasmatoPlug(client, "wellnr/home", "iot-plug-012");
        var guestsSouthEastLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-013");
        var pyramideLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-014");
        var unknownLights04 = new TasmatoPlug(client, "wellnr/home", "iot-plug-015");
        var sleepingRoomLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-016");
        var entranceWindowSmallLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-017");
        var bathroomLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-018");
        var wardrobeLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-019");
        var kitchenLights = new TasmatoPlug(client, "wellnr/home", "iot-plug-020");

        this.nightLights = List.of(
            galleryLights, livingRoomLights
        );

        this.workLights = List.of(
            entranceLights, guestsNorthLights, workWindowLights, workWallLights, guestsSouthLights, unknownLights01, pianoLights, unknownLights02, diningRoomLights, unknownLights03, guestsSouthEastLights, pyramideLights, unknownLights04, sleepingRoomLights, entranceWindowSmallLights, bathroomLights, wardrobeLights, kitchenLights
        );

        this.outsideSwitch = outsideSwitch;

        var location = new Location(String.valueOf(LATITUDE), String.valueOf(LONGITUDE));
        this.timeZone = TimeZone.getTimeZone("Europe/Berlin");
        this.sunriseSunsetCalculator = new SunriseSunsetCalculator(location, timeZone);
        this.nightLightsTurnedOn = null;
        this.workLightsTurnedOn = null;
    }

    @Scheduled(fixedRate = 30_000)
    public void switchLights() {
        var nextSunriseAndNextSunset = this.getNextSunriseAndNextSunset();

        var shutdownWorkLightsTime = LocalTime.parse("00:30:00");
        var turnOnWorkLightsMorning = LocalTime.parse("06:00:00");

        var now = LocalTime.now(this.timeZone.toZoneId());
        var today = nextSunriseAndNextSunset.get_3().toLocalDate();
        var sunrise = nextSunriseAndNextSunset.get_1().toLocalDate();
        var sunset = nextSunriseAndNextSunset.get_2().toLocalDate();
        var sunriseIsToday = today.isEqual(sunrise);
        var sunsetIsToday = today.isEqual(sunset);

        if (sunriseIsToday && sunsetIsToday) { // after midnight
            Operators.ignoreExceptions(this::turnOnNightLights, log);

            if (now.isBefore(shutdownWorkLightsTime) || now.isAfter(turnOnWorkLightsMorning)) {
                Operators.ignoreExceptions(this::turnOnWorkLights, log);
            } else {
                Operators.ignoreExceptions(this::turnOffWorkLights, log);
            }
        } else if (!sunriseIsToday && sunsetIsToday) { // during daylight
            /* Usually */
            Operators.ignoreExceptions(this::turnOffNightLights, log);
            Operators.ignoreExceptions(this::turnOffWorkLights, log);
            /* Christmas Days */
            /*
            Operators.ignoreExceptions(this::turnOnNightLights, log);
            Operators.ignoreExceptions(this::turnOnWorkLights, log);
            */
             */
        } else { // after sunset
            Operators.ignoreExceptions(this::turnOnNightLights, log);
            Operators.ignoreExceptions(this::turnOnWorkLights, log);
        }
    }

    private void turnOnNightLights() {
        if (Objects.isNull(this.nightLightsTurnedOn) || !this.nightLightsTurnedOn) {
            log.info("Send request to turn on the lights ...");

            this.nightLights.forEach(plug -> {
                plug.turnOn();
                Operators.suppressExceptions(() -> Thread.sleep(300));
            });

            this.outsideSwitch.turnOn();
            this.nightLightsTurnedOn = true;
        }
    }

    private void turnOnWorkLights() {
        if (Objects.isNull(this.workLightsTurnedOn) || !this.workLightsTurnedOn) {
            log.info("Send request to turn on work lights");

            this.workLights.forEach(plug -> {
                plug.turnOn();
                Operators.suppressExceptions(() -> Thread.sleep(300));
            });

            this.workLightsTurnedOn = true;
        }
    }

    private void turnOffNightLights() {
        if (Objects.isNull(this.nightLightsTurnedOn) || this.nightLightsTurnedOn) {
            log.info("Send request to turn off the lights ...");

            this.nightLights.forEach(plug -> {
                plug.turnOff();
                Operators.suppressExceptions(() -> Thread.sleep(300));
            });

            this.outsideSwitch.turnOff();
            this.nightLightsTurnedOn = false;
        }
    }

    private void turnOffWorkLights() {
        if (Objects.isNull(this.workLightsTurnedOn) || this.workLightsTurnedOn) {
            log.info("Send request to turn off work lights ...");

            this.workLights.forEach(plug -> {
                plug.turnOff();
                Operators.suppressExceptions(() -> Thread.sleep(300));
            });

            this.workLightsTurnedOn = false;
        }
    }

    /**
     * Calculates the next sunrise and next sunset based ont the current time.
     *
     * @return A tuple with three values: Next Sunrise, next sunset and the current date and time.
     */
    private Tuple3<LocalDateTime, LocalDateTime, LocalDateTime> getNextSunriseAndNextSunset() {
        var timeOffsetMinutes = 30;

        var weather = this.weatherPort.getCurrentWeather(LATITUDE, LONGITUDE);
        var cloudCoverThreshold = 25;
        var maxOffsetMinutesExtension = 120;
        var cloudCover = Math.round(
            (float) Math.max(0, weather.getCloudCover() - 100 + cloudCoverThreshold) / cloudCoverThreshold * maxOffsetMinutesExtension
        );
        timeOffsetMinutes += cloudCover;

        var calendar = Calendar.getInstance(timeZone);
        var sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
            .plus(Duration.ofMinutes(timeOffsetMinutes));

        var sunset = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar))
            .minus(Duration.ofMinutes(timeOffsetMinutes));

        var now = LocalDateTime.now(this.timeZone.toZoneId());

        if (now.isAfter(sunset) || now.isEqual(sunset)) {
            calendar.add(Calendar.DATE, 1);

            sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
                .plus(Duration.ofMinutes(timeOffsetMinutes));

            sunset = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar))
                .minus(Duration.ofMinutes(timeOffsetMinutes));
        } else if (now.isAfter(sunrise) || now.isEqual(sunrise)) {
            calendar.add(Calendar.DATE, 1);

            sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
                .plus(Duration.ofMinutes(timeOffsetMinutes));
        }

        return Tuple3.apply(sunrise, sunset, now);
    }

    private LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(
            calendar.toInstant(), this.timeZone.toZoneId()
        );
    }

}
