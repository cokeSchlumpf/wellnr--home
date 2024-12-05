package com.wellnr.home.ports.weather;

public interface WeatherPort {

    Weather getCurrentWeather(double latitude, double longitude);

}
