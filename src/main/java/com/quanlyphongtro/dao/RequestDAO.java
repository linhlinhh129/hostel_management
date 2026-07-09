package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

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

    public int countManagerTickets(int managerId, String type, String status, String keyword) {
        int totalTickets = 0;
        StringBuilder whereClause = new StringBuilder(
                " WHERE f.manager_id = ? AND req.deleted_at IS NULL AND u.role = ?");
        List<Object> params = new ArrayList<>();
        params.add(managerId);
        params.add(type);

        if ("TENANT".equals(type)) {
            whereClause.append(" AND req.assigned_staff_id = ?");
            params.add(managerId);
        }

        if (status != null && !status.trim().isEmpty()) {
            whereClause.append(" AND req.status = ?");
            params.add(status.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeParam = "%" + keyword.trim() + "%";
            whereClause.append(" AND (req.title LIKE ? OR req.code LIKE ?)");
            params.add(likeParam);
            params.add(likeParam);
        }

        String countSql = "SELECT COUNT(*) FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL"
                + whereClause.toString();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalTickets = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("countManagerTickets failed", e);
        }
        return totalTickets;
    }

    public List<Map<String, Object>> getManagerTickets(int managerId, String type, String status, String keyword, int offset, int limit) {
        List<Map<String, Object>> tickets = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder(
                " WHERE f.manager_id = ? AND req.deleted_at IS NULL AND u.role = ?");
        List<Object> params = new ArrayList<>();
        params.add(managerId);
        params.add(type);

        if ("TENANT".equals(type)) {
            whereClause.append(" AND req.assigned_staff_id = ?");
            params.add(managerId);
        }

        if (status != null && !status.trim().isEmpty()) {
            whereClause.append(" AND req.status = ?");
            params.add(status.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeParam = "%" + keyword.trim() + "%";
            whereClause.append(" AND (req.title LIKE ? OR req.code LIKE ?)");
            params.add(likeParam);
            params.add(likeParam);
        }

        String selectSql = "SELECT req.*, u.full_name AS sender_name, u.role AS sender_role, r.room_id, r.code AS room_code, f.name AS facility_name "
                + "FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL"
                + whereClause.toString() +
                " ORDER BY req.request_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            int i = 0;
            for (; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setInt(i + 1, offset);
            ps.setInt(i + 2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> ticket = new HashMap<>();
                    ticket.put("id", rs.getInt("request_id"));
                    ticket.put("code", rs.getString("code"));
                    ticket.put("category", rs.getString("category"));
                    ticket.put("title", rs.getString("title"));
                    ticket.put("senderName", rs.getString("sender_name"));
                    ticket.put("senderRole", rs.getString("sender_role"));
                    ticket.put("roomId", rs.getInt("room_id"));
                    ticket.put("roomCode", rs.getString("room_code"));
                    ticket.put("facilityName", rs.getString("facility_name"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    ticket.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                    ticket.put("status", rs.getString("status"));
                    tickets.add(ticket);
                }
            }
        } catch (Exception e) {
            logger.error("getManagerTickets failed", e);
        }
        return tickets;
    }

    public Map<String, Object> getManagerTicketDetail(int ticketId) {
        Map<String, Object> ticket = null;
        String ticketSql = "SELECT req.*, u.full_name AS sender_name, u.role AS sender_role, u.phone AS sender_phone, "
                + "r.room_id, r.code AS room_code, f.facility_id, f.name AS facility_name, f.manager_id, o.full_name AS assigned_operator_name "
                + "FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL "
                + "LEFT JOIN dbo.users o ON req.assigned_staff_id = o.user_id " +
                "WHERE req.request_id = ? AND req.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(ticketSql)) {
            ps.setInt(1, ticketId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ticket = new HashMap<>();
                    ticket.put("id", rs.getInt("request_id"));
                    ticket.put("code", rs.getString("code"));
                    ticket.put("category", rs.getString("category"));
                    ticket.put("title", rs.getString("title"));
                    ticket.put("content", rs.getString("content"));
                    ticket.put("status", rs.getString("status"));
                    ticket.put("senderId", rs.getInt("sender_id"));
                    ticket.put("senderName", rs.getString("sender_name"));
                    ticket.put("senderRole", rs.getString("sender_role"));
                    ticket.put("senderPhone", rs.getString("sender_phone"));
                    ticket.put("roomId", rs.getInt("room_id"));
                    ticket.put("roomCode", rs.getString("room_code"));
                    ticket.put("facilityId", rs.getInt("facility_id"));
                    ticket.put("facilityName", rs.getString("facility_name"));
                    ticket.put("managerId", rs.getInt("manager_id"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    Timestamp uAt = rs.getTimestamp("updated_at");
                    ticket.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                    ticket.put("updatedAt", uAt != null ? uAt.toLocalDateTime().toString().replace("T", " ") : "");
                    ticket.put("assignedOperatorName", rs.getString("assigned_operator_name"));
                    ticket.put("rejectionReason", rs.getString("rejection_reason"));
                    ticket.put("attachmentUrls1", rs.getString("attachment_urls1"));
                    ticket.put("attachmentUrls2", rs.getString("attachment_urls2"));
                    Timestamp appointAt = rs.getTimestamp("appoint_schedule");
                    if (appointAt != null) {
                        ticket.put("appointSchedule", appointAt.toLocalDateTime().toString().replace("T", " "));
                        ticket.put("appointScheduleFormatted", appointAt.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getManagerTicketDetail failed", e);
        }
        return ticket;
    }

    public List<Map<String, Object>> getOperatorsForFacility(int facilityId, int managerId) {
        List<Map<String, Object>> operators = new ArrayList<>();
        String opsSql;
        if (facilityId > 0) {
            opsSql = "SELECT u.user_id, u.full_name FROM dbo.users u " +
                    "JOIN dbo.facilities f ON u.user_id = f.operator_id " +
                    "WHERE u.role = 'OPERATOR' AND u.status = 'ACTIVE' AND u.deleted_at IS NULL " +
                    "AND f.facility_id = ? AND f.deleted_at IS NULL " +
                    "ORDER BY u.full_name";
        } else {
            opsSql = "SELECT u.user_id, u.full_name FROM dbo.users u " +
                    "JOIN dbo.facilities f ON u.user_id = f.operator_id " +
                    "WHERE u.role = 'OPERATOR' AND u.status = 'ACTIVE' AND u.deleted_at IS NULL " +
                    "AND f.manager_id = ? AND f.deleted_at IS NULL " +
                    "ORDER BY u.full_name";
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(opsSql)) {
            if (facilityId > 0) {
                ps.setInt(1, facilityId);
            } else {
                ps.setInt(1, managerId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> op = new HashMap<>();
                    op.put("id", rs.getInt("user_id"));
                    op.put("fullName", rs.getString("full_name"));
                    operators.add(op);
                }
            }
        } catch (Exception e) {
            logger.error("getOperatorsForFacility failed", e);
        }
        return operators;
    }

    public boolean receiveTicket(int ticketId) {
        String sql = "UPDATE dbo.requests SET status = 'RECEIVED', updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("receiveTicket failed for id={}", ticketId, e);
            return false;
        }
    }

    public boolean assignTicket(int ticketId, int operatorId) {
        String sql = "UPDATE dbo.requests SET status = 'ASSIGNED', assigned_staff_id = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("assignTicket failed for id={}", ticketId, e);
            return false;
        }
    }

    public boolean rejectTicket(int ticketId, String reason) {
        String sql = "UPDATE dbo.requests SET status = 'REJECTED', rejection_reason = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason);
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("rejectTicket failed for id={}", ticketId, e);
            return false;
        }
    }

    public boolean scheduleTicket(int ticketId, java.time.LocalDateTime scheduleTime) {
        String sql = "UPDATE dbo.requests SET status = 'IN_PROGRESS', appoint_schedule = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (scheduleTime != null) {
                ps.setTimestamp(1, java.sql.Timestamp.valueOf(scheduleTime));
            } else {
                ps.setNull(1, java.sql.Types.TIMESTAMP);
            }
            ps.setInt(2, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("scheduleTicket failed for id={}", ticketId, e);
            return false;
        }
    }

    public boolean completeTicket(int ticketId, String notes, String attachmentUrls2) {
        String sql = "UPDATE dbo.requests SET status = 'DONE', rejection_reason = ?, attachment_urls2 = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes);
            if (attachmentUrls2 != null) {
                ps.setString(2, attachmentUrls2);
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }
            ps.setInt(3, ticketId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("completeTicket failed for id={}", ticketId, e);
            return false;
        }
    }
}
