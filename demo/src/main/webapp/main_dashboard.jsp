<jsp:include page="/ConnServlet" />
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>
<%@ page import="java.util.*" %>
<%
    model.User loggedUser = (model.User) session.getAttribute("loggedUser");
    if (loggedUser == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    model.dao.DeviceDAO deviceDAO = (model.dao.DeviceDAO) session.getAttribute("deviceDAO");
    model.dao.OrderDAO orderDAO = (model.dao.OrderDAO) session.getAttribute("orderDAO");

    if (deviceDAO == null || orderDAO == null) {
        model.dao.DBConnector db = new model.dao.DBConnector();
        java.sql.Connection conn = db.openConnection();
        if (deviceDAO == null) {
            deviceDAO = new model.dao.DeviceDAO(conn);
            session.setAttribute("deviceDAO", deviceDAO);
        }
        if (orderDAO == null) {
            orderDAO = new model.dao.OrderDAO(conn);
            session.setAttribute("orderDAO", orderDAO);
        }
    }

    session.setAttribute("activeOrderId", orderDAO.findActiveOrderId(loggedUser.getUserID()));
    String deviceName = request.getParameter("deviceName");
    String deviceType = request.getParameter("deviceType");
    List<Device> deviceList;

    if ((deviceName != null && !deviceName.trim().isEmpty()) || (deviceType != null && !deviceType.trim().isEmpty())) {
        deviceList = deviceDAO.searchDevices(
            deviceName != null ? deviceName.trim() : "",
            deviceType != null ? deviceType.trim() : ""
        );
    } else {
        deviceList = deviceDAO.getAllDevices();
    }


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

    <h1>Device Catalogue</h1>
    <form class="search-form" method="get" action="main_dashboard.jsp">
        <input type="text" name="deviceName" placeholder="Search by device name" />
        <input type="text" name="deviceType" placeholder="Type (e.g., Sensor, Camera)" />
        <button type="submit" class="btn btn-blue">Search</button>
        <a href="main_dashboard.jsp" class="btn btn-gray">Reset</a>
    </form>


    <table class="device-table">
        <tr>
            <th>Device Name</th>
            <th>Type</th>
            <th>Price</th>
            <th>Stock</th>
            <th>Action</th>
        </tr>
        <% 
        for (Device device : deviceList) { 
        %>
        <tr>
            <td><%= device.getName() %></td>
            <td><%= device.getType() %></td>
            <td>$<%= String.format("%.2f", device.getPrice()) %></td>
            <td><%= device.getStock() %></td>
            <td>
              <form method="POST" action="OrderServlet">
                <input type="hidden" name="action" value="add_to_order"/>
                <input type="hidden" name="deviceId" value="<%= device.getId() %>"/>
                <button 
                    type="submit" 
                    class="add-to-order-btn <%= device.getStock() == 0 ? " disabled" : "" %>"
                    <%= device.getStock() == 0 ? "disabled" : "" %>>
                    Add to Order
                </button>
              </form>
            </td>
        </tr>
        <% } %>
    </table>
</body>
</html>
