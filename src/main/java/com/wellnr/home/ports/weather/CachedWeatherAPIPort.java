package com.wellnr.home.ports.weather;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CachedWeatherAPIPort implements WeatherPort {

    private final WeatherPort delegate;

    private final Map<String, CachedWeatherData> cache;

    public CachedWeatherAPIPort(WeatherPort delegate) {
        this.delegate = delegate;
        this.cache = new HashMap<>();
    }

    @Override
    public Weather getCurrentWeather(double latitude, double longitude) {
        var key = String.format("%f,%f", latitude, longitude);

        if (cache.containsKey(key) && !cache.get(key).isExpired()) {
            return cache.get(key).getWeather();
        } else {
            var weather = delegate.getCurrentWeather(latitude, longitude);
            cache.put(key, CachedWeatherData.apply(weather));
            return weather;
        }
    }

    @Value
    @AllArgsConstructor(staticName = "apply")
    private static class CachedWeatherData {

        Weather weather;

        LocalDateTime expires;

        public static CachedWeatherData apply(Weather weather) {
            return apply(weather, LocalDateTime.now().plusHours(2));
        }

        public boolean isExpired() {
            return LocalDateTime.now().isAfter(expires);
        }

    }

}
