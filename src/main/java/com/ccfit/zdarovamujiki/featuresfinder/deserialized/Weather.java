package com.ccfit.zdarovamujiki.featuresfinder.deserialized;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Weather {
    private List<WeatherInfo> weather;
    private MainInfo main;

    @Getter @Setter
    public static class WeatherInfo {
        private String description;
    }

    @Getter @Setter
    public static class MainInfo {
        private String temp;
    }

    @Override
    public String toString() {
        return String.format("Weather: %s, %s C", weather.get(0).description, main.temp);
    }
}
