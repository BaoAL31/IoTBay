package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Device;

public class DeviceDAO {
    private final Connection connection;

    public DeviceDAO(Connection connection) {
        this.connection = connection;
    }

    // Create a new device (Staff only)
    public void addDevice(Device device) throws SQLException {
        String query = "INSERT INTO device (name, type, unit_price, stock) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, device.getName());
            statement.setString(2, device.getType());
            statement.setDouble(3, device.getPrice());
            statement.setInt(4, device.getStock());
            statement.executeUpdate();
        }
    }

    // Read all devices (Accessible to both staff and customers)
    public List<Device> getAllDevices() throws SQLException {
        List<Device> devices = new ArrayList<>();
        String query = "SELECT * FROM device";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                devices.add(new Device(
                        rs.getInt("device_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock")));
            }
        }
        return devices;
    }

    // Search devices by name and type
    public List<Device> searchDevices(String name, String type) throws SQLException {
        List<Device> devices = new ArrayList<>();
        String query = "SELECT * FROM device WHERE name LIKE ? AND type LIKE ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, "%" + name + "%");
            statement.setString(2, "%" + type + "%");
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                devices.add(new Device(
                        rs.getInt("device_id"),
                        rs.getString("name"),
                        rs.getString("type"),
                        rs.getDouble("unit_price"),
                        rs.getInt("stock")));
            }
        }
        return devices;
    }

    // Update a device
    public void updateDevice(Device device) throws SQLException {
        String query = "UPDATE device SET name = ?, type = ?, unit_price = ?, stock = ? WHERE device_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, device.getName());
            statement.setString(2, device.getType());
            statement.setDouble(3, device.getPrice());
            statement.setInt(4, device.getStock());
            statement.setInt(5, device.getId());
            statement.executeUpdate();
        }
    }

    // Delete a device
    public void deleteDevice(int id) throws SQLException {
        String query = "DELETE FROM device WHERE device_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // Get device by ID
    public Device getDeviceById(int id) throws SQLException {
        String query = "SELECT * FROM device WHERE device_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new Device(
                            rs.getInt("device_id"),
                            rs.getString("name"),
                            rs.getString("type"),
                            rs.getDouble("unit_price"),
                            rs.getInt("stock"));
                }
            }
        }
        return null;
    }

    // Get stock value
    public int getStock(int deviceId) throws SQLException {
        String query = "SELECT stock FROM device WHERE device_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, deviceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
                throw new SQLException("Device not found: " + deviceId);
            }
        }
    }

    // Update stock quantity
    public void updateStock(int deviceId, int newStock) throws SQLException {
        String query = "UPDATE device SET stock = ? WHERE device_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, newStock);
            statement.setInt(2, deviceId);
            statement.executeUpdate();
        }
    }
}
