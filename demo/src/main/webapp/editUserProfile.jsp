<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.User" %>
<%
    User user = (User) request.getAttribute("user");
%>
<html>
<head>
    <title>Edit User Profile</title>
</head>
<body>
    <h2>Edit Profile</h2>

    <form action="UserProfileServlet" method="post">
        <input type="hidden" name="action" value="update"/>
        <input type="hidden" name="userID" value="<%= user.getUserID() %>"/>

        <label>Name:</label><br/>
        <input type="text" name="name" value="<%= user.getFullName() %>" required/>

        <label>Email:</label><br/>
        <input type="email" name="email" value="<%= user.getEmail() %>" required/><br/><br/>

        <label>Password:</label><br/>
        <input type="password" name="password" value="<%= user.getPassword() %>" required/><br/><br/>

        <label>Phone Number:</label><br/>
        <input type="text" name="phoneNumber" value="<%= user.getPhoneNumber() %>" required/><br/><br/>

        <label>Address:</label><br/>
        <input type="text" name="address" value="<%= user.getAddress() %>" required/><br/><br/>

        <input type="submit" value="Update Profile"/>
    </form>

    <p style="color:red;">
        ${error != null ? error : ""}
    </p>
</body>
</html>
