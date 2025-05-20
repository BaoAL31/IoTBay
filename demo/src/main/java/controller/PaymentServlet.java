package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.dao.OrderDAO;
import model.dao.PaymentDAO;

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
                    if (!validator.validateCardNumber(cardNumber)) {
                        session.setAttribute("errorMsg", "Invalid card number format.");
                        response.sendRedirect("make_payment.jsp?orderId=" + orderId);
                        return;
                    }
                    paymentDAO.createPayment(orderId, method, cardNumber, totalAmount, "completed");
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