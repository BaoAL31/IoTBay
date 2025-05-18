<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register User</title>
    <link rel="stylesheet" type="text/css" href="css/global.css" />
    <style>
        .register-container {
            max-width: 450px;
            margin: 50px auto;
            padding: 30px;
            background-color: white;
            box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
            border-radius: 8px;
        }

        .register-container h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        .register-container label {
            font-weight: bold;
            display: block;
            margin-bottom: 5px;
        }

        .register-container input[type="text"],
        .register-container input[type="email"],
        .register-container input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 20px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }

        .register-container input[type="submit"] {
            width: 100%;
            background-color: #4CAF50;
            color: white;
            padding: 12px;
            font-size: 16px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .register-container input[type="submit"]:hover {
            background-color: #45a049;
        }

        .error-msg {
            color: red;
            text-align: center;
            font-weight: bold;
        }

        .btn-register {
            width: 100%;
            background-color: #4CAF50;
            color: white;
            padding: 12px;
            font-size: 16px;
            border: none;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
            transition: background-color 0.3s ease;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .btn-register:hover {
            background-color: #45a049;
        }

        .btn-register:active {
            background-color: #3d8b40;
            transform: scale(0.98);
        }
    </style>
</head>
<body>

<div class="register-container">
    <h2>Register New User</h2>

    <form action="UserProfileServlet" method="post">
        <input type="hidden" name="action" value="register"/>

        <label for="name">Name:</label>
        <input type="text" name="name" required/>

        <label for="email">Email:</label>
        <input type="email" name="email" required/>

        <label for="password">Password:</label>
        <input type="password" name="password" required/>

        <label for="phoneNumber">Phone Number:</label>
        <input type="text" name="phoneNumber" required/>

        <label for="address">Address:</label>
        <input type="text" name="address" required/>

        <button type="submit" class="btn-register">Register</button>
    </form>

    <p class="error-msg">${error != null ? error : ""}</p>
</div>

</body>
</html>
