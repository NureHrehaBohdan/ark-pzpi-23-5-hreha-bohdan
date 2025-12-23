package com.skiing.demo.DTOs.User;

import jakarta.persistence.Column;

public record UserDTO(

        @Column(length = 50, nullable = false)
        String username,

        @Column(length = 100, nullable = false)
        String email,

        @Column(length = 20)
        String emergencyPhone
) {
}
