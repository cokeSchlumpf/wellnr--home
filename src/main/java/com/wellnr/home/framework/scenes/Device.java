package com.wellnr.home.framework.scenes;

import com.wellnr.home.framework.twins.DeviceTwin;

import java.util.List;

public interface Device extends DeviceExpression {

    String getName();

    @Override
    default List<Device> getDevices() {
        return List.of(this);
    }

}
