<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.dao.UserDAO, java.util.List" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");

    if (loggedUser == null || !"admin".equals(loggedUser.getUserType())) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    List<User> users = userDAO.getAllUsers();  // Make sure this method is implemented in UserDAO
%>
<html>
<head>
    <title>Admin Dashboard</title>
</head>
<body>
    <h2>Admin Dashboard</h2>
    <p>Welcome, Admin <%= loggedUser.getFullName() %></p>

    <!-- Add New User Form -->
    <h3>Add New User</h3>
    <form action="AdminUserServlet" method="post">
        <input type="hidden" name="action" value="add" />
        Name: <input type="text" name="name" required />
        Email: <input type="email" name="email" required />
        Password: <input type="text" name="password" required />
        Phone: <input type="text" name="phone" />
        Address: <input type="text" name="address" />
        Role: 
        <select name="user_type" required>
            <option value="user">User</option>
            <option value="admin">Admin</option>
        </select>

        <input type="submit" value="Add User" />
    </form>

    <hr/>

    <!-- User Table -->
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
        <% for (User u : users) { %>
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
                <% if (u.getUserID() != loggedUser.getUserID()) { %>
                    | <a href="AdminUserServlet?action=delete&userId=<%= u.getUserID() %>" 
                         onclick="return confirm('Are you sure you want to delete this user?');">Delete</a>
                <% } else { %>
                    | (You)
                <% } %>
            </td>
        </tr>
        <% } %>
    </table>

    <p><a href="welcome_page.jsp">Back to Welcome Page</a></p>
    <p><a href="logout.jsp">Logout</a></p>
</body>
</html>
