package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Delivery;
import model.dao.DeliveryDAO;

// @WebServlet("/DeliveryServlet")
public class DeliveryServlet extends HttpServlet {

    /**
     * Handles POST requests for creating or updating delivery records.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        System.out.println("DeliveryServlet.doPost() called with action: " + action);

        DeliveryDAO deliveryDAO = (DeliveryDAO) session.getAttribute("deliveryDAO");
        if (deliveryDAO == null) {
            deliveryDAO = new DeliveryDAO();
            session.setAttribute("deliveryDAO", deliveryDAO);
        }

        try {
            switch (action) {
                case "create":
                    createDelivery(request, response, deliveryDAO);
                    break;
                case "update":
                    updateDelivery(request, response, deliveryDAO);
                    break;
                default:
                    throw new ServletException("Invalid action: " + action);
            }
        } catch (SQLException e) {
            Logger.getLogger(DeliveryServlet.class.getName()).log(Level.SEVERE, null, e);
            throw new ServletException("Database operation failed", e);
        }
    }

    /**
     * Handles GET requests for viewing or deleting deliveries.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");

        System.out.println("DeliveryServlet.doGet() called with action: " + action);

        DeliveryDAO deliveryDAO = (DeliveryDAO) session.getAttribute("deliveryDAO");
        if (deliveryDAO == null) {
            deliveryDAO = new DeliveryDAO();
            session.setAttribute("deliveryDAO", deliveryDAO);
        }

        try {
            switch (action) {
                case "viewAll":
                    viewAllDeliveries(request, response, deliveryDAO);
                    break;
                case "delete":
                    deleteDelivery(request, response, deliveryDAO);
                    break;
                default:
                    response.sendRedirect("index.jsp");
            }
        } catch (SQLException e) {
            Logger.getLogger(DeliveryServlet.class.getName()).log(Level.SEVERE, null, e);
            throw new ServletException("Database fetch failed", e);
        }
    }

    /**
     * Creates a new delivery record based on form data.
     */
    private void createDelivery(HttpServletRequest request, HttpServletResponse response, DeliveryDAO deliveryDAO)
            throws ServletException, IOException, SQLException {

        String trackingId = request.getParameter("trackingId");
        String orderId = request.getParameter("orderId");
        String userId = request.getParameter("userId");
        String status = request.getParameter("status");
        String carrier = request.getParameter("carrier");

        // Parse the delivery date
        Date estimatedDate;
        try {
            String dateStr = request.getParameter("estimatedDeliveryDate");
            estimatedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            request.setAttribute("error", "Invalid delivery date.");
            request.getRequestDispatcher("addDelivery.jsp").forward(request, response);
            return;
        }

        Delivery d = new Delivery(trackingId, orderId, userId, status, estimatedDate, carrier);
        boolean created = deliveryDAO.createDelivery(d);

        if (created) {
            request.setAttribute("message", "Delivery created.");
            request.getRequestDispatcher("deliverySuccess.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to create delivery.");
            request.getRequestDispatcher("addDelivery.jsp").forward(request, response);
        }
    }

    /**
     * Updates an existing delivery record with new data.
     */
    private void updateDelivery(HttpServletRequest request, HttpServletResponse response, DeliveryDAO deliveryDAO)
            throws ServletException, IOException, SQLException {

        String trackingId = request.getParameter("trackingId");
        String status = request.getParameter("status");
        String carrier = request.getParameter("carrier");

        // Parse delivery date
        Date estimatedDate;
        try {
            String dateStr = request.getParameter("estimatedDeliveryDate");
            estimatedDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (ParseException e) {
            request.setAttribute("error", "Invalid date.");
            request.getRequestDispatcher("editDelivery.jsp").forward(request, response);
            return;
        }

        // Fetch existing delivery and update fields
        Delivery d = deliveryDAO.getDeliveryByTrackingId(trackingId);
        if (d != null) {
            d.setStatus(status);
            d.setCarrier(carrier);
            d.setEstimatedDeliveryDate(estimatedDate);

            boolean updated = deliveryDAO.updateDelivery(d);

            if (updated) {
                request.setAttribute("message", "Delivery updated.");
                request.setAttribute("delivery", d);
                request.getRequestDispatcher("deliveryDetails.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Update failed.");
                request.getRequestDispatcher("editDelivery.jsp").forward(request, response);
            }
        } else {
            request.setAttribute("error", "Delivery not found.");
            request.getRequestDispatcher("editDelivery.jsp").forward(request, response);
        }
    }

    /**
     * Deletes a delivery by its tracking ID.
     */
    private void deleteDelivery(HttpServletRequest request, HttpServletResponse response, DeliveryDAO deliveryDAO)
            throws ServletException, IOException, SQLException {

        String trackingId = request.getParameter("trackingId");
        boolean deleted = deliveryDAO.deleteDelivery(trackingId);

        if (deleted) {
            request.setAttribute("message", "Delivery deleted.");
        } else {
            request.setAttribute("error", "Failed to delete delivery.");
        }

        // Refresh the delivery list
        viewAllDeliveries(request, response, deliveryDAO);
    }

    /**
     * Displays all deliveries for a given user ID.
     */
    private void viewAllDeliveries(HttpServletRequest request, HttpServletResponse response, DeliveryDAO deliveryDAO)
            throws ServletException, IOException, SQLException {

        String userId = request.getParameter("userId");
        List<Delivery> deliveries = deliveryDAO.getAllDeliveriesByUser(userId);

        request.setAttribute("deliveries", deliveries);
        request.getRequestDispatcher("trackDelivery.jsp").forward(request, response);
    }
}
