package com.skiing.demo.repo;

import com.skiing.demo.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    List<Route> getAllByDifficultyLevel(String difficultyLevel);

    @Query("""
    SELECT r.name,
           COUNT(t) * 1.0 / COUNT(DISTINCT DATE(t.createdAt))
    FROM TrainingSession t
    JOIN t.route r
    WHERE t.status = 'FINISHED'
    GROUP BY r.name
""")
    List<Object[]> getAverageActivityPerRoute();
}
