package com.skiing.demo.controllers.user;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.TrainingSession.TrainingSessionDTO;
import com.skiing.demo.model.TrainingSession;
import com.skiing.demo.service.interfaces.ITrainingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/training")
public class TrainingController {

    private final ITrainingService trainingService;

    public TrainingController(ITrainingService trainingService) {
        this.trainingService = trainingService;

    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addRoute(@RequestBody TrainingSessionDTO traininig){
        return ResponseEntity.ok(trainingService.postTraining(traininig));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<List<TrainingSession>>> getTrainingSessionByUser(@PathVariable int id){
        return ResponseEntity.ok(trainingService.getTrainingByUserId(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTraining(@PathVariable int id){
        return ResponseEntity.ok(trainingService.deleteTraining(id));
    }
}
