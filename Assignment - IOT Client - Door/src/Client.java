import com.google.gson.*;
import com.phidget22.*;
import java.nio.charset.*;
import org.eclipse.paho.client.mqttv3.*;

public class Client
{
	private static final GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
	private static final Gson gson = gsonBuilder.create();
	private static final int DOOR_ID = 1;
	private static final String USER_ID = "15083802";
	private static final String SERVER_URL = "http://localhost:8080";
	private static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	private static final String TOPIC_DOOR = USER_ID + "/door";
	static MqttConnection mqtt;
	static ServerConnection server;

	private Client() throws PhidgetException, MqttException
	{
		System.out.println("Starting up...");
		mqtt = new MqttConnection(BROKER_URL, USER_ID);
		server = new ServerConnection(SERVER_URL);
		System.out.println("Initializing Door");
		Door door = new Door(DOOR_ID);
		mqtt.subscribe(TOPIC_DOOR, new IMqttMessageListener() {
			@Override
			public void messageArrived(String s, MqttMessage mqttMessage) throws Exception
			{
				String response = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
				JsonObject object = (new JsonParser()).parse(response).getAsJsonObject();
				switch(object.get("action").getAsString()) {
					case "open":
						door.open(object.get("door_id").getAsInt());
						break;
					case "close":
						door.close(object.get("door_id").getAsInt());
						break;
					default:
						System.out.println("WARNING: Unrecognized action: " + object.get("action"));
						break;
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run()
			{
				System.out.println("DISCONNECTING");
				try {
					door.disconnect();
				}
				catch(PhidgetException e) {
					e.printStackTrace();
				}
			}
		});
		System.out.println("ONLINE!");
		sleep(60);
	}

	static void sleep(int seconds)
	{
		try {
			Thread.sleep(seconds * 1000);
		}
		catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		try {
			new Client();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
