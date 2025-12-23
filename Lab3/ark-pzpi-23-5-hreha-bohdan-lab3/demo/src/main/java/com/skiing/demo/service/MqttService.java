package com.skiing.demo.service;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class MqttService {

    private MqttClient client;
    private boolean connected = false;

    public MqttService() {
        try {
            client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
            client.connect();
            connected = true;
        } catch (MqttException e) {
            System.err.println(e.getMessage());
            connected = false;
        }
    }

    public void publish(String topic, String payload) {
        if (!connected || client == null) {
            return; // Просто игнорируем если MQTT не подключен
        }

        try {
            MqttMessage msg = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
            msg.setQos(1);
            client.publish(topic, msg);
        } catch (MqttException e) {
            System.err.println(e.getMessage());
        }
    }

    public boolean isConnected() {
        return connected && client != null && client.isConnected();
    }
}