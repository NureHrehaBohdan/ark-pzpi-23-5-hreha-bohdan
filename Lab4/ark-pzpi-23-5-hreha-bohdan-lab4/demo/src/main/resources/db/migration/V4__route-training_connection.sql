ALTER TABLE training_sessions
ADD COLUMN route_id INT NOT NULL;

ALTER TABLE training_sessions
ADD CONSTRAINT fk_route
FOREIGN KEY (route_id) REFERENCES routes(id);