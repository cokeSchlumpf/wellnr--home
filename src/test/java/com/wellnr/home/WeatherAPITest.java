package com.wellnr.home;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wellnr.home.ports.weather.BrightSightWeatherAPIPort;
import com.wellnr.home.ports.weather.CachedWeatherAPIPort;
import okhttp3.OkHttpClient;
import org.junit.jupiter.api.Test;

public class WeatherAPITest {

    // @Test
    public void testWeatherAPI() {
        var om = new ObjectMapper();
        var client = new OkHttpClient();

        var weatherPort = new CachedWeatherAPIPort(new BrightSightWeatherAPIPort(client, om));

        var weather = weatherPort.getCurrentWeather(49.442009, 6.636030);
        System.out.println("Current weather: " + weather);

        weather = weatherPort.getCurrentWeather(49.442009, 6.636030);
        System.out.println("Current weather: " + weather);
    }

}
