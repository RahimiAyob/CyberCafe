<h2>Desk #<%= session.getAttribute("CURRENT_SEAT_ID") %></h2>

<form action="LoginServlet" method="POST">
    <h3>Member Login</h3>
    <input type="text" name="username" placeholder="Username" required><br>
    <input type="password" name="password" placeholder="Password" required><br>
    <button type="submit">Unlock PC</button>
</form>

<hr>

<div style="text-align: center;">
    <h3>Just want to play?</h3>
    <a href="guest_billing.jsp" class="guest-btn">Quick Start as Guest</a>
</div>