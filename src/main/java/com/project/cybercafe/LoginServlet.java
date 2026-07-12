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
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String usernameParam = request.getParameter("username");
        String passwordParam = request.getParameter("password");
        String loginSource = request.getParameter("login_source"); // Catch the hidden form identifier
        HttpSession session = request.getSession();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "SELECT * FROM USERS WHERE USER_NAME = ? AND USER_PASSWORD = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, usernameParam);
                stmt.setString(2, passwordParam);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int userId = rs.getInt("USER_ID");
                        String username = rs.getString("USER_NAME");
                        int bankedMins = rs.getInt("USER_MINUTES");

                        // Check if user is an admin
                        boolean isAdmin = false;
                        try {
                            isAdmin = rs.getBoolean("USER_ADMIN");
                        } catch (SQLException e) {
                            isAdmin = false;
                        }

                        // If user is admin, redirect directly to admin dashboard
                        if (isAdmin) {
                            session.setAttribute("LOGGED_IN_USER", username);
                            session.setAttribute("USER_ID", userId);
                            session.setAttribute("IS_ADMIN", true);
                            response.sendRedirect("AdminDashboard");
                            return;
                        }

                        // grab the seat ID that was stored during the QR scan phase
                        Integer seatId = (Integer) session.getAttribute("CURRENT_SEAT_ID");
                        if (seatId == null) {
                            response.sendRedirect("scan_qr.jsp?error=no_seat_selected");
                            return;
                        }

                        // Verify if the target seat is already occupied
                        String checkSeatSql = "SELECT SEAT_STATUS FROM SEATS WHERE SEAT_ID = ?";
                        try (PreparedStatement checkSeatStmt = conn.prepareStatement(checkSeatSql)) {
                            checkSeatStmt.setInt(1, seatId);
                            try (ResultSet rsSeat = checkSeatStmt.executeQuery()) {
                                if (rsSeat.next()) {
                                    String currentStatus = rsSeat.getString("SEAT_STATUS");
                                    if ("OCCUPIED".equalsIgnoreCase(currentStatus)) {
                                        response.sendRedirect("login.jsp?error=seat_already_occupied");
                                        return;
                                    }
                                }
                            }
                        }

                        // 1. clear any leaking dead sessions for this user first
                        String clearOldSessions = "UPDATE SESSIONS SET SESSION_ACTIVE = FALSE WHERE SESSION_USER_ID = ? AND SESSION_ACTIVE = TRUE";
                        try (PreparedStatement clearStmt = conn.prepareStatement(clearOldSessions)) {
                            clearStmt.setInt(1, userId);
                            clearStmt.executeUpdate();
                        }

                        // 2. set the physical seat status to OCCUPIED for the admin panel layout
                        String occupySeat = "UPDATE SEATS SET SEAT_STATUS = 'OCCUPIED' WHERE SEAT_ID = ?";
                        try (PreparedStatement seatStmt = conn.prepareStatement(occupySeat)) {
                            seatStmt.setInt(1, seatId);
                            seatStmt.executeUpdate();
                        }

                        // 3. calculate session end time based on banked minutes
                        long loginTime = System.currentTimeMillis();
                        long totalDurationMs = (long) bankedMins * 60 * 1000;
                        long expiryTime = loginTime + totalDurationMs;

                        // Convert to SQL format string for END_TIME
                        java.sql.Timestamp endTimeStamp = new java.sql.Timestamp(expiryTime);

                        // 4. insert the active session tracking entry with USER_ID and END_TIME
                        String insertSession = "INSERT INTO SESSIONS (SESSION_SEAT_ID, SESSION_USER_ID, SESSION_GUEST_TAG, SESSION_START_TIME, SESSION_END_TIME, SESSION_ACTIVE) VALUES (?, ?, NULL, NOW(), ?, TRUE)";
                        try (PreparedStatement sessStmt = conn.prepareStatement(insertSession)) {
                            sessStmt.setInt(1, seatId);
                            sessStmt.setInt(2, userId);
                            sessStmt.setTimestamp(3, endTimeStamp);
                            sessStmt.executeUpdate();
                        }

                        // 5. lock down matching session attributes for dashboard consumption
                        session.setAttribute("LOGGED_IN_USER", username);
                        session.setAttribute("USER_ID", userId);
                        session.setAttribute("SESSION_END_TIME", expiryTime);
                        session.setAttribute("IS_GUEST", false);

                        // DYNAMIC BALANCE GATEWAY REDIRECT
                        if (bankedMins > 0) {
                            response.sendRedirect("dashboard.jsp");
                        } else {
                            response.sendRedirect("member_billing.jsp");
                        }
                        return;
                    } else {
                        // Routing gateway for failed attempts
                        if ("admin".equals(loginSource)) {
                            response.sendRedirect("admin_login.jsp?error=invalid_credentials");
                        } else {
                            response.sendRedirect("login.jsp?error=invalid_credentials");
                        }
                        return;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            if ("admin".equals(loginSource)) {
                response.sendRedirect("admin_login.jsp?error=db_error");
            } else {
                response.sendRedirect("login.jsp?error=db_error");
            }
        }
    }
}