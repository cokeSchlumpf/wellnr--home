package com.wellnr.home.ports.weather;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Condition {

    DRY("dry"),
    FOG("fog"),
    RAIN("rain"),
    SLEET("sleet"),
    SNOW("snow"),
    HAIL("hail"),
    THUNDERSTORM("thunderstorm"),
    NULL("null");

    private final String value;

    Condition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

}
