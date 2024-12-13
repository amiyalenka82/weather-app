package com.weather.app.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class WeatherResponse {
    private double temperature;
    private double humidity;
    private String description;
    private String postalCode;
    private String userName;
    private int windDirection;
    private ErrorResponse errorResponse;
}