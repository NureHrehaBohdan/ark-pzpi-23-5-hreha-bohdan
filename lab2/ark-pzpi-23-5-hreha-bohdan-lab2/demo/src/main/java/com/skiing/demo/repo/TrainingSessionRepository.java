package com.skiing.demo.repo;

import com.skiing.demo.model.TrainingSession;
import com.skiing.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
    List<TrainingSession> findAllByUser(User user);

    List<TrainingSession> findAllByUser_(User user);

    List<TrainingSession> findAllByUser_Id(int userId);

    List<TrainingSession> getAllByRoute_Id(int routeId);
}
