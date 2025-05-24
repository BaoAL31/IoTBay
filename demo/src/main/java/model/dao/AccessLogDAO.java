package model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.AccessLog;

public class AccessLogDAO {
    private Connection connection;

    public AccessLogDAO(Connection connection) {
        this.connection = connection;
    }

    // Insert a new access log record (login/logout)
    public void insertAccessLog(int userId, String accessType) throws SQLException {
        String sql = "INSERT INTO access_log (user_id, access_type) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, accessType);
            stmt.executeUpdate();
        }
    }


    public List<AccessLog> getAccessLogsByUserId(int userId) throws SQLException {
        List<AccessLog> logs = new ArrayList<>();
        String sql = "SELECT access_type, access_time FROM access_log WHERE user_id = ? ORDER BY access_time DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String accessType = rs.getString("access_type");
                    Date accessTime = rs.getTimestamp("access_time");

                    // Format the timestamp to hh:mm:ss
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    String formattedTime = sdf.format(accessTime);

                    logs.add(new AccessLog(accessType, formattedTime));
                }
            }
        }
        return logs;
    }

}
