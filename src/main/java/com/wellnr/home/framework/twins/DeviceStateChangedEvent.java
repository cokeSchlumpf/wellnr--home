package com.wellnr.home.framework.twins;

import com.wellnr.home.framework.twins.Device;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "apply")
public class DeviceStateChangedEvent<T, D extends Device<T, D>> {

    D device;

    T value;

    boolean fromDevice;

}
