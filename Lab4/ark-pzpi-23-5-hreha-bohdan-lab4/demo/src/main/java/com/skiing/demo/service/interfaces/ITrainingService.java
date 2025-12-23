package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.TrainingSession.TrainingSessionDTO;
import com.skiing.demo.model.TrainingSession;

import java.util.List;

public interface ITrainingService {
    ApiResponse<Void> postTraining(TrainingSessionDTO session);
    ApiResponse<List<TrainingSession>> getTrainingByUserId(int userId);
    ApiResponse<Void> deleteTraining(int id);
    ApiResponse<List<TrainingSession>> getTrainingByRouteId(int id);
}
