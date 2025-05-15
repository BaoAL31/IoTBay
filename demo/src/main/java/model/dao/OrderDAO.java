package model.dao;

import model.*;
import java.sql.*;
import java.util.*;

public class OrderDAO {

    private Connection conn;

    public OrderDAO(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public int createOrder(int userId) throws SQLException {
        String query = "INSERT INTO order (user_id) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
            throw new SQLException("Order creation failed, no ID obtained.");
        }
    }

    public Integer getOrderIdByUserId(int userId) throws SQLException {
        String query = "SELECT Order_id FROM Order WHERE user_id = ? AND status = 'ACTIVE'";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("Order_id");
            }
            return -1;
        }
    }

    public Order findOrder(int orderId) throws SQLException {
        String query = "SELECT * FROM OrderItem WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Order(rs.getInt("user_id"), rs.getInt("order_id"), rs.getTimestamp("created_at"),
                        rs.getString("status"));
            }
            return null;
        }
    }

    public void addOrderItem(int orderId, Device item, int quantity) throws SQLException {
        String query = "INSERT INTO OrderItem (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, item.getId());
            stmt.setInt(3, quantity);
            stmt.setDouble(4, item.getPrice());
            stmt.executeUpdate();
        }
    }

    public void removeOrderItem(int orderId, int productId) throws SQLException {
        String query = "DELETE FROM OrderItem WHERE order_id = ? AND device_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }

    public void updateOrderItem(int orderId, int productId, int quantity) throws SQLException {
        String query = "UPDATE orderitem SET quantity = ? WHERE order_id = ? AND device_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, quantity);
            stmt.setInt(2, orderId);
            stmt.setInt(3, productId);
            stmt.executeUpdate();
        }
    }

    public List<Integer> getOrderIdsByStatusAndSearchQuery(int userId, String status, String searchQueryId,
            String searchQueryDate) throws SQLException {

        String sql = "SELECT order_id FROM `order` WHERE user_id = ? AND status = ?";

        boolean hasIdFilter = searchQueryId != null && !searchQueryId.trim().isEmpty();
        boolean hasDateFilter = searchQueryDate != null && !searchQueryDate.trim().isEmpty();

        if (hasIdFilter) {
            sql += " AND CAST(order_id AS CHAR) LIKE ?";
        }
        if (hasDateFilter) {
            sql += " AND created_at LIKE ?";
        }

        List<Integer> orderIds = new ArrayList<>();
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
                    orderIds.add(rs.getInt("order_id"));
                }
            }
        }
        return orderIds;
    }

    // Returns a list of items in the order with their quantities added to order
    public Map<Integer, Integer> getOrderItems(Integer orderId) throws SQLException {
        String sql = "SELECT " +
                "    d.device_id, " +
                "    d.name, " +
                "    d.type, " +
                "    d.unit_price, " +
                "    d.stock, " +
                "    ci.quantity " +
                "FROM OrderItem ci " +
                "JOIN Device d ON ci.device_id = d.device_id " +
                "WHERE ci.order_id = ?";

        Map<Integer, Integer> items = new HashMap<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
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

    public String getOrderCreatedDate(Integer orderId) throws SQLException {
        String query = "SELECT created_at FROM `order` WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getTimestamp("created_at").toString();
                }
            }
        }
        return null;
    }

    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE `order` SET status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        }
    }

}
