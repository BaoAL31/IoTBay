package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.Device;
import model.Order;

public class OrderDAO {

    private Connection conn;

    public OrderDAO(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public int createOrder(int userId) throws SQLException {
        String query = "INSERT INTO `order` (user_id) VALUES (?)";
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

    // Finds the latest order for a user with status 'DRAFT', returns its ID or -1 if not found
    public int findActiveOrderId(int userId) throws SQLException {
        String query = "SELECT order_id FROM `order` WHERE user_id = ? AND status = 'DRAFT' ORDER BY created_at DESC LIMIT 1";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("order_id");
            }
            return -1;
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
        // First, check if the item already exists in the order
        String checkQuery = "SELECT quantity FROM OrderItem WHERE order_id = ? AND device_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, orderId);
            checkStmt.setInt(2, item.getId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    // Item exists, update quantity
                    int existingQty = rs.getInt("quantity");
                    String updateQuery = "UPDATE OrderItem SET quantity = ? WHERE order_id = ? AND device_id = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, existingQty + quantity);
                        updateStmt.setInt(2, orderId);
                        updateStmt.setInt(3, item.getId());
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Item does not exist, insert new row
                    String insertQuery = "INSERT INTO OrderItem (order_id, device_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, orderId);
                        insertStmt.setInt(2, item.getId());
                        insertStmt.setInt(3, quantity);
                        insertStmt.setDouble(4, item.getPrice());
                        insertStmt.executeUpdate();
                    }
                }
            }
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
        System.out.println("getOrderIdsByStatusAndSearchQuery: userId=" + userId + ", status=" + status + ", searchQueryId="
                + searchQueryId + ", searchQueryDate=" + searchQueryDate);
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

    public Device getOrderItem(int orderId, int deviceId) throws SQLException {
        String sql = "SELECT d.device_id, d.name, d.type, d.unit_price, d.stock " +
                     "FROM OrderItem oi " +
                     "JOIN Device d ON oi.device_id = d.device_id " +
                     "WHERE oi.order_id = ? AND oi.device_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, deviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Device(
                        rs.getInt("device_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock")
                    );
                }
            }
        }
        return null;
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
