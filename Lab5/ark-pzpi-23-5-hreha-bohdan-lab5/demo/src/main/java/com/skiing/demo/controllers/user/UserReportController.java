package com.skiing.demo.controllers.user;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.UserReport.UserReportDTO;
import com.skiing.demo.model.UserReport;
import com.skiing.demo.service.interfaces.IUserReportService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/reports")
public class UserReportController {

    private final IUserReportService userReportService;

    public UserReportController(IUserReportService userReportService) {
        this.userReportService = userReportService;
    }

    @PostMapping()
    public ApiResponse<Void> addUserReport(@RequestBody @Valid UserReportDTO userReport, Authentication authentication) {
        Integer userId = (Integer) authentication.getPrincipal();
        return userReportService.addReport(userReport,userId);
    }
}
