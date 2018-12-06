import com.google.gson.JsonObject;

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

	void open(int doorId, boolean success) throws IOException
	{
		JsonObject requestData = new JsonObject();
		requestData.addProperty("door_id", doorId);
		requestData.addProperty("success", success);
		HttpURLConnection conn = setupPostConnection("/open", requestData);
		conn.getResponseCode();
	}

	void close(int doorId, boolean success) throws IOException
	{
		JsonObject requestData = new JsonObject();
		requestData.addProperty("door_id", doorId);
		requestData.addProperty("success", success);
		HttpURLConnection conn = setupPostConnection("/close", requestData);
		conn.getResponseCode();
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
