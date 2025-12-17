package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.User.UserDTO;
import com.skiing.demo.exception.DataNotFoundException;
import com.skiing.demo.model.User;
import com.skiing.demo.repo.UserRepository;
import com.skiing.demo.service.interfaces.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserService implements  IUserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<UserDTO> getUserById(int id) {
        User user =  userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));
        return new ApiResponse<>(true,"success",
                new UserDTO(
                user.getUsername(),
                user.getEmail(),
                user.getEmergencyPhone())
        );
    }

    @Override
    public ApiResponse<Void> updateUser(UserDTO user, int id) {
        User userDetails = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));

        userDetails.setUsername(user.username());
        userDetails.setEmail(user.email());
        userDetails.setEmergencyPhone(user.emergencyPhone());

        userRepository.save(userDetails);

        return new ApiResponse<>(true, "successfully updated", null);
    }



}
