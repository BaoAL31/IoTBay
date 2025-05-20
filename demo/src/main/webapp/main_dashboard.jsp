<jsp:include page="/ConnServlet" />
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>
<%@ page import="java.util.*" %>
<%
    User loggedUser = (User) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    DeviceDAO deviceDAO = (DeviceDAO) session.getAttribute("deviceDAO");
    List<Device> deviceList = deviceDAO.getAllDevices();
%>
<!DOCTYPE html>
<html>
<head>
    <title>IoTBay - Dashboard</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/main_dashboard.css">
</head>
<!-- Navbar -->

<body>
    <nav class="navbar">
        <a href="main_dashboard.jsp" class="nav-item current">Main Dashboard</a>
        <a href="order.jsp" class="nav-item">View Orders</a>
        <a href="payment_history.jsp" class="nav-item">Payment History</a>
        <div class="nav-right">
            <a href="logout.jsp" class="nav-item">Logout</a>
        </div>
    </nav>

    <h2>Device Catalogue</h2>
    <table class="device-table">
        <tr>
            <th>Device Name</th>
            <th>Type</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Action</th>
        </tr>
        <% for (Device device : deviceList) { %>
        <tr>
            <td><%= device.getName() %></td>
            <td><%= device.getType() %></td>
            <td>$<%= String.format("%.2f", device.getPrice()) %></td>
            <td><%= device.getStock() %></td>
            <td>
              <form method="POST" action="CartServlet" style="margin:0;">
                  <input type="hidden" name="deviceId" value="<%= device.getId() %>"/>
                  <button type="submit" class="add-to-order-btn">Add to Order</button>
              </form>
            </td>
        </tr>
        <% } %>
    </table>
</body>
</html>
