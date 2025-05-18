<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>IoTBay - Home</title>
    <link rel="stylesheet" type="text/css" href="css/global.css" />
    <link rel="stylesheet" type="text/css" href="css/landing.css" />
</head>
<body>
    <div class="container">
        <h1>Welcome to <span class="highlight">IoTBay</span></h1>
        <div class="button-group">
            <a href="login.jsp" class="btn">Login</a>
            <a href="registerUser.jsp" class="btn">Register</a>
        </div>
    </div>
    <jsp:include page="/ConnServlet" flush="true" />
</body>
</html>
