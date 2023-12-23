package com.wellnr.home.framework.scenes;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class Switch implements Device {

    String deviceName;

    boolean on;

    public static Switch apply(String deviceName) {
        return new Switch(deviceName, false);
    }

    public Switch turnedOn() {
        return apply(deviceName, true);
    }

    public Switch turnedOff() {
        return apply(deviceName, false);
    }

}
