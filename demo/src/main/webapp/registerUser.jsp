<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Register User</title>
    <link rel="stylesheet" href="css/global.css">
    <style>
        .form-container {
            max-width: 450px;
            margin: 50px auto;
            padding: 30px;
            background: white;
            border-radius: 10px;
            box-shadow: 0 0 15px rgba(0,0,0,0.05);
        }

        .form-container h2 {
            text-align: center;
            margin-bottom: 25px;
        }

        label {
            display: block;
            font-weight: bold;
            margin-bottom: 4px;
        }

        input[type="text"],
        input[type="email"],
        input[type="password"] {
            width: 100%;
            padding: 10px;
            margin-bottom: 15px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 14px;
        }

        .btn {
            width: 100%;
            background-color: #007bff; /* Blue */
            color: white;
            padding: 12px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 15px;
            font-weight: bold;
            transition: background-color 0.3s ease;
        }

        .btn:hover {
            background-color: #0056b3;
        }

        .error-message {
            color: red;
            text-align: center;
            margin-top: 10px;
        }
    </style>
</head>
<body>
    <div class="form-container">
        <h2>Register New User</h2>

        <form action="UserProfileServlet" method="post">
            <input type="hidden" name="action" value="register"/>

            <label for="name">Name:</label>
            <input type="text" name="name" id="name" required/>

            <label for="email">Email:</label>
            <input type="email" name="email" id="email" required/>

            <label for="password">Password:</label>
            <input type="password" name="password" id="password" required/>

            <label for="phoneNumber">Phone Number:</label>
            <input type="text" name="phoneNumber" id="phoneNumber" required/>

            <label for="address">Address:</label>
            <input type="text" name="address" id="address" required/>
            <select name="user_type">
                <option value="user">User</option>
                <option value="staff">Staff</option>
            </select>

            <input type="password" name="staff_password" placeholder="Enter staff key (if any)">


            <input type="submit" class="btn" value="Register"/>
        </form>

        <p class="error-message">${error != null ? error : ""}</p>
    </div>
</body>
</html>
