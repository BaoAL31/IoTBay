<jsp:include page="/ConnServlet"/>
<%@ page import="java.util.*" %>
<%@ page import="model.*" %>
<%@ page import="model.dao.*" %>

<%
    PaymentDAO paymentDAO = (PaymentDAO) session.getAttribute("paymentDAO");
    Integer userId = 1; // TODO: Retrieve from session

    // Read search parameters
    String searchPaymentId   = request.getParameter("paymentId")   != null 
                              ? request.getParameter("paymentId").trim()   : "";
    String searchPaymentDate = request.getParameter("paymentDate") != null 
                              ? request.getParameter("paymentDate").trim() : "";

    // Define statuses in the order to display
    List<String> statuses = Arrays.asList("pending", "completed", "cancelled");
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

    <% for (String status : statuses) {
         // fetch filtered IDs
         List<Integer> paymentIds = paymentDAO
           .getPaymentIdsByStatusAndSearchQuery(
               userId, status, 
               searchPaymentId, searchPaymentDate
           );
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
            Map<Integer,Integer> items = paymentDAO.getPaymentItems(paymentId);
      %>
      <div class="payment-block">
        <div class="payment-header">
          <h3 class="payment-id">Payment #<%= paymentId %></h3>
          <div class="payment-actions">
            <form method="POST" action="PaymentServlet" class="submit-form">
              <input type="hidden" name="action"     value="submit"/>
              <input type="hidden" name="paymentId"  value="<%= paymentId %>"/>
              <button 
                type="submit" 
                class="submit-btn <%= (status.equals("completed")||status.equals("cancelled")) ? "disabled" : "" %>">
                Submit
              </button>
            </form>
            <form method="POST" action="PaymentServlet" class="cancel-form">
              <input type="hidden" name="action"     value="cancel"/>
              <input type="hidden" name="paymentId"  value="<%= paymentId %>"/>
              <button 
                type="submit" 
                class="cancel-btn <%= (status.equals("completed")||status.equals("cancelled")) ? "disabled" : "" %>">
                Cancel
              </button>
            </form>
          </div>
        </div>

        <table class="payment-table" border="1">
          <tr>
            <th>Item</th><th>Description</th>
            <th>Quantity</th><th>Price</th><th>Total</th>
          </tr>
        <%
          for (Map.Entry<Integer,Integer> entry : items.entrySet()) {
            PaymentItem pi = paymentDAO.getPaymentItemById(entry.getKey());
            int qty = entry.getValue();
        %>
          <tr>
            <td><%= pi.getId() %></td>
            <td><%= pi.getDescription() %></td>
            <td class="qty-cell">
              <div class="qty-controls">
                <form method="POST" action="PaymentServlet" class="qty-form">
                  <input type="hidden" name="action"         value="decrement"/>
                  <input type="hidden" name="paymentId"      value="<%= paymentId %>"/>
                  <input type="hidden" name="paymentItemId"  value="<%= pi.getId() %>"/>
                  <input type="hidden" name="quantity"       value="<%= qty %>"/>
                  <button 
                    type="submit" 
                    class="qty-btn <%= (status.equals("completed")||status.equals("cancelled")) ? "disabled" : "" %>">
                    –
                  </button>
                </form>
                <span class="qty-value"><%= qty %></span>
                <form method="POST" action="PaymentServlet" class="qty-form">
                  <input type="hidden" name="action"         value="increment"/>
                  <input type="hidden" name="paymentId"      value="<%= paymentId %>"/>
                  <input type="hidden" name="paymentItemId"  value="<%= pi.getId() %>"/>
                  <input type="hidden" name="quantity"       value="<%= qty %>"/>
                  <button 
                    type="submit" 
                    class="qty-btn <%= (status.equals("completed")||status.equals("cancelled")) ? "disabled" : "" %>">
                    +
                  </button>
                </form>
              </div>
            </td>
            <td>$<%= String.format("%.2f", pi.getPrice()) %></td>
            <td>
              $<%= String.format("%.2f", pi.getPrice() * qty) %>
            </td>
          </tr>
        <%
          } // end items loop
        %>
        </table>
      </div>
      <%
          } // end paymentIds loop
        } // end empty check
      %>
    <% } // end statuses loop %>

  </div>
</body>
</html>
