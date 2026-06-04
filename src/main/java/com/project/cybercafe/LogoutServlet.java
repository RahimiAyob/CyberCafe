package com.project.cybercafe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session != null) {
            Integer seatId = (Integer) session.getAttribute("CURRENT_SEAT_ID");
            Integer userId = (Integer) session.getAttribute("USER_ID");
            Long endTime = (Long) session.getAttribute("SESSION_END_TIME");
            Boolean isGuest = (Boolean) session.getAttribute("IS_GUEST");

            try (Connection conn = DatabaseConnection.getConnection()) {

                // 1. ONLY update banked time if this is a registered member (not a guest)
                if ((isGuest == null || !isGuest) && userId != null && endTime != null) {
                    long now = System.currentTimeMillis();
                    long millisLeft = endTime - now;
                    int minutesToSave = 0;

                    if (millisLeft > 0) {
                        minutesToSave = (int) (millisLeft / (1000 * 60));
                    }

                    String updateTimeSql = "UPDATE USERS SET BANKED_MINUTES = ? WHERE USER_ID = ?";
                    try (PreparedStatement timeStmt = conn.prepareStatement(updateTimeSql)) {
                        timeStmt.setInt(1, minutesToSave);
                        timeStmt.setInt(2, userId);
                        timeStmt.executeUpdate();
                    }
                }

                // 2. turn off the active tracking row if a user ID or guest session exists
                if (seatId != null) {
                    if (userId != null) {
                        // For registered members, use USER_ID
                        String updateSessionSql = "UPDATE SESSIONS SET IS_ACTIVE = FALSE, END_TIME = NOW() WHERE SEAT_ID = ? AND USER_ID = ? AND IS_ACTIVE = TRUE";
                        try (PreparedStatement stmt1 = conn.prepareStatement(updateSessionSql)) {
                            stmt1.setInt(1, seatId);
                            stmt1.setInt(2, userId);
                            stmt1.executeUpdate();
                        }
                    } else {
                        // For guests, just deactivate by seat (guest sessions are tied to seat)
                        String updateSessionSql = "UPDATE SESSIONS SET IS_ACTIVE = FALSE, END_TIME = NOW() WHERE SEAT_ID = ? AND IS_ACTIVE = TRUE";
                        try (PreparedStatement stmt1 = conn.prepareStatement(updateSessionSql)) {
                            stmt1.setInt(1, seatId);
                            stmt1.executeUpdate();
                        }
                    }
                }

                // 3. ALWAYS free up the physical PC seat (crucial for both members AND guests)
                if (seatId != null) {
                    String updateSeatSql = "UPDATE SEATS SET STATUS = 'AVAILABLE' WHERE SEAT_ID = ?";
                    try (PreparedStatement stmt2 = conn.prepareStatement(updateSeatSql)) {
                        stmt2.setInt(1, seatId);
                        stmt2.executeUpdate();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            session.invalidate();
        }

        response.sendRedirect("scan_qr.jsp");
    }
}