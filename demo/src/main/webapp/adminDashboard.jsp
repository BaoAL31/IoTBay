<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.dao.UserDAO, java.util.List" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");

    if (loggedUser == null || !"admin".equals(loggedUser.getUserType())) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();

    // Optional: search term
    String searchTerm = request.getParameter("name");

    List<User> userList;
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        userList = userDAO.searchUsersByName(searchTerm);
    } else {
        userList = userDAO.getUsersByType("user");
    }

    List<User> adminList = userDAO.getUsersByType("admin");
%>
<html>
<head>
    <title>Admin Dashboard</title>
    <script>
        function showTab(tabId) {
            document.getElementById("userTab").style.display = (tabId === 'user') ? "block" : "none";
            document.getElementById("adminTab").style.display = (tabId === 'admin') ? "block" : "none";
        }
    </script>
</head>
<body>
    <h2>Admin Dashboard</h2>
    <p>Welcome, Admin <%= loggedUser.getFullName() %></p>

    <!-- Add User Form -->
    <h3>Add New User</h3>
    <form action="AdminUserServlet" method="post">
        <input type="hidden" name="action" value="add" />
        Name: <input type="text" name="name" required />
        Email: <input type="email" name="email" required />
        Password: <input type="text" name="password" required />
        Phone: <input type="text" name="phone" />
        Address: <input type="text" name="address" />
        Role:
        <select name="user_type">
            <option value="user">User</option>
            <option value="admin">Admin</option>
        </select>
        <input type="submit" value="Add User" />
    </form>

    <hr/>

    <!-- Tab Buttons -->
    <button onclick="showTab('user')">User View</button>
    <button onclick="showTab('admin')">Admin View</button>

    <!-- Search Bar for Users -->
    <div id="userTab" style="display:block;">
        <h3>All Users</h3>
        <form action="adminDashboard.jsp" method="get">
            <input type="text" name="name" placeholder="Search by full name" value="<%= (searchTerm != null) ? searchTerm : "" %>" />
            <input type="submit" value="Search" />
        </form>
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
            <% for (User u : userList) { %>
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
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- Admin Tab -->
    <div id="adminTab" style="display:none;">
        <h3>All Admins</h3>
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
            <% for (User a : adminList) { %>
            <tr>
                <td><%= a.getUserID() %></td>
                <td><%= a.getFullName() %></td>
                <td><%= a.getEmail() %></td>
                <td><%= a.getPhoneNumber() %></td>
                <td><%= a.getAddress() %></td>
                <td><%= a.getUserType() %></td>
                <td>
                    <a href="UserProfileServlet?action=view&userID=<%= a.getUserID() %>">View</a> |
                    <a href="UserProfileServlet?action=edit&userID=<%= a.getUserID() %>">Edit</a>
                    <% if (a.getUserID() != loggedUser.getUserID()) { %>
                        | <a href="AdminUserServlet?action=delete&userId=<%= a.getUserID() %>"
                             onclick="return confirm('Are you sure you want to delete this admin?');">Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <br/>
    <p><a href="welcome_page.jsp">Back to Welcome Page</a></p>
    <p><a href="logout.jsp">Logout</a></p>
</body>
</html>
