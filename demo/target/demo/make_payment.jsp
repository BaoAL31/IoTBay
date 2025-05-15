<jsp:include page="/ConnServlet"/>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>

<%
  DeviceDAO deviceDAO = (DeviceDAO) session.getAttribute("deviceDAO");
  OrderDAO orderDAO   = (OrderDAO) session.getAttribute("orderDAO");
  PaymentDAO paymentDAO = (PaymentDAO) session.getAttribute("paymentDAO");
  Integer orderId = Integer.parseInt(request.getParameter("orderId"));
  Map<Integer, Integer> items = orderDAO.getOrderItems(orderId);
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Make Payment</title>
    <link rel="stylesheet" href="css/global.css">
</head>
<body>
  <div class="payment-page">
    <h1>Make a Payment</h1>

    <!-- Display Order Items -->
    <h2 class="order-id">Order #<%= orderId %> </h2> 
    <table class="order-table" border="1">
        <tr>
            <th>Product</th>
            <th>Product Name</th>
            <th>Quantity</th>
            <th>Price</th>
            <th>Total</th>
        </tr>
        <% 
            for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
                Device orderItem = deviceDAO.getDeviceById(entry.getKey());
                Integer quantity = entry.getValue();
        %>
        <tr>
            <td><%= orderItem.getId() %></td>
            <td><%= orderItem.getName() %></td>
            <td><%= quantity %></td>
            <td>$<%= orderItem.getPrice() %></td>
            <td>$<%= String.format("%.2f", orderItem.getPrice() * quantity) %></td>
        </tr>
        <% } %>
    </table>

    <!-- Payment Form -->
    <h2>Payment Details</h2>
    <form method="POST" action="PaymentServlet">
        <input type="hidden" name="orderId" value="<%= orderId %>" />

        <label for="method">Payment Method:</label>
        <select id="method" name="method" required>
            <option value="credit_card">Credit Card</option>
            <option value="paypal">PayPal</option>
        </select>

        <label for="cardNumber">Card Number:</label>
        <input type="text" id="cardNumber" name="cardNumber" required />

        <label for="amount">Amount:</label>
        <input type="number" id="amount" name="amount" step="0.01" required />

        <button type="submit">Submit Payment</button>
    </form>
  </div>
</body>
</html>