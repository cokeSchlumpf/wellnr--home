package com.wellnr.home.services;

import com.luckycatlabs.sunrisesunset.dto.Location;
import com.wellnr.home.framework.scenes.Device;
import com.wellnr.home.framework.scenes.Scene;
import com.wellnr.home.framework.scenes.environment.Daylight;
import com.wellnr.home.framework.twins.DeviceRegistry;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;

@AllArgsConstructor
public class AutomaticLightningServiceV2 {

    private final DeviceRegistry registry;

    private final List<Scene> scenes;

    private final Map<String, Device> devices;

    @Scheduled(fixedRate = 30_000)
    public void render() {
        Map<String, Device> devicesToUpdate = new HashMap<>();

        for (var scene : scenes) {
            var now = LocalDateTime.now();

            var daylight = Daylight.apply(
                    60, 60,
                    TimeZone.getTimeZone("Europe/Berlin"), new Location("49.442009", "6.636030")
            );

            devicesToUpdate = scene.render(now, daylight)
                    .stream()
                    .flatMap(expr -> expr.getDevices().stream())
                    .collect(Collectors.toMap(Device::getName, device -> device));
        }

        // Remove devices which have not changed state since last render
        for (var device : devicesToUpdate.values()) {
            if (this.devices.containsKey(device.getName())) {
                var currentState = this.devices.get(device.getName());

                if (currentState.equals(device)) {
                    devicesToUpdate.remove(device.getName());
                }
            }
        }


    }

}
