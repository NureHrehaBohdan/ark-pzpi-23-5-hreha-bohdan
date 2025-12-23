package com.skiing.demo.service.interfaces;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.UserReport.UserReportDTO;
import com.skiing.demo.model.UserReport;

import java.util.List;

public interface IUserReportService {
    ApiResponse<Void> addReport(UserReportDTO userReport, int userId);
    ApiResponse<List<UserReport>> getAllReportsByUserId(int userId);
    ApiResponse<List<UserReport>> getAllReportsToday();
}
