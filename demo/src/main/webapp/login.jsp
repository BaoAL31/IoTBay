<%@ page import="model.User" %>
<%
    if (session.getAttribute("manager") == null) {
        response.sendRedirect("ConnServlet");
        return;
    }
%>


<!DOCTYPE html>
<html>
<head>
    <title>IoTBay - Login</title>
</head>
<body>
    <h1>Login</h1>

    <form action="LoginServlet" method="POST">
        Email: <input type="text" name="email" required><br>
        Password: <input type="password" name="password" required><br>
        <input type="submit" name="loginButton" value="Login">
    </form>
    <br><br>
    <a href="registerUser.jsp">Register</a>

    <% 
        String errorMessage = (String) session.getAttribute("errorMsg");
        if (errorMessage != null) {
    %>
        <h2 style="color: red;"><%= errorMessage %></h2>
    <%
            session.removeAttribute("errorMsg"); // clears after displaying
        }
    %>
</body>
</html>
