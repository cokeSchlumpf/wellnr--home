package com.wellnr.home.framework.twins;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeviceRegistry {

    /**
     * An internal map of all devices mapped to their unique name.
     */
    private final Map<String, Device<?, ?>> devices;

    /**
     * Creates a new instance.
     *
     * @return The instance.
     */
    public static DeviceRegistry apply() {
        return new DeviceRegistry(new HashMap<>());
    }

    /**
     * Registers a new device.
     *
     * @param device The device to register.
     * @throws RuntimeException If the device with the given name has already been registered.
     */
    public void registerDevice(Device<?, ?> device) {
        if (devices.containsKey(device.getDeviceName())) {
            throw new RuntimeException("Device with name `" + device.getDeviceName() + "` has already been registered.");
        }

        this.devices.put(device.getDeviceName(), device);
    }

    /**
     * Returns the device with the given name.
     *
     * @param name The name of the device to retrieve.
     * @return The device with the given name.
     * @throws RuntimeException If the device with the given name has not been registered.
     */
    public Device<?, ?> getDeviceByName(String name) {
        if (!devices.containsKey(name)) {
            throw new RuntimeException("Device with name `" + name + "` has not been registered.");
        }

        return devices.get(name);
    }

}
