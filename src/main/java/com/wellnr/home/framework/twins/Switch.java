package com.wellnr.home.framework.twins;

import java.util.Optional;

public interface Switch {

    void turnOn();

    void turnOff();

    Optional<Boolean> getDeviceState();

}
