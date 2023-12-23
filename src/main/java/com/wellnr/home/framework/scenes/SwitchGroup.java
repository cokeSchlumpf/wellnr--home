package com.wellnr.home.framework.scenes;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class SwitchGroup implements Device {

    List<Switch> switches;

    boolean on;

    public static SwitchGroup apply(List<Switch> switches) {
        return new SwitchGroup(switches, false);
    }

    public static SwitchGroup apply(Switch...switches) {
        return apply(List.of(switches));
    }

    public SwitchGroup turnedOn() {
        return apply(switches, true);
    }

    public SwitchGroup turnedOff() {
        return apply(switches, false);
    }

}
