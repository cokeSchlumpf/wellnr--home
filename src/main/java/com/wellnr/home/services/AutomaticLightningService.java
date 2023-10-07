package com.wellnr.home.services;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import com.wellnr.common.markup.Tuple3;
import com.wellnr.home.framework.EWeLinkSwitch;
import com.wellnr.home.framework.TasmatoPlug;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
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

    Boolean lightsTurnedOn;

    public AutomaticLightningService(IMqttClient client, EWeLinkSwitch outsideSwitch) {
        this.iotPlugs = new TasmatoPlug[3];

        this.iotPlugs[0] = new TasmatoPlug(client, "wellnr/home", "iot-plug-001");
        this.iotPlugs[1] = new TasmatoPlug(client, "wellnr/home", "iot-plug-002");
        this.iotPlugs[2] = new TasmatoPlug(client, "wellnr/home", "iot-plug-003");

        this.outsideSwitch = outsideSwitch;

        var location = new Location("49.442009", "6.636030");
        this.timeZone = TimeZone.getTimeZone("Europe/Berlin");
        this.sunriseSunsetCalculator = new SunriseSunsetCalculator(location, timeZone);
        this.lightsTurnedOn = null;
    }

    @Scheduled(fixedRate = 30_000)
    public void switchLights() {
        var nextSunriseAndNextSunset = this.getNextSunriseAndNextSunset();

        var today = nextSunriseAndNextSunset.get_3().toLocalDate();
        var sunrise = nextSunriseAndNextSunset.get_1().toLocalDate();
        var sunset = nextSunriseAndNextSunset.get_2().toLocalDate();
        var sunriseIsToday = today.isEqual(sunrise);
        var sunsetIsToday = today.isEqual(sunset);

        if (sunriseIsToday && sunsetIsToday) {
            turnOn();
        } else if (!sunriseIsToday && sunsetIsToday) {
            turnOff();
        } else {
            turnOn();
        }
    }

    private void turnOn() {
        if (Objects.isNull(this.lightsTurnedOn) || !this.lightsTurnedOn) {
            log.info("Send request to turn on the lights ...");
            this.iotPlugs[0].turnOn();
            this.iotPlugs[1].turnOn();
            this.iotPlugs[2].turnOn();

            this.outsideSwitch.turnOn();

            this.lightsTurnedOn = true;
        }
    }

    private void turnOff() {
        if (Objects.isNull(this.lightsTurnedOn) || this.lightsTurnedOn) {
            log.info("Send request to turn off the lights ...");

            this.iotPlugs[0].turnOff();
            this.iotPlugs[1].turnOff();
            this.iotPlugs[2].turnOff();

            this.outsideSwitch.turnOff();

            this.lightsTurnedOn = false;
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
            .plus(Duration.ofMinutes(15));

        var sunset = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar))
            .minus(Duration.ofMinutes(15));

        var now = LocalDateTime.now(this.timeZone.toZoneId());

        if (now.isAfter(sunset) || now.isEqual(sunset)) {
            calendar.add(Calendar.DATE, 1);

            sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
                .plus(Duration.ofMinutes(15));

            sunset = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunsetCalendarForDate(calendar))
                .minus(Duration.ofMinutes(15));
        } else if (now.isAfter(sunrise) || now.isEqual(sunrise)) {
            calendar.add(Calendar.DATE, 1);

            sunrise = toLocalDateTime(sunriseSunsetCalculator.getOfficialSunriseCalendarForDate(calendar))
                .plus(Duration.ofMinutes(15));
        }

        return Tuple3.apply(sunrise, sunset, now);
    }

    private LocalDateTime toLocalDateTime(Calendar calendar) {
        return LocalDateTime.ofInstant(
            calendar.toInstant(), this.timeZone.toZoneId()
        );
    }

}
