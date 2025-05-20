<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User, model.Device, model.dao.UserDAO, model.dao.DeviceDAO, model.dao.DBConnector, java.util.List" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null || !"admin".equals(loggedUser.getUserType())) {
        response.sendRedirect("unauthorized.jsp");
        return;
    }

    UserDAO userDAO = new UserDAO();
    DBConnector db = new DBConnector();
    DeviceDAO deviceDAO = new DeviceDAO(db.openConnection());

    
    String action = request.getParameter("action");
    String searchTerm = request.getParameter("name");
    String deviceSearchName = request.getParameter("deviceName");
    String deviceSearchType = request.getParameter("deviceType");

    List<User> userList = ("Search".equals(action) && searchTerm != null && !searchTerm.trim().isEmpty()) ?
                          userDAO.searchUsersByName(searchTerm) :
                          userDAO.getUsersByType("user");

    List<User> adminList = userDAO.getUsersByType("admin");

    List<Device> deviceList = (deviceSearchName != null || deviceSearchType != null) ?
                              deviceDAO.searchDevices(deviceSearchName != null ? deviceSearchName : "", 
                                                      deviceSearchType != null ? deviceSearchType : "") :
                              deviceDAO.getAllDevices();
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

    <!-- Toggle Add User Form -->
    <button class="btn btn-blue" onclick="openModal()">+ Add New User</button>
    <button class="btn btn-blue" onclick="openDeviceModal()">+ Add New Device</button>

    <!-- Tabs -->
    <!-- Tab Buttons -->
    <div class="tabs">
        <button class="tab-btn" id="tab-user" onclick="switchTab('user')">Users</button>
        <button class="tab-btn" id="tab-admin" onclick="switchTab('admin')">Admins</button>
        <button class="tab-btn" id="tab-device" onclick="switchTab('device')">Devices</button>
    </div>

    <!-- USER TAB -->
    <div id="userTab" class="tab-content" style="display: block;">
        <h3>All Users</h3>
        <form action="adminDashboard.jsp" method="get" class="search-form">
            <input type="hidden" name="tab" value="user" />
            <input type="text" name="name" placeholder="Search by full name" value="<%= (searchTerm != null) ? searchTerm : "" %>" class="text-field"/>
            <input type="submit" name="action" value="Search" class="btn" />
            <a href="adminDashboard.jsp?tab=user" class="btn">Reset</a>
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
                        | <a href="#" onclick="confirmDelete(<%= u.getUserID() %>, false); return false;">Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- ADMIN TAB -->
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
                        | <a href="#" onclick="confirmDelete(<%= a.getUserID() %>, true); return false;">Delete</a>
                    <% } else { %> | (You) <% } %>
                </td>
            </tr>
            <% } %>
        </table>
    </div>

    <!-- DEVICE TAB -->
    <div id="deviceTab" class="tab-content" style="display:none;">
        <h3>All Devices</h3>
        <form action="adminDashboard.jsp" method="get" class="search-form">
            <input type="hidden" name="tab" value="device" />
            <input type="text" name="deviceName" placeholder="Search by device name" value="<%= deviceSearchName != null ? deviceSearchName : "" %>" class="text-field"/>
            <input type="text" name="deviceType" placeholder="Type (e.g., Sensor, Camera)" value="<%= deviceSearchType != null ? deviceSearchType : "" %>" class="text-field"/>
            <input type="submit" value="Search" class="btn" />
            <a href="adminDashboard.jsp?tab=device" class="btn">Reset</a>
        </form>
        <table>
            <tr>
                <th>ID</th><th>Name</th><th>Type</th><th>Price</th><th>Stock</th><th>Actions</th>
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

    <!-- Device Deletion Confirmation Modal -->
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


    <!-- Delete Confirmation Modal for Users/Admins -->
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



    <script>
        function openModal() {
            document.getElementById("addUserModal").style.display = "flex";
        }

        function closeUserModal() {
            document.getElementById("addUserModal").style.display = "none";
        }

        // Optional: Close modal if clicked outside
        window.addEventListener("click", function(event) {
            const modal = document.getElementById("addUserModal");
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });

        function confirmDelete(userId, isAdmin) {
            document.getElementById("deleteUserId").value = userId;
            document.getElementById("deleteConfirmModal").style.display = "flex";

            // Optional: Update modal message depending on role
            const message = isAdmin ? "Are you sure you want to delete this admin?" : "Are you sure you want to delete this user?";
            document.querySelector("#deleteConfirmModal .modal-message").textContent = message;
        }

        function closeDeleteModal() {
            document.getElementById("deleteConfirmModal").style.display = "none";
        }

        // Close when clicking outside the modal
        window.addEventListener("click", function(event) {
            const modal = document.getElementById("deleteConfirmModal");
            if (event.target === modal) {
                modal.style.display = "none";
            }
        });

        function switchTab(tabId) {
            const tabs = ["user", "admin", "device"];
            tabs.forEach(id => {
                document.getElementById(id + "Tab").style.display = (id === tabId) ? "block" : "none";
                document.getElementById("tab-" + id).classList.toggle("active", id === tabId);
            });
        }

        // Detect tab from query param on page load
        window.addEventListener("DOMContentLoaded", () => {
            const urlParams = new URLSearchParams(window.location.search);
            const activeTab = urlParams.get("tab") || "user";  // default to 'user'
            switchTab(activeTab);
        });


        function openDeviceModal() {
            document.getElementById("addDeviceModal").style.display = "flex";
        }

        function closeDeviceModal() {
            document.getElementById("addDeviceModal").style.display = "none";
        }

        function confirmDeleteDevice(deviceId) {
            document.getElementById("deleteDeviceId").value = deviceId;
            document.getElementById("deleteDeviceModal").style.display = "flex";
        }

        function closeDeleteDeviceModal() {
            document.getElementById("deleteDeviceModal").style.display = "none";
        }

        window.addEventListener("click", function(event) {
            const modal = document.getElementById("deleteDeviceModal");
            if (event.target === modal) {
            modal.style.display = "none";
            }
        });

    </script>

    <div class="mt-20 centered">
        <a href="welcome_page.jsp">Back to Welcome Page</a> |
        <a href="logout.jsp">Logout</a>
    </div>
</div>
</body>
</html>
