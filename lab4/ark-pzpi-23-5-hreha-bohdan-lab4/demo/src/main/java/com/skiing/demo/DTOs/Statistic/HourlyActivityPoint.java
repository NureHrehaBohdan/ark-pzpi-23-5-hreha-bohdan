package com.skiing.demo.DTOs.Statistic;

public record HourlyActivityPoint(
        int hour,      // 0..23
        long sessions  // кількість сесій
) {
}
