package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Weather.WeatherDataDTO;
import com.skiing.demo.service.interfaces.IWeatherService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/weather")
public class AdminWeatherController {

    private final IWeatherService weatherService;

    public AdminWeatherController(IWeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<WeatherDataDTO>>> getAllWeatherData() {
        ApiResponse<List<WeatherDataDTO>> response = weatherService.getAllWeatherData();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sensor/{sensorId}")
    public ResponseEntity<ApiResponse<List<WeatherDataDTO>>> getWeatherBySensor(@PathVariable int sensorId) {
        ApiResponse<List<WeatherDataDTO>> response = weatherService.getWeatherDataBySensorId(sensorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sensor/{sensorId}/latest")
    public ResponseEntity<ApiResponse<WeatherDataDTO>> getLatestBySensor(@PathVariable int sensorId) {
        ApiResponse<WeatherDataDTO> response = weatherService.getLatestBySensorId(sensorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/latest")
    public ResponseEntity<ApiResponse<List<WeatherDataDTO>>> getAllLatest() {
        ApiResponse<List<WeatherDataDTO>> response = weatherService.getAllLatest();
        return ResponseEntity.ok(response);
    }
}

