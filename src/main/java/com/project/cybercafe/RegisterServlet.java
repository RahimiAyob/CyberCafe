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
            String checkSql = "SELECT USER_ID FROM USERS WHERE USERNAME = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setString(1, usernameParam);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        // User exists, stop deployment and kick back
                        response.sendRedirect("register.jsp?error=username_taken");
                        return;
                    }
                }
            }

            // INSIDE REGISTERSERVLET.JAVA - CHANGE STEP 3 TO THIS:
            String insertSql = "INSERT INTO USERS (USERNAME, PASSWORD, BANKED_MINUTES, IS_ADMIN) VALUES (?, ?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setString(1, usernameParam);
                insertStmt.setString(2, passwordParam);
                insertStmt.setInt(3, 0); // NEW USERS START RAW WITH ZERO MINUTES
                insertStmt.setBoolean(4, false); // NEW USERS ARE NOT ADMINS
                insertStmt.executeUpdate();
            } catch (SQLException e) {
                // If IS_ADMIN column doesn't exist, try without it (for backwards compatibility)
                String insertSqlFallback = "INSERT INTO USERS (USERNAME, PASSWORD, BANKED_MINUTES) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSqlFallback)) {
                    insertStmt.setString(1, usernameParam);
                    insertStmt.setString(2, passwordParam);
                    insertStmt.setInt(3, 0);
                    insertStmt.executeUpdate();
                } catch (SQLException fallbackException) {
                    throw fallbackException;
                }
            }

            // Account created successfully! Send them straight to the login page
            response.sendRedirect("login.jsp?registration=success");

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.jsp?error=db_error");
        }
    }
}