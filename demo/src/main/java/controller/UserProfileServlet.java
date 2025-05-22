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
import model.dao.UserDAO;

// Servlet for handling user profile actions (view, edit, register, delete)
public class UserProfileServlet extends HttpServlet {

    /**
     * Handles POST requests (register, update, delete).
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();                       // get or create session
        String action = request.getParameter("action");                   // determine operation
        System.out.println("doPost action: " + action);

        // reuse UserDAO in session if available
        UserDAO userDAO = (UserDAO) session.getAttribute("userDAO");
        if (userDAO == null) {
            userDAO = new UserDAO();
            session.setAttribute("userDAO", userDAO);
        }

        try {
            switch (action) {
                case "register":
                    registerUser(request, response, userDAO);
                    break;
                case "update":
                    updateUser(request, response, userDAO);
                    break;
                case "delete":
                    deleteUser(request, response, userDAO);
                    break;
                default:
                    throw new ServletException("Unknown action: " + action);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserProfileServlet.class.getName())
                  .log(Level.SEVERE, null, ex);
            throw new ServletException("DB error", ex);
        }
    }

    /**
     * Handles GET requests (view profile, show edit form).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        System.out.println("doGet action: " + action);

        UserDAO userDAO = (UserDAO) session.getAttribute("userDAO");
        if (userDAO == null) {
            userDAO = new UserDAO();
            session.setAttribute("userDAO", userDAO);
        }

        try {
            switch (action) {
                case "view":
                    viewUser(request, response, userDAO);
                    break;
                case "edit":
                    showEditForm(request, response, userDAO);
                    break;
                default:
                    response.sendRedirect("index.jsp");  // fallback to home
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserProfileServlet.class.getName())
                  .log(Level.SEVERE, null, ex);
            throw new ServletException("DB fetch failed", ex);
        }
    }

    /**
     * Register a new user from form data.
     */
    private void registerUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        // read form fields
        String name     = request.getParameter("name");
        String email    = request.getParameter("email");
        String password = request.getParameter("password");
        String phone    = request.getParameter("phoneNumber");
        String address  = request.getParameter("address");
        String staffPassword = request.getParameter("staff_password");
        String requestedType = request.getParameter("user_type");  // value from radio/dropdown, optional
        String userType;

        if ("staff".equalsIgnoreCase(requestedType) && "0000".equals(staffPassword)) {
            userType = "staff";
        } else {
            userType = "user"; // default fallback
        }


        System.out.println("Registering: " + name + ", " + email);

        User user = new User(name, email, password, phone, address, userType);
        boolean created = userDAO.createUser(user);

        if (created) {
            request.setAttribute("user", user);
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } else {
            request.setAttribute("error", "Registration failed.");
            request.getRequestDispatcher("registerUser.jsp").forward(request, response);
        }
    }

    /**
     * Update an existing user's profile.
     */
    private void updateUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();

        // read updated fields
        int userID     = Integer.parseInt(request.getParameter("userID"));
        String name    = request.getParameter("name");
        String email   = request.getParameter("email");
        String password= request.getParameter("password");
        String phone   = request.getParameter("phoneNumber");
        String address = request.getParameter("address");
        String userType= request.getParameter("user_type");
        if (userType == null || userType.isEmpty()) {
            userType = "user";
        }

        User loggedUser = (User) session.getAttribute("loggedUser");

        // prepare updated user object
        User updatedUser = new User(name, email, password, phone, address, userType);
        updatedUser.setUserID(userID);

        boolean updated = userDAO.updateUser(updatedUser);

        if (updated) {
            request.setAttribute("user", updatedUser);

            if (loggedUser != null && loggedUser.getUserID() == userID) {
                session.setAttribute("loggedUser", updatedUser);  // refresh session data
                request.setAttribute("message", "Profile updated.");
                request.getRequestDispatcher("userProfile.jsp").forward(request, response);
            } else {
                // admin updating others: redirect based on role
                String tab = ("user".equals(updatedUser.getUserType())) ? "user" : "admin";
                response.sendRedirect("adminDashboard.jsp?tab=" + tab);
            }
        } else {
            request.setAttribute("error", "Update failed.");
            request.getRequestDispatcher("editUserProfile.jsp").forward(request, response);
        }
    }

    /**
     * Show a user's profile after permission check.
     */
    private void viewUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");
        int userID = Integer.parseInt(request.getParameter("userID"));
        User user = userDAO.getUserById(userID);

        // allow view only if admin or owner
        if (loggedUser == null || user == null ||
            (!"admin".equals(loggedUser.getUserType()) && loggedUser.getUserID() != userID)) {
            response.sendRedirect("unauthorized.jsp");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("userProfile.jsp").forward(request, response);
    }

    /**
     * Display edit form with current user data.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        HttpSession session = request.getSession();
        User loggedUser = (User) session.getAttribute("loggedUser");
        String idParam = request.getParameter("userID");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("unauthorized.jsp");
            return;
        }

        int userID = Integer.parseInt(idParam);
        User user = userDAO.getUserById(userID);

        // check permission
        if (loggedUser == null || user == null ||
            (!"admin".equals(loggedUser.getUserType()) && loggedUser.getUserID() != userID)) {
            response.sendRedirect("unauthorized.jsp");
            return;
        }

        request.setAttribute("user", user);
        request.getRequestDispatcher("editUserProfile.jsp").forward(request, response);
    }

    /**
     * Delete the user and, if successful, invalidate session.
     */
    private void deleteUser(HttpServletRequest request, HttpServletResponse response, UserDAO userDAO)
            throws ServletException, IOException, SQLException {

        int userID = Integer.parseInt(request.getParameter("userID"));
        boolean deleted = userDAO.deleteUser(userID);

        if (deleted) {
            request.getSession().invalidate();   // logout
            response.sendRedirect("login.jsp");
        } else {
            request.setAttribute("error", "Delete failed.");
            viewUser(request, response, userDAO);  // show profile again
        }
    }
}
