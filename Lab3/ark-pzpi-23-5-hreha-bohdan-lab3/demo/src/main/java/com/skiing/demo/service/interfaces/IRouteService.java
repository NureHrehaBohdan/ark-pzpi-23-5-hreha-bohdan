package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Route.RouteDTO;
import com.skiing.demo.enums.Difficulty;
import com.skiing.demo.model.Route;

import java.util.List;

public interface IRouteService {
    ApiResponse<Void> addRoute(RouteDTO route);
    ApiResponse<List<Route>> getAllRoutes();
    ApiResponse<List<Route>> getAllRoutesByDifficulty(Difficulty difficulty);
    ApiResponse<Void> deleteRouteById(int id);
}
