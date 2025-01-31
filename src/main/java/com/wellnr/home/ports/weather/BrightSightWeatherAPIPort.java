package com.wellnr.home.ports.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import java.util.Objects;

@Slf4j
@AllArgsConstructor
public final class BrightSightWeatherAPIPort implements WeatherPort {

    private final OkHttpClient client;

    private final ObjectMapper om;

    @Override
    public Weather getCurrentWeather(double latitude, double longitude) {
        var url = String.format(
            "https://api.brightsky.dev/current_weather?lat=%s&lon=%s",
            latitude,
            longitude
        );

        var request = new Request.Builder()
            .get()
            .url(url)
            .header("Accept", "application/json")
            .build();

        try (var response = client.newCall(request).execute()) {
            var json = Objects.requireNonNull(response.body(), "Expected response JSON in body.").string();
            var resp = om.readValue(json, CurrentWeatherResponse.class);

            log.info("Fetched weather data: {}", resp);

            return resp.getWeather();
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch weather data", e);
        }
    }

}
