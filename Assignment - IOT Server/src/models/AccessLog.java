package models;

import java.sql.*;

public class AccessLog
{
	public enum Action
	{
		scan,
		open,
		close,
	}

	public int id;
	public int doorId;
	public String rfidTag;
	public Action action;
	public boolean success;
	public Timestamp timestamp;
}
