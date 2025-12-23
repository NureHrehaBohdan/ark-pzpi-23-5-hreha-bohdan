package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.exception.DataNotFoundException;
import com.skiing.demo.model.Sensor;
import com.skiing.demo.repo.SensorRepository;
import com.skiing.demo.service.interfaces.ISensorService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SensorService implements ISensorService {

    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public ApiResponse<Void> addSensor(Sensor sensor) {
        sensorRepository.save(sensor);
        return new ApiResponse<>(true, "sensor added", null);
    }

    @Override
    public ApiResponse<List<Sensor>> getAllSensors() {
        return new ApiResponse<>(true, "success", sensorRepository.findAll());
    }

    @Override
    public ApiResponse<Void> deleteSensorById(int sensorId) {
        if (!sensorRepository.existsById(sensorId)) {
            throw new DataNotFoundException("Sensor not found with id " + sensorId);
        }
        sensorRepository.deleteById(sensorId);
        return new ApiResponse<>(true, "sensor deleted", null);
    }
}
