package com.wellnr.home.scenes;

import com.wellnr.home.framework.scenes.environment.Daylight;
import com.wellnr.home.framework.scenes.DeviceExpression;
import com.wellnr.home.framework.scenes.Switch;
import com.wellnr.home.framework.scenes.SwitchGroup;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.wellnr.home.framework.scenes.Expressions.when;

@AllArgsConstructor
public class Lightning {

    private final SwitchGroup allNightLights;

    private final SwitchGroup workAndLiveLights;

    private final SwitchGroup sleepingRoomLights;

    public static Lightning apply() {
        var basementStairs = Switch.apply("iot-plug-017");
        var changingRoom = Switch.apply("iot-plug-018");
        var diningLeft = Switch.apply("iot-plug-010");
        var diningRight = Switch.apply("iot-plug-011");
        var gallery = Switch.apply("iot-plug-008");
        var galleryReading = Switch.apply("iot-plug-003");
        var guestRoom01 = Switch.apply("iot-plug-005"); // Not checked
        var guestRoom02 = Switch.apply("iot-plug-007");
        var kitchenWindow = Switch.apply("iot-plug-013");
        var livingRoomCoach = Switch.apply("iot-plug-012");
        var livingRoomCorner = Switch.apply("iot-plug-001");
        var livingRoomPiano = Switch.apply("iot-plug-016");
        var livingRoomTVLeft = Switch.apply("iot-plug-006");
        var livingRoomTVRight = Switch.apply("iot-plug-009");
        var sleepingRoom = Switch.apply("iot-plug-015");
        var stairs = Switch.apply("iot-plug-014");
        var typewriter = Switch.apply("iot-plug-002");
        var workplacesWindow = Switch.apply("iot-plug-004");

        var outsideFrontDoor = Switch.apply("iot-switch-001");

        var allNightLights = SwitchGroup.apply(
                outsideFrontDoor,
                gallery,
                galleryReading,
                livingRoomCorner,
                typewriter
        );

        var workAndLiveLights = SwitchGroup.apply(
                basementStairs,
                changingRoom,
                diningLeft,
                diningRight,
                gallery,
                galleryReading,
                kitchenWindow,
                livingRoomCoach,
                livingRoomCorner,
                livingRoomPiano,
                livingRoomTVLeft,
                livingRoomTVRight,
                stairs,
                typewriter,
                workplacesWindow
        );

        var sleepingRoomLights = SwitchGroup.apply(
                sleepingRoom,
                guestRoom01,
                guestRoom02
        );

        return new Lightning(allNightLights, workAndLiveLights, sleepingRoomLights);
    }

    public List<DeviceExpression> render(LocalDateTime now, Daylight daylight) {
        var turnOffWorkAndLiveLights = LocalTime.parse("01:30:00");
        var turnOnWorkAndLiveLights = LocalTime.parse("06:00:00");

        var turnOffSleepingRoomLights = LocalTime.parse("00:00:00");

        return List.of(
                when(
                        daylight.isNightAndBefore(turnOffWorkAndLiveLights) || daylight.isNightAndAfter(turnOnWorkAndLiveLights),
                        workAndLiveLights.turnedOn()
                ).otherwise(
                        workAndLiveLights.turnedOff()
                ),
                when(
                        daylight.isNight(),
                        allNightLights.turnedOn()
                ).otherwise(
                        allNightLights.turnedOff()
                ),
                when(
                        daylight.isNightAndBefore(turnOffSleepingRoomLights),
                        sleepingRoomLights.turnedOn()
                ).otherwise(
                        sleepingRoomLights.turnedOff()
                )
        );
    }

}
