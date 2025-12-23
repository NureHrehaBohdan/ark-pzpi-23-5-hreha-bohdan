package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.TrainingSession.TrainingSessionDTO;
import com.skiing.demo.exception.DataNotFoundException;
import com.skiing.demo.model.Route;
import com.skiing.demo.model.TrainingSession;
import com.skiing.demo.model.User;
import com.skiing.demo.repo.RouteRepository;
import com.skiing.demo.repo.TrainingSessionRepository;
import com.skiing.demo.repo.UserRepository;
import com.skiing.demo.service.interfaces.ITrainingService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;

@Service
public class TrainingService implements ITrainingService {

    private final TrainingSessionRepository trainingSessionRepository;
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;

    public TrainingService(TrainingSessionRepository trainingSessionRepository, UserRepository userRepository, RouteRepository routeRepository) {
        this.trainingSessionRepository = trainingSessionRepository;
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
    }

    @Override
    public ApiResponse<Void> postTraining(TrainingSessionDTO session) {
        TrainingSession trainingSession = new TrainingSession();
        User user = userRepository.findById(session.userId())
                .orElseThrow(() -> new DataNotFoundException(
                        "User not found with id " + session.userId()
                ));
        Route route = routeRepository.findById(session.routeId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Route not found with id " + session.routeId()
                ));
        trainingSession.setUser(user);
        trainingSession.setRoute(route);
        trainingSession.setStatus(session.status());
        trainingSession.setStartTime(session.startTime());
        trainingSession.setEndTime(LocalDateTime.now());
        trainingSession.setDistanceKm(session.distanceKm());
        trainingSession.setAvgSpeedKmh(session.avgSpeedKmh());

        trainingSessionRepository.save(trainingSession);

        return new ApiResponse<>(true, "success", null);
    }

    @Override
    public ApiResponse<List<TrainingSession>> getTrainingByUserId(int userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataNotFoundException("User not found with id " + userId);
        }
        List<TrainingSession> result = trainingSessionRepository.findAllByUser_Id(userId);

        return new ApiResponse<>(true, "success", result);
    }

    @Override
    public ApiResponse<Void> deleteTraining(int id) {
        if (!trainingSessionRepository.existsById(id)) {
            throw new DataNotFoundException("Training not found with id " + id);
        }
        trainingSessionRepository.deleteById(id);
        return new ApiResponse<>(true, "success", null);
    }

    @Override
    public ApiResponse<List<TrainingSession>> getTrainingByRouteId(int id) {
        if (!routeRepository.existsById(id)) {
            throw new DataNotFoundException("Route not found with id " + id);
        }
        return ApiResponse.Success(trainingSessionRepository.getAllByRoute_Id(id));
    }
}
