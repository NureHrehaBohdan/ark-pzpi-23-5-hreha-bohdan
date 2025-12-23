import paho.mqtt.client as mqtt
import json
import time
import random
import os
from datetime import datetime

# Configuration
BROKER_HOST = os.getenv('BROKER_HOST', 'mosquitto')
BROKER_PORT = int(os.getenv('BROKER_PORT', '1883'))
SENSOR_ID = os.getenv('SENSOR_ID', 'sensor_001')
TOPIC = f'sensors/{SENSOR_ID}/data'

# Base values (realistic for a ski resort)
BASE_TEMP = 0
BASE_HUMIDITY = 60
BASE_WIND = 10
BASE_PRESSURE = 1013

class WeatherSensor:
    def __init__(self, sensor_id):
        self.sensor_id = sensor_id
        self.client = mqtt.Client(client_id=sensor_id)
        self.client.on_connect = self.on_connect
        self.client.on_disconnect = self.on_disconnect
        self.client.on_publish = self.on_publish
        self.connected = False
        
    def on_connect(self, client, userdata, flags, rc):
        if rc == 0:
            self.connected = True
            print(f"[{self.sensor_id}] Connected to broker {BROKER_HOST}:{BROKER_PORT}")
        else:
            print(f"[{self.sensor_id}] Connection error. Code: {rc}")
    
    def on_disconnect(self, client, userdata, rc):
        self.connected = False
        if rc != 0:
            print(f"[{self.sensor_id}] Unexpected disconnect. Code: {rc}")
        else:
            print(f"[{self.sensor_id}] Disconnected from broker")
    
    def on_publish(self, client, userdata, mid):
        print(f"[{self.sensor_id}] Message published to topic: {TOPIC}")
    
    def generate_reading(self):
        """Generate realistic weather data"""
        temp = BASE_TEMP + random.uniform(-3, 3)
        humidity = BASE_HUMIDITY + random.uniform(-10, 10)
        wind = BASE_WIND + random.uniform(-5, 5)
        pressure = BASE_PRESSURE + random.uniform(-5, 5)
        
        # Clamp values to realistic limits
        temp = max(-15, min(15, temp))
        humidity = max(30, min(100, humidity))
        wind = max(0, wind)
        pressure = max(950, min(1050, pressure))
        
        return {
            'sensor_id': self.sensor_id,
            'timestamp': datetime.now().isoformat(),
            'temperature': round(temp, 1),
            'humidity': round(humidity, 1),
            'wind_speed': round(wind, 1),
            'pressure': round(pressure, 1)
        }
    
    def connect(self):
        """Connect to the broker"""
        print(f"[{self.sensor_id}] Connecting to {BROKER_HOST}:{BROKER_PORT}...")
        try:
            self.client.connect(BROKER_HOST, BROKER_PORT, keepalive=60)
            self.client.loop_start()
            time.sleep(2)
        except Exception as e:
            print(f"[{self.sensor_id}] Connection failed: {e}")
            return False
        return True
    
    def send_reading(self):
        """Send one measurement"""
        if not self.connected:
            print(f"[{self.sensor_id}] Not connected. Skipping sending.")
            return False
        
        reading = self.generate_reading()
        payload = json.dumps(reading)
        
        try:
            result = self.client.publish(TOPIC, payload, qos=1)
            if result.rc != mqtt.MQTT_ERR_SUCCESS:
                print(f"[{self.sensor_id}] Send error: {result.rc}")
                return False
            
            print(f"[{self.sensor_id}] Sent: {payload}")
            return True
        except Exception as e:
            print(f"[{self.sensor_id}] Exception while sending: {e}")
            return False
    
    def disconnect(self):
        """Disconnect from the broker"""
        self.client.loop_stop()
        self.client.disconnect()
    
    def run(self, interval=5, duration=None):
        """
        Start continuous sending of data
        
        Args:
            interval: send interval in seconds
            duration: total run time in seconds (None = infinite)
        """
        if not self.connect():
            return
        
        print(f"[{self.sensor_id}] Simulation started (interval: {interval} sec)")
        
        start_time = time.time()
        sent_count = 0
        
        try:
            while True:
                self.send_reading()
                sent_count += 1
                
                if duration and (time.time() - start_time) > duration:
                    print(f"[{self.sensor_id}] Finished. Sent {sent_count} messages.")
                    break
                
                time.sleep(interval)
        except KeyboardInterrupt:
            print(f"\n[{self.sensor_id}] Stopping...")
        finally:
            self.disconnect()
            print(f"[{self.sensor_id}] Total messages sent: {sent_count}")


if __name__ == '__main__':
    sensor = WeatherSensor(SENSOR_ID)
    sensor.run(interval=5)
