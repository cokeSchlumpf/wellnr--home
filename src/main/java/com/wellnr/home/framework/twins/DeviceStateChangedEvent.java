package com.wellnr.home.framework.twins;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeviceStateChangedEvent<T, D extends DeviceTwin<T, D>> {

    D device;

    T value;

    boolean fromDevice;

}
