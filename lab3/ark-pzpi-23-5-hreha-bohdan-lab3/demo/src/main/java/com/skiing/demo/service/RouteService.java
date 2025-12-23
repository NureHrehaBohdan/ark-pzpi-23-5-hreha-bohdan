package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Route.RouteDTO;
import com.skiing.demo.enums.Difficulty;
import com.skiing.demo.exception.DataNotFoundException;
import com.skiing.demo.model.Route;
import com.skiing.demo.repo.RouteRepository;
import com.skiing.demo.service.interfaces.IRouteService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService implements IRouteService {

    private final RouteRepository routeRepository;

    public RouteService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    @Override
    public ApiResponse<Void> addRoute(@Valid RouteDTO route) {
        Route routeEntity = new Route();
        routeEntity.setName(route.name());
        routeEntity.setDifficultyLevel(route.difficulty().toString());
        routeEntity.setLengthKm(route.lengthKm());
        routeEntity.setHeightDrop(route.heightDrop());

        routeRepository.save(routeEntity);

        return new ApiResponse<>(true,"Route added",null);
    }

    @Override
    public ApiResponse<List<Route>> getAllRoutes() {
        return new ApiResponse<>(true,"Routes found",routeRepository.findAll());
    }

    @Override
    public ApiResponse<List<Route>> getAllRoutesByDifficulty(Difficulty difficulty) {
        return new ApiResponse<>(
                true,
                "success",
                routeRepository.getAllByDifficultyLevel(difficulty.toString())
        );
    }

    @Override
    public ApiResponse<Void> deleteRouteById(int id) {
        if (!routeRepository.existsById(id)) {
            throw new DataNotFoundException("Route not found with id " + id);
        }
        routeRepository.deleteById(id);
        return new ApiResponse<>(true,"Route deleted",null);
    }
}
