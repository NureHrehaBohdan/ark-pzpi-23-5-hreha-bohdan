package com.skiing.demo.repo;

import com.skiing.demo.model.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface WeatherDataRepository extends JpaRepository<WeatherData, Integer> {
    Optional<WeatherData> findFirstBySensor_IdOrderByRecordedAtDesc(int sensorId);

    List<WeatherData> findAllBySensor_Id(int sensorId);

    @Query("SELECT w1 FROM WeatherData w1 " +
            "WHERE w1.recordedAt = (SELECT MAX(w2.recordedAt) FROM WeatherData w2 WHERE w2.sensor.id = w1.sensor.id)")
    List<WeatherData> findLatestForAllSensors();
}
