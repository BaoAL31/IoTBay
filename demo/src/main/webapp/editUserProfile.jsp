<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User user = (User) request.getAttribute("user");
    if (user == null) {
        out.println("<p style='color:red;'>Error: User not loaded properly.</p>");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Edit User Profile</title>
    <link rel="stylesheet" type="text/css" href="css/global.css">
    <link rel="stylesheet" type="text/css" href="css/edit_profile.css">
</head>
<body>
    <div class="edit-profile-container">
        <div class="edit-profile-card">
            <h2>Edit Profile</h2>
            <form action="UserProfileServlet" method="post">
                <input type="hidden" name="action" value="update"/>
                <input type="hidden" name="userID" value="<%= user.getUserID() %>"/>

                <label>Name:</label>
                <input type="text" name="name" value="<%= user.getFullName() %>" required/>

                <label>Email:</label>
                <input type="email" name="email" value="<%= user.getEmail() %>" required/>

                <label>Password:</label>
                <input type="password" name="password" value="<%= user.getPassword() %>" required/>

                <label>Phone Number:</label>
                <input type="text" name="phoneNumber" value="<%= user.getPhoneNumber() %>" required/>

                <label>Address:</label>
                <input type="text" name="address" value="<%= user.getAddress() %>" required/>

                <div class="button-group">
                    <button type="submit" class="btn">Update Profile</button>
                    <a href="UserProfileServlet?action=view&userID=<%= user.getUserID() %>" class="btn-cancel">Cancel</a>
                </div>
            </form>
            <p class="error-message">
                ${error != null ? error : ""}
            </p>
        </div>
    </div>
</body>
</html>
