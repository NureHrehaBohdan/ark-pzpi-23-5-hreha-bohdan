package com.skiing.demo.repo;

import com.skiing.demo.model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface UserReportRepository extends JpaRepository<UserReport, Integer> {
    List<UserReport> getUserReportsByUser_Id(int userId);

    List<UserReport> findByCreatedAtBetween(LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);
}
