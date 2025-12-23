package com.skiing.demo.repo;

import com.skiing.demo.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    boolean existsById(int id);
}
