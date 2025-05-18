package controller;

import model.Payment;
import model.User;
import model.dao.DeviceDAO;
import model.dao.OrderDAO;
import model.dao.PaymentDAO;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class PaymentServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();

        Validator validator = new Validator();
        OrderDAO orderDAO = (OrderDAO) session.getAttribute("orderDAO");
        PaymentDAO paymentDAO = (PaymentDAO) session.getAttribute("paymentDAO");

        String action = request.getParameter("action");
        int orderId = Integer.parseInt(request.getParameter("orderId"));
        double totalAmount = Double.parseDouble(request.getParameter("totalAmount"));

        try {
            switch (action) {
                case "checkout":
                    String method = request.getParameter("method");
                    String cardNumber = request.getParameter("cardNumber");
                    String status = "submitted";
                    if (!validator.validateCardNumber(cardNumber)) {
                        session.setAttribute("errorMsg", "Invalid card number format.");
                        response.sendRedirect("make_payment.jsp?orderId=" + orderId);
                        return;
                    }
                    paymentDAO.createPayment(orderId, method, cardNumber, totalAmount, status);
                    orderDAO.updateOrderStatus(orderId, "submitted");
                    response.sendRedirect("main_dashboard.jsp");
                    break;

                case "cancel":
                    response.sendRedirect("order.jsp");
                    break;

                default:
                    throw new ServletException("Unknown action: " + action);
            }
        } catch (SQLException ex) {
            throw new ServletException(ex);
        }
    }

}