package com.skiing.demo.service;

import com.skiing.demo.DTOs.User.AuthResponce;
import com.skiing.demo.DTOs.User.LoginRequest;
import com.skiing.demo.DTOs.User.RegisterRequest;
import com.skiing.demo.model.User;
import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.repo.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public ApiResponse<Void> register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setAdmin(false);

        userRepository.save(user);

        return new ApiResponse<>(true, "User registered", null);
    }

    public ApiResponse<Void> createAdmin(RegisterRequest user) {
        User admin = new User();
        admin.setUsername(user.username());
        admin.setEmail(user.email());
        admin.setAdmin(true);
        admin.setPassword(passwordEncoder.encode(user.password()));

        userRepository.save(admin);

        return ApiResponse.Success();
    }

    public ApiResponse<AuthResponce> login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            String token = jwtService.generateToken(user);
            AuthResponce response = new AuthResponce(user.getId(), token);

            return new ApiResponse<>(true, "User logged in", response);

        } catch (BadCredentialsException | UsernameNotFoundException e) {
            return new ApiResponse<>(false, "Invalid email or password", null);
        }

    }


}