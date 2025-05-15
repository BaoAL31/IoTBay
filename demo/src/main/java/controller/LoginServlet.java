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


        // 1- retrieve the current session
        HttpSession session = request.getSession();
        // 2- create an instance of the Validator class
        Validator validator = new Validator();
        // 3- capture the posted email
        String email = request.getParameter("email");
        // 4- capture the posted password
        String password = request.getParameter("password");
        // 5- retrieve the manager instance from session
        DBManager manager = (DBManager) session.getAttribute("manager");

        System.out.println("LoginServlet.doPost() called");

        if (manager == null) {
            throw new ServletException("DBManager not found in session");
        }
        User user = null;

        try {
            user = manager.findUser(email, password);
        } catch (SQLException ex) {
            Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (!validator.validateEmail(email)) {
            System.out.println("Invalid email format");
            // 8-set incorrect email error to the session
            session.setAttribute("errorMsg", "Invalid email format.");
            // 9- redirect user back to the login.jsp
            // request.getRequestDispatcher("login.jsp").include(request, response);
            response.sendRedirect("login.jsp");
        } else if (!validator.validatePassword(password)) {
            // 11-set incorrect password error to the session
            session.setAttribute("errorMsg", "Invalid password format.");
            // 12- redirect user back to the login.jsp
            response.sendRedirect("login.jsp");
        } else if (user != null) {
            // 13-save the logged in user object to the session
            session.setAttribute("user", user);
            // 14- redirect user to the main.jsp
            request.getRequestDispatcher("welcome_page.jsp").include(request, response);
        } else {
            // 15-set user does not exist error to the session
            session.setAttribute("errorMsg", "User does not exist.");
            // 16- redirect user back to the login.jsp
            request.getRequestDispatcher("login.jsp").include(request, response);
        }

    }
}
