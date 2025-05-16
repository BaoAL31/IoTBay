package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import model.Delivery;

public class DeliveryDAO {

    // Inserts a new delivery record into the database
    public boolean createDelivery(Delivery d) {
        String sql = "INSERT INTO Deliveries (trackingId, orderId, userId, status, estimatedDeliveryDate, carrier) VALUES (?, ?, ?, ?, ?, ?)";

        try {
            // Establish a connection using the DBConnector class
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the SQL insert statement
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, d.getTrackingId());
            stmt.setString(2, d.getOrderId());
            stmt.setString(3, d.getUserId());
            stmt.setString(4, d.getStatus());
            stmt.setDate(5, new java.sql.Date(d.getEstimatedDeliveryDate().getTime()));
            stmt.setString(6, d.getCarrier());

            // Execute the insert and return true if successful
            boolean result = stmt.executeUpdate() > 0;

            // Close connection to prevent resource leaks
            db.closeConnection();
            return result;

        } catch (Exception e) {
            // Print error stack trace if something goes wrong
            e.printStackTrace();
            return false;
        }
    }

    // Retrieves a delivery record using its unique tracking ID
    public Delivery getDeliveryByTrackingId(String trackingId) {
        String sql = "SELECT * FROM Deliveries WHERE trackingId = ?";

        try {
            // Open DB connection
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the SELECT query
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, trackingId);
            ResultSet rs = stmt.executeQuery();

            // If a matching record is found, populate a Delivery object and return it
            if (rs.next()) {
                Delivery d = new Delivery(
                        rs.getString("trackingId"),
                        rs.getString("orderId"),
                        rs.getString("userId"),
                        rs.getString("status"),
                        rs.getDate("estimatedDeliveryDate"),
                        rs.getString("carrier")
                );

                db.closeConnection();
                return d;
            }

            db.closeConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return null if delivery not found or error occurred
        return null;
    }

    // Returns all delivery records for a specific user (based on userId)
    public List<Delivery> getAllDeliveriesByUser(String userId) {
        List<Delivery> deliveries = new ArrayList<>();
        String sql = "SELECT * FROM Deliveries WHERE userId = ?";

        try {
            // Connect to DB
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare and execute SELECT query
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, userId);
            ResultSet rs = stmt.executeQuery();

            // Add each result row to the list of deliveries
            while (rs.next()) {
                deliveries.add(new Delivery(
                        rs.getString("trackingId"),
                        rs.getString("orderId"),
                        rs.getString("userId"),
                        rs.getString("status"),
                        rs.getDate("estimatedDeliveryDate"),
                        rs.getString("carrier")
                ));
            }

            db.closeConnection();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return deliveries;
    }

    // Updates the delivery status, estimated date, or carrier of an existing record
    public boolean updateDelivery(Delivery d) {
        String sql = "UPDATE Deliveries SET status=?, estimatedDeliveryDate=?, carrier=? WHERE trackingId=?";

        try {
            // Establish DB connection
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare the SQL UPDATE statement
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, d.getStatus());
            stmt.setDate(2, new java.sql.Date(d.getEstimatedDeliveryDate().getTime()));
            stmt.setString(3, d.getCarrier());
            stmt.setString(4, d.getTrackingId());

            // Execute the update and return true if successful
            boolean result = stmt.executeUpdate() > 0;

            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Deletes a delivery record by its tracking ID
    public boolean deleteDelivery(String trackingId) {
        String sql = "DELETE FROM Deliveries WHERE trackingId = ?";

        try {
            // Connect to database
            DBConnector db = new DBConnector();
            Connection conn = db.openConnection();

            // Prepare DELETE statement
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, trackingId);

            // Execute deletion and return true if record was removed
            boolean result = stmt.executeUpdate() > 0;

            db.closeConnection();
            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
