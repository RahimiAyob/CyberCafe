package com.project.cybercafe;
import java.io.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet("/QrHandler")
public class QrHandlerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String seatParam = request.getParameter("seat");

        if (seatParam != null && !seatParam.trim().isEmpty()) {
            try {
                int seatId = Integer.parseInt(seatParam);

                // 1. lock the seat number into the session
                HttpSession session = request.getSession();
                session.setAttribute("CURRENT_SEAT_ID", seatId);

                // 2. bypass the selection page and go straight to the login screen
                // if they are a guest, you can have a "play as guest" button right on the login page anyway
                response.sendRedirect("login.jsp");
                return;

            } catch (NumberFormatException e) {
                response.sendRedirect("scan_qr.jsp?error=bad_format");
                return;
            }
        }

        response.sendRedirect("scan_qr.jsp?error=missing_seat");
    }
}