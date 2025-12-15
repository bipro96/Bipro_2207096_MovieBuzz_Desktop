package com.example.moviebuzz;

import java.sql.*;

public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:moviebuzz.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "username TEXT PRIMARY KEY, " +
                    "password TEXT, " +
                    "role TEXT)");


            stmt.execute("CREATE TABLE IF NOT EXISTS movies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "title TEXT UNIQUE, " +
                    "genre TEXT, " +
                    "duration TEXT, " +
                    "posterPath TEXT)");

            stmt.execute("INSERT OR IGNORE INTO users (username, password, role) VALUES ('bb', 'bb96', 'admin')");

            System.out.println("Database and tables initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Database Initialization Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}