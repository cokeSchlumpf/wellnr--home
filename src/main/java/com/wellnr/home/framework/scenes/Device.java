package com.wellnr.home.framework.scenes;

import java.util.List;

public interface Device extends DeviceExpression {

    @Override
    default List<Device> getDevices() {
        return List.of(this);
    }

}
