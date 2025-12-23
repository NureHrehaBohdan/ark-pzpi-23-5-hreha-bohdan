package com.skiing.demo.repo;

import com.skiing.demo.model.AggregatorReading;
import com.skiing.demo.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregatorReadingRepository extends JpaRepository<AggregatorReading, Integer> {
}
