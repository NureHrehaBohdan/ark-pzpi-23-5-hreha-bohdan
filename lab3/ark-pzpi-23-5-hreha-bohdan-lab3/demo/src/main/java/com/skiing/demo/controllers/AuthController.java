package com.skiing.demo.controllers;


import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.User.AuthResponce;
import com.skiing.demo.DTOs.User.LoginRequest;
import com.skiing.demo.DTOs.User.RegisterRequest;
import com.skiing.demo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponce>> login(@RequestBody LoginRequest request) {
        ApiResponse<AuthResponce> resp = service.login(request);
        return ResponseEntity.ok(resp);
    }
}
