import com.google.gson.*;
import com.phidget22.*;
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
		System.out.println("Initializing RFID Reader");
		RfidReader rfidReader = new RfidReader();
		rfidReader.addTagListener(new RFIDTagListener() {
			@Override
			public void onTag(RFIDTagEvent event)
			{
				try {
					String rfidTag = event.getTag();
					JsonObject json;
					if(server.scan(DOOR_ID, rfidTag)) {
						json = new JsonObject();
						json.addProperty("action", "open");
						json.addProperty("door_id", DOOR_ID);
						mqtt.publish(TOPIC_DOOR, json.toString());
						sleep(3);
						json = new JsonObject();
						json.addProperty("action", "close");
						json.addProperty("door_id", DOOR_ID);
						mqtt.publish(TOPIC_DOOR, json.toString());
					}
					else {
						json = new JsonObject();
						json.addProperty("action", "unauthorized_access");
						json.addProperty("door_id", DOOR_ID);
						json.addProperty("rfid_tag", rfidTag);
						mqtt.publish(TOPIC_DOOR, json.toString());
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run()
			{
				System.out.println("DISCONNECTING");
				try {
					rfidReader.disconnect();
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
