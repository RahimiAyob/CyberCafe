package com.project.cybercafe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AdminDashboard")
public class AdminDashboardServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Map<String, Object>> seatList = new ArrayList<>();
        List<Map<String, Object>> transactionList = new ArrayList<>();

        // Query to grab all seats and link them to active sessions if they exist
        String sql = "SELECT s.SEAT_ID, s.SEAT_STATUS, sess.SESSION_USER_ID, sess.SESSION_GUEST_TAG, sess.SESSION_START_TIME " +
                "FROM SEATS s " +
                "LEFT JOIN SESSIONS sess ON sess.SESSION_ID = (" +
                "    SELECT MAX(s2.SESSION_ID) FROM SESSIONS s2 " +
                "    WHERE s2.SESSION_SEAT_ID = s.SEAT_ID " +
                "      AND s2.SESSION_ACTIVE = TRUE " +
                "      AND (s2.SESSION_END_TIME IS NULL OR s2.SESSION_END_TIME > NOW())" +
                ") " +
                "ORDER BY s.SEAT_ID ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> seatData = new HashMap<>();
                seatData.put("seatId", rs.getInt("SEAT_ID"));
                seatData.put("status", rs.getString("SEAT_STATUS"));

                // Determine user identifier from USER_ID or GUEST_TAG
                Integer userId = (Integer) rs.getObject("SESSION_USER_ID");
                String guestTag = rs.getString("SESSION_GUEST_TAG");
                String userIdentifier = (userId != null) ? "User_" + userId : guestTag;

                seatData.put("user", userIdentifier);
                seatData.put("startTime", rs.getTimestamp("SESSION_START_TIME"));
                seatList.add(seatData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Fixed table name to TRANSACTIONS and swapped to LEFT JOIN for guest rows
        String transactionSql = "SELECT t.TRANSACTION_ID, u.USER_NAME, t.TRANSACTION_AMOUNT, t.TRANSACTION_SESSION_ID " +
                "FROM TRANSACTIONS t " +
                "LEFT JOIN USERS u ON t.TRANSACTION_USER_ID = u.USER_ID " +
                "ORDER BY t.TRANSACTION_ID DESC " +
                "LIMIT 20";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(transactionSql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> transData = new HashMap<>();
                transData.put("transactionId", rs.getInt("TRANSACTION_ID"));

                // fallback to "Guest" if username comes back null from the left join
                String name = rs.getString("USER_NAME");
                transData.put("username", (name != null) ? name : "Guest");

                transData.put("amount", rs.getDouble("TRANSACTION_AMOUNT"));
                transData.put("sessionId", rs.getObject("TRANSACTION_SESSION_ID"));
                transactionList.add(transData);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Pass the lists to our JSP presentation layer
        request.setAttribute("cafeSeats", seatList);
        request.setAttribute("transactions", transactionList);
        request.getRequestDispatcher("admin_dashboard.jsp").forward(request, response);
    }
}