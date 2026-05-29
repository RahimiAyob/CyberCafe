package com.project.cybercafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // connection details pointing to your local mariadb server
    private static final String URL = "jdbc:mariadb://localhost:3306/cybercafe_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password123"; // change this to the password you set in the wizard

    public static Connection getConnection() throws SQLException {
        try {
            // explicitly load the mariadb driver class
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MariaDB Driver not found in classpath", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}