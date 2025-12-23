package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.UserReport.UserReportDTO;
import com.skiing.demo.exception.DataNotFoundException;
import com.skiing.demo.model.User;
import com.skiing.demo.model.UserReport;
import com.skiing.demo.repo.UserReportRepository;
import com.skiing.demo.repo.UserRepository;
import com.skiing.demo.service.interfaces.IUserReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserReportService implements IUserReportService {

    private final UserReportRepository userReportRepository;
    private final UserRepository userRepository;

    public UserReportService(UserReportRepository userReportRepository, UserRepository userRepository) {
        this.userReportRepository = userReportRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse<Void> addReport(UserReportDTO userReport, int userId) {
        UserReport report = new UserReport();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException(
                        "User not found with id " + userId
                ));
        report.setUser(user);
        report.setDescription(userReport.description());
        report.setLatitude(userReport.latitude());
        report.setLongitude(userReport.longitude());
        userReportRepository.save(report);
        return ApiResponse.Success();
    }

    @Override
    public ApiResponse<List<UserReport>> getAllReportsByUserId(int userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new DataNotFoundException("User not found");
        }
        return ApiResponse.Success(userReportRepository.getUserReportsByUser_Id(userId));
    }

    @Override
    public ApiResponse<List<UserReport>> getAllReportsToday() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        List<UserReport> reports = userReportRepository.findByCreatedAtBetween(startOfDay, endOfDay);

        if (reports.isEmpty()) {
            return ApiResponse.Success("No reports found today", List.of());
        }

        return ApiResponse.Success("Reports for today", reports);
    }


}
