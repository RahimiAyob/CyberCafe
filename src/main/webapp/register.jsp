<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Join the Squad</title>
    <link rel="stylesheet" href="assets/css/theme.css">
    <style>
        .error-message {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
            padding: 12px 16px;
            border-radius: 4px;
            margin-bottom: 16px;
            display: flex;
            align-items: center;
            gap: 10px;
        }
        .error-message::before {
            content: "⚠";
            font-size: 1.2em;
        }
    </style>
</head>
<body class="theme-body">

    <main class="theme-page theme-page--centered">
        <section class="theme-card theme-card--compact theme-stack">
            <div class="theme-center">
                <span class="theme-badge theme-badge--teal">New Member</span>
                <h1 class="theme-title">Member <strong>Registration</strong></h1>
                <p class="theme-subtitle">Create an account to save time balance and unlock the workstation instantly next time.</p>
            </div>

            <!-- Error Messages -->
            <%
                String error = request.getParameter("error");
                if ("username_taken".equals(error)) { %>
                <div class="error-message">That username is already taken. Please choose a different one.</div>
            <% } else if ("db_error".equals(error)) { %>
                <div class="error-message">Database error. Please try again later.</div>
            <% } else if ("empty_fields".equals(error)) { %>
                <div class="error-message">Please fill in all fields.</div>
            <% }
            %>

            <form action="RegisterServlet" method="POST" class="theme-form">
                <label for="register-username" class="theme-sr-only">Choose Username</label>
                <input id="register-username" type="text" name="username" placeholder="Choose Username" class="theme-input" required>
                <label for="register-password" class="theme-sr-only">Create Password</label>
                <input id="register-password" type="password" name="password" placeholder="Create Password" class="theme-input" required>
                <button type="submit" class="theme-btn theme-btn--primary">Create Account</button>
            </form>

            <a href="login.jsp" class="theme-link-btn theme-link-btn--secondary">Already a member? Log in here</a>
        </section>
    </main>

</body>
</html>