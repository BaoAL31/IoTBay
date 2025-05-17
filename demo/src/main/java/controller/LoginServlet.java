package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.dao.DBManager;

// @WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1- retrieve session
        HttpSession session = request.getSession();
        Validator validator = new Validator();

        // 2- get email and password from form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // 3- get DBManager from session
        DBManager manager = (DBManager) session.getAttribute("manager");

        System.out.println("LoginServlet.doPost() called");

        if (manager == null) {
            throw new ServletException("DBManager not found in session");
        }

        // 4- validation
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
            User user = manager.findUser(email, password);

            if (user != null) {
                // Store logged user with full info (including userType)
                session.setAttribute("loggedUser", user);

                System.out.println("Login success. User role: " + user.getUserType());

                // Optionally: redirect based on role
                if ("admin".equals(user.getUserType())) {
                    response.sendRedirect("adminDashboard.jsp");
                } else {
                    response.sendRedirect("welcome_page.jsp");
                }
            } else {
                session.setAttribute("errorMsg", "User does not exist.");
                response.sendRedirect("login.jsp");
            }

        } catch (SQLException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException("Database error during login", ex);
        }
    }
}
