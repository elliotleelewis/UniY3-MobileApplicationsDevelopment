import com.google.gson.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.stream.*;

import models.*;
import models.dto.*;

public class Server extends HttpServlet
{
	private static final GsonBuilder gsonBuilder = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
	private static final Gson gson = gsonBuilder.create();
	private PreparedStatement stmt;
	private DB db;

	@Override
	public void init() throws ServletException
	{
		super.init();
		try {
			db = new DB();
			db.create();
			db.seed();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void destroy()
	{
		super.destroy();
		try {
			db.close();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		System.out.println(req);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		try {
			switch (req.getRequestURI()) {
				case "/logs":
					out.print(getLogs(req, resp));
					break;
				default:
					out.print("");
					break;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(500);
		}
		finally {
			out.flush();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		System.out.println(req);
		resp.setContentType("application/json");
		resp.setCharacterEncoding("UTF-8");
		PrintWriter out = resp.getWriter();
		String body = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		try {
			switch (req.getRequestURI()) {
				case "/scan":
					out.print(scanRfid(req, resp, body));
					break;
				case "/open":
					out.print(openDoor(req, resp, body));
					break;
				case "/close":
					out.print(closeDoor(req, resp, body));
					break;
				default:
					out.print("");
					break;
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			resp.setStatus(500);
		}
		finally {
			out.flush();
		}
	}

	private String getLogs(HttpServletRequest req, HttpServletResponse resp) throws SQLException
	{
		stmt = db.conn.prepareStatement("SELECT * FROM access_logs");
		ResultSet results = stmt.executeQuery();
		JsonArray array = new JsonArray();
		while (results.next()) {
			AccessLog log = new AccessLog();
			log.id = results.getInt("id");
			log.doorId = results.getInt("door_id");
			log.rfidTag = results.getString("rfid_tag");
			log.action = AccessLog.Action.valueOf(results.getString("action"));
			log.success = results.getBoolean("success");
			log.timestamp = results.getTimestamp("timestamp");
			array.add(gson.toJsonTree(log));
		}
		return array.toString();
	}

	private String scanRfid(HttpServletRequest req, HttpServletResponse resp, String body) throws SQLException
	{
		ScanRfid params = gson.fromJson(body, ScanRfid.class);
		// Get doors available to user associated with RFID tag
		stmt = db.conn.prepareStatement(
			"SELECT doors.id " +
				"FROM tags " +
				"JOIN users ON tags.user_id=users.id " +
				"JOIN users_doors ON users.id=users_doors.user_id " +
				"JOIN doors ON users_doors.door_id=doors.id " +
				"WHERE tags.rfid_tag=?"
		);
		stmt.setString(1, params.rfidTag);
		ResultSet results = stmt.executeQuery();
		// Check if doors available to user include requested door
		boolean authorized = false;
		while (!authorized && results.next()) {
			if (results.getInt("id") == params.doorId) {
				authorized = true;
			}
		}
		log(params.doorId, params.rfidTag, AccessLog.Action.scan, authorized);
		if (authorized) {
			resp.setStatus(200);
			return gson.toJson(new Object());
		}
		resp.setStatus(403);
		return gson.toJson(new Exception());
	}

	private String openDoor(HttpServletRequest req, HttpServletResponse resp, String body) throws SQLException
	{
		OpenDoor params = gson.fromJson(body, OpenDoor.class);
		stmt = db.conn.prepareStatement("UPDATE doors SET open=? WHERE id=?");
		stmt.setBoolean(1, true);
		stmt.setInt(2, params.doorId);
		System.out.println(stmt);
		stmt.execute();
		log(params.doorId, null, AccessLog.Action.open, params.success);
		return gson.toJson(new Object());
	}

	private String closeDoor(HttpServletRequest req, HttpServletResponse resp, String body) throws SQLException
	{
		CloseDoor params = gson.fromJson(body, CloseDoor.class);
		stmt = db.conn.prepareStatement("UPDATE doors SET open=? WHERE id=?");
		stmt.setBoolean(1, false);
		stmt.setInt(2, params.doorId);
		stmt.execute();
		log(params.doorId, null, AccessLog.Action.close, params.success);
		return gson.toJson(new Object());
	}

	private void log(int doorId, String rfidTag, AccessLog.Action action, boolean success) throws SQLException
	{
		stmt = db.conn.prepareStatement(
			"INSERT INTO access_logs " +
				"(door_id, rfid_tag, action, success) VALUES " +
				"(?, ?, ?, ?)"
		);
		stmt.setInt(1, doorId);
		stmt.setString(2, rfidTag);
		stmt.setString(3, action.name());
		stmt.setBoolean(4, success);
		stmt.execute();
		System.out.println(stmt);
	}
}
