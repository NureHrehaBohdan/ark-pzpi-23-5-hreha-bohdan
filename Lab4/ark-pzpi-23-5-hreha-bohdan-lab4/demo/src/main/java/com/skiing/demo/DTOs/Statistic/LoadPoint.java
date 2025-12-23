package com.skiing.demo.DTOs.Statistic;

import java.time.LocalDateTime;

public record LoadPoint(
        LocalDateTime time,
        Long  sessions
) {
}
