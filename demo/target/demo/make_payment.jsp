<jsp:include page="/ConnServlet"/>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>

<%
    PaymentDAO paymentDAO = (PaymentDAO) session.getAttribute("paymentDAO");
    Integer userId = 1; // TODO: Retrieve from session
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

    <form method="POST" action="PaymentServlet">
        <label for="orderId">Order ID:</label>
        <input type="text" id="orderId" name="orderId" required />

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
