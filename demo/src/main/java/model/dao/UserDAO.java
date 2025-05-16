package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.User;

public class UserDAO {

    // Inserts a new user into the database
    public boolean createUser(User user) {
        String sql = "INSERT INTO Users (userID, name, email, password, phoneNumber, address) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Establish a connection to the database
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the SQL INSERT statement with parameter placeholders
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set values from the User object into the prepared statement
            stmt.setInt(1, user.getUserID());
            stmt.setString(2, user.getName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPassword());
            stmt.setString(5, user.getPhoneNumber());
            stmt.setString(6, user.getAddress());

            // Execute the INSERT and return true if a row was added
            boolean result = stmt.executeUpdate() > 0;

            // Close the database connection
            db.closeConnection();
            return result;

        } catch (Exception e) {
            // Print the stack trace for debugging if an error occurs
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves a user from the database using their userID
    public User getUserById(int userID) {
        String sql = "SELECT * FROM Users WHERE userID = ?";

        try {
            // Connect to the database
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the SELECT statement
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // If a matching user is found, populate and return a User object
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("userID"));
                user.setName(rs.getString("name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phoneNumber"));
                user.setAddress(rs.getString("address"));

                db.closeConnection();
                return user;
            }

            // Close the connection if no user is found
            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return null if user does not exist or an error occurred
        return null;
    }

    // Updates the details of an existing user
    public boolean updateUser(User user) {
        String sql = "UPDATE Users SET name=?, email=?, password=?, phoneNumber=?, address=? WHERE userID=?";

        try {
            // Establish database connection
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the UPDATE statement
            PreparedStatement stmt = conn.prepareStatement(sql);

            // Set updated user fields
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setInt(6, user.getUserID());

            // Execute the update and return result
            boolean result = stmt.executeUpdate() > 0;

            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deletes a user by their userID
    public boolean deleteUser(int userID) {
        String sql = "DELETE FROM Users WHERE userID = ?";

        try {
            // Open database connection
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the DELETE statement
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            // Execute and return true if user was deleted
            boolean result = stmt.executeUpdate() > 0;

            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Checks if a given userID already exists in the database
    public boolean userIdExists(int userID) {
        String sql = "SELECT userID FROM Users WHERE userID = ?";

        try {
            // Connect to the database
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the SELECT query
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Return true if a result is found (user exists)
            boolean exists = rs.next();

            db.closeConnection();
            return exists;

        } catch (Exception e) {
            e.printStackTrace();

            // Return true to be safe if error occurs (assume ID exists)
            return true;
        }
    }
}
