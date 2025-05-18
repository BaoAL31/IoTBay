package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import model.dao.UserDAO;

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

            System.out.println("AdminUserServlet received role: " + userType);

            User newUser = new User(name, email, password, phone, address);
            newUser.setUserType(userType); // Make sure your User class has this field

            System.out.println("User object set role: " + newUser.getUserType());

            UserDAO userDAO = new UserDAO();
            userDAO.createUser(newUser);
        }

        response.sendRedirect("adminDashboard.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        int userId = Integer.parseInt(request.getParameter("userId"));

        if ("delete".equals(action)) {
            UserDAO userDAO = new UserDAO();
            userDAO.deleteUser(userId);
        }

        response.sendRedirect("adminDashboard.jsp");
    }
}
