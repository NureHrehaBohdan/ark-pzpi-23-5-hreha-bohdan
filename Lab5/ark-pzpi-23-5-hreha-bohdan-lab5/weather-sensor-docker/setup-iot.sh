#!/bin/bash
set -e

# Docker check 
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed"
    echo "https://www.docker.com/get-started"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "docker-compose is not installed"
    echo "https://docs.docker.com/compose/install/"
    exit 1
fi

ENV_FILE=".env"

# defaults 
DEFAULT_BROKER_HOST="mosquitto"
DEFAULT_BROKER_PORT="1883"
DEFAULT_AGGREGATOR_ID="aggregator_001"
DEFAULT_BATCH_INTERVAL="30"
DEFAULT_REST_API_URL="http://host.docker.internal:8080/api/aggregator/save"
DEFAULT_SENSOR_1_ID="sensor_001"
DEFAULT_SENSOR_2_ID="sensor_002"

# load existing or defaults 
if [ -f "$ENV_FILE" ]; then
    source "$ENV_FILE"
else
    BROKER_HOST=$DEFAULT_BROKER_HOST
    BROKER_PORT=$DEFAULT_BROKER_PORT
    AGGREGATOR_ID=$DEFAULT_AGGREGATOR_ID
    BATCH_INTERVAL=$DEFAULT_BATCH_INTERVAL
    REST_API_URL=$DEFAULT_REST_API_URL
    SENSOR_1_ID=$DEFAULT_SENSOR_1_ID
    SENSOR_2_ID=$DEFAULT_SENSOR_2_ID
fi

echo " Weather system setup"
echo "Press Enter to accept the default value shown in [brackets]"

read -p "MQTT Broker host [$BROKER_HOST]: " input
BROKER_HOST=${input:-$BROKER_HOST}

read -p "MQTT Broker port [$BROKER_PORT]: " input
BROKER_PORT=${input:-$BROKER_PORT}

read -p "Aggregator ID [$AGGREGATOR_ID]: " input
AGGREGATOR_ID=${input:-$AGGREGATOR_ID}

read -p "Aggregator batch interval [$BATCH_INTERVAL]: " input
BATCH_INTERVAL=${input:-$BATCH_INTERVAL}

read -p "Aggregator REST API URL [$REST_API_URL]: " input
REST_API_URL=${input:-$REST_API_URL}

read -p "Sensor 1 ID [$SENSOR_1_ID]: " input
SENSOR_1_ID=${input:-$SENSOR_1_ID}

read -p "Sensor 2 ID [$SENSOR_2_ID]: " input
SENSOR_2_ID=${input:-$SENSOR_2_ID}

# save to .env
cat > "$ENV_FILE" <<EOL
BROKER_HOST=$BROKER_HOST
BROKER_PORT=$BROKER_PORT
AGGREGATOR_ID=$AGGREGATOR_ID
BATCH_INTERVAL=$BATCH_INTERVAL
REST_API_URL=$REST_API_URL
SENSOR_1_ID=$SENSOR_1_ID
SENSOR_2_ID=$SENSOR_2_ID
EOL

echo ".env file updated"

# start containers 
docker-compose up -d --build
echo "All containers are up"
docker-compose ps
