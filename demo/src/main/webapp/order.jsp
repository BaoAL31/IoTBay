<jsp:include page="/ConnServlet"/>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>
<%@ page import="java.util.*" %>

<%
    OrderDAO orderDAO   = (OrderDAO) session.getAttribute("orderDAO");
    DeviceDAO deviceDAO = (DeviceDAO) session.getAttribute("deviceDAO");
    Integer userId = 1; //TODO: Should be retreived from session

%>

<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/order.css">
    <title>Your Orders</title>
</head>
<body>
  <div class="orders-page">
    <h1>Your Orders</h1>

    <form class="search-form">
        <h3>ID:</h3>
        <input type="text" name="orderId" placeholder="Search by ID" class="search-input" />
        <h3>&nbsp;</h3>
        <h3>Created Date:</h3>
        <input type="text" name="createdDate" placeholder="YYYY-MM-DD HH:MM:SS" class="search-input" />
        <button type="submit" class="search-btn">Search</button>
    </form>

    <% 
    List<String> statuses = Arrays.asList("draft", "submitted", "cancelled");
    String searchQueryId = request.getParameter("orderId") != null ? request.getParameter("orderId") : "";
    String searchQueryDate = request.getParameter("createdDate") != null ? request.getParameter("createdDate") : "";

    for (String status : statuses) {
        List<Integer> orderIds = orderDAO.getOrderIdsByStatusAndSearchQuery(userId, status, searchQueryId, searchQueryDate);
        Map<Integer, Integer> items = new HashMap<>();
    %>
    <hr>
    <h2 class="order-status-title"> <%= status.toUpperCase() %> ORDERS </h2>
        <% for (Integer orderId : orderIds) { 
            items = orderDAO.getOrderItems(orderId);
        %>
        <div class="order-block">
            <div class="order-header">
                <h3 class="order-id">Order #<%= orderId %> </h3>
                <div class="order-actions">
                    <form method="POST" action="OrderServlet" class="submit-form">
                        <input type="hidden" name="action" value="submit"/>
                        <input type="hidden" name="orderId" value="<%= orderId %>"/>
                        <button type="submit" class="submit-btn <%= (status.equals("submitted") || status.equals("cancelled")) ? "disabled" : "" %>">Submit</button>
                    </form>
                    
                    <form method="POST" action="OrderServlet" class="cancel-form">
                        <input type="hidden" name="action" value="cancel"/>
                        <input type="hidden" name="orderId" value="<%= orderId %>"/>
                        <button type="submit" class="cancel-btn <%= (status.equals("submitted") || status.equals("cancelled")) ? "disabled" : "" %>">Cancel</button>
                    </form>
                </div>
            </div>
            <h4>Created Date: <%= orderDAO.getOrderCreatedDate(orderId) %></h4>
            <table class="order-table" border="1">
            <tr>
                <th>Product</th>
                <th>Product Name</th>
                <th>Quantity</th>
                <th>Price</th>
                <th>Total</th>
                <%-- <th>Actions</th> --%>
            </tr>

            <% for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                Device orderItem = deviceDAO.getDeviceById(entry.getKey());
                Integer quantity = entry.getValue();
            %>
            <tr>
                <td><%= orderItem.getId() %></td>
                <td><%= orderItem.getName() %></td>
                <td class="qty-cell">
                <div class="qty-controls">
                    <form method="POST" action="OrderServlet" class="qty-form">
                        <input type="hidden" name="action" value="decrement"/>
                        <input type="hidden" name="orderId" value="<%= orderId %>"/>
                        <input type="hidden" name="orderItemId" value="<%= orderItem.getId() %>"/>
                        <input type="hidden" name="quantity" value="<%= quantity %>"/>
                        <button type="submit" class="qty-btn <%= (status.equals("submitted") || status.equals("cancelled")) ? "disabled" : "" %>">-</button>
                    </form>
                    
                    <span class="qty-value"><%= quantity %></span>
                    
                    <form method="POST" action="OrderServlet" class="qty-form">
                        <input type="hidden" name="action" value="increment"/>
                        <input type="hidden" name="orderId" value="<%= orderId %>"/>
                        <input type="hidden" name="orderItemId" value="<%= orderItem.getId() %>"/>
                        <input type="hidden" name="quantity" value="<%= quantity %>"/>
                        <button type="submit" class="qty-btn <%= (status.equals("submitted") || status.equals("cancelled")) ? "disabled" : "" %>">+</button>
                    </form>
                </div>
                </td>
                <td>$<%= orderItem.getPrice() %></td>
                <td>$<%= String.format("%.2f", orderItem.getPrice() * quantity) %></td>
                <%-- <td>
                <div class="action-controls">
                    <form action="OrderServlet" method="POST" class="remove-form">
                        <input type="hidden" name="action" value="remove"/>
                        <input type="hidden" name="orderId" value="<%= orderId %>"/>
                        <input type="hidden" name="orderItemId" value="<%= orderItem.getId() %>"/>
                        <button type="submit" class="remove-btn <%= (status.equals("submitted") || status.equals("cancelled")) ? "disabled" : "" %>">Remove</button>                    
                    </form>
                </div>
                </td> --%>
            </tr>
            <% } %>
            </table>
        </div>
        <% } %>
        <% } %>
  </div>
</body>
</html>
