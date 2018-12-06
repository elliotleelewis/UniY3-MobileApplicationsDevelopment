package io.elliotlewis.assignment_iotclient_app.connections;

import android.os.StrictMode;

import com.google.gson.*;
import java.io.*;
import java.net.*;
import java.nio.charset.*;

import io.elliotlewis.assignment_iotclient_app.models.*;

public class ServerConnection
{
	private static final GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
	private static final Gson gson = gsonBuilder.create();
	private String serverUrl;

	public ServerConnection(String serverUrl)
	{
		this.serverUrl = serverUrl;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public AccessLog[] getLogs() throws IOException
	{
		HttpURLConnection conn = setupGetConnection("/logs");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		StringBuilder logString = new StringBuilder();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			logString.append(line);
		}
		return gson.fromJson(logString.toString(), AccessLog[].class);
	}

	public boolean scan(int doorId, String rfidTag) throws IOException
	{
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

	private HttpURLConnection setupGetConnection(String path) throws IOException
	{
		URL url = new URL(this.serverUrl + path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		conn.setRequestProperty("Accept", "application/json");
		conn.setDoInput(true);
		return conn;
	}
}
