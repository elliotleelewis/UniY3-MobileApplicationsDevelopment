import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;

class ServerConnection
{
	private String serverUrl;

	ServerConnection(String serverUrl)
	{
		this.serverUrl = serverUrl;
	}

	boolean scan(int doorId, String rfidTag) throws IOException
	{
		System.out.println("Scanning RFID for door: " + doorId + " tag: " + rfidTag);
		JsonObject requestData = new JsonObject();
		requestData.addProperty("door_id", doorId);
		requestData.addProperty("rfid_tag", rfidTag);
		HttpURLConnection conn = setupPostConnection("/scan", requestData);
		int responseCode = conn.getResponseCode();
		boolean authorized = responseCode == HttpURLConnection.HTTP_OK;
		System.out.println("Authorized: " + authorized);
		return authorized;
	}

	private HttpURLConnection setupPostConnection(String path, JsonObject body) throws IOException
	{
		URL url = new URL(this.serverUrl + path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoOutput(true);
		conn.setDoInput(true);
		OutputStream out = conn.getOutputStream();
		out.write(body.toString().getBytes(StandardCharsets.UTF_8));
		out.close();
		return conn;
	}
}
