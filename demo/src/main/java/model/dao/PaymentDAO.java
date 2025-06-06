package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentDAO {
    private Connection conn;

    public PaymentDAO(Connection conn) {
        this.conn = conn;
    }

    // Get payment IDs by status and search query
    public List<Integer> getPaymentIdsByStatusAndSearchQuery(Integer userId, String status, String searchQueryId,
            String searchQueryDate) throws SQLException {

        String sql = "SELECT payment_id FROM Payment WHERE order_id IN (SELECT order_id FROM `Order` WHERE user_id = ?) AND status = ?";

        boolean hasIdFilter = searchQueryId != null && !searchQueryId.trim().isEmpty();
        boolean hasDateFilter = searchQueryDate != null && !searchQueryDate.trim().isEmpty();

        if (hasIdFilter) {
            sql += " AND CAST(payment_id AS CHAR) LIKE ?";
        }
        if (hasDateFilter) {
            sql += " AND paid_at LIKE ?";
        }

        List<Integer> paymentIds = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            ps.setInt(idx++, userId);
            ps.setString(idx++, status);

            if (hasIdFilter) {
                ps.setString(idx++, "%" + searchQueryId.trim() + "%");
            }
            if (hasDateFilter) {
                ps.setString(idx++, "%" + searchQueryDate.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    paymentIds.add(rs.getInt("payment_id"));
                }
            }
        }
        return paymentIds;
    }

    public String getPaidDateByOrderId(int orderId) throws SQLException {
        String sql = "SELECT paid_at FROM Payment WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("paid_at");
                }
            }
        }
        return null;
    }

    // Returns a list of items in the payment with their quantities
    public Map<Integer, Integer> getPaymentItems(Integer paymentId) throws SQLException {
        String sql = "SELECT " +
                "    d.device_id, " +
                "    d.name, " +
                "    d.type, " +
                "    d.unit_price, " +
                "    d.stock, " +
                "    pi.quantity " +
                "FROM PaymentItem pi " +
                "JOIN Device d ON pi.device_id = d.device_id " +
                "WHERE pi.payment_id = ?";

        Map<Integer, Integer> items = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.put(
                            rs.getInt("device_id"),
                            rs.getInt("quantity"));
                }
            }
        }
        return items;
    }

    // Create a new payment
    public void createPayment(int orderId, String method, String cardNumber, double amount, String status) throws SQLException {
        String sql = "INSERT INTO Payment (order_id, method, card_number, amount, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, method);
            ps.setString(3, cardNumber);
            ps.setDouble(4, amount);
            ps.setString(5, status);
            ps.executeUpdate();
        }
    }

    // Delete a payment
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM Payment WHERE payment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
        }
    }

    // Get order_id by payment_id
    public Integer getOrderIdByPaymentId(int paymentId) throws SQLException {
        String sql = "SELECT order_id FROM payment WHERE payment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("order_id");
                }
            }
        }
        return null;
    }
    
    // Get card number by order ID
    public String getCardNumberByOrderId(int orderId) throws SQLException {
        String sql = "SELECT card_number FROM Payment WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("card_number");
                }
            }
        }
        return null;
    }

    // Get payment method by user ID (returns the first found)
    public String getPaymentMethodByOrderId(int orderId) throws SQLException {
        String sql = "SELECT method FROM Payment WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("method");
                }
            }
        }
        return null;
    }

}