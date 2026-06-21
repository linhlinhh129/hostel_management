package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RequestDAO extends BaseDAO {

    private Request mapRow(ResultSet rs) throws SQLException {
        Request r = new Request();
        r.setId(rs.getInt("request_id"));
        r.setCode(rs.getString("code"));
        r.setSenderId(rs.getInt("sender_id"));
        r.setCategory(rs.getString("category"));
        r.setTitle(rs.getString("title"));
        r.setContent(rs.getString("content"));
        r.setStatus(rs.getString("status"));
        r.setAttachmentUrls1(rs.getString("attachment_urls1"));
        r.setAttachmentUrls2(rs.getString("attachment_urls2"));
        r.setAssignedStaffId(getInteger(rs, "assigned_staff_id"));
        r.setRejectionReason(rs.getString("rejection_reason"));
        r.setCreatedAt(toLocalDateTime(rs, "created_at"));
        r.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        r.setDeletedAt(toLocalDateTime(rs, "deleted_at"));

        if (hasColumn(rs, "assigned_to")) {
            r.setAssignedTo(rs.getString("assigned_to"));
        }
        return r;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        java.sql.ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    public List<Request> findBySenderId(int senderId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT r.*, u.full_name as assigned_to " +
                     "FROM dbo.requests r " +
                     "LEFT JOIN dbo.users u ON r.assigned_staff_id = u.user_id " +
                     "WHERE r.sender_id = ? AND r.deleted_at IS NULL " +
                     "ORDER BY r.created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findBySenderId failed for senderId={}", senderId, e);
        }
        return list;
    }

    public Optional<Request> findByIdAndSenderId(int id, int senderId) {
        String sql = "SELECT r.*, u.full_name as assigned_to " +
                     "FROM dbo.requests r " +
                     "LEFT JOIN dbo.users u ON r.assigned_staff_id = u.user_id " +
                     "WHERE r.request_id = ? AND r.sender_id = ? AND r.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByIdAndSenderId failed for id={}, senderId={}", id, senderId, e);
        }
        return Optional.empty();
    }

    public boolean insert(Request r) {
        String code = "REQ" + System.currentTimeMillis();
        String sql = "INSERT INTO dbo.requests (code, sender_id, category, title, content, attachment_urls1) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, r.getSenderId());
            ps.setString(3, r.getCategory());
            ps.setString(4, r.getTitle());
            ps.setString(5, r.getContent());
            ps.setString(6, r.getAttachmentUrls1());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("insert failed for Request", e);
            return false;
        }
    }

    public int countPendingBySenderId(int senderId) {
        String sql = "SELECT COUNT(*) FROM dbo.requests WHERE sender_id = ? AND status IN ('PENDING', 'ASSIGNED', 'IN_PROGRESS') AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("countPendingBySenderId failed for senderId={}", senderId, e);
        }
        return 0;
    }
}
