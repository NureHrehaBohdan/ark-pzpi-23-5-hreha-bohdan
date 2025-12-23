package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Statistic.HourlyActivityPoint;
import com.skiing.demo.DTOs.Statistic.LoadForecastResponse;
import com.skiing.demo.DTOs.Statistic.RouteActivityStat;
import com.skiing.demo.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/load-forecast")
    public ApiResponse<LoadForecastResponse> forecast() {
        LoadForecastResponse forecast = analyticsService.forecastLoad();

        forecast = new LoadForecastResponse(
                forecast.currentLoad(),
                round(forecast.ewmaForecast(), 2),
                round(forecast.weatherAdjustedForecast(), 2),
                round(forecast.trend(), 2)
        );

        return new ApiResponse<>(
                true,
                "Load forecast calculated",
                forecast
        );
    }

    // Вспомогательный метод для округления
    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        return Math.round(value * Math.pow(10, places)) / Math.pow(10, places);
    }

    @GetMapping("/route-activity")
    public ApiResponse<List<RouteActivityStat>> routeActivity() {
        return new ApiResponse<>(
                true,
                "Average activity per route",
                analyticsService.getAverageActivityPerRoute()
        );
    }

    @GetMapping("/hourly-activity")
    public ApiResponse<List<HourlyActivityPoint>> hourlyActivity() {
        return new ApiResponse<>(
                true,
                "Sessions grouped by hour of day",
                analyticsService.getHourlyActivity()
        );
    }

}
