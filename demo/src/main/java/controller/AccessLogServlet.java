package controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.AccessLog;
import model.User;
import model.dao.AccessLogDAO;
import model.dao.DBConnector;

public class AccessLogServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        User loggedUser = (User) request.getSession().getAttribute("loggedUser");
        if (loggedUser == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        int userIdToView = loggedUser.getUserID(); // Users see their own logs only

        // If admin wants to view logs for another user, allow (optional)
        String userIdParam = request.getParameter("userId");
        if ("admin".equals(loggedUser.getUserType()) && userIdParam != null) {
            try {
                userIdToView = Integer.parseInt(userIdParam);
            } catch (NumberFormatException e) {
                // ignore fallback to logged user
            }
        }

        try {
            DBConnector db = new DBConnector();
            AccessLogDAO logDAO = new AccessLogDAO(db.openConnection());
            List<AccessLog> logs = logDAO.getAccessLogsByUserId(userIdToView);
            request.setAttribute("accessLogs", logs);
            request.getRequestDispatcher("accessLogs.jsp").forward(request, response);
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (ClassNotFoundException ex) {
        }
    }
}
