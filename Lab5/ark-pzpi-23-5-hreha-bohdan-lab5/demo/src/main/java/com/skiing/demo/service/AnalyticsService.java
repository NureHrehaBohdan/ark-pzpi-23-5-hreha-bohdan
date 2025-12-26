package com.skiing.demo.service;

import com.skiing.demo.DTOs.Statistic.HourlyActivityPoint;
import com.skiing.demo.DTOs.Statistic.LoadForecastResponse;
import com.skiing.demo.DTOs.Statistic.LoadPoint;
import com.skiing.demo.DTOs.Statistic.RouteActivityStat;
import com.skiing.demo.model.WeatherData;
import com.skiing.demo.repo.RouteRepository;
import com.skiing.demo.repo.TrainingSessionRepository;
import com.skiing.demo.repo.WeatherDataRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    //optimal temperature for skying
    private static final double T_OPT = -5;

    private final TrainingSessionRepository trainingRepo;
    private final WeatherDataRepository weatherRepo;
    private final RouteRepository routeRepository;

    public AnalyticsService(
            TrainingSessionRepository trainingRepo,
            WeatherDataRepository weatherRepo,
            RouteRepository routeRepository) {
        this.trainingRepo = trainingRepo;
        this.weatherRepo = weatherRepo;
        this.routeRepository = routeRepository;
    }


    //Exponential Weighted Moving Average
    private double ewma(List<LoadPoint> points, double alpha) {
        double smoothed = points.getFirst().sessions();

        for (int i = 1; i < points.size(); i++) {
            smoothed = alpha * points.get(i).sessions() + (1 - alpha) * smoothed;
        }
        return smoothed;
    }

    private double weatherFactor(WeatherData w) {
        double temperature = w.getTemperature().doubleValue();
        double windSpeed = w.getWindSpeed().doubleValue();
        double humidity = w.getHumidity().doubleValue();

        double factor =
                1
                + 0.03 * (temperature - T_OPT)
                - 0.05 * windSpeed
                - 0.01 * humidity;


        return Math.max(0.5, Math.min(factor, 1.3));
    }

    public LoadForecastResponse forecastLoad() {

        List<LoadPoint> points = trainingRepo.getHourlyLoad().stream()
                .map(r -> new LoadPoint(
                        ((java.sql.Timestamp) r[0]).toLocalDateTime(),
                        ((Number) r[1]).longValue()
                ))
                .toList();
        if (points.size() < 2) {
            throw new IllegalStateException("Not enough data for forecast");
        }

        double alpha = chooseAlpha(points);
        double ewmaForecast = ewma(points, alpha);

        WeatherData weather = weatherRepo.getLatest();
        double adjusted = ewmaForecast * weatherFactor(weather);

        long current = points.getLast().sessions();
        double trend = adjusted - current;

        return new LoadForecastResponse(
                current,
                ewmaForecast,
                adjusted,
                trend
        );
    }

    //smoothing factor for ewma dependent on the amount of data
    private double chooseAlpha(List<LoadPoint> points) {
        double avg = points.stream()
                .mapToLong(LoadPoint::sessions)
                .average()
                .orElse(10);

        if (avg < 10) return 0.4;
        if (avg < 25) return 0.3;
        return 0.2;
    }


    public List<RouteActivityStat> getAverageActivityPerRoute() {
        return routeRepository.getAverageActivityPerRoute()
                .stream()
                .map(r -> new RouteActivityStat(
                        (String) r[0],
                        Math.round(((Number) r[1]).doubleValue() * 100.0) / 100.0
                ))
                .toList();
    }

    public List<HourlyActivityPoint> getHourlyActivity() {

        List<Object[]> raw = trainingRepo.getSessionsByHour();

        return raw.stream()
                .map(r -> new HourlyActivityPoint(
                        ((Number) r[0]).intValue(),
                        ((Number) r[1]).longValue()
                ))
                .toList();
    }


}
