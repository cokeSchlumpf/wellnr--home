package com.wellnr.home.services;

import com.wellnr.home.framework.scenes.Scene;
import com.wellnr.home.framework.twins.DeviceRegistry;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class AutomaticLightningServiceV2 {

    private final DeviceRegistry registry;

    private final List<Scene> scenes;

    public void render() {

    }

}
