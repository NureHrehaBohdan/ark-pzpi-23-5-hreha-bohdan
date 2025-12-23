package com.skiing.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "routes")
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(length = 100, nullable = false)
    private String name;

    @Column(name = "difficulty_level", length = 20)
    private String difficultyLevel;

    @Column(name = "length_km", precision = 5, scale = 2)
    private BigDecimal lengthKm;

    @Column(name = "height_drop")
    private Integer heightDrop;

}
