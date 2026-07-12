package com.project.cybercafe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usernameParam = request.getParameter("username");
        String passwordParam = request.getParameter("password");

        // 1. Validation check
        if (usernameParam == null || usernameParam.trim().isEmpty() ||
                passwordParam == null || passwordParam.trim().isEmpty()) {
            response.sendRedirect("register.jsp?error=empty_fields");
            return;
        }

        usernameParam = usernameParam.trim();

        try (Connection conn = DatabaseConnection.getConnection()) {

            // 2. Check if the username already exists in MariaDB
            String checkSql = "SELECT USER_ID FROM USERS WHERE USER_NAME = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, usernameParam);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        response.sendRedirect("register.jsp?error=username_taken");
                        return;
                    }
                }
            }

            // 3. Insert the new user straight up
            String insertSql = "INSERT INTO USERS (USER_NAME, USER_PASSWORD, USER_MINUTES, USER_ADMIN) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, usernameParam);
                insertStmt.setString(2, passwordParam);
                insertStmt.setInt(3, 0); // NEW USERS START RAW WITH ZERO MINUTES
                insertStmt.setBoolean(4, false); // NEW USERS ARE NOT ADMINS
                insertStmt.executeUpdate();
            }

            // Account created successfully! Send them straight to the login page
            response.sendRedirect("login.jsp?registration=success");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=db_error");
        }
    }
}