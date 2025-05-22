package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.User;

// Data Access Object for User-related database operations
public class UserDAO {

    // Inserts a new user into the database and sets the generated userID on the User object
    public boolean createUser(User user) {
        // SQL with placeholders for prepared statement
        String sql = "INSERT INTO User (full_name, email, password, phone, address, user_type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();  // open DB connection
    
            // Request auto-generated keys so we can retrieve the new user_id
            PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getUserType());
            stmt.setString(7, user.getStatus());
    
            int affectedRows = stmt.executeUpdate();  // execute insert
    
            if (affectedRows > 0) {
                // retrieve the generated primary key
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    int id = keys.getInt(1);
                    user.setUserID(id);            // update User object
                    System.out.println("Generated user_id = " + id);
                }
            }
    
            db.closeConnection();  // close connection
            return affectedRows > 0;
    
        } catch (ClassNotFoundException | SQLException e) {
            // on error, return false
            return false;
        }
    }
    
    // Retrieves a user by its ID, or null if not found/ERROR
    public User getUserById(int userID) {
        String sql = "SELECT * FROM User WHERE user_id = ?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);              // bind ID parameter

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // map result set to User object
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                user.setStatus(rs.getString("status"));

                db.closeConnection();
                return user;
            }

            db.closeConnection();

        } catch (ClassNotFoundException | SQLException e) {
            // on error, return null
            return null;
        }

        return null;  // no matching user
    }

    // Updates existing user details; returns true if at least one row was updated
    public boolean updateUser(User user) {
        String sql = "UPDATE User SET full_name=?, email=?, password=?, phone=?, address=?, status=? WHERE user_id=?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setInt(6, user.getUserID());     // specify which user to update
            stmt.setString(7, user.getStatus());

            boolean result = stmt.executeUpdate() > 0;
            db.closeConnection();
            return result;

        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    // Deletes a user by ID
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

        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    // Checks if a user ID exists in the database
    public boolean userIdExists(int userID) {
        String sql = "SELECT user_id FROM User WHERE user_id = ?";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();  // true if at least one row returned

            db.closeConnection();
            return exists;

        } catch (ClassNotFoundException | SQLException e) {
            // return true to prevent usage if error occurs
            return true;
        }
    }

    // Retrieves all users from the database
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User";

        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                // build User object for each row
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                user.setStatus(rs.getString("status"));
                users.add(user);
            }

            db.closeConnection();
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }

        return users;
    }

    // Alias for createUser, used by admin functionality
    public boolean addUser(User user) {
        String sql = "INSERT INTO User (full_name, email, password, phone, address, user_type, status) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getFullName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getUserType());
            stmt.setString(7, user.getStatus());
            boolean result = stmt.executeUpdate() > 0;
            db.closeConnection();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    // Another delete method by ID (duplicate of deleteUser)
    public boolean deleteUserById(int userId) {
        String sql = "DELETE FROM User WHERE user_id = ?";
        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            boolean result = stmt.executeUpdate() > 0;
            db.closeConnection();
            return result;
        } catch (ClassNotFoundException | SQLException e) {
            return false;
        }
    }

    // Retrieves users filtered by their userType (e.g., only admins or customers)
    public List<User> getUsersByType(String type) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User WHERE user_type = ?";
        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, type);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setUserID(rs.getInt("user_id"));
                u.setFullName(rs.getString("full_name"));
                u.setEmail(rs.getString("email"));
                u.setPassword(rs.getString("password"));
                u.setPhoneNumber(rs.getString("phone"));
                u.setAddress(rs.getString("address"));
                u.setUserType(rs.getString("user_type"));
                u.setStatus(rs.getString("status"));
                users.add(u);
            }
            db.closeConnection();
        } catch (ClassNotFoundException | SQLException e) {
            return null;
        }
        return users;
    }    

    // Searches regular users by name (case-insensitive partial match)
    public List<User> searchUsersByName(String name) {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user WHERE user_type = 'user' AND full_name LIKE ?";
    
        try {
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, "%" + name.toLowerCase() + "%");
    
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                user.setStatus(rs.getString("status"));
                users.add(user);
            }
    
            db.closeConnection();
        } catch (ClassNotFoundException | SQLException e) {
            // ignore exception, return whatever was found so far
        }
    
        return users;
    }

    public void updateUserStatus(int userId, String status) throws SQLException, ClassNotFoundException {
        String sql = "UPDATE User SET status = ? WHERE user_id = ?";
        DBConnector db = new DBConnector();
        Connection conn = db.openConnection();  // open DB connection
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }    
    
}
