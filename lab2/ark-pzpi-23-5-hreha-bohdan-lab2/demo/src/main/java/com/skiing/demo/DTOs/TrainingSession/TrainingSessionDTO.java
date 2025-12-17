package com.skiing.demo.DTOs.TrainingSession;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TrainingSessionDTO(
        @NotNull(message = "User ID is required")
        Integer userId,

        @NotNull(message = "Start time is required")
        LocalDateTime startTime,

        @NotNull
        Integer routeId,

        @DecimalMin(value = "0.0", inclusive = true, message = "Distance must be non-negative")
        @Digits(integer = 3, fraction = 2, message = "Distance format invalid (max 3 digits, 2 fraction)")
        BigDecimal distanceKm,

        @DecimalMin(value = "0.0", inclusive = true, message = "Average speed must be non-negative")
        @Digits(integer = 3, fraction = 2, message = "Average speed format invalid (max 3 digits, 2 fraction)")
        BigDecimal avgSpeedKmh,

        @NotBlank(message = "Status is required")
        @Size(max = 20, message = "Status must be at most 20 characters")
        String status
) {
}
