package com.skiing.demo.DTOs.db;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BackupInfo (
    String filename,
    String size,
    LocalDateTime createdAt
){
}
