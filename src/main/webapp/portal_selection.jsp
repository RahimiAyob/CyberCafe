<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Select Portal Option</title>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">
    <main class="theme-page theme-page--centered">
        <section class="theme-card theme-card--compact theme-center theme-stack">
            <!-- pull the seat ID we just saved in the servlet -->
            <span class="theme-badge theme-badge--teal">Desk #<%= session.getAttribute("CURRENT_SEAT_ID") %></span>
            <div>
                <h1 class="theme-title">Choose your <strong>Portal</strong></h1>
                <p class="theme-subtitle">Pick how you want to unlock this PC.</p>
            </div>

            <hr class="theme-divider">

            <!-- buttons to split into member or guest flows -->
            <div class="theme-actions theme-actions--center theme-center">
                <a href="login.jsp" class="theme-link-btn theme-link-btn--primary">Log In as Member</a>
                <a href="guest_billing.jsp" class="theme-link-btn theme-link-btn--warning">Continue as Guest</a>
            </div>

            <hr class="theme-divider">

            <!-- Admin portal link -->
            <div class="theme-center">
                <a href="admin_login.jsp" class="theme-link-btn theme-link-btn--secondary" style="opacity: 0.7; font-size: 0.9rem;">Admin Access</a>
            </div>
        </section>
    </main>
</body>
</html>