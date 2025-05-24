package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.dao.AccessLogDAO;
import model.dao.DBManager; 

// @WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Validator validator = new Validator();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        DBManager manager = (DBManager) session.getAttribute("manager");

        if (manager == null) {
            throw new ServletException("DBManager not found in session");
        }

        // Validate email and password format
        if (!validator.validateEmail(email)) {
            session.setAttribute("errorMsg", "Invalid email format.");
            response.sendRedirect("login.jsp");
            return;
        }

        if (!validator.validatePassword(password)) {
            session.setAttribute("errorMsg", "Invalid password format.");
            response.sendRedirect("login.jsp");
            return;
        }

        try {
            // Step 1: Find user by email only
            User userByEmail = manager.findUserByEmail(email);

            if (userByEmail == null) {
                session.setAttribute("errorMsg", "User does not exist.");
                response.sendRedirect("login.jsp");
                return;
            }

            if (!"activated".equals(userByEmail.getStatus())) {
                session.setAttribute("errorMsg", "Account is deactivated. Contact Staff.");
                response.sendRedirect("login.jsp");
                return;
            }

            // Step 2: Check password match
            if (!userByEmail.getPassword().equals(password)) {
                session.setAttribute("errorMsg", "Incorrect password.");
                response.sendRedirect("login.jsp");
                return;
            }

            // Log successful login to access_log table
            AccessLogDAO accessLogDAO = new AccessLogDAO(manager.getConnection());
            accessLogDAO.insertAccessLog(userByEmail.getUserID(), "login");

            // Login successful
            session.setAttribute("loggedUser", userByEmail);

            // Redirect to welcome page (can customize by userType if needed)
            response.sendRedirect("welcome_page.jsp");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error during login", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");

        if (loggedUser != null) {
            // Insert logout access log
            DBManager manager = (DBManager) session.getAttribute("manager");
            AccessLogDAO accessLogDAO = new AccessLogDAO(manager.getConnection());

            try {
                accessLogDAO.insertAccessLog(loggedUser.getUserID(), "logout");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            // Logout the user
            session.invalidate();  // End session
        }

        // Redirect to login page
        response.sendRedirect("login.jsp");
    }
}
