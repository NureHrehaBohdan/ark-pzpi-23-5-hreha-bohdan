package com.skiing.demo.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Entity
@NoArgsConstructor
@Data
@Table(name = "weather_data")
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @Column(precision = 5, scale = 2)
    private BigDecimal temperature;

    @Column(precision = 5, scale = 2)
    private BigDecimal humidity;

    @Column(precision = 5, scale = 2, name = "wind_speed")
    private BigDecimal windSpeed;

    @Column(precision = 6, scale = 2)
    private BigDecimal pressure;

    @Column(length = 20)
    private String status;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt;

}
