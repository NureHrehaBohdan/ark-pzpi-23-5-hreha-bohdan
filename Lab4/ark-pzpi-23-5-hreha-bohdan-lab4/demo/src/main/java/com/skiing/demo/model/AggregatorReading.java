package com.skiing.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "aggregator_readings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AggregatorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 100)
    private String aggregatorId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 50)
    private String overallStatus;

    // Температура
    @Column(name = "temp_average")
    private BigDecimal tempAverage;

    @Column(name = "temp_status", length = 50)
    private String tempStatus;

    // Ветер
    @Column(name = "wind_average")
    private BigDecimal windAverage;

    @Column(name = "wind_status", length = 50)
    private String windStatus;

    // Влажность
    @Column(name = "humidity_average")
    private BigDecimal humidityAverage;

    @Column(name = "humidity_status", length = 50)
    private String humidityStatus;

    // Давление
    @Column(name = "pressure_average")
    private BigDecimal pressureAverage;

    @Column(name = "pressure_status", length = 50)
    private String pressureStatus;

    @Column(nullable = false)
    private Integer sensorsCount;

    private String sensorsList;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
