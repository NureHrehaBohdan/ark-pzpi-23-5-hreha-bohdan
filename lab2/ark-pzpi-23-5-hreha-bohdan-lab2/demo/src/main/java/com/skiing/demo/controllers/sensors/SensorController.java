package com.skiing.demo.controllers.sensors;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Weather.WeatherDataDTO;
import com.skiing.demo.service.interfaces.IWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sensor")
public class SensorController {

    private final IWeatherService weatherService;

    public SensorController(IWeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addWeatherData(@RequestBody WeatherDataDTO weatherData) {
        return ResponseEntity.ok(weatherService.addWeatherData(weatherData));
    }
}
