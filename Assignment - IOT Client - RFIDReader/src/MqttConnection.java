import org.eclipse.paho.client.mqttv3.*;

class MqttConnection
{
	private MqttClient mqttClient;

	MqttConnection(String brokerUrl, String clientId) throws MqttException
	{
		this.mqttClient = new MqttClient(brokerUrl, clientId + "-rfid");
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setCleanSession(false);
		mqttOptions.setWill(mqttClient.getTopic(clientId + "/LWT"), "I'm gone :(".getBytes(), 0, false);
		this.mqttClient.connect(mqttOptions);
	}

	void publish(String topic, String json) throws MqttException
	{
		MqttTopic mqttTopic = this.mqttClient.getTopic(topic);
		mqttTopic.publish(new MqttMessage(json.getBytes()));
	}
}
