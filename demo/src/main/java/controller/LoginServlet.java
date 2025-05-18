package controller;

import java.io.IOException;
import java.sql.SQLException;

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

            // Step 2: Check password match
            if (!userByEmail.getPassword().equals(password)) {
                session.setAttribute("errorMsg", "Incorrect password.");
                response.sendRedirect("login.jsp");
                return;
            }

            // Login successful
            session.setAttribute("loggedUser", userByEmail);

            if ("admin".equals(userByEmail.getUserType())) {
                response.sendRedirect("adminDashboard.jsp");
            } else {
                response.sendRedirect("welcome_page.jsp");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Database error during login", e);
        }
    }

    // protected void doPost(HttpServletRequest request, HttpServletResponse response)
    //         throws ServletException, IOException {

    //     // 1- retrieve session
    //     HttpSession session = request.getSession();
    //     Validator validator = new Validator();

    //     // 2- get email and password from form
    //     String email = request.getParameter("email");
    //     String password = request.getParameter("password");

    //     // 3- get DBManager from session
    //     DBManager manager = (DBManager) session.getAttribute("manager");

    //     System.out.println("LoginServlet.doPost() called");

    //     if (manager == null) {
    //         throw new ServletException("DBManager not found in session");
    //     }

    //     // 4- validation
    //     if (!validator.validateEmail(email)) {
    //         session.setAttribute("errorMsg", "Invalid email format.");
    //         response.sendRedirect("login.jsp");
    //         return;
    //     }

    //     if (!validator.validatePassword(password)) {
    //         session.setAttribute("errorMsg", "Invalid password format.");
    //         response.sendRedirect("login.jsp");
    //         return;
    //     }

    //     try {
    //         User user = manager.findUser(email, password);

    //         if (user != null) {
    //             // Store logged user with full info (including userType)
    //             session.setAttribute("loggedUser", user);

    //             System.out.println("Login success. User role: " + user.getUserType());

    //             // Optionally: redirect based on role
    //             if ("admin".equals(user.getUserType())) {
    //                 response.sendRedirect("adminDashboard.jsp");
    //             } else {
    //                 response.sendRedirect("welcome_page.jsp");
    //             }
    //         } else {
    //             session.setAttribute("errorMsg", "User does not exist.");
    //             response.sendRedirect("login.jsp");
    //         }

    //     } catch (SQLException ex) {
    //         Logger.getLogger(LoginServlet.class.getName()).log(Level.SEVERE, null, ex);
    //         throw new ServletException("Database error during login", ex);
    //     }
    // }
}
