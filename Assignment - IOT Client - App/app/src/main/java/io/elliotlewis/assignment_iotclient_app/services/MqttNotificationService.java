package io.elliotlewis.assignment_iotclient_app.services;

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.*;

import com.google.gson.*;

import org.eclipse.paho.client.mqttv3.*;

import java.nio.charset.StandardCharsets;

import io.elliotlewis.assignment_iotclient_app.MainActivity;
import io.elliotlewis.assignment_iotclient_app.R;
import io.elliotlewis.assignment_iotclient_app.connections.*;

import static io.elliotlewis.assignment_iotclient_app.MainActivity.*;

public class MqttNotificationService extends Service {
	private static final String CHANNEL_ID = "IOT_CLIENT_MQTT";

	MqttConnection mqtt;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		createNotificationChannel();
		mqtt = MainActivity.mqtt;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			mqtt.subscribe(TOPIC_DOOR, new IMqttMessageListener() {
				@Override
				public void messageArrived(String topic, MqttMessage message) throws Exception {
					String response = new String(message.getPayload(), StandardCharsets.UTF_8);
					JsonObject object = (new JsonParser()).parse(response).getAsJsonObject();
					switch (object.get("action").getAsString()) {
						case "open":
							break;
						case "close":
							break;
						case "unauthorized_access":
							int doorId = object.get("door_id").getAsInt();
							String rfidTag = object.get("rfid_tag").getAsString();
							createUnauthorizedAccessNotification(doorId, rfidTag);
							break;
					}
				}
			});
		} catch (MqttException e) {
			e.printStackTrace();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	private void createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			CharSequence name = getString(R.string.channel_name);
			String description = getString(R.string.channel_description);
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	private void createUnauthorizedAccessNotification(int doorId, String rfidTag) {
		String title = getString(R.string.notification_unauthorized_access_title);
		String text = getString(R.string.notification_unauthorized_access_text, rfidTag, doorId);
		NotificationCompat.Builder unauthorizedAccessNotification = new NotificationCompat.Builder(this, CHANNEL_ID)
				.setSmallIcon(R.drawable.ic_lock_24dp)
				.setContentTitle(title)
				.setContentText(text)
				.setPriority(NotificationCompat.PRIORITY_DEFAULT);
		NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
		notificationManager.notify(123, unauthorizedAccessNotification.build());
	}
}
