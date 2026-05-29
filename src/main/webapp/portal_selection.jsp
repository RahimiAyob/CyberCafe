<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <title>Select Portal Option</title>
</head>
<body>
    <div style="text-align: center; margin-top: 50px;">
        <!-- pull the seat ID we just saved in the servlet -->
        <h2>💻 Connected to Desk #<%= session.getAttribute("CURRENT_SEAT_ID") %></h2>
        <p>Choose your login method to unlock this PC:</p>

        <hr style="width: 200px; margin: 20px auto;">

        <!-- buttons to split into member or guest flows -->
        <div style="margin-top: 20px;">
            <a href="login.jsp" style="padding: 10px 20px; background: #007bff; color: white; text-decoration: none; margin-right: 10px; border-radius: 5px;">
                Log In as Member
            </a>
            <a href="guest_billing.jsp" style="padding: 10px 20px; background: #28a745; color: white; text-decoration: none; border-radius: 5px;">
                Continue as Guest
            </a>
        </div>
    </div>
</body>
</html>