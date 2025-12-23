package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.db.BackupInfo;
import com.skiing.demo.DTOs.db.CreateBackupResponse;
import com.skiing.demo.service.interfaces.IBackupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/backup")
public class AdminBackupController {

    private final IBackupService backupService;

    public AdminBackupController(IBackupService backupService) {
        this.backupService = backupService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateBackupResponse>> createBackup(){
        return ResponseEntity.ok(backupService.createBackup());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BackupInfo>>> getBackups(){
        return  ResponseEntity.ok(backupService.getAllBackups());
    }

    @DeleteMapping("/{filename}")
    public ResponseEntity<ApiResponse<BackupInfo>> deleteBackup(@PathVariable  String filename){
        return ResponseEntity.ok(backupService.deleteBackup(filename));
    }

    @PostMapping("/{filename}/restore")
    public ResponseEntity<ApiResponse<String>> restoreBackup(@PathVariable String filename){
        return ResponseEntity.ok(backupService.restoreBackup(filename));
    }
}
