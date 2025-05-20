package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.*;
import model.*;

// @WebServlet("/OrderServlet")
public class OrderServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("loggedUser");
        String action = request.getParameter("action");
        System.out.println("OrderServlet.doPost() called with action: " + action);
        int orderId;
        int orderItemId;
        int quantity;
        int deviceId;
        OrderDAO orderDAO = (OrderDAO) session.getAttribute("orderDAO");
        DeviceDAO deviceDAO = (DeviceDAO) session.getAttribute("deviceDAO");

        try {
            switch (action) {
                case "add_to_order":
                    deviceId = Integer.parseInt(request.getParameter("deviceId"));
                    if (session.getAttribute("activeOrderId") == null || "-1".equals(String.valueOf(session.getAttribute("activeOrderId")))) {
                        System.out.println("activeOrderId is null");
                        orderDAO.createOrder(user.getUserID());
                    } 
                    orderId = orderDAO.findActiveOrderId(user.getUserID());
                    System.out.println("OrderServlet.doPost() - orderId: " + orderId);
                    Device device = deviceDAO.getDeviceById(deviceId);
                    orderDAO.addOrderItem(orderId, device, 1);
                    device.setStock(device.getStock() - 1);
                    deviceDAO.updateStock(deviceId, device.getStock());
                    response.sendRedirect("main_dashboard.jsp");
                    break;
                case "increment":
                    orderId = Integer.parseInt(request.getParameter("orderId"));
                    orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
                    quantity = Integer.parseInt(request.getParameter("quantity"));
                    orderDAO.updateOrderItem(orderId, orderItemId, quantity + 1);
                    device = deviceDAO.getDeviceById(orderItemId);
                    deviceDAO.updateStock(orderItemId, device.getStock() - 1);
                    response.sendRedirect("order.jsp");
                    break;
                case "decrement":
                    orderId = Integer.parseInt(request.getParameter("orderId"));
                    orderItemId = Integer.parseInt(request.getParameter("orderItemId"));
                    quantity = Integer.parseInt(request.getParameter("quantity"));
                    if (quantity == 1) {
                        orderDAO.removeOrderItem(orderId, orderItemId);
                    } else {
                        orderDAO.updateOrderItem(orderId, orderItemId, quantity - 1);
                    }
                    device = deviceDAO.getDeviceById(orderItemId);
                    deviceDAO.updateStock(orderItemId, device.getStock() + 1);
                    response.sendRedirect("order.jsp");
                    break;
                case "cancel":
                    orderId = Integer.parseInt(request.getParameter("orderId"));
                    Map<Integer, Integer> items = orderDAO.getOrderItems(orderId);
                    for (Map.Entry<Integer, Integer> e : items.entrySet()) {
                        deviceId = e.getKey();
                        int qty = e.getValue();

                        int currentStock = deviceDAO.getStock(deviceId);
                        deviceDAO.updateStock(deviceId, currentStock + qty);
                    }
                    orderDAO.updateOrderStatus(orderId, "cancelled");
                    response.sendRedirect("order.jsp");
                    break;
                case "submit":
                    orderId = Integer.parseInt(request.getParameter("orderId"));
                    response.sendRedirect("make_payment.jsp?orderId=" + orderId);
                    break;
                default:
                    throw new ServletException("Invalid action: " + action);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrderServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException("Database update failed", ex);
        }

    }

}
