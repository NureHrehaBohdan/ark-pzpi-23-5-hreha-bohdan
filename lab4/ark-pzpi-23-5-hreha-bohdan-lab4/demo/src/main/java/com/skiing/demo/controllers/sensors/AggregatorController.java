package com.skiing.demo.controllers.sensors;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Sensor.AggregatorReadingDTO;
import com.skiing.demo.model.AggregatorReading;
import com.skiing.demo.repo.AggregatorReadingRepository;
import com.skiing.demo.service.MqttService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/aggregator")
public class AggregatorController {

    private final AggregatorReadingRepository aggregatorReadingRepository;
    private final MqttService mqttService;

    public AggregatorController(AggregatorReadingRepository aggregatorReadingRepository, MqttService mqttService) {
        this.aggregatorReadingRepository = aggregatorReadingRepository;
        this.mqttService = mqttService;
    }

    /**
     * POST /api/aggregator/save
     * Saves data from aggregator
     */
    @PostMapping("/save")
    public ResponseEntity<ApiResponse<AggregatorReading>> saveReading(@RequestBody AggregatorReadingDTO dto) {
        AggregatorReading reading = new AggregatorReading();
        reading.setAggregatorId(dto.aggregatorId());
        reading.setTimestamp(LocalDateTime.parse(dto.timestamp()));
        reading.setOverallStatus(dto.overallStatus());

        reading.setTempAverage(BigDecimal.valueOf(dto.temperature().average()));
        reading.setTempStatus(dto.temperature().status());

        reading.setWindAverage(BigDecimal.valueOf(dto.windSpeed().average()));
        reading.setWindStatus(dto.windSpeed().status());

        reading.setHumidityAverage(BigDecimal.valueOf(dto.humidity().average()));
        reading.setHumidityStatus(dto.humidity().status());

        reading.setPressureAverage(BigDecimal.valueOf(dto.pressure().average()));
        reading.setPressureStatus(dto.pressure().status());

        reading.setSensorsCount(dto.sensorsCount());
        reading.setSensorsList(String.join(",", dto.sensors()));

        AggregatorReading saved = aggregatorReadingRepository.save(reading);
        return ResponseEntity.ok(new ApiResponse<>( true, "Reading saved", saved));
    }

    @GetMapping("/latest")
    public ResponseEntity<ApiResponse<AggregatorReading>> getLatestReading() {
        try {
            AggregatorReading reading = aggregatorReadingRepository.findAll().getFirst();
            return ResponseEntity.ok(new ApiResponse<>(true, "Latest reading", reading));
        }catch (Exception e){
            return ResponseEntity.ok(new ApiResponse<>( false, "No data found", null));
        }
    }

    @PostMapping("/config")
    public ResponseEntity<ApiResponse<Void>> updateAggregator(@RequestBody String config) {
        String topic = "config/aggregator/aggregator_001";
        mqttService.publish(topic, config);
        return ResponseEntity.ok().build();
    }


}
