package com.wellnr.home.framework.scenes;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor(staticName = "apply")
public class SwitchGroup implements Switch<SwitchGroup> {

    String name;

    List<SingleSwitch> switches;

    boolean on;

    public static SwitchGroup apply(String name, List<SingleSwitch> switches) {
        return new SwitchGroup(name, switches, false);
    }

    public static SwitchGroup apply(String name, SingleSwitch...switches) {
        return apply(name, List.of(switches));
    }

    public SwitchGroup turnedOn() {
        return apply(name, switches, true);
    }

    public SwitchGroup turnedOff() {
        return apply(name, switches, false);
    }

}
