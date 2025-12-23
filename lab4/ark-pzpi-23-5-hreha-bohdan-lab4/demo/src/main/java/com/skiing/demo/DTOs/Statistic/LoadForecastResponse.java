package com.skiing.demo.DTOs.Statistic;

public record LoadForecastResponse(
        long currentLoad,
        double ewmaForecast,
        double weatherAdjustedForecast,
        double trend
) {
}
