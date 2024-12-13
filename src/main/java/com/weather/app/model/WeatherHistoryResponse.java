package com.weather.app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WeatherHistoryResponse {
    private String postalCode;
    private String user;
    private double temperature;
    private double humidity;
    private String weatherCondition;
}
