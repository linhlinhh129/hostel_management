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
        r.setAppointSchedule(toLocalDateTime(rs, "appoint_schedule"));
        r.setCreatedAt(toLocalDateTime(rs, "created_at"));
        r.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        r.setDeletedAt(toLocalDateTime(rs, "deleted_at"));

        if (hasColumn(rs, "assigned_to")) {
            r.setAssignedTo(rs.getString("assigned_to"));
        }
        if (hasColumn(rs, "sender_name")) {
            r.setSenderName(rs.getString("sender_name"));
        }
        if (hasColumn(rs, "room_code")) {
            r.setRoomCode(rs.getString("room_code"));
        }
        if (hasColumn(rs, "facility_name")) {
            r.setFacilityName(rs.getString("facility_name"));
        }
        return r;
    }


    // ==================== HEAD (OPERATOR) METHODS ====================

    public Request getRequestById(int requestId) {
        String sql = "SELECT rq.*, u.full_name AS sender_name, r.code AS room_code, f.name AS facility_name " +
                     "FROM requests rq " +
                     "LEFT JOIN users u ON rq.sender_id = u.user_id " +
                     "LEFT JOIN rooms r ON u.user_id = r.tenant_id " +
                     "LEFT JOIN facilities f ON r.facility_id = f.facility_id " +
                     "WHERE rq.request_id = ? AND rq.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("getRequestById failed", e);
        }
        return null;
    }

    public boolean updateRequestStatus(int requestId, String newStatus, String expectedOldStatus, Integer staffId, String rejectReason) {
        String sql = "UPDATE requests SET status = ?, assigned_staff_id = ?, rejection_reason = ?, updated_at = GETDATE() " +
                     "WHERE request_id = ? AND status = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, newStatus);
            if (staffId != null) {
                ps.setInt(2, staffId);
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, rejectReason);
            ps.setInt(4, requestId);
            ps.setString(5, expectedOldStatus);
            
            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            logger.error("updateRequestStatus failed", e);
            return false;
        }
    }

    public int countRequests(Integer assigneeId, String status, String category) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM requests rq WHERE rq.deleted_at IS NULL");
        
        if (assigneeId != null) {
            sql.append(" AND (rq.assigned_staff_id = ").append(assigneeId).append(" OR (rq.status = 'PENDING' AND EXISTS(SELECT 1 FROM users u WHERE u.user_id = rq.sender_id AND u.role = 'TENANT')))");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND rq.status = ?");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND rq.category = ?");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (category != null && !category.isEmpty()) {
                ps.setString(paramIndex++, category);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("countRequests failed", e);
        }
        return 0;
    }

    public List<Request> getRequests(Integer assigneeId, String status, String category, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT rq.*, u.full_name AS sender_name, r.code AS room_code, f.name AS facility_name " +
            "FROM requests rq " +
            "LEFT JOIN users u ON rq.sender_id = u.user_id " +
            "LEFT JOIN rooms r ON u.user_id = r.tenant_id " +
            "LEFT JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE rq.deleted_at IS NULL"
        );

        if (assigneeId != null) {
            sql.append(" AND (rq.assigned_staff_id = ").append(assigneeId).append(" OR (rq.status = 'PENDING' AND EXISTS(SELECT 1 FROM users uu WHERE uu.user_id = rq.sender_id AND uu.role = 'TENANT')))");
        }
        if (status != null && !status.isEmpty()) {
            sql.append(" AND rq.status = ?");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND rq.category = ?");
        }

        sql.append(" ORDER BY rq.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (status != null && !status.isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (category != null && !category.isEmpty()) {
                ps.setString(paramIndex++, category);
            }
            
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("getRequests failed", e);
        }
        return list;
    }

    public boolean completeRequest(int requestId, String notes, String attachmentUrls2) {
        String sql = "UPDATE requests SET status = 'DONE', rejection_reason = ?, attachment_urls2 = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setString(2, attachmentUrls2);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("completeRequest failed", e);
        }
        return false;
    }

    public boolean updateAppointmentSchedule(int requestId, java.time.LocalDateTime appointSchedule) {
        String sql = "UPDATE requests SET status = 'IN_PROGRESS', appoint_schedule = ?, updated_at = GETDATE() WHERE request_id = ? AND status = 'ASSIGNED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, java.sql.Timestamp.valueOf(appointSchedule));
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("updateAppointmentSchedule failed", e);
        }
        return false;
    }

    public boolean insertIncidentReport(Request req) {
        String sql = "INSERT INTO requests (code, sender_id, category, title, content, status, attachment_urls1, created_at) VALUES (?, ?, ?, ?, ?, 'PENDING', ?, GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, req.getCode());
            ps.setInt(2, req.getSenderId());
            ps.setString(3, req.getCategory());
            ps.setString(4, req.getTitle());
            ps.setString(5, req.getContent());
            ps.setString(6, req.getAttachmentUrls1());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("insertIncidentReport failed", e);
        }
        return false;
    }

    public boolean updateIncidentReport(Request req) {
        String sql = "UPDATE requests SET category = ?, title = ?, content = ?, attachment_urls1 = ?, updated_at = GETDATE() WHERE request_id = ? AND status = 'PENDING' AND sender_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, req.getCategory());
            ps.setString(2, req.getTitle());
            ps.setString(3, req.getContent());
            ps.setString(4, req.getAttachmentUrls1());
            ps.setInt(5, req.getId());
            ps.setInt(6, req.getSenderId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("updateIncidentReport failed", e);
        }
        return false;
    }

    public int countIncidentsBySender(int senderId) {
        String sql = "SELECT COUNT(*) FROM requests rq WHERE rq.sender_id = ? AND rq.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("countIncidentsBySender failed", e);
        }
        return 0;
    }

    public List<Request> getIncidentsBySender(int senderId, int offset, int limit) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT rq.*, u.full_name AS sender_name, r.code AS room_code, f.name AS facility_name " +
                     "FROM requests rq " +
                     "LEFT JOIN users u ON rq.sender_id = u.user_id " +
                     "LEFT JOIN rooms r ON u.user_id = r.tenant_id " +
                     "LEFT JOIN facilities f ON r.facility_id = f.facility_id " +
                     "WHERE rq.sender_id = ? AND rq.deleted_at IS NULL " +
                     "ORDER BY rq.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, senderId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("getIncidentsBySender failed", e);
        }
        return list;
    }

    // ==================== FEATURE/TENANT-LINH METHODS ====================

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
