package com.wellnr.home.services;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.wellnr.common.Operators;
import com.wellnr.common.markup.Tuple3;
import com.wellnr.home.framework.EWeLinkSwitch;
import com.wellnr.home.framework.TasmatoPlug;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

@Slf4j
@Component
public class AutomaticLightningService {

    private final TasmatoPlug[] iotPlugs;

    private final EWeLinkSwitch outsideSwitch;

    private final TimeZone timeZone;

    private final SunriseSunsetCalculator sunriseSunsetCalculator;

    Boolean nightLightsTurnedOn;

    Boolean workLightsTurnedOn;

    public AutomaticLightningService(IMqttClient client, EWeLinkSwitch outsideSwitch) {
        this.iotPlugs = new TasmatoPlug[18];

        this.iotPlugs[0] = new TasmatoPlug(client, "wellnr/home", "iot-plug-001"); // Gallery
        this.iotPlugs[1] = new TasmatoPlug(client, "wellnr/home", "iot-plug-002"); // Living-Room
        this.iotPlugs[2] = new TasmatoPlug(client, "wellnr/home", "iot-plug-003"); // Entrance/ Typewriter
        this.iotPlugs[3] = new TasmatoPlug(client, "wellnr/home", "iot-plug-004"); // Work window
        this.iotPlugs[4] = new TasmatoPlug(client, "wellnr/home", "iot-plug-005"); // Work window
        this.iotPlugs[5] = new TasmatoPlug(client, "wellnr/home", "iot-plug-006"); // Work wall
        this.iotPlugs[6] = new TasmatoPlug(client, "wellnr/home", "iot-plug-007"); // Guests south
        this.iotPlugs[7] = new TasmatoPlug(client, "wellnr/home", "iot-plug-008");
        this.iotPlugs[8] = new TasmatoPlug(client, "wellnr/home", "iot-plug-009");
        this.iotPlugs[9] = new TasmatoPlug(client, "wellnr/home", "iot-plug-010");
        this.iotPlugs[10] = new TasmatoPlug(client, "wellnr/home", "iot-plug-011");
        this.iotPlugs[11] = new TasmatoPlug(client, "wellnr/home", "iot-plug-012");
        this.iotPlugs[12] = new TasmatoPlug(client, "wellnr/home", "iot-plug-013");
        this.iotPlugs[13] = new TasmatoPlug(client, "wellnr/home", "iot-plug-014");
        this.iotPlugs[14] = new TasmatoPlug(client, "wellnr/home", "iot-plug-015");
        this.iotPlugs[15] = new TasmatoPlug(client, "wellnr/home", "iot-plug-016");
        this.iotPlugs[16] = new TasmatoPlug(client, "wellnr/home", "iot-plug-017");
        this.iotPlugs[17] = new TasmatoPlug(client, "wellnr/home", "iot-plug-018");

        this.outsideSwitch = outsideSwitch;

        var location = new Location("49.442009", "6.636030");
        this.timeZone = TimeZone.getTimeZone("Europe/Berlin");
        this.sunriseSunsetCalculator = new SunriseSunsetCalculator(location, timeZone);
        this.nightLightsTurnedOn = null;
        this.workLightsTurnedOn = null;
    }

    @Scheduled(fixedRate = 30_000)
    public void switchLights() {
        var nextSunriseAndNextSunset = this.getNextSunriseAndNextSunset();

        var shutdownWorkLightsTime = LocalTime.parse("00:05:00");
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
            Operators.ignoreExceptions(this::turnOffNightLights, log);
            Operators.ignoreExceptions(this::turnOffWorkLights, log);
        } else { // after sunset
            Operators.ignoreExceptions(this::turnOnNightLights, log);
            Operators.ignoreExceptions(this::turnOnWorkLights, log);
        }
    }

    private void turnOnNightLights() {
        if (Objects.isNull(this.nightLightsTurnedOn) || !this.nightLightsTurnedOn) {
            log.info("Send request to turn on the lights ...");
            for (var i = 0; i < 3; i++) {
                this.iotPlugs[i].turnOn();
            }

            this.outsideSwitch.turnOn();
            this.nightLightsTurnedOn = true;
        }
    }

    private void turnOnWorkLights() {
        if (Objects.isNull(this.workLightsTurnedOn) || !this.workLightsTurnedOn) {
            log.info("Send request to turn on work lights");
            for (var i = 3; i < this.iotPlugs.length; i++) {
                this.iotPlugs[i].turnOn();
                Operators.suppressExceptions(() -> Thread.sleep(300));
            }

            this.workLightsTurnedOn = true;
        }
    }

    private void turnOffNightLights() {
        if (Objects.isNull(this.nightLightsTurnedOn) || this.nightLightsTurnedOn) {
            log.info("Send request to turn off the lights ...");

            for (var i = 0; i < 3; i++) {
                this.iotPlugs[i].turnOff();
            }

            this.outsideSwitch.turnOff();
            this.nightLightsTurnedOn = false;
        }
    }

    private void turnOffWorkLights() {
        if (Objects.isNull(this.workLightsTurnedOn) || this.workLightsTurnedOn) {
            log.info("Send request to turn off work lights ...");

            for (var i = 3; i < this.iotPlugs.length; i++) {
                this.iotPlugs[i].turnOff();
                Operators.suppressExceptions(() -> Thread.sleep(300));
            }

            this.workLightsTurnedOn = false;
        }
    }

    /**
     * Calculates the next sunrise and next sunset based ont the current time.
     *
     * @return A tuple with three values: Next Sunrise, next sunset and the current date and time.
     */
    private Tuple3<LocalDateTime, LocalDateTime, LocalDateTime> getNextSunriseAndNextSunset() {
        var calendar = Calendar.getInstance(timeZone);
        var sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
            .plus(Duration.ofMinutes(60));

        var sunset = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar))
            .minus(Duration.ofMinutes(30));

        var now = LocalDateTime.now(this.timeZone.toZoneId());

        if (now.isAfter(sunset) || now.isEqual(sunset)) {
            calendar.add(Calendar.DATE, 1);

            sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
                .plus(Duration.ofMinutes(60));

            sunset = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar))
                .minus(Duration.ofMinutes(30));
        } else if (now.isAfter(sunrise) || now.isEqual(sunrise)) {
            calendar.add(Calendar.DATE, 1);

            sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
                .plus(Duration.ofMinutes(60));
        }

        return Tuple3.apply(sunrise, sunset, now);
    }

    private LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(
            calendar.toInstant(), this.timeZone.toZoneId()
        );
    }

}
