package com.skiing.demo.controllers.user;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Route.RouteDTO;

import com.skiing.demo.enums.Difficulty;
import com.skiing.demo.model.Route;
import com.skiing.demo.service.RouteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/route")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Route>>> getAllRoutes(){
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<ApiResponse<List<Route>>> getAllRoutesByDifficulty(@PathVariable Difficulty difficulty) {
        return ResponseEntity.ok(routeService.getAllRoutesByDifficulty(difficulty));
    }
}
