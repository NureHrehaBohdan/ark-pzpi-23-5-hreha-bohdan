package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Weather.WeatherDataDTO;

import java.util.List;

public interface IWeatherService {
    ApiResponse<Void> addWeatherData(WeatherDataDTO weatherData);
    ApiResponse<List<WeatherDataDTO>> getWeatherDataBySensorId(int sensorId);
    ApiResponse<List<WeatherDataDTO>> getAllWeatherData();
    ApiResponse<WeatherDataDTO> getLatestBySensorId(int sensorId);
    ApiResponse<List<WeatherDataDTO>> getAllLatest();

}
