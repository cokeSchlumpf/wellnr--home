package com.wellnr.home.ports.weather;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Optional;

@Value
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Weather {

    private static final String CONDITION = "condition";
    private static final String CLOUD_COVER = "cloud_cover";
    private static final String RELATIVE_HUMIDITY = "relative_humidity";

    @JsonProperty(CONDITION)
    Condition condition;

    @JsonProperty(CLOUD_COVER)
    int cloudCover;

    @JsonProperty(RELATIVE_HUMIDITY)
    int relativeHumidity;

    @JsonCreator
    public static Weather apply(
        @JsonProperty(CONDITION) Condition condition,
        @JsonProperty(CLOUD_COVER) int cloudCover,
        @JsonProperty(RELATIVE_HUMIDITY) int relativeHumidity
    ) {
        return new Weather(condition, cloudCover, relativeHumidity);
    }

    public Condition getCondition() {
        return Optional.ofNullable(condition).orElse(Condition.NULL);
    }

}
