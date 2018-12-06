import org.eclipse.paho.client.mqttv3.*;

class MqttConnection
{
	private MqttClient mqttClient;

	MqttConnection(String brokerUrl, String clientId) throws MqttException
	{
		this.mqttClient = new MqttClient(brokerUrl, clientId + "-door");
		MqttConnectOptions mqttOptions = new MqttConnectOptions();
		mqttOptions.setCleanSession(false);
		mqttOptions.setWill(mqttClient.getTopic(clientId + "/LWT"), "I'm gone :(".getBytes(), 0, false);
		this.mqttClient.connect(mqttOptions);
	}

	void subscribe(String topic, IMqttMessageListener listener) throws MqttException
	{
		this.mqttClient.subscribe(topic, listener);
	}
}
