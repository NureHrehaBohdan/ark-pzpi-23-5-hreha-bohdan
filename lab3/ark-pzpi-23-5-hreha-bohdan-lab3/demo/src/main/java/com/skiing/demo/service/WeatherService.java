package com.skiing.demo.service;

import com.skiing.demo.DTOs.ApiResponse;
import com.skiing.demo.DTOs.Weather.WeatherDataDTO;
import com.skiing.demo.exception.DataNotFoundException;
import com.skiing.demo.model.Sensor;
import com.skiing.demo.model.WeatherData;
import com.skiing.demo.repo.SensorRepository;
import com.skiing.demo.repo.WeatherDataRepository;
import com.skiing.demo.service.interfaces.IWeatherService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherService implements IWeatherService {
    private final WeatherDataRepository weatherDataRepository;
    private final SensorRepository sensorRepository;

    public WeatherService(WeatherDataRepository weatherDataRepository, SensorRepository sensorRepository) {
        this.weatherDataRepository = weatherDataRepository;
        this.sensorRepository = sensorRepository;
    }

    @Override
    public ApiResponse<Void> addWeatherData(WeatherDataDTO dto) {
        Sensor sensor = sensorRepository.findById(dto.sensorId())
                .orElseThrow(() -> new DataNotFoundException("Sensor not found with id " + dto.sensorId()));

        WeatherData wd = new WeatherData();
        wd.setSensor(sensor);
        wd.setTemperature(dto.temperature());
        wd.setHumidity(dto.humidity());
        wd.setWindSpeed(dto.windSpeed());
        wd.setPressure(dto.pressure());
        wd.setStatus(dto.status());
        wd.setRecordedAt(dto.recordedAt() != null ? dto.recordedAt() : LocalDateTime.now());

        weatherDataRepository.save(wd);
        return new ApiResponse<>(true, "Weather data added", null);
    }

    @Override
    public ApiResponse<List<WeatherDataDTO>> getWeatherDataBySensorId(int sensorId) {
        if (!sensorRepository.existsById(sensorId)) {
            throw new DataNotFoundException("Sensor not found with id " + sensorId);
        }
        List<WeatherDataDTO> data = weatherDataRepository.findAllBySensor_Id(sensorId)
                .stream()
                .map(this::toDTO)
                .toList();

        return new ApiResponse<>(true, "Success", data);
    }

    @Override
    public ApiResponse<List<WeatherDataDTO>> getAllWeatherData() {
        List<WeatherDataDTO> data = weatherDataRepository.findAll()
                .stream()
                .map(this::toDTO)
                .toList();
        return new ApiResponse<>(true, "Success", data);
    }

    @Override
    public ApiResponse<WeatherDataDTO> getLatestBySensorId(int sensorId) {
        WeatherData latest = weatherDataRepository.findFirstBySensor_IdOrderByRecordedAtDesc(sensorId).
                orElseThrow(()-> new DataNotFoundException("Sensor not found with id " + sensorId));
        return new ApiResponse<>(true, "Success", toDTO(latest));
    }

    @Override
    public ApiResponse<List<WeatherDataDTO>> getAllLatest() {
        List<WeatherDataDTO> data = weatherDataRepository.findLatestForAllSensors()
                .stream()
                .map(this::toDTO)
                .toList();

        return new ApiResponse<>(true, "Success", data);
    }

    private WeatherDataDTO toDTO(WeatherData wd) {
        return new WeatherDataDTO(
                wd.getSensor().getId(),
                wd.getTemperature(),
                wd.getHumidity(),
                wd.getWindSpeed(),
                wd.getPressure(),
                wd.getStatus(),
                wd.getRecordedAt()
        );
    }
}
