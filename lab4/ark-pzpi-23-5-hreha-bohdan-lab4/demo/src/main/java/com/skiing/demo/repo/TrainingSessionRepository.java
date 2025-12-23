package com.skiing.demo.repo;

import com.skiing.demo.DTOs.Statistic.LoadPoint;
import com.skiing.demo.model.TrainingSession;
import com.skiing.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrainingSessionRepository extends JpaRepository<TrainingSession, Integer> {
    List<TrainingSession> findAllByUser(User user);

    List<TrainingSession> findAllByUser_(User user);

    List<TrainingSession> findAllByUser_Id(int userId);

    List<TrainingSession> getAllByRoute_Id(int routeId);

    @Query(value = """
    SELECT date_trunc('hour', t.created_at) AS time,
           COUNT(*) AS sessions
    FROM training_sessions t
    WHERE t.status = 'FINISHED'
    GROUP BY date_trunc('hour', t.created_at)
    ORDER BY date_trunc('hour', t.created_at)
""", nativeQuery = true)
    List<Object[]> getHourlyLoad();


    @Query("""
    SELECT
        EXTRACT(HOUR FROM t.createdAt),
        COUNT(t)
    FROM TrainingSession t
    WHERE t.status = 'FINISHED'
    GROUP BY EXTRACT(HOUR FROM t.createdAt)
    ORDER BY EXTRACT(HOUR FROM t.createdAt)
""")
    List<Object[]> getSessionsByHour();

}
