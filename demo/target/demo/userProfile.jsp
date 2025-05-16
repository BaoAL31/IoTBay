<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User user = (User) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>User Profile</title>
</head>
<body>
    <h2>User Profile</h2>

    <%
        if (user != null) {
    %>
        <p><strong>User ID:</strong> <%= user.getUserID() %></p>
        <p><strong>Name:</strong> <%= user.getName() %></p>
        <p><strong>Email:</strong> <%= user.getEmail() %></p>
        <p><strong>Phone Number:</strong> <%= user.getPhoneNumber() %></p>
        <p><strong>Address:</strong> <%= user.getAddress() %></p>

        <form action="UserProfileServlet" method="get">
            <input type="hidden" name="action" value="edit"/>
            <input type="hidden" name="userID" value="<%= user.getUserID() %>"/>
            <input type="submit" value="Edit Profile"/>
        </form>
    <%
        } else if (error != null) {
    %>
        <p style="color:red;"><%= error %></p>
    <%
        } else {
    %>
        <p>No user data available.</p>
    <%
        }
    %>
</body>
</html>
