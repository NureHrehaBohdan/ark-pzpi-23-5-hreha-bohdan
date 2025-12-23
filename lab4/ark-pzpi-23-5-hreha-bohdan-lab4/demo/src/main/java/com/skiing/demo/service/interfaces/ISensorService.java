package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.model.Sensor;

import java.util.List;

public interface ISensorService {
    ApiResponse<Void> addSensor(Sensor sensor);
    ApiResponse<List<Sensor>> getAllSensors();
    ApiResponse<Void> deleteSensorById(int sensorId);
}
