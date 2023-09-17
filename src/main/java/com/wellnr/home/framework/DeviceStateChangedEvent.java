package com.wellnr.home.framework;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeviceStateChangedEvent<T, D extends Device<T, D>> {

    D device;

    T value;

    boolean fromDevice;

}
