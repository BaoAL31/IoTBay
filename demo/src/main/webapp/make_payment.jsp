<jsp:include page="/ConnServlet"/>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>

<%
    DeviceDAO deviceDAO = (DeviceDAO) session.getAttribute("deviceDAO");
    OrderDAO orderDAO = (OrderDAO) session.getAttribute("orderDAO");
    Integer orderId = Integer.parseInt(request.getParameter("orderId"));
    Map<Integer,Integer> items = orderDAO.getOrderItems(orderId);

    // Calculate total amount
    double totalAmount = 0.0;
    for (Map.Entry<Integer,Integer> entry : items.entrySet()) {
        Device d = deviceDAO.getDeviceById(entry.getKey());
        int qty = entry.getValue();
        totalAmount += d.getPrice() * qty;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Make Payment</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/make_payment.css">
</head>
<body>
  <div class="payment-page">
    <h1>Make a Payment</h1>

    <!-- Order Summary -->
    <h2 class="order-id">Order #<%= orderId %></h2>
    <table class="order-table" border="1">
      <tr>
        <th>Product</th>
        <th>Product Name</th>
        <th>Quantity</th>
        <th>Price</th>
        <th>Total</th>
      </tr>
      <% for (Map.Entry<Integer,Integer> entry : items.entrySet()) {
           Device d = deviceDAO.getDeviceById(entry.getKey());
           int qty = entry.getValue();
      %>
      <tr>
        <td><%= d.getId() %></td>
        <td><%= d.getName() %></td>
        <td><%= qty %></td>
        <td>$<%= String.format("%.2f", d.getPrice()) %></td>
        <td>$<%= String.format("%.2f", d.getPrice() * qty) %></td>
      </tr>
      <% } %>
    </table>

    <!-- Payment Form -->
    <h2>Payment Details</h2>
    <form method="POST" action="PaymentServlet" class="payment-form">
      <input type="hidden" name="orderId" value="<%= orderId %>" />
      <input type="hidden" name="totalAmount" value="<%= totalAmount %>" />

      <label for="method">Payment Method:</label>
      <select id="method" name="method" required>
        <option value="credit_card">Credit Card</option>
        <option value="paypal">PayPal</option>
      </select>

      <label for="cardNumber">Card Number:</label>
      <input type="text" id="cardNumber" name="cardNumber" required />

      <label for="amount">Amount:</label>
      <div id="amount" class="payment-total-input">
        $<%= String.format("%.2f", totalAmount) %>
      </div>

      <button type="submit" name="action" class="submit-btn" value="checkout">Checkout</button>
      <button type="button" name="action" class="cancel-btn" value="cancel">Cancel</button>
    </form>

    <% 
        String errorMessage = (String) session.getAttribute("errorMsg");
        if (errorMessage != null) {
    %>
      <script>
        alert("<%= errorMessage %>");
      </script>
    <%        
    }
    %>
  </div>
</body>
</html>
