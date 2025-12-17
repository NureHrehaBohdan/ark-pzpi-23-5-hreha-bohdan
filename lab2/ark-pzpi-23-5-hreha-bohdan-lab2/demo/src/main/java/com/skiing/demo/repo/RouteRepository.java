package com.skiing.demo.repo;

import com.skiing.demo.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Integer> {
    List<Route> getAllByDifficultyLevel(String difficultyLevel);
}
