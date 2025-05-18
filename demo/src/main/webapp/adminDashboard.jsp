<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.dao.UserDAO, java.util.List" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");

    if (loggedUser == null || !"admin".equals(loggedUser.getUserType())) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();

    String action = request.getParameter("action");
    String searchTerm = request.getParameter("name");

    List<User> userList;

    if ("Search".equals(action) && searchTerm != null && !searchTerm.trim().isEmpty()) {
        userList = userDAO.searchUsersByName(searchTerm);
    } else {
        userList = userDAO.getUsersByType("user");
    }

    List<User> adminList = userDAO.getUsersByType("admin");
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/admin.css">
    <style>
        /* Tab font consistency */
        .tab-btn {
            font-family: 'Poppins', sans-serif;
            font-size: 15px;
            font-weight: 600;
            padding: 10px 20px;
            background: #f1f5f9;
            border: none;
            cursor: pointer;
            border-radius: 6px 6px 0 0;
            transition: background-color 0.2s ease, color 0.2s ease;
        }

        .tab-btn:hover {
            background-color: #e5e7eb;
            color: #1e40af;
        }

        .tab-btn active {
            background-color: #ffffff;
            border-bottom: 2px solid #3B82F6;
            color: #3B82F6;
        }
    </style>
    <script>
        function switchTab(tabId) {
            document.getElementById("userTab").style.display = (tabId === 'user') ? "block" : "none";
            document.getElementById("adminTab").style.display = (tabId === 'admin') ? "block" : "none";
            document.getElementById("tab-user").classList.toggle("active", tabId === 'user');
            document.getElementById("tab-admin").classList.toggle("active", tabId === 'admin');
        }

        function toggleAddUser() {
            const section = document.getElementById("addUserForm");
            section.style.display = section.style.display === "none" ? "block" : "none";
        }

        function confirmDelete(userId, isAdmin) {
            const modal = document.getElementById("confirmModal");
            const deleteLink = document.getElementById("deleteLink");
            deleteLink.href = "AdminUserServlet?action=delete&userId=" + userId;
            modal.querySelector(".modal-message").textContent = isAdmin 
                ? "Are you sure you want to delete this admin?" 
                : "Are you sure you want to delete this user?";
            modal.style.display = "flex";
        }

        function closeModal() {
            document.getElementById("confirmModal").style.display = "none";
        }
    </script>
</head>
<body>
<div class="admin-container">
    <h2>Admin Dashboard</h2>
    <p>Welcome, Admin </p>

    <!-- Toggle Add User Form -->
    <button class="btn btn-blue" onclick="openModal()">+ Add New User</button>

    <!-- Modal Background -->
    <div id="addUserModal" class="modal-overlay">
    <div class="modal-content">
        <h3>Add New User</h3>
        <form action="AdminUserServlet" method="post" class="modal-form">
        <input type="hidden" name="action" value="add" />
        
        <!-- Form fields here -->
        <label>Name:</label>
        <input type="text" name="name" required />
        
        <label>Email:</label>
        <input type="email" name="email" required />
        
        <label>Password:</label>
        <input type="text" name="password" required />
        
        <label>Phone:</label>
        <input type="text" name="phone" />
        
        <label>Address:</label>
        <input type="text" name="address" />
        
        <label>Role:</label>
        <select name="user_type">
            <option value="user">User</option>
            <option value="admin">Admin</option>
        </select>

        <div class="form-buttons">
            <button type="submit" class="btn btn-blue">Add User</button>
            <button type="button" class="btn btn-gray" onclick="closeModal()">Cancel</button>
        </div>
        </form>
    </div>
    </div>

    <script>
        function openModal() {
            document.getElementById("addUserModal").style.display = "flex";
        }

        function closeModal() {
            document.getElementById("addUserModal").style.display = "none";
        }

        // Optional: Close on click outside modal
        window.onclick = function(event) {
            const modal = document.getElementById("addUserModal");
            if (event.target === modal) {
            modal.style.display = "none";
            }
        }
    </script>

    <!-- Tabs -->
    <div class="tabs">
        <button class="tab-btn active" id="tab-user" onclick="switchTab('user')">User View</button>
        <button class="tab-btn" id="tab-admin" onclick="switchTab('admin')">Admin View</button>
    </div>

    <!-- User Tab -->
    <div id="userTab" class="tab-content" style="display: block;">
        <h3>All Users</h3>
        <form action="adminDashboard.jsp" method="get" class="search-form">
            <input type="text" name="name" placeholder="Search by full name" value="<%= (searchTerm != null) ? searchTerm : "" %>" />
            <input type="submit" name="action" value="Search" class="btn" />
            <input type="submit" name="action" value="Reset" class="btn" />
        </form>
        <table>
            <tr>
                <th>User ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th>Role</th><th>Actions</th>
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
                        | <a href="#" onclick=confirmDelete(<%= u.getUserID() %>); return false;>Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- Admin Tab -->
    <div id="adminTab" class="tab-content" style="display:none;">
        <h3>All Admins</h3>
        <table>
            <tr>
                <th>User ID</th><th>Name</th><th>Email</th><th>Phone</th><th>Address</th><th>Role</th><th>Actions</th>
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
                        | <a href="#" onclick=confirmDelete(<%= a.getUserID() %>); return false;>Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- Confirmation Modal -->
    <!-- Delete Confirmation Modal -->
    <div id="deleteConfirmModal" class="modal-overlay">
        <div class="modal-content small">
            <h3>Are you sure?</h3>
            <form id="deleteForm" method="get" action="AdminUserServlet">
            <input type="hidden" name="action" value="delete" />
            <input type="hidden" id="deleteUserId" name="userId" />
            <div class="form-buttons">
                <button type="submit" class="btn btn-red">Delete</button>
                <button type="button" class="btn btn-gray" onclick="closeDeleteModal()">Cancel</button>
            </div>
            </form>
        </div>
    </div>

    <script>
        function confirmDelete(userId) {
            document.getElementById("deleteUserId").value = userId;
            document.getElementById("deleteConfirmModal").style.display = "flex";
        }

        function closeDeleteModal() {
            document.getElementById("deleteConfirmModal").style.display = "none";
        }
    </script>



    <div class="mt-20 centered">
        <a href="welcome_page.jsp">Back to Welcome Page</a> |
        <a href="logout.jsp">Logout</a>
    </div>
</div>
</body>
</html>
