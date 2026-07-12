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

@WebServlet("/TopUpServlet")
public class TopUpServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        String minutesParam = request.getParameter("minutes");
        String redirectTarget = request.getParameter("redirect");

        if (session != null && session.getAttribute("LOGGED_IN_USER") != null && minutesParam != null) {
            String username = (String) session.getAttribute("LOGGED_IN_USER");
            Integer userId = (Integer) session.getAttribute("USER_ID");
            int minutesToAdd = Integer.parseInt(minutesParam);
            double amount = calculatePrice(minutesToAdd);

            try (Connection conn = DatabaseConnection.getConnection()) {
                // 1. update the database balance
                String topUpSql = "UPDATE USERS SET USER_MINUTES = USER_MINUTES + ? WHERE USER_NAME = ?";
                try (PreparedStatement topUpStmt = conn.prepareStatement(topUpSql)) {
                    topUpStmt.setInt(1, minutesToAdd);
                    topUpStmt.setString(2, username);
                    topUpStmt.executeUpdate();
                }

                // 2. log the transaction to TRANSACTIONS table (dynamically fetching active session)
                if (userId != null) {
                    String transactionSql = "INSERT INTO TRANSACTIONS (TRANSACTION_USER_ID, TRANSACTION_SESSION_ID, TRANSACTION_AMOUNT) " +
                            "VALUES (?, (SELECT SESSION_ID FROM SESSIONS WHERE SESSION_USER_ID = ? AND SESSION_ACTIVE = TRUE LIMIT 1), ?)";
                    try (PreparedStatement tranStmt = conn.prepareStatement(transactionSql)) {
                        tranStmt.setInt(1, userId);
                        tranStmt.setInt(2, userId);
                        tranStmt.setDouble(3, amount);
                        tranStmt.executeUpdate();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                // 3. Update SESSION_END_TIME based on current BANKED_MINUTES
                Long currentEndTime = (Long) session.getAttribute("SESSION_END_TIME");
                if (currentEndTime != null) {
                    long updatedEndTime = currentEndTime + (minutesToAdd * 60L * 1000L);
                    session.setAttribute("SESSION_END_TIME", updatedEndTime);

                    if (userId != null) {
                        String updateSessionSql = "UPDATE SESSIONS SET SESSION_END_TIME = ? WHERE SESSION_USER_ID = ? AND SESSION_ACTIVE = TRUE";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSessionSql)) {
                            updateStmt.setTimestamp(1, new java.sql.Timestamp(updatedEndTime));
                            updateStmt.setInt(2, userId);
                            updateStmt.executeUpdate();
                        }
                    }
                } else {
                    String bankingSql = "SELECT USER_MINUTES FROM USERS WHERE USER_NAME = ?";
                    try (PreparedStatement bankStmt = conn.prepareStatement(bankingSql)) {
                        bankStmt.setString(1, username);
                        try (ResultSet rs = bankStmt.executeQuery()) {
                            if (rs.next()) {
                                int totalBankedMins = rs.getInt("USER_MINUTES");
                                long loginTime = System.currentTimeMillis();
                                long totalDurationMs = (long) totalBankedMins * 60 * 1000;
                                long newExpiryTime = loginTime + totalDurationMs;
                                session.setAttribute("SESSION_END_TIME", newExpiryTime);
                            }
                        }
                    }
                }

                if ("billing".equals(redirectTarget)) {
                    response.sendRedirect("member_billing.jsp?status=success");
                } else {
                    response.sendRedirect("dashboard.jsp?status=success");
                }
                return;

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("dashboard.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameParam = request.getParameter("username");
        String minutesParam = request.getParameter("minutes");

        if (usernameParam != null && minutesParam != null) {
            int minutesToAdd = Integer.parseInt(minutesParam);
            double amount = calculatePrice(minutesToAdd);

            try (Connection conn = DatabaseConnection.getConnection()) {
                String topUpSql = "UPDATE USERS SET USER_MINUTES = USER_MINUTES + ? WHERE USER_NAME = ?";
                try (PreparedStatement topUpStmt = conn.prepareStatement(topUpSql)) {
                    topUpStmt.setInt(1, minutesToAdd);
                    topUpStmt.setString(2, usernameParam.trim());
                    topUpStmt.executeUpdate();
                }

                String userIdSql = "SELECT USER_ID FROM USERS WHERE USER_NAME = ?";
                try (PreparedStatement userStmt = conn.prepareStatement(userIdSql)) {
                    userStmt.setString(1, usernameParam.trim());
                    try (ResultSet rs = userStmt.executeQuery()) {
                        if (rs.next()) {
                            int userId = rs.getInt("USER_ID");

                            // dynamically tracking the active session for standalone kiosk terminal forms
                            String transactionSql = "INSERT INTO TRANSACTIONS (TRANSACTION_USER_ID, TRANSACTION_SESSION_ID, TRANSACTION_AMOUNT) " +
                                    "VALUES (?, (SELECT SESSION_ID FROM SESSIONS WHERE SESSION_USER_ID = ? AND SESSION_ACTIVE = TRUE LIMIT 1), ?)";
                            try (PreparedStatement tranStmt = conn.prepareStatement(transactionSql)) {
                                tranStmt.setInt(1, userId);
                                tranStmt.setInt(2, userId);
                                tranStmt.setDouble(3, amount);
                                tranStmt.executeUpdate();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                response.sendRedirect("topup.jsp?status=success");
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        response.sendRedirect("topup.jsp?status=db_error");
    }

    /**
     * Updated pricing structure to match the frontend cards:
     * 30 mins = RM 2.00
     * 60 mins = RM 4.00
     * 120 mins = RM 8.00
     */
    private double calculatePrice(int minutes) {
        if (minutes == 30) return 2.00;
        if (minutes == 60) return 4.00;
        if (minutes == 120) return 8.00;
        return (minutes / 30.0) * 2.00;
    }
}