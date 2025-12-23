package com.skiing.demo.DTOs.UserReport;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UserReportDTO(
        @NotBlank(message = "Description must not be blank")
        String description,

        @NotNull(message = "Latitude is required")
        @Digits(integer = 2, fraction = 8,
                message = "Latitude must have up to 2 integer digits and 8 decimal places")
        BigDecimal latitude,

        @NotNull(message = "Longitude is required")
        @Digits(integer = 3, fraction = 8,
                message = "Longitude must have up to 3 integer digits and 8 decimal places")
        BigDecimal longitude

) {
}
