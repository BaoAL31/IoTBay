<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.Device, model.dao.UserDAO, model.dao.DeviceDAO, model.dao.OrderDAO, model.dao.DBConnector, java.util.List" %>
<%
    // Ensure only logged-in admins can access this page
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null || !"admin".equals(loggedUser.getUserType())) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    // Initialize DAOs
    UserDAO userDAO = new UserDAO();
    DBConnector db = new DBConnector();
    DeviceDAO deviceDAO = new DeviceDAO(db.openConnection());

    // Read request parameters for filtering/search
    String action = request.getParameter("action");
    String searchTerm = request.getParameter("name");
    String deviceSearchName = request.getParameter("deviceName");
    String deviceSearchType = request.getParameter("deviceType");

    // Fetch user lists based on search or default user type
    List<User> userList = ("Search".equals(action) && searchTerm != null && !searchTerm.trim().isEmpty())
                          ? userDAO.searchUsersByName(searchTerm)
                          : userDAO.getUsersByType("user");
    List<User> adminList = userDAO.getUsersByType("admin");

    // Fetch device list based on search filters or all devices
    List<Device> deviceList = (deviceSearchName != null || deviceSearchType != null)
                              ? deviceDAO.searchDevices(
                                    deviceSearchName != null ? deviceSearchName : "",
                                    deviceSearchType != null ? deviceSearchType : "")
                              : deviceDAO.getAllDevices();
%>

<!DOCTYPE html>
<html>
<head>
    <title>Admin Dashboard</title>
    <link rel="stylesheet" href="css/admin.css">
</head>
<body>
<div class="admin-container">
    <h2>Admin Dashboard</h2>
    <p>Welcome, Admin</p>

    <!-- Buttons to open modals for adding users/devices -->
    <button class="btn btn-blue" onclick="openModal()">+ Add New User</button>
    <button class="btn btn-blue" onclick="openDeviceModal()">+ Add New Device</button>

    <!-- Tab navigation -->
    <div class="tabs">
        <button class="tab-btn" id="tab-user" onclick="switchTab('user')">Users</button>
        <button class="tab-btn" id="tab-admin" onclick="switchTab('admin')">Admins</button>
        <button class="tab-btn" id="tab-device" onclick="switchTab('device')">Devices</button>
    </div>

    <!-- USER TAB CONTENT -->
    <div id="userTab" class="tab-content" style="display: block;">
        <h3>All Users</h3>
        <!-- Search form for users -->
        <form action="adminDashboard.jsp" method="get" class="search-form">
            <input type="hidden" name="tab" value="user" />
            <input type="text" name="name" placeholder="Search by full name"
                   value="<%= (searchTerm != null) ? searchTerm : "" %>" class="text-field"/>
            <input type="submit" name="action" value="Search" class="btn" />
            <a href="adminDashboard.jsp?tab=user" class="btn">Reset</a>
        </form>
        <%-- Show message if no users found --%>
        <% if (userList != null && userList.isEmpty()) { %>
            <p style="color: #a00; font-weight: 600;">
                No users found matching "<%= searchTerm %>"
            </p>
        <% } %>
        <!-- User table -->
        <table>
            <tr>
                <th>User ID</th><th>Name</th><th>Email</th>
                <th>Phone</th><th>Address</th><th>Role</th><th>Actions</th>
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
                        | <a href="#" onclick="confirmDelete(<%= u.getUserID() %>, false); return false;">Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- ADMIN TAB CONTENT -->
    <div id="adminTab" class="tab-content" style="display:none;">
        <h3>All Admins</h3>
        <!-- Admins only need listing, no search -->
        <table>
            <tr>
                <th>User ID</th><th>Name</th><th>Email</th>
                <th>Phone</th><th>Address</th><th>Role</th><th>Actions</th>
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
                        | <a href="#" onclick="confirmDelete(<%= a.getUserID() %>, true); return false;">Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- DEVICE TAB CONTENT -->
    <div id="deviceTab" class="tab-content" style="display:none;">
        <h3>All Devices</h3>
        <!-- Search form for devices -->
        <form action="adminDashboard.jsp" method="get" class="search-form">
            <input type="hidden" name="tab" value="device" />
            <input type="text" name="deviceName" placeholder="Search by device name"
                   value="<%= deviceSearchName != null ? deviceSearchName : "" %>" class="text-field"/>
            <input type="text" name="deviceType" placeholder="Type (e.g., Sensor, Camera)"
                   value="<%= deviceSearchType != null ? deviceSearchType : "" %>" class="text-field"/>
            <input type="submit" value="Search" class="btn" />
            <a href="adminDashboard.jsp?tab=device" class="btn">Reset</a>
        </form>
        <!-- Device table -->
        <table>
            <tr>
                <th>ID</th><th>Name</th><th>Type</th>
                <th>Price</th><th>Stock</th><th>Actions</th>
            </tr>
            <% for (Device d : deviceList) { %>
            <tr>
                <td><%= d.getId() %></td>
                <td><%= d.getName() %></td>
                <td><%= d.getType() %></td>
                <td>$<%= d.getPrice() %></td>
                <td><%= d.getStock() %></td>
                <td>
                    <a href="DeviceListServlet?action=edit&id=<%= d.getId() %>">Edit</a> |
                    <a href="#" onclick="confirmDeleteDevice(<%= d.getId() %>); return false;">Delete</a>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- ADD DEVICE MODAL -->
    <div id="addDeviceModal" class="modal-overlay">
        <div class="modal-content">
            <h3>Add New Device</h3><br>
            <form action="DeviceListServlet" method="post" class="modal-form">
                <input type="hidden" name="action" value="add" />
                <label>Device Name:</label>
                <input type="text" name="name" required class="text-field"/>
                <label>Type:</label>
                <input type="text" name="type" required class="text-field"/>
                <label>Unit Price:</label>
                <input type="number" step="0.01" name="price" required class="text-field"/>
                <label>Stock:</label>
                <input type="number" name="stock" required class="text-field"/>
                <div class="form-buttons">
                    <button type="submit" class="btn btn-blue">Add Device</button>
                    <button type="button" class="btn btn-gray" onclick="closeDeviceModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- DEVICE DELETE CONFIRMATION MODAL -->
    <div id="deleteDeviceModal" class="modal-overlay">
        <div class="modal-content small">
            <h3>Confirm Delete</h3>
            <p>Are you sure you want to delete this device?</p>
            <form id="deleteDeviceForm" method="get" action="DeviceListServlet">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" id="deleteDeviceId" name="id" />
                <div class="form-buttons">
                    <button type="submit" class="btn btn-red">Delete</button>
                    <button type="button" class="btn btn-gray" onclick="closeDeleteDeviceModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- ADD USER MODAL -->
    <div id="addUserModal" class="modal-overlay">
        <div class="modal-content">
            <h3>Add New User</h3><br>
            <form action="AdminUserServlet" method="post" class="modal-form">
                <input type="hidden" name="action" value="add" />
                <label>Name:</label>
                <input type="text" name="name" required class="text-field"/>
                <label>Email:</label>
                <input type="email" name="email" required class="text-field"/>
                <label>Password:</label>
                <input type="text" name="password" required class="text-field"/>
                <label>Phone:</label>
                <input type="text" name="phone" class="text-field"/>
                <label>Address:</label>
                <input type="text" name="address" class="text-field"/>
                <label>Role:</label>
                <select name="user_type" required class="text-field">
                    <option value="user">User</option>
                    <option value="admin">Admin</option>
                </select>
                <div class="form-buttons">
                    <button type="submit" class="btn btn-blue">Add User</button>
                    <button type="button" class="btn btn-gray" onclick="closeUserModal()">Cancel</button>
                </div>
            </form>
        </div>
    </div>

    <!-- USER DELETE CONFIRMATION MODAL -->
    <div id="deleteConfirmModal" class="modal-overlay">
        <div class="modal-content small">
            <h3 class="modal-message">Are you sure you want to delete this user?</h3>
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

    <!-- JavaScript for modals and tab switching -->
    <script>
        // User modal controls
        function openModal() { document.getElementById("addUserModal").style.display = "flex"; }
        function closeUserModal() { document.getElementById("addUserModal").style.display = "none"; }

        // Confirm user deletion
        function confirmDelete(userId, isAdmin) {
            document.getElementById("deleteUserId").value = userId;
            document.getElementById("deleteConfirmModal").style.display = "flex";
            // Update message based on role
            const msg = isAdmin 
                        ? "Are you sure you want to delete this admin?" 
                        : "Are you sure you want to delete this user?";
            document.querySelector("#deleteConfirmModal .modal-message").textContent = msg;
        }
        function closeDeleteModal() { document.getElementById("deleteConfirmModal").style.display = "none"; }

        // Device modal controls
        function openDeviceModal() { document.getElementById("addDeviceModal").style.display = "flex"; }
        function closeDeviceModal() { document.getElementById("addDeviceModal").style.display = "none"; }
        function confirmDeleteDevice(deviceId) {
            document.getElementById("deleteDeviceId").value = deviceId;
            document.getElementById("deleteDeviceModal").style.display = "flex";
        }
        function closeDeleteDeviceModal() { document.getElementById("deleteDeviceModal").style.display = "none"; }

        // Tab switching logic
        function switchTab(tabId) {
            ["user","admin","device"].forEach(id => {
                document.getElementById(id + "Tab").style.display = (id === tabId) ? "block" : "none";
                document.getElementById("tab-" + id).classList.toggle("active", id === tabId);
            });
        }
        // On load, activate proper tab from URL param
        window.addEventListener("DOMContentLoaded", () => {
            const params = new URLSearchParams(window.location.search);
            switchTab(params.get("tab") || "user");
        });
    </script>

    <!-- Footer links -->
    <div class="mt-20 centered">
        <a href="welcome_page.jsp">Back to Welcome Page</a> |
        <a href="logout.jsp">Logout</a>
    </div>
</div>
</body>
</html>
