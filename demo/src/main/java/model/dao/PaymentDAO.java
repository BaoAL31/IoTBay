package model.dao;

import model.Payment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

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
    public void createPayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO Payment (order_id, method, card_number, amount, status) VALUES (?, ?, ?, ?, 'draft')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, payment.getOrderId());
            ps.setString(2, payment.getMethod());
            ps.setString(3, payment.getCardNumber());
            ps.setDouble(4, payment.getAmount());
            ps.executeUpdate();
        }
    }

    // Update an existing payment
    public void updatePayment(Payment payment) throws SQLException {
        String sql = "UPDATE Payment SET method = ?, card_number = ?, amount = ? WHERE payment_id = ? AND status = 'draft'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, payment.getMethod());
            ps.setString(2, payment.getCardNumber());
            ps.setDouble(3, payment.getAmount());
            ps.setInt(4, payment.getPaymentId());
            ps.executeUpdate();
        }
    }

    // Delete a payment
    public void deletePayment(int paymentId) throws SQLException {
        String sql = "DELETE FROM Payment WHERE payment_id = ? AND status = 'draft'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, paymentId);
            ps.executeUpdate();
        }
    }
}