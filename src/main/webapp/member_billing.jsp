<%@ page contentType="text/html;charset=UTF-8" %>
<%
    // security check: make sure they are actually logged in as a member first
    if (session.getAttribute("LOGGED_IN_USER") == null || (Boolean) session.getAttribute("IS_GUEST") == true) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Member Top-Up</title>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">

    <main class="theme-page">
        <section class="theme-card theme-card--wide theme-stack">
            <div class="theme-center">
                <span class="theme-badge theme-badge--teal">Member Billing</span>
                <h1 class="theme-title">Welcome back, <strong><%= session.getAttribute("LOGGED_IN_USER") %></strong>!</h1>
                <p class="theme-subtitle">Select a time package to add directly to your permanent account balance.</p>
            </div>

            <% if ("success".equals(request.getParameter("status"))) { %>
            <div class="theme-alert theme-alert--success theme-center">💸 Top-up successful! Your new time balance has been loaded.</div>
            <% } %>

            <div class="theme-grid theme-grid--pricing">
                <div class="theme-panel theme-panel--raised theme-center">
                    <h3>Bronze Boost</h3>
                    <p class="theme-muted">30 Minutes</p>
                    <div class="theme-price">RM 2.00</div>
                    <a href="TopUpServlet?minutes=30&redirect=billing" class="theme-link-btn theme-link-btn--primary">Add to Account</a>
                </div>

                <div class="theme-panel theme-panel--raised theme-center">
                    <h3>Silver Session</h3>
                    <p class="theme-muted">60 Minutes</p>
                    <div class="theme-price">RM 4.00</div>
                    <a href="TopUpServlet?minutes=60&redirect=billing" class="theme-link-btn theme-link-btn--primary">Add to Account</a>
                </div>

                <div class="theme-panel theme-panel--raised theme-center">
                    <h3>Gold Grind</h3>
                    <p class="theme-muted">120 Minutes</p>
                    <div class="theme-price">RM 8.00</div>
                    <a href="TopUpServlet?minutes=120&redirect=billing" class="theme-link-btn theme-link-btn--primary">Add to Account</a>
                </div>
            </div>

            <div class="theme-actions theme-actions--center theme-center">
                <a href="dashboard.jsp" class="theme-link-btn theme-link-btn--secondary">Go to Dashboard</a>
                <a href="LogoutServlet" class="theme-link-btn theme-link-btn--danger">Log Out</a>
            </div>
        </section>
    </main>


 </body>
 </html>
