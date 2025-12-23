import paho.mqtt.client as mqtt
import json
import os
import time
from datetime import datetime
from collections import defaultdict
from statistics import mean
import requests


#  MQTT CONFIG 

BROKER_HOST = os.getenv("BROKER_HOST", "mosquitto")
BROKER_PORT = int(os.getenv("BROKER_PORT", "1883"))
AGGREGATOR_ID = os.getenv("AGGREGATOR_ID", "aggregator_001")

INPUT_TOPIC = "sensors/+/data"
OUTPUT_TOPIC = "aggregator/weather/status"


#  AGGREGATOR 

class WeatherAggregator:

    def __init__(self, aggregator_id):
        self.aggregator_id = aggregator_id

        #  MQTT 
        self.client = mqtt.Client(client_id=aggregator_id)
        self.client.on_connect = self.on_connect
        self.client.on_disconnect = self.on_disconnect
        self.client.on_message = self.on_message

        self.CONFIG_TOPIC = f"config/aggregator/{self.aggregator_id}"
        self.client.message_callback_add(self.CONFIG_TOPIC, self.on_config_message)

        #  STORAGE 
        self.sensor_readings = defaultdict(list)

        #  REST 
        self.REST_API_URL = os.getenv(
            "REST_API_URL",
            "http://host.docker.internal:8080/api/aggregator/save"
        )
        self.default_rest_api_url = self.REST_API_URL
        #  DEFAULTS 
        self.default_batch_interval = 30

        self.default_temperature_thresholds = {
            "optimal": (-10, 0),
            "warning": (-20, 15)
        }

        self.default_wind_thresholds = {
            "optimal": 15,
            "warning": 25
        }

        self.default_humidity_thresholds = {
            "optimal": (40, 80),
            "warning": (30, 90)
        }

        self.default_pressure_thresholds = {
            "optimal": (1000, 1020),
            "warning": (990, 1030)
        }

        #  ACTIVE CONFIG 
        self.reset_to_defaults()

        #  TIME 
        self.last_publish_time = time.time()


    #  RESET 

    def reset_to_defaults(self):
        self.BATCH_INTERVAL = self.default_batch_interval
        self.temperature_thresholds = self.default_temperature_thresholds.copy()
        self.wind_thresholds = self.default_wind_thresholds.copy()
        self.humidity_thresholds = self.default_humidity_thresholds.copy()
        self.pressure_thresholds = self.default_pressure_thresholds.copy()
        self.REST_API_URL = self.default_rest_api_url
        print(f"[{self.aggregator_id}] RESET_TO_DEFAULTS applied")


    #  MQTT CALLBACKS 

    def on_connect(self, client, userdata, flags, rc):
        if rc == 0:
            print(f"[{self.aggregator_id}] Connected to {BROKER_HOST}:{BROKER_PORT}")
            self.client.subscribe(INPUT_TOPIC, qos=1)
            self.client.subscribe(self.CONFIG_TOPIC, qos=1)
            print(f"[{self.aggregator_id}] Subscribed to {INPUT_TOPIC}")
            print(f"[{self.aggregator_id}] Subscribed to {self.CONFIG_TOPIC}")
        else:
            print(f"[{self.aggregator_id}] Connection error {rc}")

    def on_disconnect(self, client, userdata, rc):
        print(f"[{self.aggregator_id}] Disconnected ({rc})")


    def on_message(self, client, userdata, msg):
        try:
            payload = json.loads(msg.payload.decode())
            sensor_id = payload.get("sensor_id")

            self.sensor_readings[sensor_id].append(payload)

            print(
                f"[{self.aggregator_id}] Sensor {sensor_id}: "
                f"T={payload['temperature']} "
                f"H={payload['humidity']} "
                f"W={payload['wind_speed']}"
            )

            now = time.time()
            if now - self.last_publish_time >= self.BATCH_INTERVAL:
                self.aggregate_and_publish()
                self.last_publish_time = now

        except Exception as e:
            print(f"[{self.aggregator_id}] Sensor message error: {e}")


    def on_config_message(self, client, userdata, msg):
        try:
            payload = json.loads(msg.payload.decode())
            print(f"[{self.aggregator_id}] ⚙ CONFIG RECEIVED: {payload}")

            if payload.get("RESET_TO_DEFAULTS") is True:
                self.reset_to_defaults()
                return

            if "BATCH_INTERVAL" in payload:
                self.BATCH_INTERVAL = int(payload["BATCH_INTERVAL"])
                print(f"[{self.aggregator_id}] BATCH_INTERVAL = {self.BATCH_INTERVAL}")

            if "temperature_thresholds" in payload:
                self.temperature_thresholds = {
                    "optimal": tuple(payload["temperature_thresholds"]["optimal"]),
                    "warning": tuple(payload["temperature_thresholds"]["warning"])
                }

            if "wind_thresholds" in payload:
                self.wind_thresholds.update(payload["wind_thresholds"])

            if "humidity_thresholds" in payload:
                self.humidity_thresholds = {
                    "optimal": tuple(payload["humidity_thresholds"]["optimal"]),
                    "warning": tuple(payload["humidity_thresholds"]["warning"])
                }

            if "pressure_thresholds" in payload:
                self.pressure_thresholds = {
                    "optimal": tuple(payload["pressure_thresholds"]["optimal"]),
                    "warning": tuple(payload["pressure_thresholds"]["warning"])
                }

            if "REST_API_URL" in payload:
                self.REST_API_URL = payload["REST_API_URL"]

        except Exception as e:
            print(f"[{self.aggregator_id}] Config error: {e}")


    #  STATUS LOGIC 

    def get_range_status(self, value, thresholds):
        opt_min, opt_max = thresholds["optimal"]
        warn_min, warn_max = thresholds["warning"]

        if opt_min <= value <= opt_max:
            return "GOOD", 100
        elif warn_min <= value <= warn_max:
            return "WARNING", 70
        else:
            return "BAD", 0

    def get_wind_status(self, wind):
        if wind <= self.wind_thresholds["optimal"]:
            return "GOOD", 100
        elif wind <= self.wind_thresholds["warning"]:
            return "WARNING", 70
        else:
            return "BAD", 0


    #  AGGREGATION 

    def aggregate_and_publish(self):
        if not self.sensor_readings:
            return

        print(f"[{self.aggregator_id}] Aggregating...")

        temps, winds, hums, press = [], [], [], []

        for readings in self.sensor_readings.values():
            for r in readings:
                temps.append(r["temperature"])
                winds.append(r["wind_speed"])
                hums.append(r["humidity"])
                press.append(r["pressure"])

        avg_temp = round(mean(temps), 1)
        avg_wind = round(mean(winds), 1)
        avg_hum = round(mean(hums), 1)
        avg_press = round(mean(press), 1)

        temp_status, ts = self.get_range_status(avg_temp, self.temperature_thresholds)
        wind_status, ws = self.get_wind_status(avg_wind)
        hum_status, hs = self.get_range_status(avg_hum, self.humidity_thresholds)
        press_status, ps = self.get_range_status(avg_press, self.pressure_thresholds)

        overall_score = (ts + ws + hs + ps) / 4
        overall_status = (
            "EXCELLENT" if overall_score >= 95 else
            "GOOD" if overall_score >= 70 else
            "WARNING" if overall_score >= 50 else
            "BAD"
        )

        result = {
            "aggregator_id": self.aggregator_id,
            "timestamp": datetime.now().isoformat(),
            "overall_status": overall_status,
            "temperature": {"average": avg_temp, "status": temp_status},
            "wind_speed": {"average": avg_wind, "status": wind_status},
            "humidity": {"average": avg_hum, "status": hum_status},
            "pressure": {"average": avg_press, "status": press_status},
            "sensors_count": len(self.sensor_readings),
            "sensors": list(self.sensor_readings.keys())
        }

        self.client.publish(OUTPUT_TOPIC, json.dumps(result), qos=1)
        print(f"[{self.aggregator_id}] Published: {overall_status}")

        self.send_to_rest_api(result)
        self.sensor_readings.clear()


    #  REST 

    def send_to_rest_api(self, data):
        try:
            r = requests.post(self.REST_API_URL, json=data, timeout=5)
            print(f"[{self.aggregator_id}] REST status {r.status_code}")
        except Exception as e:
            print(f"[{self.aggregator_id}] REST error: {e}")


    #  MAIN LOOP 

    def run(self):
        print(f"[{self.aggregator_id}] Starting aggregator...")
        self.client.connect(BROKER_HOST, BROKER_PORT, 60)
        self.client.loop_start()

        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            print(f"[{self.aggregator_id}] Stopped")
        finally:
            self.client.loop_stop()
            self.client.disconnect()


# ENTRY 

if __name__ == "__main__":
    WeatherAggregator(AGGREGATOR_ID).run()
