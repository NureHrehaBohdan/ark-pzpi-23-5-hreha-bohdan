package com.skiing.demo.DTOs.db;

public record CreateBackupResponse(
        String filename,
        String path,
        float size
) {
}
