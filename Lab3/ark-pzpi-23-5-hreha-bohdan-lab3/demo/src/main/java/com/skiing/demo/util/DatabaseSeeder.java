package com.skiing.demo.util;

import com.skiing.demo.model.*;
import com.skiing.demo.repo.*;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DatabaseSeeder {

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final TrainingSessionRepository trainingSessionRepository;
    private final SensorRepository sensorRepository;
    private final WeatherDataRepository weatherDataRepository;

    public DatabaseSeeder(UserRepository userRepository,
                          RouteRepository routeRepository,
                          TrainingSessionRepository trainingSessionRepository,
                          SensorRepository sensorRepository,
                          WeatherDataRepository weatherDataRepository) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.trainingSessionRepository = trainingSessionRepository;
        this.sensorRepository = sensorRepository;
        this.weatherDataRepository = weatherDataRepository;
    }

    @PostConstruct
    public void seed() {
        weatherDataRepository.deleteAll();
        trainingSessionRepository.deleteAll();

        seedUsers();
        seedRoutes();
        seedSensors();
        seedTrainingSessions();
        seedWeatherData();
    }

    private void seedUsers() {
        if(userRepository.count() > 0) return;

        userRepository.saveAll(List.of(
                new User(0, "admin", "admin@ski.com", "hashedpass", null, true),
                new User(0, "user1", "user1@ski.com", "hashedpass", null, false),
                new User(0, "user2", "user2@ski.com", "hashedpass", null, false)
        ));
    }

    private void seedRoutes() {
        if(routeRepository.count() > 0) return;

        routeRepository.saveAll(List.of(
                new Route(0, "Easy slope", "EASY", BigDecimal.valueOf(1.2), 50),
                new Route(0, "Medium slope", "MEDIUM", BigDecimal.valueOf(2.5), 120),
                new Route(0, "Expert slope", "HARD", BigDecimal.valueOf(3.0), 200)
        ));

    }

    private void seedSensors() {
        if(sensorRepository.count() > 0) return;

        sensorRepository.saveAll(List.of(
                new Sensor(
                        0,
                        "Sensor1",
                        "Slope 1",                 // location
                        BigDecimal.valueOf(50.0),  // latitude
                        BigDecimal.valueOf(50.0),  // longitude
                        100                         // heightMeters
                ),
                new Sensor(
                        0,
                        "Sensor2",
                        "Slope 2",
                        BigDecimal.valueOf(60.0),
                        BigDecimal.valueOf(60.0),
                        200
                )
        ));

    }

    private void seedTrainingSessions() {
        if(trainingSessionRepository.count() > 0) return;

        Random rand = new Random();
        List<User> users = userRepository.findAll();
        List<Route> routes = routeRepository.findAll();

        List<TrainingSession> sessions = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for(int i=0; i<200; i++) {
            User user = users.get(rand.nextInt(users.size()));
            Route route = routes.get(rand.nextInt(routes.size()));

            LocalDateTime start = now.minusHours(rand.nextInt(48)); // последние 48 часов
            LocalDateTime end = start.plusMinutes(15 + rand.nextInt(90));

            BigDecimal lengthKm = route.getLengthKm();
            BigDecimal factor = BigDecimal.valueOf(0.5 + rand.nextDouble());
            BigDecimal distanceKm = lengthKm.multiply(factor);
            BigDecimal avgSpeed = BigDecimal.valueOf(10 + rand.nextDouble() * 30);

            sessions.add(new TrainingSession(
                    0,
                    user,
                    start,
                    end,
                    route,
                    distanceKm,
                    avgSpeed,
                    "FINISHED",
                    start
            ));

        }

        trainingSessionRepository.saveAll(sessions);
    }

    private void seedWeatherData() {
        if(weatherDataRepository.count() > 0) return;

        List<Sensor> sensors = sensorRepository.findAll();
        List<WeatherData> weatherList = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for(Sensor sensor : sensors) {
            for(int i = 0; i < 48; i++) {
                LocalDateTime recordTime = now.minusHours(i);

                //sinus function for data generation
                double sinus = 5 * Math.sin(2 * Math.PI * i / 24);
                double temp = 5 + sinus;
                double humidity = 50 + 20 * Math.sin(2 * Math.PI * i / 24 + Math.PI / 3);
                double windSpeed = 5 + 5 * Math.sin(2 * Math.PI * i / 24 + Math.PI / 2);
                double pressure = 1015 + sinus;

                weatherList.add(new WeatherData(
                        0,
                        sensor,
                        BigDecimal.valueOf(temp),
                        BigDecimal.valueOf(humidity),
                        BigDecimal.valueOf(windSpeed),
                        BigDecimal.valueOf(pressure),
                        "OK",
                        recordTime
                ));
            }
        }

        weatherDataRepository.saveAll(weatherList);
    }

}
