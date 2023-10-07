package com.wellnr.home;


import com.wellnr.common.Operators;
import com.wellnr.home.framework.EWeLinkSwitch;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class ApplicationSpringConfiguration {

    @Value("${app.mqtt-broker}")
    String mqttBrokerUrl;

    @Value("${app.mqtt-publisher-id}")
    String mqttPublisherId;

    @Value("${app.ewelink.outside.enable-url}")
    String eWeLinkOutsideEnableUrl;

    @Value("${app.ewelink.outside.disable-url}")
    String eWeLinkOutsideDisableUrl;

    @Bean
    public IMqttClient getMqttClient() {
        return Operators.suppressExceptions(() -> {
            var publisherId = "mqtt.home.wellnr.com";
            var client = new MqttClient(mqttBrokerUrl, publisherId);

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            client.connect(options);

            return client;
        });
    }

    @Bean
    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
            .callTimeout(Duration.ofSeconds(10))
            .build();
    }

    @Bean
    public EWeLinkSwitch getEWeLinkSwitch(OkHttpClient client) {
        return new EWeLinkSwitch(
            "ewelink-switch-outside", eWeLinkOutsideEnableUrl, eWeLinkOutsideDisableUrl, client
        );
    }

}
