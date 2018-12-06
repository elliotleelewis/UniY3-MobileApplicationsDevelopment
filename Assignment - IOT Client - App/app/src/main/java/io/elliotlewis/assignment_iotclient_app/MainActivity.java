package io.elliotlewis.assignment_iotclient_app;

import android.content.*;
import android.support.design.widget.*;
import android.support.v7.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;

import com.google.gson.*;
import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;

import io.elliotlewis.assignment_iotclient_app.connections.*;
import io.elliotlewis.assignment_iotclient_app.models.AccessLog;
import io.elliotlewis.assignment_iotclient_app.services.*;

public class MainActivity extends AppCompatActivity {

	public static final String USER_ID = "15083802";
	public static final String SERVER_URL = "http://10.0.2.2:8080";
	public static final String BROKER_URL = "tcp://iot.eclipse.org:1883";
	public static final String TOPIC_DOOR = USER_ID + "/door";
	public static MqttConnection mqtt;
	public static ServerConnection server;
	private Intent mqttNotificationService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		try {
			mqtt = new MqttConnection(BROKER_URL, USER_ID);
		} catch (MqttException e) {
			e.printStackTrace();
		}
		server = new ServerConnection(SERVER_URL);

		mqttNotificationService = new Intent(this, MqttNotificationService.class);
		startService(mqttNotificationService);

		init();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(mqttNotificationService);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId()) {
			case R.id.action_refresh:
				refreshLogs();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void init() {
		final TextInputEditText tagText = findViewById(R.id.tag);
		final TextInputEditText doorText = findViewById(R.id.door);
		Button activateDoorButton = findViewById(R.id.activate_door);
		Button openDoorButton = findViewById(R.id.open_door);
		Button closeDoorButton = findViewById(R.id.close_door);

		refreshLogs();

		activateDoorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int doorId = Integer.valueOf(doorText.getText().toString());
				String rfidTag = tagText.getText().toString();
				if (doorId > 0 && rfidTag.length() > 0) {
					activateDoor(doorId, rfidTag);
				}
				else {
					toastInvalidInput();
				}
			}
		});
		openDoorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int doorId = Integer.valueOf(doorText.getText().toString());
				String rfidTag = tagText.getText().toString();
				if (doorId > 0 && rfidTag.length() > 0) {
					openDoor(doorId, rfidTag);
				}
				else {
					toastInvalidInput();
				}
			}
		});
		closeDoorButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int doorId = Integer.valueOf(doorText.getText().toString());
				String rfidTag = tagText.getText().toString();
				if (doorId > 0 && rfidTag.length() > 0) {
					closeDoor(doorId, rfidTag);
				}
				else {
					toastInvalidInput();
				}
			}
		});
	}

	private void refreshLogs() {
		ListView accessDataList = findViewById(R.id.access_data);
		try {
			AccessLog[] accessLogs = server.getLogs();
			ArrayAdapter<AccessLog> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, accessLogs);
			accessDataList.setAdapter(adapter);
		} catch (IOException e) {
			Toast.makeText(getApplicationContext(), getString(R.string.toast_unable_logs), Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	private void activateDoor(final int doorId, String rfidTag) {
		JsonObject json = new JsonObject();
		json.addProperty("action", "open");
		json.addProperty("door_id", 1);
		try {
			mqtt.publish(TOPIC_DOOR, json.toString());
			(new Handler()).postDelayed(new Runnable() {
				@Override
				public void run() {
					JsonObject json = new JsonObject();
					json.addProperty("action", "close");
					json.addProperty("door_id", doorId);
					try {
						mqtt.publish(TOPIC_DOOR, json.toString());
					} catch (MqttException e) {
						e.printStackTrace();
					}
				}
			}, 3000);
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private void openDoor(int doorId, String rfidTag) {
		JsonObject json = new JsonObject();
		json.addProperty("action", "open");
		json.addProperty("door_id", doorId);
		try {
			mqtt.publish(TOPIC_DOOR, json.toString());
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private void closeDoor(int doorId, String rfidTag) {
		JsonObject json = new JsonObject();
		json.addProperty("action", "close");
		json.addProperty("door_id", doorId);
		try {
			mqtt.publish(TOPIC_DOOR, json.toString());
		} catch (MqttException e) {
			e.printStackTrace();
		}
	}

	private void toastInvalidInput() {
		Toast.makeText(getApplicationContext(), getString(R.string.toast_invalid_input), Toast.LENGTH_LONG).show();
	}
}
