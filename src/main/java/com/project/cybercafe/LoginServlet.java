package controller;

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
import jakarta.servlet.http.HttpSession;
import util.DatabaseConnection;
package com.project.cybercafe;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usernameParam = request.getParameter("username");
        String passwordParam = request.getParameter("password");
        HttpSession session = request.getSession();

        try (Connection conn = DatabaseConnection.getConnection()) {
            // check if user exists and password matches
            String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usernameParam);
                stmt.setString(2, passwordParam);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        // user found! log them into the web session
                        session.setAttribute("LOGGED_IN_USER", rs.getString("USERNAME"));
                        session.setAttribute("USER_ID", rs.getInt("USER_ID"));
                        session.setAttribute("MINUTES_LEFT", rs.getInt("BANKED_MINUTES"));

                        // redirect straight to your active console session dashboard
                        response.sendRedirect("dashboard.jsp");
                        return;
                    } else {
                        // bad credentials
                        response.sendRedirect("login.jsp?error=invalid_credentials");
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=db_error");
        }
    }
}