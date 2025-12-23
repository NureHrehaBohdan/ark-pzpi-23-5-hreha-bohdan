package com.skiing.demo.DTOs.Sensor;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record AggregatorReadingDTO(
        String aggregatorId,
        String timestamp,
        String overallStatus,
        MetricDTO temperature,
        @JsonProperty("wind_speed")
        MetricDTO windSpeed,
        MetricDTO humidity,
        MetricDTO pressure,
        Integer sensorsCount,
        List<String> sensors
) {}

