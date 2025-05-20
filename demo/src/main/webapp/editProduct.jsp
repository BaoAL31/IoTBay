<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="model.Device" %>
<%
    Device device = (Device) request.getAttribute("device");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Edit Device</title>
    <link rel="stylesheet" href="css/global.css">
    <style>
        .card {
            max-width: 600px;
            margin: 60px auto;
            padding: 30px;
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 8px 30px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
            font-weight: 700;
        }
        label {
            font-weight: 600;
            display: block;
            margin: 15px 0 5px;
        }
        input[type="text"], input[type="number"] {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }
        .form-buttons {
            display: flex;
            justify-content: space-between;
            margin-top: 25px;
        }
        .btn {
            padding: 10px 20px;
            font-weight: bold;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }
        .btn-blue {
            background-color: #5a73f0;
            color: white;
        }
        .btn-gray {
            background-color: #e0e0e0;
            color: #333;
        }
    </style>
</head>
<body>
<div class="card">
    <h2>Edit Device</h2>
    <form action="DeviceListServlet" method="post">
        <input type="hidden" name="action" value="update" />
        <input type="hidden" name="id" value="<%= device.getId() %>" />

        <label>Device Name:</label>
        <input type="text" name="name" value="<%= device.getName() %>" required />

        <label>Device Type:</label>
        <input type="text" name="type" value="<%= device.getType() %>" required />

        <label>Unit Price:</label>
        <input type="number" step="0.01" name="price" value="<%= device.getPrice() %>" required />

        <label>Stock:</label>
        <input type="number" name="stock" value="<%= device.getStock() %>" required />

        <div class="form-buttons">
            <button type="submit" class="btn btn-blue">Update Device</button>
            <a href="DeviceListServlet" class="btn btn-gray">Cancel</a>
        </div>
    </form>
</div>
</body>
</html>
