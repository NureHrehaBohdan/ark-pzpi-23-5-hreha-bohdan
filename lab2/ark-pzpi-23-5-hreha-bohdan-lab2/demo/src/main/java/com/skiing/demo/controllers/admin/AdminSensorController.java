package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.model.Sensor;
import com.skiing.demo.service.interfaces.ISensorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/sensor")
public class AdminSensorController {

    private final ISensorService sensorService;

    public AdminSensorController(ISensorService sensorService) {
        this.sensorService = sensorService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addSensor(@RequestBody Sensor sensor) {
        return ResponseEntity.ok(sensorService.addSensor(sensor));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Sensor>>> getAllSensors() {
        return ResponseEntity.ok(sensorService.getAllSensors());
    }

    @DeleteMapping("/{sensorId}")
    public ResponseEntity<ApiResponse<Void>> deleteSensorById(@PathVariable int sensorId) {
        return ResponseEntity.ok(sensorService.deleteSensorById(sensorId));
    }
}
