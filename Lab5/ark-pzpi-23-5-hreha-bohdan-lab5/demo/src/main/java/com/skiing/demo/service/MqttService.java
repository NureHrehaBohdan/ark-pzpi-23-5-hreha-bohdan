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

    String brokerHost = System.getenv().getOrDefault("BROKER_HOST", "mosquitto-broker");
    int brokerPort = Integer.parseInt(System.getenv().getOrDefault("BROKER_PORT", "1883"));

    public MqttService() {
        try {
            client = new MqttClient("tcp://" + brokerHost + ":" + brokerPort, MqttClient.generateClientId());
            client.connect();
            connected = true;
        } catch (MqttException e) {
            System.err.println(e.getMessage());
            connected = false;
        }
    }

    public void publish(String topic, String payload) {
        if (!connected || client == null) {
            return;
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