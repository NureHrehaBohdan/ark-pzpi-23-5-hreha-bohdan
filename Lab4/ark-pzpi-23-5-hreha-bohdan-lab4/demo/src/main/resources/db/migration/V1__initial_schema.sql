CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    emergency_phone VARCHAR(20)
);

CREATE TABLE sensors (
     id SERIAL PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     location VARCHAR(100),
     latitude DECIMAL(10,8),
     longitude DECIMAL(11,8),
     height_meters INTEGER
);


CREATE TABLE weather_data (
    id SERIAL PRIMARY KEY,
    sensor_id INTEGER NOT NULL REFERENCES sensors(id) ON DELETE CASCADE,
    temperature DECIMAL(5,2),
    humidity DECIMAL(5,2),
    wind_speed DECIMAL(5,2),
    pressure DECIMAL(6,2),
    status VARCHAR(20),  -- SAFE, CAUTION, DANGEROUS
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE routes (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    difficulty_level VARCHAR(20),
    length_km DECIMAL(5,2),
    height_drop INTEGER
);


CREATE TABLE training_sessions (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    distance_km DECIMAL(5,2),
    avg_speed_kmh DECIMAL(5,2),
    status VARCHAR(20), -- ACTIVE, COMPLETED, ABANDONED
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_reports (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    description TEXT,
    latitude DECIMAL(10,8),
    longitude DECIMAL(11,8),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
