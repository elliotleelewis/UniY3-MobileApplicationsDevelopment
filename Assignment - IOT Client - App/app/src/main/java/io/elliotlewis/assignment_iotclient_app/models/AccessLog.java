package io.elliotlewis.assignment_iotclient_app.models;

import java.sql.*;

public class AccessLog {
	public int id;
	public int doorId;
	public String rfidTag;
	public String action;
	public boolean success;
	public Timestamp timestamp;

	@Override
	public String toString() {
		return action.toUpperCase() + "\n" +
				"DoorID: " + doorId + "\n" +
				"RFIDTag: " + rfidTag + "\n" +
				"Success: " + success + "\n" +
				"Timestamp: " + timestamp;
	}
}
