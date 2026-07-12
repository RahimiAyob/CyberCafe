<%@ page contentType="text/html;charset=UTF-8" %>
<%
    if (session.getAttribute("LOGGED_IN_USER") == null) {
        response.sendRedirect("scan_qr.jsp");
        return;
    }

    Object userIdObj = session.getAttribute("USER_ID");
    String userIdDisplay = (userIdObj != null) ? userIdObj.toString() : "GUEST_USER";

    // fetch the absolute millisecond timestamp we generated in the servlets
    Long sessionEndTime = (Long) session.getAttribute("SESSION_END_TIME");

    // fallback to current time if something goes sideways so it doesn't break javascript execution
    long expiryTimestamp = (sessionEndTime != null) ? sessionEndTime : System.currentTimeMillis();
%>

<!DOCTYPE html>
<html>
<head>
    <title>CyberCafe - Active Session</title>
    <link rel="stylesheet" href="assets/css/theme.css">
</head>
<body class="theme-body">
    <main class="theme-page">
        <section class="theme-card theme-card--wide">
            <div class="theme-hero">
                <div>
                    <span class="theme-badge theme-badge--teal">Live Session</span>
                    <h1 class="theme-title">Welcome back, <strong><%= session.getAttribute("LOGGED_IN_USER") %></strong></h1>
                    <p class="theme-hero__meta">User ID: <%= userIdDisplay %></p>
                </div>
                <div class="theme-center">
                    <span class="theme-badge theme-badge--orange">Session Countdown</span>
                    <div class="theme-timer" id="countdown">Calculating...</div>
                </div>
            </div>

            <div class="theme-actions">
                <%-- show the topup link only if they are a registered member, not a transient guest --%>
                <% if (session.getAttribute("IS_GUEST") == null || !(Boolean)session.getAttribute("IS_GUEST")) { %>
                <a href="member_billing.jsp" class="theme-link-btn theme-link-btn--primary">💸 Buy More Time</a>
                <% } %>

                <a href="LogoutServlet" class="theme-link-btn theme-link-btn--danger">End Session &amp; Log Out</a>
            </div>
        </section>
    </main>

    <script>
      // now injecting the matching java variable name into the client side
      const expiryTime = <%= expiryTimestamp %>;

      function updateTimer() {
        const now = Date.now();
        const timeLeftMs = expiryTime - now;

        if (timeLeftMs <= 0) {
          document.getElementById("countdown").innerHTML = "00:00:00 - TIME EXPIRED";
          clearInterval(timerInterval);
          window.location.href = "LogoutServlet";
          return;
        }

        let totalSeconds = Math.floor(timeLeftMs / 1000);
        let hrs = Math.floor(totalSeconds / 3600);
        let mins = Math.floor((totalSeconds % 3600) / 60);
        let secs = totalSeconds % 60;

        let displayHrs = hrs < 10 ? "0" + hrs : hrs;
        let displayMins = mins < 10 ? "0" + mins : mins;
        let displaySecs = secs < 10 ? "0" + secs : secs;

        document.getElementById("countdown").innerHTML = displayHrs + ":" + displayMins + ":" + displaySecs;
      }

       updateTimer();
       let timerInterval = setInterval(updateTimer, 1000);
     </script>
 </body>
 </html>
