package controller;
import java.sql.*;
import java.util.logging.*;
import model.dao.*;
import model.*;

public class TestDB {


    public static void main(String[] args) {

        try {

            DBConnector connector = new DBConnector();

            Connection conn = connector.openConnection();

            DBManager db = new DBManager(conn);
            
            User user = db.findUser("Ba@gmail.com", "1234");
            if (user != null) {
                System.out.println("User found: " + user.toString());
            } else {
                System.out.println("User not found.");
            }
            
            connector.closeConnection();

        } catch (ClassNotFoundException | SQLException ex) {

            Logger.getLogger(TestDB.class.getName()).log(Level.SEVERE, null, ex);

        }

    }

}
