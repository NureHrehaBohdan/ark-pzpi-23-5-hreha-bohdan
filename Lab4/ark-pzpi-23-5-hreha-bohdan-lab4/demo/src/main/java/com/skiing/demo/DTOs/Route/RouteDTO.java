package com.skiing.demo.DTOs.Route;

import com.skiing.demo.enums.Difficulty;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RouteDTO(
        @NotBlank
        String name,


        Difficulty difficulty,

        @NotNull
        @Digits(integer = 3, fraction = 2)
        BigDecimal lengthKm,

        @NotNull
        int heightDrop
) {
}
