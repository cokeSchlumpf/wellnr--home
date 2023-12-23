package com.wellnr.home.framework.twins.impl;

import com.jayway.jsonpath.JsonPath;
import com.wellnr.common.Operators;
import com.wellnr.home.framework.twins.Device;
import com.wellnr.home.framework.twins.Switch;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class TasmatoPlug extends Device<Boolean, TasmatoPlug> implements Switch {

    private final IMqttClient client;

    private final String deviceName;

    private final String publishTopic;

    private Boolean isOn = null;

    public TasmatoPlug(IMqttClient client, String topicPrefix, String deviceName) {
        this.client = client;
        this.deviceName = topicPrefix + "/plugs/tasmato/" + deviceName;
        this.publishTopic = topicPrefix + "/cmnd/" + deviceName + "/Power";

        Operators.suppressExceptions(() -> {
            var statusTopic = topicPrefix + "/stat/" + deviceName + "/RESULT";

            log.info("Tasmato device `{}` is listening to status topic `{}` ...", deviceName, statusTopic);
            client.subscribe(statusTopic, this::handleResult);
            client.publish(publishTopic, "".getBytes(StandardCharsets.UTF_8), 0, false);
        });
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public Optional<Boolean> getDeviceState() {
        return Optional.ofNullable(isOn);
    }

    public void turnOn() {
        Operators.suppressExceptions(() -> client.publish(
            publishTopic,
            "ON".getBytes(StandardCharsets.UTF_8),
            0,
            false
        ));
    }

    public void turnOff() {
        Operators.suppressExceptions(() -> client.publish(
            publishTopic,
            "OFF".getBytes(StandardCharsets.UTF_8),
            0,
            false
        ));
    }

    private void handleResult(String topic, MqttMessage msg) {
        if (topic.endsWith("RESULT")) {
            var json = new String(msg.getPayload(), StandardCharsets.UTF_8);
            var isOn = JsonPath.read(json, "$.POWER").equals("ON");

            log.trace("Tasmato device `{}` is `{}`", deviceName, isOn ? "ON" : "OFF");

            if (Objects.isNull(this.isOn)) {
                this.isOn = isOn;
                fireEvent(this.isOn, false);
            } else if (this.isOn != isOn) {
                this.isOn = isOn;
                fireEvent(this.isOn, true);
            } else {
                fireEvent(this.isOn, false);
            }
        } else {
            log.warn("Unhandled message on topic `{}`: `{}`", topic, new String(msg.getPayload(), StandardCharsets.UTF_8));
        }
    }

}
