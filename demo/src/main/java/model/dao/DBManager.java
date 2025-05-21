package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;

/* 
* DBManager is the primary DAO class to interact with the database. 
* Complete the existing methods of this classes to perform CRUD operations with the db.
*/

public class DBManager {

    private Connection conn;

    public DBManager(Connection conn) throws SQLException {
        this.conn = conn;
    }

    // Find user by email and password in the database
    public User findUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, email);
        pstmt.setString(2, password);
        ResultSet rs = pstmt.executeQuery();
    
        if (rs.next()) {
            User user = new User();
            user.setUserID(rs.getInt("user_id"));
            user.setFullName(rs.getString("full_name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setPhoneNumber(rs.getString("phone"));
            user.setAddress(rs.getString("address"));
            user.setUserType(rs.getString("user_type"));
            user.setStatus(rs.getString("status"));
            return user;
        }
        return null;
    }

    public User findUserByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
    
            if (rs.next()) {
                User user = new User();
                user.setUserID(rs.getInt("user_id"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setPhoneNumber(rs.getString("phone"));
                user.setAddress(rs.getString("address"));
                user.setUserType(rs.getString("user_type"));
                user.setStatus(rs.getString("status")); // ✅ Make sure this line exists
                return user;
            }
        }
        return null;
    }
    
    
    


    // update a user details in the database
    public void updateUser(String email, String name, String password, String gender, String favcol)
            throws SQLException {
        // code for update-operation

    }

    // delete a user from the database
    public void deleteUser(String email) throws SQLException {
        // code for delete-operation

    }

}
