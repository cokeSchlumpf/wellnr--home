package com.wellnr.home.framework.twins;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceRegistry {

    private final Map<String, Device<?, ?>> devices;

    public static DeviceRegistry apply() {
        return new DeviceRegistry(new HashMap<>());
    }

    public void registerDevice(Device<?,?> device) {
        this.devices.put(device.getDeviceName(), device);
    }

    public Device<?, ?> getDeviceByName(String name) {
        if (!devices.containsKey(name)) {
            throw new RuntimeException("Device with name `" + name + "` has not been registered.");
        }

        return devices.get(name);
    }

}
