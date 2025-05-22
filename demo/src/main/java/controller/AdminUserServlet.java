package controller;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.dao.UserDAO;

// Servlet for admin actions on users (add, delete)
public class AdminUserServlet extends HttpServlet {

    /**
     * Handles POST requests for adding new users.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");  // e.g. "add"
        User loggedUser = (User) request.getSession().getAttribute("loggedUser");

        if (!"admin".equals(loggedUser.getUserType())) {
            response.sendRedirect("unauthorized.jsp");
            return;
        }
        
        if ("add".equals(action)) {
            // read form parameters
            String name     = request.getParameter("name");
            String email    = request.getParameter("email");
            String password = request.getParameter("password");
            String phone    = request.getParameter("phone");
            String address  = request.getParameter("address");
            String userType = request.getParameter("user_type");

            // create User object
            User newUser = new User(name, email, password, phone, address, userType);

            UserDAO userDAO = new UserDAO();
            boolean created = userDAO.createUser(newUser);  // insert into DB

            // set feedback message
            if (created) {
                request.setAttribute("message", "User added successfully.");
            } else {
                request.setAttribute("error", "Failed to add user.");
            }

            // redirect back to appropriate tab in admin dashboard
            if ("user".equals(userType)) {
                response.sendRedirect("adminDashboard.jsp?tab=user");
            } else {
                response.sendRedirect("adminDashboard.jsp?tab=staff");
            }
        } else if ("toggleStatus".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId); // You'll need to ensure this method exists
            String currentStatus = user.getStatus();
            String newStatus = "activated".equals(currentStatus) ? "deactivated" : "activated";
            try {
                userDAO.updateUserStatus(userId, newStatus);
            } catch (SQLException | ClassNotFoundException ex) {
                
            }
        
            // Redirect to the appropriate tab
            String tab = "user".equals(user.getUserType()) ? "user" : "staff";
            response.sendRedirect("adminDashboard.jsp?tab=" + tab);
        }
        
    }

    /**
     * Handles GET requests for deleting users.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");  // e.g. "delete"

        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
        if (!"admin".equals(loggedUser.getUserType())) {
            response.sendRedirect("unauthorized.jsp");
            return;
        }


        if ("delete".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));  // ID to delete
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(userId);  // fetch before deleting

            if (user != null) {
                userDAO.deleteUser(userId);  // perform deletion

                // redirect to correct dashboard tab based on deleted user's type
                if ("admin".equalsIgnoreCase(user.getUserType())) {
                    response.sendRedirect("adminDashboard.jsp?tab=staff");
                } else {
                    response.sendRedirect("adminDashboard.jsp?tab=user");
                }
            } else {
                // no such user, default back to user tab
                response.sendRedirect("adminDashboard.jsp?tab=user");
            }
        } else {
            // unrecognized action, default to user tab
            response.sendRedirect("adminDashboard.jsp?tab=user");
        }
    }

}
