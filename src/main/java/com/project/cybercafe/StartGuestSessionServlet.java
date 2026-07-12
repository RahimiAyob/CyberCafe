package com.project.cybercafe;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/StartGuestSessionServlet")
public class StartGuestSessionServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String minutesParam = request.getParameter("minutes");
        HttpSession session = request.getSession();

        if (minutesParam != null && session.getAttribute("CURRENT_SEAT_ID") != null) {
            int minutes = Integer.parseInt(minutesParam);
            int seatId = (Integer) session.getAttribute("CURRENT_SEAT_ID");
            double amount = calculatePrice(minutes);

            // 1. calculate expiration time
            long endTimeMillis = System.currentTimeMillis() + (minutes * 60L * 1000L);
            String guestIdentifier = "Guest_Station" + seatId;

            // 2. sync with mariadb so the admin panel actually sees the seat is locked
            try (Connection conn = DatabaseConnection.getConnection()) {

                // CRITICAL GATEWAY CHECK: Block guest if seat is already occupied by someone else
                String checkSeatSql = "SELECT SEAT_STATUS FROM SEATS WHERE SEAT_ID = ?";
                try (PreparedStatement checkSeatStmt = conn.prepareStatement(checkSeatSql)) {
                    checkSeatStmt.setInt(1, seatId);
                    try (ResultSet rsSeat = checkSeatStmt.executeQuery()) {
                        if (rsSeat.next()) {
                            String currentStatus = rsSeat.getString("SEAT_STATUS");
                            if ("OCCUPIED".equalsIgnoreCase(currentStatus)) {
                                response.sendRedirect("scan_qr.jsp?error=seat_already_occupied");
                                return;
                            }
                        }
                    }
                }

                // update the physical seat status to occupied
                String updateSeatSql = "UPDATE SEATS SET SEAT_STATUS = 'OCCUPIED' WHERE SEAT_ID = ?";
                try (PreparedStatement seatStmt = conn.prepareStatement(updateSeatSql)) {
                    seatStmt.setInt(1, seatId);
                    seatStmt.executeUpdate();
                }

                // insert an active row into your tracking log table so the admin knows WHO is sitting there
                String insertSessionSql = "INSERT INTO SESSIONS (SESSION_SEAT_ID, SESSION_USER_ID, SESSION_GUEST_TAG, SESSION_START_TIME, SESSION_END_TIME, SESSION_ACTIVE) VALUES (?, NULL, ?, NOW(), DATE_ADD(NOW(), INTERVAL ? MINUTE), TRUE)";
                try (PreparedStatement sessStmt = conn.prepareStatement(insertSessionSql, Statement.RETURN_GENERATED_KEYS)) {
                    sessStmt.setInt(1, seatId);
                    sessStmt.setString(2, guestIdentifier);
                    sessStmt.setInt(3, minutes);
                    sessStmt.executeUpdate();

                    Integer createdSessionId = null;
                    try (ResultSet generatedKeys = sessStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            createdSessionId = generatedKeys.getInt(1);
                        }
                    }

                    if (createdSessionId != null) {
                        String transactionSql = "INSERT INTO TRANSACTIONS (TRANSACTION_USER_ID, TRANSACTION_SESSION_ID, TRANSACTION_AMOUNT) VALUES (NULL, ?, ?)";
                        try (PreparedStatement tranStmt = conn.prepareStatement(transactionSql)) {
                            tranStmt.setInt(1, createdSessionId);
                            tranStmt.setDouble(2, amount);
                            tranStmt.executeUpdate();
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                response.sendRedirect("scan_qr.jsp?error=db_error");
                return;
            }

            // 3. store variables into session context for the frontend clock
            session.setAttribute("LOGGED_IN_USER", guestIdentifier);
            session.setAttribute("SESSION_END_TIME", endTimeMillis);
            session.setAttribute("IS_GUEST", true);

            response.sendRedirect("dashboard.jsp");
            return;
        }

        response.sendRedirect("scan_qr.jsp");
    }

    private double calculatePrice(int minutes) {
        if (minutes == 30) return 2.00;
        if (minutes == 60) return 4.00;
        if (minutes == 120) return 8.00;
        return (minutes / 30.0) * 2.00;
    }
}