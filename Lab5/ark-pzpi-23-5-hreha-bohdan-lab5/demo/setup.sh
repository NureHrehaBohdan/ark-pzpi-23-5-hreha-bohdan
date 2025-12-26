#!/bin/bash

# Check Docker 
if ! command -v docker &> /dev/null; then
    echo "Docker is not installed."
    echo "Please download it from: https://www.docker.com/get-started"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "docker-compose is not installed."
    echo "Please install it: https://docs.docker.com/compose/install/"
    exit 1
fi

echo "Docker and docker-compose found"

ENV_FILE=".env"

# Defaults
DEFAULT_DB_NAME="ski_resort"
DEFAULT_DB_USER="postgres"
DEFAULT_DB_PASSWORD="postgres"
DEFAULT_BACKEND_PORT="8080"

# Load existing env if present
if [ -f "$ENV_FILE" ]; then
    echo "Loading existing .env values..."
    source "$ENV_FILE"
fi

echo
echo "Configuration (press Enter to keep current/default value)"
echo "--------------------------------------------------------"

read -p "Database name [$POSTGRES_DB]: " INPUT_DB_NAME
DB_NAME=${INPUT_DB_NAME:-${POSTGRES_DB:-$DEFAULT_DB_NAME}}

read -p "Database user [$POSTGRES_USER]: " INPUT_DB_USER
DB_USER=${INPUT_DB_USER:-${POSTGRES_USER:-$DEFAULT_DB_USER}}

# password input
if [ -n "$DB_PASSWORD" ]; then
    PASSWORD_HINT="press Enter to keep current"
else
    PASSWORD_HINT=""
fi

read -s -p "Database password $PASSWORD_HINT: " INPUT_DB_PASSWORD
echo
DB_PASSWORD=${INPUT_DB_PASSWORD:-${DB_PASSWORD:-$DEFAULT_DB_PASSWORD}}

read -p "Backend port [$BACKEND_PORT]: " INPUT_BACKEND_PORT
BACKEND_PORT=${INPUT_BACKEND_PORT:-${BACKEND_PORT:-$DEFAULT_BACKEND_PORT}}

# Save .env
cat > "$ENV_FILE" <<EOL
POSTGRES_DB=$DB_NAME
POSTGRES_USER=$DB_USER
DB_PASSWORD=$DB_PASSWORD
BACKEND_PORT=$BACKEND_PORT
EOL

echo
echo ".env file created/updated"
echo "Configuration summary:"
echo "  POSTGRES_DB=$DB_NAME"
echo "  POSTGRES_USER=$DB_USER"
echo "  DB_PASSWORD=******"
echo "  BACKEND_PORT=$BACKEND_PORT"

echo
echo "Starting containers..."
docker-compose up -d --build

echo "Done"
