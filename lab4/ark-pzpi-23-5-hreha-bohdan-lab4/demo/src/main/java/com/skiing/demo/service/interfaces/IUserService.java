package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.User.RegisterRequest;
import com.skiing.demo.DTOs.User.UserDTO;
import com.skiing.demo.model.TrainingSession;

public interface IUserService {
    ApiResponse<UserDTO> getUserById(int id);
    ApiResponse<Void> updateUser(UserDTO user, int id);

}
