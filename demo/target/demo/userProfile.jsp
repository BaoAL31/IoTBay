<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User user = (User) request.getAttribute("user");
    String error = (String) request.getAttribute("error");
%>
<!DOCTYPE html>
<html>
<head>
    <title>User Profile</title>
    <link rel="stylesheet" href="css/global.css" />
    <link rel="stylesheet" href="css/user_profile.css" />
    <style>
        .modal-overlay {
            position: fixed;
            top: 0; left: 0;
            width: 100%; height: 100%;
            background: rgba(0, 0, 0, 0.6);
            display: none;
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }

        .modal-content {
            background: white;
            padding: 30px 40px;
            border-radius: 10px;
            text-align: center;
            max-width: 400px;
            width: 90%;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }

        .modal-content p {
            margin-bottom: 20px;
            font-size: 18px;
            font-weight: 500;
        }

        .modal-content .btn {
            margin: 0 10px;
        }
    </style>
</head>
<body>
<div class="profile-container">
    <%
        if (user != null) {
    %>
    <div class="profile-card">
        <h2>User Profile</h2>
        <p><strong>User ID:</strong> <%= user.getUserID() %></p>
        <p><strong>Name:</strong> <%= user.getFullName() %></p>
        <p><strong>Email:</strong> <%= user.getEmail() %></p>
        <p><strong>Phone Number:</strong> <%= user.getPhoneNumber() %></p>
        <p><strong>Address:</strong> <%= user.getAddress() %></p>

        <div class="button-group">
            <form action="UserProfileServlet" method="get" style="display:inline;">
                <input type="hidden" name="action" value="edit"/>
                <input type="hidden" name="userID" value="<%= user.getUserID() %>"/>
                <button class="btn btn-edit" type="submit">Edit Profile</button>
            </form>

            <button class="btn btn-danger" onclick="showDeleteModal(<%= user.getUserID() %>)">Delete Account</button>
        </div>

        <a href="welcome_page.jsp" class="btn btn-secondary">Back to Home</a>
    </div>

    <!-- Delete Confirmation Modal -->
    <div class="modal-overlay" id="deleteModal">
        <div class="modal-content">
            <p>Are you sure you want to delete your account?</p>
            <form id="deleteForm" action="UserProfileServlet" method="post" style="display:inline;">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="userID" id="deleteUserId" />
                <button type="submit" class="btn btn-danger">Delete</button>
                <button type="button" class="btn btn-secondary" onclick="hideDeleteModal()">Cancel</button>
            </form>
        </div>
    </div>

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
</div>

<script>
    function showDeleteModal(userId) {
        document.getElementById("deleteUserId").value = userId;
        document.getElementById("deleteModal").style.display = "flex";
    }

    function hideDeleteModal() {
        document.getElementById("deleteModal").style.display = "none";
    }
</script>
</body>
</html>
