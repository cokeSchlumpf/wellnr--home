package com.wellnr.home.framework.twins;

import java.util.Optional;

public interface SwitchTwin {

    void turnOn();

    void turnOff();

    Optional<Boolean> getDeviceState();

}
