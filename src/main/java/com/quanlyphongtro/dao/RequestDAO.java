package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO {

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
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setCode(rs.getString("code"));
                    req.setSenderId(rs.getInt("sender_id"));
                    req.setCategory(rs.getString("category"));
                    req.setTitle(rs.getString("title"));
                    req.setContent(rs.getString("content"));
                    req.setStatus(rs.getString("status"));
                    req.setAttachmentUrls1(rs.getString("attachment_urls1"));
                    req.setAttachmentUrls2(rs.getString("attachment_urls2"));
                    
                    int assignedStaffId = rs.getInt("assigned_staff_id");
                    if (!rs.wasNull()) req.setAssignedStaffId(assignedStaffId);
                    
                    req.setRejectionReason(rs.getString("rejection_reason"));
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setUpdatedAt(rs.getTimestamp("updated_at"));
                    req.setDeletedAt(rs.getTimestamp("deleted_at"));
                    
                    req.setSenderName(rs.getString("sender_name"));
                    req.setRoomCode(rs.getString("room_code"));
                    req.setFacilityName(rs.getString("facility_name"));
                    return req;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the request status with optimistic locking (concurrency control).
     * @return true if successful (rows affected > 0), false otherwise
     */
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            sql.append(" AND (rq.assigned_staff_id = ").append(assigneeId).append(" OR (rq.status = 'PENDING' AND u.role = 'TENANT'))");
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
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setCode(rs.getString("code"));
                    req.setSenderId(rs.getInt("sender_id"));
                    req.setCategory(rs.getString("category"));
                    req.setTitle(rs.getString("title"));
                    req.setContent(rs.getString("content"));
                    req.setStatus(rs.getString("status"));
                    req.setAttachmentUrls1(rs.getString("attachment_urls1"));
                    req.setAttachmentUrls2(rs.getString("attachment_urls2"));
                    
                    int assignedStaffId = rs.getInt("assigned_staff_id");
                    if (!rs.wasNull()) req.setAssignedStaffId(assignedStaffId);
                    
                    req.setRejectionReason(rs.getString("rejection_reason"));
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setUpdatedAt(rs.getTimestamp("updated_at"));
                    req.setDeletedAt(rs.getTimestamp("deleted_at"));
                    
                    req.setSenderName(rs.getString("sender_name"));
                    req.setRoomCode(rs.getString("room_code"));
                    req.setFacilityName(rs.getString("facility_name"));
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean completeRequest(int requestId, String notes, String attachmentUrls2) {
        String sql = "UPDATE requests SET status = 'COMPLETED', rejection_reason = ?, attachment_urls2 = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes);
            ps.setString(2, attachmentUrls2);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAppointmentDateText(int requestId, String appointmentDateStr) {
        String sql = "UPDATE requests SET rejection_reason = ?, updated_at = GETDATE() WHERE request_id = ? AND status IN ('PENDING', 'IN_PROGRESS')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, appointmentDateStr);
            ps.setInt(2, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            ps.setInt(5, req.getRequestId());
            ps.setInt(6, req.getSenderId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setCode(rs.getString("code"));
                    req.setSenderId(rs.getInt("sender_id"));
                    req.setCategory(rs.getString("category"));
                    req.setTitle(rs.getString("title"));
                    req.setContent(rs.getString("content"));
                    req.setStatus(rs.getString("status"));
                    req.setAttachmentUrls1(rs.getString("attachment_urls1"));
                    req.setAttachmentUrls2(rs.getString("attachment_urls2"));
                    
                    int assignedStaffId = rs.getInt("assigned_staff_id");
                    if (!rs.wasNull()) req.setAssignedStaffId(assignedStaffId);
                    
                    req.setRejectionReason(rs.getString("rejection_reason"));
                    req.setCreatedAt(rs.getTimestamp("created_at"));
                    req.setUpdatedAt(rs.getTimestamp("updated_at"));
                    req.setDeletedAt(rs.getTimestamp("deleted_at"));
                    
                    req.setSenderName(rs.getString("sender_name"));
                    req.setRoomCode(rs.getString("room_code"));
                    req.setFacilityName(rs.getString("facility_name"));
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
