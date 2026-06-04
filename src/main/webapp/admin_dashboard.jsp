<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Admin Control Center</title>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">

    <main class="theme-page">
        <section class="theme-card theme-card--wide">
            <div class="theme-hero">
                <div>
                    <span class="theme-badge theme-badge--teal">Admin Console</span>
                    <h1 class="theme-title">CyberCafe <strong>Operational Grid</strong></h1>
                    <p class="theme-hero__meta">Monitor live station availability and active sessions.</p>
                </div>
                <a href="AdminDashboard" class="theme-link-btn theme-link-btn--primary">🔄 Refresh Station Statuses</a>
            </div>

            <div class="theme-grid theme-grid--dashboard">
                <%
                    Object seatsObj = request.getAttribute("cafeSeats");
                    if (seatsObj instanceof List) {
                        List seats = (List) seatsObj;
                        for (Object seatObj : seats) {
                            @SuppressWarnings("unchecked")
                            Map<String, Object> safeSeat = (Map<String, Object>) seatObj;
                            String status = (String) safeSeat.get("status");
                            String user = (String) safeSeat.get("user");
                            Object startTime = safeSeat.get("startTime");
                            String cardClass = "OCCUPIED".equals(status)
                                    ? "theme-panel theme-seat-card theme-seat-card--occupied"
                                    : "theme-panel theme-seat-card theme-seat-card--available";
                %>
                <div class="<%= cardClass %>">
                    <div class="theme-hero theme-mb-14">
                        <div>
                            <h3 class="theme-mb-6">Terminal #<%= safeSeat.get("seatId") %></h3>
                            <span class="theme-badge <%= "OCCUPIED".equals(status) ? "theme-badge--red" : "theme-badge--green" %>"><%= status %></span>
                        </div>
                    </div>

                    <div class="theme-muted">
                        <% if ("OCCUPIED".equals(status)) { %>
                        <strong>User:</strong> <%= (user != null) ? user : "Anonymous Guest" %><br>
                        <strong>Active Since:</strong> <%= (startTime != null) ? startTime.toString().substring(11, 16) : "N/A" %>
                        <% } else { %>
                        Station is clean and waiting for the next scan confirmation.
                        <% } %>
                    </div>
                </div>
                <%
                    }
                } else {
                %>
                <div class="theme-panel">
                    <p class="theme-note">No desk terminals are mapped in the current database configuration.</p>
                </div>
                <% } %>
            </div>

            <div class="theme-card theme-card--wide theme-mt-20" style="margin-top: 30px;">
                <div class="theme-hero">
                    <div>
                        <span class="theme-badge theme-badge--purple">Payment Log</span>
                        <h2 class="theme-title">Recent <strong>Transactions</strong></h2>
                        <p class="theme-hero__meta">Latest member top-ups and payments recorded in the system.</p>
                    </div>
                    <a href="AdminDashboard" class="theme-link-btn theme-link-btn--primary">🔄 Refresh Transactions</a>
                </div>

                <%
                    Object transObj = request.getAttribute("transactions");
                    if (transObj instanceof List) {
                        List transactions = (List) transObj;
                        if (!transactions.isEmpty()) {
                %>
                <div style="overflow-x: auto; margin-top: 20px;">
                    <table style="width: 100%; border-collapse: collapse; color: var(--theme-text, #ffffff);">
                        <thead>
                        <tr style="background-color: rgba(255, 255, 255, 0.05); border-bottom: 2px solid rgba(255, 255, 255, 0.1);">
                            <th style="padding: 14px 12px; text-align: left; font-weight: 600; color: #a0aec0;">Transaction ID</th>
                            <th style="padding: 14px 12px; text-align: left; font-weight: 600; color: #a0aec0;">Username</th>
                            <th style="padding: 14px 12px; text-align: left; font-weight: 600; color: #a0aec0;">Amount</th>
                            <th style="padding: 14px 12px; text-align: left; font-weight: 600; color: #a0aec0;">Session ID</th>
                        </tr>
                        </thead>
                        <tbody>
                        <%
                            for (Object transactionObj : transactions) {
                                @SuppressWarnings("unchecked")
                                Map<String, Object> transaction = (Map<String, Object>) transactionObj;
                                int transId = (Integer) transaction.get("transactionId");
                                String username = (String) transaction.get("username");
                                double amount = (Double) transaction.get("amount");
                                Object sessionId = transaction.get("sessionId");
                        %>
                        <tr style="border-bottom: 1px solid rgba(255, 255, 255, 0.05); background-color: rgba(0, 0, 0, 0.1);">
                            <td style="padding: 14px 12px; color: #a0aec0;">#<%= transId %></td>
                            <td style="padding: 14px 12px;"><strong style="color: #ffffff;"><%= username %></strong></td>
                            <td style="padding: 14px 12px; color: #2ecc71; font-weight: bold;">RM <%= String.format("%.2f", amount) %></td>
                            <td style="padding: 14px 12px; color: #e2e8f0;"><%= (sessionId != null) ? sessionId : "<em style=\"color: #718096;\">N/A</em>" %></td>
                        </tr>
                        <%
                            }
                        %>
                        </tbody>
                    </table>
                </div>
                <%
                } else {
                %>
                <div class="theme-panel">
                    <p class="theme-note">No transactions recorded yet.</p>
                </div>
                <%
                    }
                } else {
                %>
                <div class="theme-panel">
                    <p class="theme-note">Unable to load transaction data.</p>
                </div>
                <% } %>
            </div>
        </section>
    </main>

</body>
</html>