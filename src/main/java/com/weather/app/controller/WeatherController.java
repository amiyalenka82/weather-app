package com.weather.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.weather.app.model.WeatherHistoryResponse;
import com.weather.app.model.WeatherResponse;
import com.weather.app.service.WeatherService;

@RestController
@RequestMapping("/app")
public class WeatherController {
    @Autowired
    private WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<WeatherResponse> getWeather(@RequestParam(required = false) String postalCode,
                                                               @RequestParam(required = false) String userName) {
        return ResponseEntity.ok(weatherService.getWeather(postalCode, userName));
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<WeatherHistoryResponse>> getHistory(@RequestParam(required = false) String postalCode,
                                                               @RequestParam(required = false) String userName) {
        return ResponseEntity.ok(weatherService.getHistory(postalCode, userName));
    }
    
    @GetMapping("/home")
    public String home() {
        return "A poc for weather app";
    }
}