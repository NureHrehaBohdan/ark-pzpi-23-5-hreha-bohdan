package com.skiing.demo.controllers.user;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.User.UserDTO;
import com.skiing.demo.service.UserService;
import com.skiing.demo.service.interfaces.IUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    private final IUserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById( @PathVariable int id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping()
    public ResponseEntity<ApiResponse<Void>> updateUser(@RequestBody UserDTO user, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return ResponseEntity.ok(userService.updateUser(user, userId));
    }
}
