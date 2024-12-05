package com.wellnr.home.ports.weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CurrentWeatherResponse {

    public static final String WEATHER = "weather";

    @JsonProperty(WEATHER)
    Weather weather;

    @JsonCreator
    public static CurrentWeatherResponse apply(
        @JsonProperty(WEATHER) Weather weather
    ) {
        return new CurrentWeatherResponse(weather);
    }

}
