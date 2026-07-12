<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="com.project.cybercafe.DatabaseConnection" %>
<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Login</title>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">
    <%
        Object seatObj = session.getAttribute("CURRENT_SEAT_ID");
        String seatLabel = (seatObj != null) ? seatObj.toString() : "Unknown";
        String error = request.getParameter("error");
        String registration = request.getParameter("registration");
    %>
    <main class="theme-page theme-page--centered">
        <section class="theme-card theme-card--compact">
            <div class="theme-center theme-stack">
                <span class="theme-badge theme-badge--teal">Desk #<%= seatLabel %></span>
                <div>
                    <h1 class="theme-title">Member <strong>Login</strong></h1>
                    <p class="theme-subtitle">Unlock the workstation with your member account or start a guest session instead.</p>
                </div>
            </div>

            <%
                if ("invalid_credentials".equals(error)) {
            %>
            <div class="error-message">Invalid username or password. Please try again.</div>
            <%
            } else if ("db_error".equals(error)) {
            %>
            <div class="error-message">Database error. Please try again later.</div>
            <%
            } else if ("no_seat_selected".equals(error)) {
            %>
            <div class="error-message">Please scan a desk QR code first.</div>
            <%
            } else if ("seat_already_occupied".equals(error)) {
            %>
            <div class="error-message">Terminal Unavailable: This station is already occupied by an active session!</div>
            <%
                }
            %>

            <%
                if ("success".equals(registration)) {
            %>
            <div class="success-message">Account created! Log in with your new credentials.</div>
            <%
                }
            %>

            <form action="LoginServlet" method="POST" class="theme-form theme-mt-18">
                <label for="login-username" class="theme-sr-only">Username</label>
                <input id="login-username" type="text" name="username" placeholder="Username" class="theme-input" required>
                <label for="login-password" class="theme-sr-only">Password</label>
                <input id="login-password" type="password" name="password" placeholder="Password" class="theme-input" required>
                <button type="submit" class="theme-btn theme-btn--primary">Unlock PC</button>
            </form>

            <hr class="theme-divider">

            <div class="theme-stack theme-center">
                <a href="register.jsp" class="theme-link-btn theme-link-btn--secondary">Create a new member account</a>
                <div>
                    <h3 class="theme-heading-sm theme-mb-6">Just want to play?</h3>
                    <%
                        boolean isTableOccupied = false;
                        if (seatObj instanceof Integer) {
                            try (Connection conn = DatabaseConnection.getConnection()) {
                                String query = "SELECT SEAT_STATUS FROM SEATS WHERE SEAT_ID = ?";
                                try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                                    pstmt.setInt(1, (Integer) seatObj);
                                    try (ResultSet rs = pstmt.executeQuery()) {
                                        if (rs.next()) {
                                            isTableOccupied = "OCCUPIED".equalsIgnoreCase(rs.getString("SEAT_STATUS"));
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        if (isTableOccupied) {
                    %>
                    <a href="login.jsp?error=seat_already_occupied" class="theme-link-btn theme-link-btn--warning">Quick Start as Guest</a>
                    <%
                    } else {
                    %>
                    <a href="guest_billing.jsp" class="theme-link-btn theme-link-btn--warning">Quick Start as Guest</a>
                    <%
                        }
                    %>
                </div>
            </div>
        </section>
    </main>
</body>
</html>