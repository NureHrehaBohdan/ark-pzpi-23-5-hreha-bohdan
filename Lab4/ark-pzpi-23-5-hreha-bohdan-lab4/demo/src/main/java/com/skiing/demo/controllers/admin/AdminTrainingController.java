package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.model.TrainingSession;
import com.skiing.demo.service.interfaces.ITrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/training")
public class AdminTrainingController {

    private final ITrainingService trainingService;

    public AdminTrainingController(ITrainingService trainingService) {
        this.trainingService = trainingService;
    }

    @GetMapping("/route/{id}")
    public ResponseEntity<ApiResponse<List<TrainingSession>>> getTrainingByRouteId(@PathVariable int id) {
        return ResponseEntity.ok(trainingService.getTrainingByRouteId(id));
    }

}
