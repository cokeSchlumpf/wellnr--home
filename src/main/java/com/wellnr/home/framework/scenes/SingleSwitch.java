package com.wellnr.home.framework.scenes;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class SingleSwitch implements Device, Switch<SingleSwitch> {

    String name;

    boolean on;

    public static SingleSwitch apply(String name) {
        return new SingleSwitch(name, false);
    }

    @Override
    public SingleSwitch turnedOn() {
        return apply(name, true);
    }

    @Override
    public SingleSwitch turnedOff() {
        return apply(name, false);
    }

}
