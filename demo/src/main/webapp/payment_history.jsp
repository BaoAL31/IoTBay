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
    <link rel="stylesheet" href="css/order.css">
    
    <%-- <link rel="stylesheet" href="css/make_payment.css"> --%>
</head>
<body>
  <div class="payment-page">
    <h1>Payment Management</h1>

    <!-- Combined Search Form -->
    <form class="search-form" method="GET" action="payment_history.jsp">
        <h3>Order ID:</h3>
        <input type="text" name="orderId" placeholder="Search by Order ID" class="search-input"/>
        <h3>&nbsp;</h3>
        <h3>Created Date:</h3>
        <input type="text" name="paymentDate" placeholder="YYYY-MM-DD" class="search-input"/>
        <button type="submit" class="search-btn">Search</button>
    </form>

    <% 
    String searchQueryId = request.getParameter("orderId") != null ? request.getParameter("orderId") : "";
    String searchQueryDate = request.getParameter("createdDate") != null ? request.getParameter("createdDate") : "";
    
    for (String status : statuses) {
      // fetch filtered IDs
      // List<Integer> paymentIds = paymentDAO.getPaymentIdsByStatusAndSearchQuery(userId, status, searchPaymentId, searchPaymentDate);
      List<Integer> orderIds = orderDAO.getOrderIdsByStatusAndSearchQuery(userId, "submitted", searchQueryId, searchQueryDate);
    %>
      <hr>
      <h2 class="payment-status-title">
        <%= status.toUpperCase() %> PAYMENTS
      </h2>

      <%
        if (orderIds.isEmpty()) {
      %>
        <p>No <%= status %> payments.</p>
      <%
        } else {
          for (Integer orderId : orderIds) {
            // Integer orderId = paymentDAO.getOrderIdByPaymentId(paymentId);
            items = orderDAO.getOrderItems(orderId);
      %>
        <div class="order-block">
        <h3 class="order-id">Order #<%= orderId %> </h3>
          <h4>Created Date: <%= orderDAO.getOrderCreatedDate(orderId) %></h4>
          <table class="order-table" border="1">
          <tr>
              <%-- <th>Product Id</th> --%>
              <th>Product Name</th>
              <th>Quantity</th>
              <th>Price</th>
              <th>Item Total</th>
              <%-- <th>Actions</th> --%>
          </tr>

          <% 
          Double orderTotal = 0.0;
          for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
              Device orderItem = deviceDAO.getDeviceById(entry.getKey());
              Integer quantity = entry.getValue();
              Double itemTotal = orderItem.getPrice() * quantity; 
              orderTotal += itemTotal;
          %>
          <tr>
              <%-- <td><%= orderItem.getId() %></td> --%>
              <td><%= orderItem.getName() %></td>
              <td><%= quantity %></td>
              <td>$<%= orderItem.getPrice() %></td>
              <td>$<%= String.format("%.2f", itemTotal) %></td>
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
          <tr>
              <td colspan="4" class="order-total">
                <strong>Order Total: $<%= String.format("%.2f", orderTotal) %></strong>
              </td>
          </tr>
          </table>
        </div>
        <% } %>
      <% } %>
      <% } %>
  </div>
</body>
</html>