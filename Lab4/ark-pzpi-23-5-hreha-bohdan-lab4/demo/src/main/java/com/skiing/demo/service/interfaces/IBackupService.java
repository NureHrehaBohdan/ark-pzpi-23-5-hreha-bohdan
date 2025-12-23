package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.db.BackupInfo;
import com.skiing.demo.DTOs.db.CreateBackupResponse;

import java.util.List;

public interface IBackupService {

    ApiResponse<List<BackupInfo>> getAllBackups();
    ApiResponse<CreateBackupResponse> createBackup();
    ApiResponse<BackupInfo> deleteBackup(String filename);
    ApiResponse<String> restoreBackup(String filename);
}
