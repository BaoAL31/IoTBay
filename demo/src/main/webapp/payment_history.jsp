<jsp:include page="/ConnServlet"/>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>

<%
    DeviceDAO deviceDAO = (DeviceDAO) session.getAttribute("deviceDAO");
    OrderDAO orderDAO   = (OrderDAO) session.getAttribute("orderDAO");
    PaymentDAO paymentDAO = (PaymentDAO) session.getAttribute("paymentDAO");
    Integer userId = 1; // TODO: Retrieve from session

    // Read search parameters
    String searchPaymentId   = request.getParameter("paymentId")   != null 
                              ? request.getParameter("paymentId").trim()   : "";
    String searchPaymentDate = request.getParameter("paymentDate") != null 
                              ? request.getParameter("paymentDate").trim() : "";

    // Define statuses in the order to display (currently only "completed")
    List<String> statuses = Arrays.asList("completed");
    Map<Integer,Integer> items = new HashMap<>();

%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Management</title>
    <link rel="stylesheet" href="css/global.css">
    <link rel="stylesheet" href="css/payment.css">
</head>
<body>
  <div class="payment-page">
    <h1>Payment Management</h1>

    <!-- Combined Search Form -->
    <form class="search-form" method="GET" action="payment.jsp">
        <h3>ID:</h3>
        <input 
          type="text" 
          name="paymentId" 
          placeholder="Search by Payment ID" 
          class="search-input"
          value="<%= searchPaymentId %>"/>
        <h3>&nbsp;</h3>
        <h3>Created Date:</h3>
        <input 
          type="text" 
          name="paymentDate" 
          placeholder="YYYY-MM-DD" 
          class="search-input"
          value="<%= searchPaymentDate %>"/>
        <button type="submit" class="search-btn">Search</button>
    </form>

    <% 
    for (String status : statuses) {
      // fetch filtered IDs
      List<Integer> paymentIds = paymentDAO.getPaymentIdsByStatusAndSearchQuery(userId, status, searchPaymentId, searchPaymentDate);
    %>
      <hr>
      <h2 class="payment-status-title">
        <%= status.toUpperCase() %> PAYMENTS
      </h2>

      <%
        if (paymentIds.isEmpty()) {
      %>
        <p>No <%= status %> payments.</p>
      <%
        } else {
          for (Integer paymentId : paymentIds) {
            Integer orderId = paymentDAO.getOrderIdByPaymentId(paymentId);
            items = orderDAO.getOrderItems(orderId);
      %>
        <div class="order-block">
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
      <% } %>
  </div>
</body>
</html>