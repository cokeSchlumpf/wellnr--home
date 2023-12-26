package com.wellnr.home.scenes;

import com.wellnr.home.framework.scenes.environment.Daylight;
import com.wellnr.home.framework.scenes.DeviceExpression;
import com.wellnr.home.framework.scenes.SingleSwitch;
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
        var basementStairs = SingleSwitch.apply("iot-plug-017");
        var changingRoom = SingleSwitch.apply("iot-plug-018");
        var diningLeft = SingleSwitch.apply("iot-plug-010");
        var diningRight = SingleSwitch.apply("iot-plug-011");
        var gallery = SingleSwitch.apply("iot-plug-008");
        var galleryReading = SingleSwitch.apply("iot-plug-003");
        var guestRoom01 = SingleSwitch.apply("iot-plug-005"); // Not checked
        var guestRoom02 = SingleSwitch.apply("iot-plug-007");
        var kitchenWindow = SingleSwitch.apply("iot-plug-013");
        var livingRoomCoach = SingleSwitch.apply("iot-plug-012");
        var livingRoomCorner = SingleSwitch.apply("iot-plug-001");
        var livingRoomPiano = SingleSwitch.apply("iot-plug-016");
        var livingRoomTVLeft = SingleSwitch.apply("iot-plug-006");
        var livingRoomTVRight = SingleSwitch.apply("iot-plug-009");
        var sleepingRoom = SingleSwitch.apply("iot-plug-015");
        var stairs = SingleSwitch.apply("iot-plug-014");
        var typewriter = SingleSwitch.apply("iot-plug-002");
        var workplacesWindow = SingleSwitch.apply("iot-plug-004");

        var outsideFrontDoor = SingleSwitch.apply("iot-switch-001");

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
