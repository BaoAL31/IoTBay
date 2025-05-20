package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Device;
import model.dao.DBConnector;
import model.dao.DeviceDAO;

public class DeviceListServlet extends HttpServlet {
    // DAO instance for CRUD operations on devices
    private DeviceDAO deviceDAO;

    @Override
    public void init() {
        try {
            // Initialize database connection and DAO
            DBConnector dbConnector = new DBConnector();
            Connection connection = dbConnector.openConnection();
            deviceDAO = new DeviceDAO(connection);
        } catch (ClassNotFoundException | SQLException e) {
            // Log and fail fast if DB setup fails
            Logger.getLogger(DeviceListServlet.class.getName())
                  .log(Level.SEVERE, "Database connection error", e);
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Determine which action to take (list, delete, edit)
        String action = request.getParameter("action");

        if (action == null || action.trim().isEmpty()) {
            try {
                // No action: show all devices
                listDevices(request, response);
            } catch (SQLException ex) {
                // On error, forward to error page
                request.setAttribute("errorMessage", "Error processing request.");
                request.getRequestDispatcher("error.jsp").forward(request, response);
            }
            return;
        }

        try {
            switch (action) {
                case "delete":
                    deleteDevice(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                default:
                    listDevices(request, response);
                    break;
            }
        } catch (ServletException | IOException | SQLException e) {
            // Handle exceptions in action processing
            request.setAttribute("errorMessage", "Error processing request.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Handle form submissions: add or update
        String action = request.getParameter("action");
        try {
            if (action == null) {
                // Redirect back if no action provided
                response.sendRedirect("DeviceListServlet?tab=device");
            } else switch (action) {
                case "add":
                    addDevice(request, response);
                    break;
                case "update":
                    updateDevice(request, response);
                    break;
                default:
                    response.sendRedirect("DeviceListServlet?tab=device");
                    break;
            }
        } catch (ServletException | IOException e) {
            // Forward to error page on exception
            request.setAttribute("errorMessage", "Something went wrong in POST");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Fetches all devices and forwards to dashboard
    private void listDevices(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<Device> deviceList = deviceDAO.getAllDevices();
        request.setAttribute("deviceList", deviceList);
        RequestDispatcher dispatcher = request.getRequestDispatcher("adminDashboard.jsp");
        dispatcher.forward(request, response);
    }

    // Adds a new device after validating inputs
    private void addDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String name     = request.getParameter("name");
            String type     = request.getParameter("type");
            String priceStr = request.getParameter("price");
            String stockStr = request.getParameter("stock");

            // Basic validation: all fields required
            if (name == null || type == null || priceStr == null || stockStr == null ||
                name.trim().isEmpty() || type.trim().isEmpty() ||
                priceStr.trim().isEmpty() || stockStr.trim().isEmpty()) {
                request.setAttribute("errorMessage", "All fields are required.");
                request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
                return;
            }

            // Parse numeric values
            double price = Double.parseDouble(priceStr.trim());
            int stock    = Integer.parseInt(stockStr.trim());

            // Create and persist new device
            Device device = new Device(0, name.trim(), type.trim(), price, stock);
            deviceDAO.addDevice(device);

            // Redirect back to device list
            response.sendRedirect("DeviceListServlet?tab=device");
        } catch (ServletException | IOException | NumberFormatException | SQLException e) {
            // On failure, show error on dashboard
            request.setAttribute("errorMessage", "Failed to add device.");
            request.getRequestDispatcher("adminDashboard.jsp").forward(request, response);
        }
    }

    // Updates an existing device with provided form data
    private void updateDevice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int id        = Integer.parseInt(request.getParameter("id"));
            String name   = request.getParameter("name");
            String type   = request.getParameter("type");
            double price  = Double.parseDouble(request.getParameter("price"));
            int stock     = Integer.parseInt(request.getParameter("stock"));

            Device updatedDevice = new Device(id, name, type, price, stock);
            deviceDAO.updateDevice(updatedDevice);

            // Redirect back to list after update
            response.sendRedirect("DeviceListServlet?tab=device");
        } catch (IOException | NumberFormatException | SQLException e) {
            request.setAttribute("errorMessage", "Error updating device");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // Loads device data and forwards to edit form
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Device existingDevice = deviceDAO.getDeviceById(id);
        request.setAttribute("device", existingDevice);
        RequestDispatcher dispatcher = request.getRequestDispatcher("editProduct.jsp");
        dispatcher.forward(request, response);
    }

    // Deletes the specified device then reloads the list
    private void deleteDevice(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        deviceDAO.deleteDevice(id);
        response.sendRedirect("DeviceListServlet?tab=device");
    }
}
