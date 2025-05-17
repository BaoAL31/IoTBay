<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");

    if (loggedUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<html>
<head>
    <title>Welcome</title>
</head>
<body>
    <h2>Welcome, <%= loggedUser.getFullName() %>!</h2>
    <p>You are logged in as: <strong><%= loggedUser.getUserType() %></strong></p>

    <% if ("admin".equals(loggedUser.getUserType())) { %>
        <p><a href="adminDashboard.jsp">Go to Admin Dashboard</a></p>
    <% } else { %>
        <p><a href="UserProfileServlet?action=view&userID=<%= loggedUser.getUserID() %>">View My Profile</a></p>
    <% } %>

    <p><a href="logout.jsp">Logout</a></p>
</body>
</html>
