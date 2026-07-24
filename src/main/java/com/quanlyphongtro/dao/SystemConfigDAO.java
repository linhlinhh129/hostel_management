package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

public class SystemConfigDAO {
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigDAO.class);

    public String getConfigValue(String key) {
        String query = "SELECT config_value FROM system_config WHERE config_key = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, key);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("config_value");
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching config for key: " + key, e);
        }
        return null;
    }

    public Map<String, String> getEmailConfig() {
        return getConfigByPrefix("email.");
    }

    public Map<String, String> getVNPayConfig() {
        return getConfigByPrefix("vnpay.");
    }

    private Map<String, String> getConfigByPrefix(String prefix) {
        Map<String, String> configMap = new HashMap<>();
        String query = "SELECT config_key, config_value FROM system_config WHERE config_key LIKE ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, prefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    configMap.put(rs.getString("config_key"), rs.getString("config_value"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching config for prefix: " + prefix, e);
        }
        return configMap;
    }

    public void updateConfigValue(String key, String value, int updatedBy, Connection conn) throws SQLException {
        // Use UPSERT/MERGE or check if exists, then update or insert
        String checkQuery = "SELECT 1 FROM system_config WHERE config_key = ?";
        boolean exists = false;
        try (PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            checkPs.setString(1, key);
            try (ResultSet rs = checkPs.executeQuery()) {
                exists = rs.next();
            }
        }

        if (exists) {
            String updateQuery = "UPDATE system_config SET config_value = ?, updated_at = ?, updated_by = ? WHERE config_key = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateQuery)) {
                ps.setString(1, value);
                ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                ps.setInt(3, updatedBy);
                ps.setString(4, key);
                ps.executeUpdate();
            }
        } else {
            String insertQuery = "INSERT INTO system_config (config_key, config_value, updated_at, updated_by) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertQuery)) {
                ps.setString(1, key);
                ps.setString(2, value);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setInt(4, updatedBy);
                ps.executeUpdate();
            }
        }
    }

    public ConfigMetadata getConfigMetadata(String keyPrefix) {
        // Fetch the max updated_at and the corresponding updated_by name for the given prefix
        String query = "SELECT TOP 1 c.updated_at, u.full_name as updated_by_name " +
                       "FROM system_config c LEFT JOIN users u ON c.updated_by = u.user_id " +
                       "WHERE c.config_key LIKE ? ORDER BY c.updated_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, keyPrefix + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new ConfigMetadata(rs.getTimestamp("updated_at"), rs.getString("updated_by_name"));
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching config metadata for prefix: " + keyPrefix, e);
        }
        return new ConfigMetadata(null, null);
    }

    public static class ConfigMetadata {
        public final Timestamp updatedAt;
        public final String updatedByName;
        public ConfigMetadata(Timestamp updatedAt, String updatedByName) {
            this.updatedAt = updatedAt;
            this.updatedByName = updatedByName;
        }
    }
}
