package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import model.dao.UserDAO;

// Uncomment the following line if you want to use annotations instead of web.xml mapping
// @WebServlet("/UserProfileServlet")
public class UserProfileServlet extends HttpServlet {

    /**
     * Handles POST requests for operations like registering a new user and updating user profile.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the current session (or create one if it doesn't exist)
        HttpSession session = request.getSession();
        // Retrieve the action parameter to determine which operation to perform
        String action = request.getParameter("action");
        System.out.println("UserProfileServlet.doPost() called with action: " + action);

        // Retrieve or create a UserDAO instance and store it in session for reuse
        UserDAO userDAO = (UserDAO) session.getAttribute("userDAO");
        if (userDAO == null) {
            userDAO = new UserDAO();
            session.setAttribute("userDAO", userDAO);
        }

        try {
            // Decide operation based on 'action' parameter value
            switch (action) {
                case "register":
                    // Process user registration
                    registerUser(request, response, userDAO);
                    break;
                case "update":
                    // Process user profile update
                    updateUser(request, response, userDAO);
                    break;
                default:
                    // If action is unrecognized, throw an error
                    throw new ServletException("Invalid action: " + action);
            }
        } catch (SQLException ex) {
            // Log SQL exceptions and wrap them as ServletExceptions
            Logger.getLogger(UserProfileServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException("Database operation failed", ex);
        }
    }

    /**
     * Handles GET requests for viewing and editing the user profile.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Get the current session
        HttpSession session = request.getSession();
        // Retrieve the 'action' parameter to determine the requested operation
        String action = request.getParameter("action");
        System.out.println("UserProfileServlet.doGet() called with action: " + action);

        // Retrieve or create a UserDAO instance as needed
        UserDAO userDAO = (UserDAO) session.getAttribute("userDAO");
        if (userDAO == null) {
            userDAO = new UserDAO();
            session.setAttribute("userDAO", userDAO);
        }

        try {
            // Determine which action to perform based on the 'action' parameter
            switch (action) {
                case "view":
                    // Display the user profile
                    viewUser(request, response, userDAO);
                    break;
                case "edit":
                    // Display the edit form with current user details
                    showEditForm(request, response, userDAO);
                    break;
                default:
                    // Redirect to the home page if action is not recognized
                    response.sendRedirect("index.jsp");
            }
        } catch (SQLException ex) {
            // Log and re-throw SQL exceptions
            Logger.getLogger(UserProfileServlet.class.getName()).log(Level.SEVERE, null, ex);
            throw new ServletException("Database fetch failed", ex);
        }
    }

    /**
     * Registers a new user using the data from the registration form.
     */
    private void registerUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        // Retrieve user registration parameters from the request
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phoneNumber");
        String address = request.getParameter("address");

        // Generate a unique 5-digit user ID using the helper method
        int userID = generateUniqueUserID(userDAO);

        // Create a new User object with the provided data and generated ID
        User user = new User(name, email, password, phone, address);
        user.setUserID(userID);

        // Attempt to create the user in the database
        boolean created = userDAO.createUser(user);

        // Forward to appropriate JSP page based on the outcome
        if (created) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to register user.");
            request.getRequestDispatcher("registerUser.jsp").forward(request, response);
        }
    }

    /**
     * Updates an existing user profile with data from the update form.
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        // Retrieve updated user details from the request
        int userID = Integer.parseInt(request.getParameter("userID"));
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String phone = request.getParameter("phoneNumber");
        String address = request.getParameter("address");

        // Create a new User object with updated data
        User user = new User(name, email, password, phone, address);
        user.setUserID(userID);

        // Attempt to update the user in the database
        boolean updated = userDAO.updateUser(user);

        // Forward to the profile view or edit form depending on the outcome
        if (updated) {
            request.setAttribute("user", user);
            request.setAttribute("message", "Profile updated.");
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Failed to update profile.");
            request.getRequestDispatcher("editUserProfile.jsp").forward(request, response);
        }
    }

    /**
     * Retrieves and displays a user's profile based on their userID.
     */
    private void viewUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        // Parse userID parameter and fetch user from database
        int userID = Integer.parseInt(request.getParameter("userID"));
        User user = userDAO.getUserById(userID);

        // Forward to the profile JSP with user data if found
        if (user != null) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
        } else {
            // If user is not found, display error message on profile page
            request.setAttribute("error", "User not found.");
            request.getRequestDispatcher("userProfile.jsp").forward(request, response);
        }
    }

    /**
     * Retrieves user data for editing and forwards the data to the edit form.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        // Get the userID parameter from the request and fetch the corresponding user
        int userID = Integer.parseInt(request.getParameter("userID"));
        User user = userDAO.getUserById(userID);

        // If user exists, set the user object as an attribute and forward to the edit form
        if (user != null) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("editUserProfile.jsp").forward(request, response);
        } else {
            // If the user isn't found, redirect to the default profile page
            response.sendRedirect("userProfile.jsp");
        }
    }

    /**
     * Generates a unique 5-digit user ID by checking the database for duplicates.
     */
    private int generateUniqueUserID(UserDAO userDAO) {
        Random rand = new Random();
        int userID;
        // Loop until a generated userID does not exist in the database
        do {
            userID = 10000 + rand.nextInt(90000);
        } while (userDAO.userIdExists(userID));
        return userID;
    }
}
