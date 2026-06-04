<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // security check: must scan a desk first
    if (session.getAttribute("CURRENT_SEAT_ID") == null) {
        response.sendRedirect("scan_qr.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Guest Quick Start</title>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">

    <main class="theme-page">
        <section class="theme-card theme-card--wide theme-stack">
            <div class="theme-center">
                <span class="theme-badge theme-badge--orange">Guest Session</span>
                <h1 class="theme-title">Desk #<%= session.getAttribute("CURRENT_SEAT_ID") %> - <strong>Quick Start</strong></h1>
                <p class="theme-subtitle">Select a pre-paid time package. Unused time will be burned upon logging out.</p>
            </div>

            <div class="theme-grid theme-grid--pricing">
                <div class="theme-panel theme-panel--raised theme-center">
                    <h3>Quick Play</h3>
                    <p class="theme-muted">30 Minutes</p>
                    <div class="theme-price">RM 2.00</div>
                    <a href="StartGuestSessionServlet?minutes=30" class="theme-link-btn theme-link-btn--warning">Insert Cash</a>
                </div>

                <div class="theme-panel theme-panel--raised theme-center">
                    <h3>Standard Blast</h3>
                    <p class="theme-muted">60 Minutes</p>
                    <div class="theme-price">RM 4.00</div>
                    <a href="StartGuestSessionServlet?minutes=60" class="theme-link-btn theme-link-btn--warning">Insert Cash</a>
                </div>
            </div>
        </section>
    </main>

</body>
</html>