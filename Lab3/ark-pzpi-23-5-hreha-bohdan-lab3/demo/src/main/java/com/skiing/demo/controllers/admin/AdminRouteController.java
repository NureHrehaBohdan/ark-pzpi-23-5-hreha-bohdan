package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Route.RouteDTO;
import com.skiing.demo.service.RouteService;
import com.skiing.demo.service.interfaces.IRouteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/route")
public class AdminRouteController {

    private final IRouteService routeService;

    public AdminRouteController(IRouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addRoute(@RequestBody @Valid RouteDTO  routeDTO) {
        return ResponseEntity.ok(routeService.addRoute(routeDTO));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> deleteRoute(@RequestBody int id) {
        return ResponseEntity.ok(routeService.deleteRouteById(id));
    }

    @GetMapping({"/{id}/avg-speed"})
    public ResponseEntity<ApiResponse<Double>> getAvgSpeedByRouteId(@PathVariable int id) {
        return ResponseEntity.ok(ApiResponse.Success(0.0));
    }

}
