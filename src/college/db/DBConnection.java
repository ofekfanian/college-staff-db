package college.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // PostgreSQL 17 (Homebrew) on port 5433 — trust auth, no password required
    private static final String URL      = "jdbc:postgresql://localhost:5433/college_db";
    private static final String USER     = "ofekfanian";
    private static final String PASSWORD = "";

    private static Connection connection = null;

    private DBConnection() {}

    // Returns a single shared connection; reconnects if closed
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
            connection = null;
        }
    }
}
