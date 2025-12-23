package com.skiing.demo.controllers.admin;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.model.UserReport;
import com.skiing.demo.service.interfaces.IUserReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/report")
public class AdminReportController {

    private final IUserReportService userReportService;

    public AdminReportController(IUserReportService userReportService) {
        this.userReportService = userReportService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<UserReport>>> getAllUserReports(@PathVariable int userId) {
        return ResponseEntity.ok(userReportService.getAllReportsByUserId(userId));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<UserReport>>> getTodayUserReports() {
        return ResponseEntity.ok(userReportService.getAllReportsToday());
    }

}
