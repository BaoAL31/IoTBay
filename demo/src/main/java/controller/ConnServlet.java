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
import jakarta.servlet.http.HttpSession;
import model.dao.DBConnector;
import model.dao.DBManager;
import model.dao.DeviceDAO;
import model.dao.OrderDAO;
import model.dao.PaymentDAO;
import model.dao.UserDAO;

// @WebServlet("/ConnServlet")
public class ConnServlet extends HttpServlet {

    private DBConnector db;
    private DBManager manager;
    private DeviceDAO deviceDAO;
    private OrderDAO orderDAO;
    private PaymentDAO paymentDAO;
    private Connection conn;
    private UserDAO userDAO;

    @Override // Create and instance of DBConnector for the deployment session
    public void init() {
        try {
            db = new DBConnector();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ConnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override // Add the DBConnector, DBManager, Connection instances to the session
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("ConnServlet.init() called");
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();

        conn = db.openConnection();

        try {
            manager = new DBManager(conn);
            orderDAO = new OrderDAO(conn);
            deviceDAO = new DeviceDAO(conn);
            paymentDAO = new PaymentDAO(conn);
            userDAO = new UserDAO(); // ✅ Add this

        } catch (SQLException ex) {
            Logger.getLogger(ConnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        session.setAttribute("manager", manager);
        session.setAttribute("orderDAO", orderDAO);
        session.setAttribute("deviceDAO", deviceDAO);
        session.setAttribute("paymentDAO", paymentDAO);
        session.setAttribute("userDAO", userDAO); // ✅ Add this
    }


    @Override // Destroy the servlet and release the resources of the application (terminate
              // also the db connection)

    public void destroy() {
        try {
            db.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(ConnServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}