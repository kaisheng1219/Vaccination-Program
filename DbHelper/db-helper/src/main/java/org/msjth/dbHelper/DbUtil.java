package org.msjth.dbHelper;

import java.sql.*;

import io.github.cdimascio.dotenv.Dotenv;

public class DbUtil {
    private static Connection conn;

    public static Connection getConnection() throws SQLException {
        if (conn == null) {
            Dotenv dotenv = Dotenv.load();
            conn = DriverManager.getConnection(dotenv.get("REMOTE_DB_URL"), dotenv.get("REMOTE_DB_USERNAME"), dotenv.get("REMOTE_DB_PASSWORD"));
            System.out.println("Connected to DB.");
        }
        return conn;
    }

    public static void closeConnection() throws SQLException {
        if (conn != null && !conn.isClosed())
            conn.close();
        System.out.println("Disconnected from DB");
    }
}
