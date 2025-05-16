<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Register User</title>
</head>
<body>
    <h2>Register New User</h2>

    <form action="UserProfileServlet" method="post">
        <input type="hidden" name="action" value="register"/>

        <label>Name:</label><br/>
        <input type="text" name="name" required/><br/><br/>

        <label>Email:</label><br/>
        <input type="email" name="email" required/><br/><br/>

        <label>Password:</label><br/>
        <input type="password" name="password" required/><br/><br/>

        <label>Phone Number:</label><br/>
        <input type="text" name="phoneNumber" required/><br/><br/>

        <label>Address:</label><br/>
        <input type="text" name="address" required/><br/><br/>

        <input type="submit" value="Register"/>
    </form>

    <p style="color:red;">
        ${error != null ? error : ""}
    </p>
</body>
</html>
