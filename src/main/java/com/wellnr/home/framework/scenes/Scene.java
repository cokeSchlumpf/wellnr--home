package com.wellnr.home.framework.scenes;

import com.wellnr.home.framework.scenes.environment.Daylight;

import java.time.LocalDateTime;
import java.util.List;

public interface Scene {

    public List<DeviceExpression> render(LocalDateTime now, Daylight daylight);

}
