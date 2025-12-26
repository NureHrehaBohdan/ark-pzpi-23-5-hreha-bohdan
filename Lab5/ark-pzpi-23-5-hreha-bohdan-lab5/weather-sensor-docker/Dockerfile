FROM python:3.11-slim

WORKDIR /app

RUN pip install --no-cache-dir paho-mqtt==1.6.1

COPY sensor.py .

ENV BROKER_HOST=mosquitto
ENV BROKER_PORT=1883
ENV SENSOR_ID=sensor_001

CMD ["python", "-u", "sensor.py"]