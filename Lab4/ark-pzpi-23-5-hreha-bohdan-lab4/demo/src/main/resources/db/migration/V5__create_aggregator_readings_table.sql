CREATE TABLE aggregator_readings (
    id               SERIAL PRIMARY KEY,
    aggregator_id    VARCHAR(100) NOT NULL,
    timestamp        TIMESTAMP NOT NULL,
    overall_status   VARCHAR(50) NOT NULL,

    temp_average     DECIMAL(5, 2),
    temp_status      VARCHAR(50),

    wind_average     DECIMAL(5, 2),
    wind_status      VARCHAR(50),

    humidity_average DECIMAL(5, 2),
    humidity_status  VARCHAR(50),

    pressure_average DECIMAL(7, 2),
    pressure_status  VARCHAR(50),

    sensors_count    INT,
    sensors_list     VARCHAR(50),

    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
