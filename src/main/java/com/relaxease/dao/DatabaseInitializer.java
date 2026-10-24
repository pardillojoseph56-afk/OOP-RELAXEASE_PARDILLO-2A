package com.relaxease.dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    // creates all tables from the ERD if they do not exist yet
    public static void initialize() {
        String users = "CREATE TABLE IF NOT EXISTS users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT NOT NULL UNIQUE, "
                + "password TEXT NOT NULL, "
                + "roles TEXT NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'PENDING')";

        String clients = "CREATE TABLE IF NOT EXISTS clients ("
                + "client_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "full_name TEXT NOT NULL, "
                + "phone_number TEXT, "
                + "address TEXT NOT NULL, "
                + "landmark TEXT)";

        String service = "CREATE TABLE IF NOT EXISTS service ("
                + "service_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "service_name TEXT NOT NULL UNIQUE, "
                + "duration_minutes INTEGER NOT NULL CHECK (duration_minutes > 0), "
                + "price REAL NOT NULL CHECK (price >= 0), "
                + "description TEXT)";

        String booking = "CREATE TABLE IF NOT EXISTS booking ("
                + "booking_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "client_id INTEGER NOT NULL, "
                + "assigned_staff_id INTEGER, "
                + "users_id INTEGER, "
                + "service_id INTEGER NOT NULL, "
                + "appointment_datetime TEXT NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'PENDING' "
                + "CHECK (status IN ('PENDING','CONFIRMED','IN TRANSIT','COMPLETED','CANCELLED')), "
                + "total_amount REAL NOT NULL CHECK (total_amount >= 0), "
                + "FOREIGN KEY (client_id) REFERENCES clients(client_id), "
                + "FOREIGN KEY (assigned_staff_id) REFERENCES users(id), "
                + "FOREIGN KEY (users_id) REFERENCES users(id), "
                + "FOREIGN KEY (service_id) REFERENCES service(service_id))";

        String activityLogs = "CREATE TABLE IF NOT EXISTS activity_logs ("
                + "log_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER NOT NULL, "
                + "action TEXT NOT NULL, "
                + "details TEXT, "
                + "log_timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                + "FOREIGN KEY (user_id) REFERENCES users(id))";

        // default admin account (only added if it doesn't exist yet)
        String defaultAdmin = "INSERT OR IGNORE INTO users (username, password, roles, status) "
                + "VALUES ('admin', 'admin123', 'ADMIN', 'APPROVED')";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            st.execute(users);
            st.execute(clients);
            st.execute(service);
            st.execute(booking);
            st.execute(activityLogs);
            st.execute(defaultAdmin);
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}