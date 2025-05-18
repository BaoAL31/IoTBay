<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Unauthorized Access</title>
    <link rel="stylesheet" href="css/global.css" />
    <style>
        body {
            background-color: #f4f6f9;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            margin: 0;
        }

        .unauth-container {
            background: #fff;
            padding: 40px;
            border-radius: 10px;
            text-align: center;
            box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
        }

        h2 {
            color: #cc0000;
            margin-bottom: 15px;
        }

        p {
            margin-bottom: 25px;
            color: #333;
        }

        a {
            text-decoration: none;
            background-color: #4f6bed;
            color: white;
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: 500;
        }

        a:hover {
            background-color: #3c52cc;
        }
    </style>
</head>
<body>
    <div class="unauth-container">
        <h2>Access Denied</h2>
        <p>You are not authorized to view this page.</p>
        <a href="login.jsp">Return to Login</a>
    </div>
</body>
</html>
