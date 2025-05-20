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
<link rel="stylesheet" href="css/global.css" />
<link rel="stylesheet" href="css/welcome.css" />
<head>
    <title>Welcome</title>
</head>
<body>

    <div class="welcome-container">
        <div class="welcome-card">
            <h2>Welcome, <%= loggedUser.getFullName() %>!</h2>
            <p class="subtitle">You are logged in as: <strong><%= loggedUser.getUserType() %></strong></p>

            <% if ("admin".equals(loggedUser.getUserType())) { %>
                <a class="btn" href="adminDashboard.jsp">Go to Admin Dashboard</a>
            <% } else { %>
                <a class="btn" href="UserProfileServlet?action=view&userID=<%= loggedUser.getUserID() %>">View My Profile</a>
                <a class="btn" href="main_dashboard.jsp">View Products</a>
            <% } %>

            <a class="btn btn-secondary" href="logout.jsp">Logout</a>
        </div>
    </div>

</body>
</html>
