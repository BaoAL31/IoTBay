package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.User;

public class UserDAO {

    // Inserts a new user into the database
    public boolean createUser(User user) {
        String sql = "INSERT INTO User (full_name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());

            boolean result = stmt.executeUpdate() > 0;
            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves a user from the database using their user_id
    public User getUserById(int userID) {
        String sql = "SELECT * FROM User WHERE user_id = ?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));

                db.closeConnection();
                return user;
            }

            db.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Updates user details
    public boolean updateUser(User user) {
        String sql = "UPDATE User SET full_name=?, email=?, password=?, phone=?, address=? WHERE user_id=?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setInt(6, user.getUserID());

            boolean result = stmt.executeUpdate() > 0;
            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deletes a user
    public boolean deleteUser(int userID) {
        String sql = "DELETE FROM User WHERE user_id = ?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            boolean result = stmt.executeUpdate() > 0;
            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if user_id exists
    public boolean userIdExists(int userID) {
        String sql = "SELECT user_id FROM User WHERE user_id = ?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            db.closeConnection();
            return exists;

        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
