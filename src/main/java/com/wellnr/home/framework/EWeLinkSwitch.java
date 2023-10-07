package com.wellnr.home.framework;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class EWeLinkSwitch extends Device<Boolean, EWeLinkSwitch> {

    private final String deviceName;

    private final String enableWebhookUrl;

    private final String disableWebhookUrl;

    private final OkHttpClient client;

    private Boolean isOn = null;

    public EWeLinkSwitch(String deviceName, String enableWebhookUrl, String disableWebhookUrl, OkHttpClient client) {
        this.deviceName = deviceName;
        this.enableWebhookUrl = enableWebhookUrl;
        this.disableWebhookUrl = disableWebhookUrl;
        this.client = client;
    }

    public void turnOn() {
        var request = new Request.Builder().url(enableWebhookUrl).get().build();
        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                this.isOn = true;
            }
        } catch (Exception e) {
            log.warn("Failed to turn on device " + deviceName, e);
        }
    }

    public void turnOff() {
        var request = new Request.Builder().url(disableWebhookUrl).get().build();
        try (var response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                this.isOn = false;
            }
        } catch (Exception e) {
            log.warn("Failed to turn off device " + deviceName, e);
        }
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }

    @Override
    public Optional<Boolean> getDeviceState() {
        return Optional.ofNullable(isOn);
    }

}
