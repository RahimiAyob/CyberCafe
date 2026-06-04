<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Admin Login</title>
    <link rel="stylesheet" href="assets/css/theme.css">
    <style>
        .error-message {
            background-color: rgba(231, 76, 60, 0.15);
            border: 1px solid #e74c3c;
            color: #e74c3c;
            padding: 12px 16px;
            border-radius: 4px;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 10px;
            font-size: 14px;
            font-weight: bold;
        }
        .error-message::before {
            content: "🛑";
            font-size: 1.1em;
        }
    </style>
</head>
<body class="theme-body">
    <%
        String error = request.getParameter("error");
    %>
    <main class="theme-page theme-page--centered">
        <section class="theme-card theme-card--compact">
            <div class="theme-center theme-stack">
                <span class="theme-badge theme-badge--red">Admin Only</span>
                <div>
                    <h1 class="theme-title">Admin <strong>Access</strong></h1>
                    <p class="theme-subtitle">Sign in with your admin credentials to access the operational dashboard.</p>
                </div>
            </div>

            <%
                if ("invalid_credentials".equals(error)) {
            %>
            <div class="error-message">Invalid admin credentials. Please check your username and password.</div>
            <%
            } else if ("db_error".equals(error)) {
            %>
            <div class="error-message">Database error. Please try again later.</div>
            <%
                }
            %>

            <form action="LoginServlet" method="POST" class="theme-form theme-mt-18">
                <input type="hidden" name="login_source" value="admin">

                <label for="login-username" class="theme-sr-only">Username</label>
                <input id="login-username" type="text" name="username" placeholder="Admin Username" class="theme-input" required>
                <label for="login-password" class="theme-sr-only">Password</label>
                <input id="login-password" type="password" name="password" placeholder="Admin Password" class="theme-input" required>
                <button type="submit" class="theme-btn theme-btn--primary">Access Dashboard</button>
            </form>

            <hr class="theme-divider">

            <div class="theme-stack theme-center">
                <a href="scan_qr.jsp" class="theme-link-btn theme-link-btn--secondary">Back to Member Portal</a>
            </div>
        </section>
    </main>
</body>
</html>