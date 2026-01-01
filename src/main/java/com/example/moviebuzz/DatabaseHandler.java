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
                    "username TEXT PRIMARY KEY, password TEXT, role TEXT, balance REAL DEFAULT 0.0)");


            stmt.execute("CREATE TABLE IF NOT EXISTS movies (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT UNIQUE, " +
                    "genre TEXT, duration TEXT, posterPath TEXT)");


            stmt.execute("CREATE TABLE IF NOT EXISTS shows (" +
                    "showId INTEGER PRIMARY KEY AUTOINCREMENT, movieTitle TEXT, " +
                    "showDate TEXT, showTime TEXT, price REAL, status TEXT DEFAULT 'Active')");


            stmt.execute("CREATE TABLE IF NOT EXISTS bookings (" +
                    "bookingId INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT, " +
                    "showId INTEGER, movieTitle TEXT, amountPaid REAL, status TEXT DEFAULT 'Confirmed')");

            stmt.execute("INSERT OR IGNORE INTO users (username, password, role, balance) VALUES ('bb', 'bb96', 'admin', 0.0)");
            System.out.println("Database expanded with Shows and Bookings.");
        } catch (SQLException e) { e.printStackTrace(); }
    }
}