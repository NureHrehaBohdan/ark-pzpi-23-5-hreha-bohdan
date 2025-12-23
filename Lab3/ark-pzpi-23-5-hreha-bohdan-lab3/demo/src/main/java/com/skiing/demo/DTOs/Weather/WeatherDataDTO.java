package com.skiing.demo.DTOs.Weather;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record WeatherDataDTO(
        @NotNull
        Integer sensorId,
        BigDecimal temperature,
        BigDecimal humidity,
        BigDecimal windSpeed,
        BigDecimal pressure,
        String status,
        LocalDateTime recordedAt
) {
}
