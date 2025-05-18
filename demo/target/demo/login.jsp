<%@ page import="model.User" %>
<jsp:include page="ConnServlet" />

<!DOCTYPE html>
<html>
<head>
    <title>IoTBay - Login</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/login.css">
</head>
<body>
    <div class="form-container">
        <h1>Login</h1>

        <form action="LoginServlet" method="POST">
            <label for="email">Email:</label>
            <input type="text" name="email" id="email" required>

            <label for="password">Password:</label>
            <input type="password" name="password" id="password" required>

            <input type="submit" name="loginButton" value="Login" class="btn">
        </form>

        <div class="form-footer">
            <a href="registerUser.jsp">Don't have an account? Register Now</a>
        </div>

        <%
            String errorMessage = (String) session.getAttribute("errorMsg");
            if (errorMessage != null) {
        %>
            <p class="error-message"><%= errorMessage %></p>
        <%
                session.removeAttribute("errorMsg");
            }
        %>
    </div>
</body>
</html>
