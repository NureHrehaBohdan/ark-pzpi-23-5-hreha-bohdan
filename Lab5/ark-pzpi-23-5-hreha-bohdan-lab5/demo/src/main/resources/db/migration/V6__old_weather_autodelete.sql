CREATE OR REPLACE FUNCTION cleanup_old_readings()
RETURNS TRIGGER AS $$
BEGIN
    -- Удаляем записи старше 30 дней при каждой новой вставке
    DELETE FROM aggregator_readings
    WHERE created_at < NOW() - INTERVAL '14 days';

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER cleanup_trigger
    AFTER INSERT ON aggregator_readings
                    FOR EACH STATEMENT
EXECUTE FUNCTION cleanup_old_readings();

CREATE INDEX IF NOT EXISTS idx_aggregator_readings_created_at
    ON aggregator_readings(created_at DESC);