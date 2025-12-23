package com.skiing.demo.DTOs.Sensor;

import java.util.Map;

public record AggregatorConfig(
        String aggregatorId,
        Integer BATCH_INTERVAL,
        Map<String, Object> temperatureThresholds,
        Map<String, Object> windThresholds,
        Map<String, Object> humidityThresholds,
        Map<String, Object> pressureThresholds,
        String REST_API_URL
) {}
