import java.sql.*;

class DB
{
	Connection conn;
	private static final String USER = "lewisel";
	private static final String PASSWORD = "";
	private static final String URL = "jdbc:mysql://mudfoot.doc.stu.mmu.ac.uk:6306/" + USER;
	private PreparedStatement stmt;

	DB() throws SQLException
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
		}
		catch(Exception e) {
			System.out.println(e);
		}
		conn = DriverManager.getConnection(URL, USER, PASSWORD);
	}

	void create() throws SQLException
	{
		stmt = conn.prepareStatement(
			"CREATE TABLE IF NOT EXISTS users (" +
				"id INT NOT NULL AUTO_INCREMENT," +
				"name VARCHAR(128)," +
				"PRIMARY KEY (id)" +
				")"
		);
		stmt.execute();
		stmt = conn.prepareStatement(
			"CREATE TABLE IF NOT EXISTS tags (" +
				"id INT NOT NULL AUTO_INCREMENT," +
				"user_id INT NOT NULL," +
				"rfid_tag VARCHAR(64) NOT NULL," +
				"FOREIGN KEY (user_id) REFERENCES users (id)," +
				"PRIMARY KEY (id)" +
				")"
		);
		stmt.execute();
		stmt = conn.prepareStatement(
			"CREATE TABLE IF NOT EXISTS doors (" +
				"id INT NOT NULL AUTO_INCREMENT," +
				"open BOOL NOT NULL DEFAULT 0," +
				"PRIMARY KEY (id)" +
				")"
		);
		stmt.execute();
		stmt = conn.prepareStatement(
			"CREATE TABLE IF NOT EXISTS users_doors (" +
				"user_id INT NOT NULL," +
				"door_id INT NOT NULL," +
				"FOREIGN KEY (user_id) REFERENCES users (id)," +
				"FOREIGN KEY (door_id) REFERENCES doors (id)," +
				"PRIMARY KEY (user_id, door_id)" +
				")"
		);
		stmt.execute();
		stmt = conn.prepareStatement(
			"CREATE TABLE IF NOT EXISTS access_logs (" +
				"id INT NOT NULL AUTO_INCREMENT," +
				"door_id INT NOT NULL," +
				"rfid_tag VARCHAR(64)," +
				"action ENUM ('scan', 'open', 'close') NOT NULL," +
				"success BOOLEAN NOT NULL," +
				"timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
				"PRIMARY KEY (id)" +
				")"
		);
		stmt.execute();
	}

	void seed() throws SQLException
	{
		// Drop existing data in tables
		String[] tables = { "access_logs", "users_doors", "doors", "tags", "users" };
		for (String table: tables) {
			stmt = conn.prepareStatement("DELETE FROM " + table);
			stmt.execute();
			stmt = conn.prepareStatement("ALTER TABLE " + table + " AUTO_INCREMENT=1");
			stmt.execute();
		}
		// Seed users
		String[] users = { "Test A", "Test B", "Test C" };
		for (String user: users) {
			stmt = conn.prepareStatement("INSERT INTO users (name) VALUES (?)");
			stmt.setString(1, user);
			stmt.execute();
		}
		// Seed tags
		stmt = conn.prepareStatement("SELECT * FROM users");
		ResultSet user = stmt.executeQuery();
		String[] tags = { "0106936200", "01077594b6", "ABCDTEST123" };
		for (String tag: tags) {
			stmt = conn.prepareStatement("INSERT INTO tags (rfid_tag, user_id) VALUES (?, ?)");
			stmt.setString(1, tag);
			user.next();
			stmt.setInt(2, user.getInt("id"));
			stmt.execute();
		}
		// Seed doors
		stmt = conn.prepareStatement("INSERT INTO doors VALUES ()");
		stmt.execute();
		// Seed users_doors
		stmt = conn.prepareStatement("SELECT * FROM doors");
		ResultSet door = stmt.executeQuery();
		stmt = conn.prepareStatement("INSERT INTO users_doors VALUES (?, ?)");
		user.first();
		stmt.setInt(1, user.getInt("id"));
		door.first();
		stmt.setInt(2, door.getInt("id"));
		stmt.execute();
	}

	void close() throws SQLException
	{
		conn.close();
		stmt.close();
	}
}
