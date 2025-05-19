package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.dao.UserDAO;

// Uncomment if not using web.xml
// @WebServlet("/AdminUserServlet")
public class AdminUserServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("add".equals(action)) {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String phone = request.getParameter("phone");
            String address = request.getParameter("address");
            String userType = request.getParameter("user_type");

            User newUser = new User(name, email, password, phone, address, userType);
            newUser.setUserType(userType);

            UserDAO userDAO = new UserDAO();
            boolean created = userDAO.createUser(newUser);

            if (created) {
                request.setAttribute("message", "User added successfully.");
            } else {
                request.setAttribute("error", "Failed to add user.");
            }

            // Forward or redirect after add
            response.sendRedirect("adminDashboard.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("delete".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            UserDAO userDAO = new UserDAO();
            userDAO.deleteUser(userId);
            response.sendRedirect("adminDashboard.jsp");
        } else {
            // Default fallback
            response.sendRedirect("adminDashboard.jsp");
        }
    }
}
