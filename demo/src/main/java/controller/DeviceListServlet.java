package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.dao.*;
import model.Device;

import java.util.List;
import jakarta.servlet.RequestDispatcher;

public class DeviceListServlet extends HttpServlet {
    private DeviceDAO deviceDAO;

    public void init() {
        try {
            DBConnector dbConnector = new DBConnector();
            Connection connection = dbConnector.openConnection();
            deviceDAO = new DeviceDAO(connection);
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(DeviceListServlet.class.getName()).log(Level.SEVERE, "Database connection error", e);
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Device> deviceList = null;
        try {
            deviceList = deviceDAO.getAllDevices();
            request.setAttribute("deviceList", deviceList);
            RequestDispatcher dispatcher = request.getRequestDispatcher("device_list.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            Logger.getLogger(DeviceListServlet.class.getName()).log(Level.SEVERE, "Error fetching devices", e);
            request.setAttribute("errorMessage", "Unable to fetch device list. Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("error.jsp");
            dispatcher.forward(request, response);
        }
    }
}
