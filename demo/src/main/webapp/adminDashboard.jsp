<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.dao.UserDAO, java.util.List" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");

    if (loggedUser == null || !"admin".equals(loggedUser.getUserType())) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    List<User> users = userDAO.getAllUsers();  // You must implement this method in UserDAO
%>
<html>
<head>
    <title>Admin Dashboard</title>
</head>
<body>
    <h2>Admin Dashboard</h2>
    <p>Welcome, Admin <%= loggedUser.getFullName() %></p>

    <table border="1" cellpadding="5" cellspacing="0">
        <tr>
            <th>User ID</th>
            <th>Name</th>
            <th>Email</th>
            <th>Phone</th>
            <th>Address</th>
            <th>Role</th>
            <th>Actions</th>
        </tr>
        <%
            for (User u : users) {
        %>
        <tr>
            <td><%= u.getUserID() %></td>
            <td><%= u.getFullName() %></td>
            <td><%= u.getEmail() %></td>
            <td><%= u.getPhoneNumber() %></td>
            <td><%= u.getAddress() %></td>
            <td><%= u.getUserType() %></td>
            <td>
                <a href="UserProfileServlet?action=view&userID=<%= u.getUserID() %>">View</a> |
                <a href="UserProfileServlet?action=edit&userID=<%= u.getUserID() %>">Edit</a>
            </td>
        </tr>
        <% } %>
    </table>

    <p><a href="welcome_page.jsp">Back to Welcome Page</a></p>
    <p><a href="logout.jsp">Logout</a></p>
</body>
</html>
