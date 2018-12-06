package io.elliotlewis.assignment_iotclient_app.connections;

import org.eclipse.paho.client.mqttv3.*;

public class MqttConnection {
	private MqttClient mqttClient;

	public MqttConnection(String brokerUrl, String clientId) throws MqttException
	{
		this.mqttClient = new MqttClient(brokerUrl, clientId + "-app", null);
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setCleanSession(false);
		mqttOptions.setWill(mqttClient.getTopic(clientId + "/LWT"), "I'm gone :(".getBytes(), 0, false);
		this.mqttClient.connect(mqttOptions);
	}

	public void publish(String topic, String json) throws MqttException
	{
		MqttTopic mqttTopic = this.mqttClient.getTopic(topic);
		mqttTopic.publish(new MqttMessage(json.getBytes()));
	}

	public void subscribe(String topic, IMqttMessageListener listener) throws MqttException
	{
		this.mqttClient.subscribe(topic, listener);
	}
}
