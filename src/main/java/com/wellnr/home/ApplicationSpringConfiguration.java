package com.wellnr.home;


import com.wellnr.common.Operators;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class ApplicationSpringConfiguration {

    @Value("${app.mqtt-broker}")
    String mqttBrokerUrl;

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

}
