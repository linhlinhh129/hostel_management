package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class NotificationDAO extends BaseDAO {

    private Notification mapRow(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getInt("notification_id"));
        n.setCode(rs.getString("code"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setTargetType(rs.getString("target_type"));
        n.setFacilityId(getInteger(rs, "facility_id"));
        n.setRoomId(getInteger(rs, "room_id"));
        n.setStatus(rs.getString("status"));
        n.setCreatedBy(getInteger(rs, "created_by"));
        n.setCreatedAt(toLocalDateTime(rs, "created_at"));
        n.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        n.setSentAt(toLocalDateTime(rs, "sent_at"));
        n.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        n.generateSummary();
        try {
            n.setCreatedByName(rs.getString("created_by_name"));
        } catch (SQLException ignored) {
            // column not present
        }
        return n;
    }

    private static final String BASE_SELECT =
        "SELECT n.*, u.full_name AS created_by_name " +
        "FROM dbo.notifications n " +
        "LEFT JOIN dbo.users u ON u.user_id = n.created_by " +
        "WHERE n.deleted_at IS NULL AND n.target_type = 'ALL'";

    public List<Notification> findAll(String keyword, int page, int pageSize) {
        List<Notification> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (n.code LIKE ? OR n.title LIKE ? OR n.content LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        sql.append(" ORDER BY n.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("NotificationDAO.findAll failed", e);
        }
        return list;
    }

    public List<Notification> findForTenant(int roomId, int facilityId, int page, int pageSize) {
        return findForTenant(roomId, facilityId, null, page, pageSize);
    }

    public List<Notification> findForTenant(int roomId, int facilityId, String keyword, int page, int pageSize) {
        List<Notification> list = new ArrayList<>();
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT n.*, u.full_name AS created_by_name " +
                     "FROM dbo.notifications n " +
                     "LEFT JOIN dbo.users u ON u.user_id = n.created_by " +
                     "WHERE n.status = 'SENT' AND n.deleted_at IS NULL " +
                     "AND (n.target_type = 'ALL' OR (n.target_type = 'FACILITY' AND n.facility_id = ?) OR (n.target_type = 'ROOM' AND n.room_id = ?)) " +
                     (hasKeyword ? "AND (n.title LIKE ? OR n.content LIKE ?) " : "") +
                     "ORDER BY n.sent_at DESC, n.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            ps.setInt(idx++, facilityId);
            ps.setInt(idx++, roomId);
            if (hasKeyword) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx++, like);
            }
            ps.setInt(idx++, (page - 1) * pageSize);
            ps.setInt(idx,   pageSize);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("findForTenant failed for roomId={}, facilityId={}, keyword={}", roomId, facilityId, keyword, e);
        }
        return list;
    }

    public int countForTenant(int roomId, int facilityId) {
        return countForTenant(roomId, facilityId, null);
    }

    public int countForTenant(int roomId, int facilityId, String keyword) {
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();
        String sql = "SELECT COUNT(*) FROM dbo.notifications " +
                     "WHERE status = 'SENT' AND deleted_at IS NULL " +
                     "AND (target_type = 'ALL' OR (target_type = 'FACILITY' AND facility_id = ?) OR (target_type = 'ROOM' AND room_id = ?)) " +
                     (hasKeyword ? "AND (title LIKE ? OR content LIKE ?)" : "");
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int idx = 1;
            ps.setInt(idx++, facilityId);
            ps.setInt(idx++, roomId);
            if (hasKeyword) {
                String like = "%" + keyword.trim() + "%";
                ps.setString(idx++, like);
                ps.setString(idx,   like);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("countForTenant failed for roomId={}, facilityId={}, keyword={}", roomId, facilityId, keyword, e);
        }
        return 0;
    }

    public Optional<Notification> findByIdForTenant(int id, int roomId, int facilityId) {
        String sql = "SELECT n.*, u.full_name AS created_by_name FROM dbo.notifications n " +
                     "LEFT JOIN dbo.users u ON u.user_id = n.created_by " +
                     "WHERE n.notification_id = ? AND n.status = 'SENT' AND n.deleted_at IS NULL " +
                     "AND (n.target_type = 'ALL' OR (n.target_type = 'FACILITY' AND n.facility_id = ?) OR (n.target_type = 'ROOM' AND n.room_id = ?))";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, facilityId);
            ps.setInt(3, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByIdForTenant failed for id={}", id, e);
        }
        return Optional.empty();
    }

    public int countUnreadForTenant(int roomId, int facilityId, java.time.LocalDateTime lastReadTime) {
        if (lastReadTime == null) return countForTenant(roomId, facilityId);

        String sql = "SELECT COUNT(*) FROM dbo.notifications " +
                     "WHERE status = 'SENT' AND deleted_at IS NULL " +
                     "AND (target_type = 'ALL' OR (target_type = 'FACILITY' AND facility_id = ?) OR (target_type = 'ROOM' AND room_id = ?)) " +
                     "AND (sent_at > ? OR created_at > ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setInt(2, roomId);
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(lastReadTime));
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(lastReadTime));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("countUnreadForTenant failed", e);
        }
        return 0;
    }

    public int count(String keyword) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM dbo.notifications n WHERE n.deleted_at IS NULL AND n.target_type = 'ALL'");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (n.code LIKE ? OR n.title LIKE ? OR n.content LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("NotificationDAO.count failed", e);
        }
        return 0;
    }

    public List<Notification> findNotificationsForOperator(int facilityId, int page, int limit) {
        List<Notification> list = new ArrayList<>();
        
        String condition = facilityId > 0 
            ? "(n.target_type = 'ALL' OR (n.target_type = 'FACILITY' AND n.facility_id = ?))" 
            : "n.target_type = 'ALL'";
            
        String sql = "SELECT n.*, u.full_name AS created_by_name " +
                     "FROM dbo.notifications n " +
                     "LEFT JOIN dbo.users u ON u.user_id = n.created_by " +
                     "WHERE n.status = 'SENT' AND n.deleted_at IS NULL " +
                     "AND " + condition + " " +
                     "ORDER BY n.sent_at DESC, n.created_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (facilityId > 0) {
                ps.setInt(paramIndex++, facilityId);
            }
            ps.setInt(paramIndex++, (page - 1) * limit);
            ps.setInt(paramIndex, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findNotificationsForOperator failed for facilityId={}", facilityId, e);
        }
        return list;
    }

    public int countNotificationsForOperator(int facilityId) {
        String condition = facilityId > 0 
            ? "(n.target_type = 'ALL' OR (n.target_type = 'FACILITY' AND n.facility_id = ?))" 
            : "n.target_type = 'ALL'";
            
        String sql = "SELECT COUNT(*) FROM dbo.notifications n " +
                     "WHERE n.status = 'SENT' AND n.deleted_at IS NULL " +
                     "AND " + condition;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (facilityId > 0) {
                ps.setInt(1, facilityId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("countNotificationsForOperator failed for facilityId={}", facilityId, e);
        }
        return 0;
    }

    public Optional<Notification> findById(int id) {
        String sql = BASE_SELECT + " AND n.notification_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("NotificationDAO.findById failed for id={}", id, e);
        }
        return Optional.empty();
    }

    public int insert(Notification n) {
        String sql = "INSERT INTO dbo.notifications " +
            "(code, title, content, target_type, facility_id, room_id, status, " +
            " created_by, created_at, updated_at, sent_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE(), " +
            "  CASE WHEN ? = 'SENT' THEN GETDATE() ELSE NULL END)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, n.getCode());
            ps.setString(2, n.getTitle());
            ps.setString(3, n.getContent());
            ps.setString(4, n.getTargetType() != null ? n.getTargetType() : "ALL");
            if (n.getFacilityId() != null) ps.setInt(5, n.getFacilityId());
            else ps.setNull(5, Types.INTEGER);
            if (n.getRoomId() != null) ps.setInt(6, n.getRoomId());
            else ps.setNull(6, Types.INTEGER);
            ps.setString(7, n.getStatus() != null ? n.getStatus() : "DRAFT");
            if (n.getCreatedBy() != null) ps.setInt(8, n.getCreatedBy());
            else ps.setNull(8, Types.INTEGER);
            ps.setString(9, n.getStatus() != null ? n.getStatus() : "DRAFT");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            logger.error("NotificationDAO.insert failed", e);
        }
        return -1;
    }

    /**
     * Generates next notification code in format NTF-{TYPE}-{SEQ}.
     * TYPE: ALL, FAC, ROOM (mapped from target_type ALL/FACILITY/ROOM)
     * SEQ: 3-digit sequence scoped per type, e.g. NTF-ALL-001
     */
    public String generateCode(String targetType) {
        String typeTag;
        switch (targetType == null ? "ALL" : targetType.toUpperCase()) {
            case "FACILITY": typeTag = "FAC";  break;
            case "ROOM":     typeTag = "ROOM"; break;
            case "DEBT":     typeTag = "DEBT"; break;
            default:         typeTag = "ALL";  break;
        }
        // Use MAX to avoid duplicates under concurrent inserts
        String sql = "SELECT ISNULL(MAX(CAST(SUBSTRING(code, LEN(?) + 2, 3) AS INT)), 0) " +
                     "FROM dbo.notifications " +
                     "WHERE code LIKE ? AND deleted_at IS NULL";
        String prefix = "NTF-" + typeTag + "-";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, prefix);
            ps.setString(2, prefix + "___");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int next = rs.getInt(1) + 1;
                    return String.format("NTF-%s-%03d", typeTag, next);
                }
            }
        } catch (Exception e) {
            logger.error("NotificationDAO.generateCode failed for targetType={}", targetType, e);
        }
        return String.format("NTF-%s-001", typeTag);
    }

    public int countManagerNotifications(int managerId, String tab, String type, Integer filterFacilityId, String keyword) {
        int totalCount = 0;
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if ("general".equals(tab)) {
            if ("received".equals(type)) {
                whereClause.append(" WHERE n.deleted_at IS NULL AND n.status = 'SENT' AND u.role = 'ADMIN'");
                whereClause.append(" AND (n.target_type = 'ALL'");
                whereClause.append(" OR n.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)");
                whereClause.append(" OR n.room_id IN (SELECT r.room_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL))");
                params.add(managerId);
                params.add(managerId);
            } else {
                whereClause.append(" WHERE n.deleted_at IS NULL AND n.created_by = ? AND n.code NOT LIKE 'NTF-DEBT-%'");
                params.add(managerId);
            }
        } else if ("payment-reminder".equals(tab)) {
            whereClause.append(" WHERE n.deleted_at IS NULL AND n.created_by = ? AND n.code LIKE 'NTF-DEBT-%'");
            params.add(managerId);
        } else {
            whereClause.append(" WHERE n.deleted_at IS NULL AND (n.created_by = ? OR n.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL) OR n.room_id IN (SELECT r.room_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL))");
            params.add(managerId);
            params.add(managerId);
            params.add(managerId);
        }

        if (filterFacilityId != null) {
            whereClause.append(" AND (n.facility_id = ? OR n.room_id IN (SELECT room_id FROM dbo.rooms WHERE facility_id = ? AND deleted_at IS NULL))");
            params.add(filterFacilityId);
            params.add(filterFacilityId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            whereClause.append(" AND (n.title LIKE ? OR n.content LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }

        String countSql = "SELECT COUNT(*) FROM dbo.notifications n" +
                " JOIN dbo.users u ON n.created_by = u.user_id " +
                " LEFT JOIN dbo.rooms r ON n.room_id = r.room_id AND r.deleted_at IS NULL " +
                " LEFT JOIN dbo.facilities f ON (n.facility_id = f.facility_id OR r.facility_id = f.facility_id) AND f.deleted_at IS NULL"
                + whereClause.toString();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) totalCount = rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("countManagerNotifications failed", e);
        }
        return totalCount;
    }

    public List<Map<String, Object>> getManagerNotifications(int managerId, String tab, String type, Integer filterFacilityId, String keyword, int offset, int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();

        if ("general".equals(tab)) {
            if ("received".equals(type)) {
                whereClause.append(" WHERE n.deleted_at IS NULL AND n.status = 'SENT' AND u.role = 'ADMIN'");
                whereClause.append(" AND (n.target_type = 'ALL'");
                whereClause.append(" OR n.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)");
                whereClause.append(" OR n.room_id IN (SELECT r.room_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL))");
                params.add(managerId);
                params.add(managerId);
            } else {
                whereClause.append(" WHERE n.deleted_at IS NULL AND n.created_by = ? AND n.code NOT LIKE 'NTF-DEBT-%'");
                params.add(managerId);
            }
        } else if ("payment-reminder".equals(tab)) {
            whereClause.append(" WHERE n.deleted_at IS NULL AND n.created_by = ? AND n.code LIKE 'NTF-DEBT-%'");
            params.add(managerId);
        } else {
            whereClause.append(" WHERE n.deleted_at IS NULL AND (n.created_by = ? OR n.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL) OR n.room_id IN (SELECT r.room_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL))");
            params.add(managerId);
            params.add(managerId);
            params.add(managerId);
        }

        if (filterFacilityId != null) {
            whereClause.append(" AND (n.facility_id = ? OR n.room_id IN (SELECT room_id FROM dbo.rooms WHERE facility_id = ? AND deleted_at IS NULL))");
            params.add(filterFacilityId);
            params.add(filterFacilityId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            whereClause.append(" AND (n.title LIKE ? OR n.content LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }

        String selectSql = "SELECT n.*, u.full_name AS creator_name, u.role AS creator_role FROM dbo.notifications n " +
                "JOIN dbo.users u ON n.created_by = u.user_id " +
                "LEFT JOIN dbo.rooms r ON n.room_id = r.room_id AND r.deleted_at IS NULL " +
                "LEFT JOIN dbo.facilities f ON (n.facility_id = f.facility_id OR r.facility_id = f.facility_id) AND f.deleted_at IS NULL"
                + whereClause.toString() +
                " ORDER BY n.notification_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

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
                    Map<String, Object> notif = new HashMap<>();
                    notif.put("id", rs.getInt("notification_id"));
                    notif.put("code", rs.getString("code"));
                    notif.put("title", rs.getString("title"));
                    notif.put("recipientType", rs.getString("target_type"));
                    notif.put("createdByName", rs.getString("creator_name"));
                    notif.put("creatorRole", rs.getString("creator_role"));
                    notif.put("status", rs.getString("status"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    String dateLabel = "";
                    if (cAt != null) {
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                        dateLabel = sdf.format(cAt);
                    }
                    notif.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                    notif.put("createdDateLabel", dateLabel);
                    list.add(notif);
                }
            }
        } catch (Exception e) {
            logger.error("getManagerNotifications failed", e);
        }
        return list;
    }

    public List<Map<String, Object>> getAssignedFacilitiesForManager(int managerId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT facility_id, code, name FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id", rs.getInt("facility_id"));
                    f.put("code", rs.getString("code"));
                    f.put("name", rs.getString("name"));
                    list.add(f);
                }
            }
        } catch (Exception e) {
            logger.error("getAssignedFacilitiesForManager failed", e);
        }
        return list;
    }

    public List<Map<String, Object>> getReportedIncorrectInvoices(int managerId, Integer filterFacilityId, String keyword) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder incorrectSql = new StringBuilder(
                "SELECT i.invoice_id, i.code AS invoice_code, r.room_id, r.code AS room_code, f.name AS facility_name, f.code AS facility_code, " +
                "mr.meter_id, mr.electric, mr.water, mr.reading_date, mr.status AS meter_status, i.total_amount, " +
                "(SELECT TOP 1 req.status FROM dbo.requests req WHERE req.category = 'UTILITY' AND req.title LIKE N'%' + RTRIM(r.code) AND req.content LIKE N'%Hóa đơn kỳ ' + FORMAT(mr.reading_date, 'MM/yyyy') + '%' AND req.deleted_at IS NULL ORDER BY req.request_id DESC) AS ticket_status, " +
                "(SELECT TOP 1 req.request_id FROM dbo.requests req WHERE req.category = 'UTILITY' AND req.title LIKE N'%' + RTRIM(r.code) AND req.content LIKE N'%Hóa đơn kỳ ' + FORMAT(mr.reading_date, 'MM/yyyy') + '%' AND req.deleted_at IS NULL ORDER BY req.request_id DESC) AS ticket_id, " +
                "(SELECT TOP 1 uop.full_name FROM dbo.requests req JOIN dbo.users uop ON req.assigned_staff_id = uop.user_id WHERE req.category = 'UTILITY' AND req.title LIKE N'%' + RTRIM(r.code) AND req.content LIKE N'%Hóa đơn kỳ ' + FORMAT(mr.reading_date, 'MM/yyyy') + '%' AND req.deleted_at IS NULL ORDER BY req.request_id DESC) AS operator_name " +
                "FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "JOIN dbo.meter_readings mr ON i.meter_id = mr.meter_id " +
                "WHERE f.manager_id = ? AND mr.status IN ('INCORRECT', 'REPORTED', 'UPDATED') AND i.deleted_at IS NULL AND mr.deleted_at IS NULL"
        );
        List<Object> incParams = new ArrayList<>();
        incParams.add(managerId);

        if (filterFacilityId != null) {
            incorrectSql.append(" AND f.facility_id = ?");
            incParams.add(filterFacilityId);
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            incorrectSql.append(" AND (i.code LIKE ? OR r.code LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            incParams.add(kw);
            incParams.add(kw);
        }
        incorrectSql.append(" ORDER BY mr.reading_date DESC, i.invoice_id DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(incorrectSql.toString())) {
            for (int i = 0; i < incParams.size(); i++) {
                ps.setObject(i + 1, incParams.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("invoice_id"));
                    item.put("code", rs.getString("invoice_code"));
                    item.put("roomId", rs.getInt("room_id"));
                    item.put("roomCode", rs.getString("room_code"));
                    item.put("facilityName", rs.getString("facility_name"));
                    item.put("facilityCode", rs.getString("facility_code"));
                    item.put("electric", rs.getInt("electric"));
                    item.put("water", rs.getInt("water"));
                    item.put("meterStatus", rs.getString("meter_status"));
                    item.put("totalAmount", rs.getDouble("total_amount"));
                    item.put("ticketStatus", rs.getString("ticket_status"));
                    item.put("ticketId", rs.getObject("ticket_id") != null ? rs.getInt("ticket_id") : null);
                    item.put("operatorName", rs.getString("operator_name"));
                    
                    java.sql.Date rDate = rs.getDate("reading_date");
                    if (rDate != null) {
                        java.time.LocalDate localDate = rDate.toLocalDate();
                        item.put("billingPeriod", String.format("%02d/%d", localDate.getMonthValue(), localDate.getYear()));
                    } else {
                        item.put("billingPeriod", "—");
                    }
                    list.add(item);
                }
            }
        } catch (Exception e) {
            logger.error("getReportedIncorrectInvoices failed", e);
        }
        return list;
    }

    public Integer getRoomFacilityId(int roomId) {
        String sql = "SELECT facility_id FROM dbo.rooms WHERE room_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("facility_id");
                }
            }
        } catch (Exception e) {
            logger.error("getRoomFacilityId failed", e);
        }
        return null;
    }

    public List<Map<String, Object>> getAssignedRoomsForManager(int managerId) {
        List<Map<String, Object>> list = new ArrayList<>();
        String roomsSql = "SELECT r.room_id, r.code AS room_code, r.facility_id " +
                "FROM dbo.rooms r " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND r.tenant_id IS NOT NULL AND r.deleted_at IS NULL AND f.deleted_at IS NULL " +
                "ORDER BY f.name, r.code";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(roomsSql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> r = new HashMap<>();
                    r.put("id", rs.getInt("room_id"));
                    r.put("code", rs.getString("room_code"));
                    r.put("facilityId", rs.getInt("facility_id"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            logger.error("getAssignedRoomsForManager failed", e);
        }
        return list;
    }

    public boolean verifyFacilityManager(int facilityId, int managerId) {
        String sql = "SELECT 1 FROM dbo.facilities WHERE facility_id = ? AND manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("verifyFacilityManager failed", e);
            return false;
        }
    }

    public Integer verifyRoomManagerAndGetFacilityId(int roomId, int managerId) {
        String sql = "SELECT r.facility_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE r.room_id = ? AND f.manager_id = ? AND r.tenant_id IS NOT NULL AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("facility_id");
                }
            }
        } catch (Exception e) {
            logger.error("verifyRoomManagerAndGetFacilityId failed", e);
        }
        return null;
    }

    public int insertNotificationAndGetId(String code, String title, String content, String recipientType, Integer facilityId, Integer roomId, int createdBy) {
        String sql = "INSERT INTO dbo.notifications (code, title, content, target_type, facility_id, room_id, status, created_by, created_at, sent_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'SENT', ?, GETDATE(), GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, title.trim());
            ps.setString(3, content.trim());
            ps.setString(4, recipientType);
            if (facilityId != null) ps.setInt(5, facilityId); else ps.setNull(5, java.sql.Types.INTEGER);
            if (roomId != null) ps.setInt(6, roomId); else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setInt(7, createdBy);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("insertNotificationAndGetId failed", e);
        }
        return -1;
    }

    public Map<String, Object> getNotificationDetail(int notificationId) {
        Map<String, Object> notification = null;
        String sql = "SELECT n.*, u.full_name AS creator_name, " +
                "f.code AS facility_code, f.name AS facility_name, f.manager_id AS target_facility_manager_id, " +
                "r.code AS room_code, rf.manager_id AS target_room_facility_manager_id " +
                "FROM dbo.notifications n " +
                "JOIN dbo.users u ON n.created_by = u.user_id " +
                "LEFT JOIN dbo.facilities f ON n.facility_id = f.facility_id AND f.deleted_at IS NULL " +
                "LEFT JOIN dbo.rooms r ON n.room_id = r.room_id AND r.deleted_at IS NULL " +
                "LEFT JOIN dbo.facilities rf ON r.facility_id = rf.facility_id AND rf.deleted_at IS NULL " +
                "WHERE n.notification_id = ? AND n.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    notification = new HashMap<>();
                    notification.put("id", rs.getInt("notification_id"));
                    notification.put("code", rs.getString("code"));
                    notification.put("title", rs.getString("title"));
                    notification.put("content", rs.getString("content"));
                    notification.put("recipientType", rs.getString("target_type"));
                    notification.put("createdByName", rs.getString("creator_name"));
                    notification.put("status", rs.getString("status"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    Timestamp sAt = rs.getTimestamp("sent_at");
                    notification.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                    notification.put("sentAt", sAt != null ? sAt.toLocalDateTime().toString().replace("T", " ") : "—");
                    
                    notification.put("created_by", rs.getInt("created_by"));
                    notification.put("target_facility_manager_id", rs.getObject("target_facility_manager_id") != null ? rs.getInt("target_facility_manager_id") : null);
                    notification.put("target_room_facility_manager_id", rs.getObject("target_room_facility_manager_id") != null ? rs.getInt("target_room_facility_manager_id") : null);

                    String targetType = rs.getString("target_type");
                    if ("FACILITY".equals(targetType)) {
                        notification.put("recipientName", rs.getString("facility_name") + " (" + rs.getString("facility_code") + ")");
                    } else if ("ROOM".equals(targetType)) {
                        notification.put("recipientName", "Phòng " + rs.getString("room_code"));
                    } else {
                        notification.put("recipientName", "Toàn hệ thống");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getNotificationDetail failed", e);
        }
        return notification;
    }

    public Map<String, Object> getInvoiceVerifyDetails(int invoiceId) {
        Map<String, Object> invoice = null;
        String verifySql = "SELECT i.meter_id, i.code, i.status, f.manager_id FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE i.invoice_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invoice = new HashMap<>();
                    invoice.put("meterId", rs.getInt("meter_id"));
                    invoice.put("code", rs.getString("code"));
                    invoice.put("status", rs.getString("status"));
                    invoice.put("managerId", rs.getInt("manager_id"));
                }
            }
        } catch (Exception e) {
            logger.error("getInvoiceVerifyDetails failed", e);
        }
        return invoice;
    }

    public boolean updateMeterReadingStatus(int meterId, String status) {
        String updateSql = "UPDATE dbo.meter_readings SET status = ?, updated_at = GETDATE() WHERE meter_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, status);
            ps.setInt(2, meterId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("updateMeterReadingStatus failed", e);
            return false;
        }
    }

    public Map<String, Object> getInvoiceDetailsForSendOperator(int invoiceId) {
        Map<String, Object> invoice = null;
        String sql = "SELECT i.*, r.code AS room_code, f.facility_id, f.code AS facility_code, f.name AS facility_name, f.manager_id, " +
                "mr.electric, mr.water, mr.reading_date, mr.status AS meter_status, " +
                "(SELECT TOP 1 req.status FROM dbo.requests req WHERE req.category = 'UTILITY' AND req.title LIKE N'%' + RTRIM(r.code) AND req.content LIKE N'%Hóa đơn kỳ ' + FORMAT(mr.reading_date, 'MM/yyyy') + '%' AND req.deleted_at IS NULL ORDER BY req.request_id DESC) AS ticket_status, " +
                "(SELECT TOP 1 req.request_id FROM dbo.requests req WHERE req.category = 'UTILITY' AND req.title LIKE N'%' + RTRIM(r.code) AND req.content LIKE N'%Hóa đơn kỳ ' + FORMAT(mr.reading_date, 'MM/yyyy') + '%' AND req.deleted_at IS NULL ORDER BY req.request_id DESC) AS ticket_id, " +
                "(SELECT TOP 1 uop.full_name FROM dbo.requests req JOIN dbo.users uop ON req.assigned_staff_id = uop.user_id WHERE req.category = 'UTILITY' AND req.title LIKE N'%' + RTRIM(r.code) AND req.content LIKE N'%Hóa đơn kỳ ' + FORMAT(mr.reading_date, 'MM/yyyy') + '%' AND req.deleted_at IS NULL ORDER BY req.request_id DESC) AS operator_name " +
                "FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "JOIN dbo.meter_readings mr ON i.meter_id = mr.meter_id " +
                "WHERE i.invoice_id = ? AND i.deleted_at IS NULL AND mr.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invoice = new HashMap<>();
                    invoice.put("id", rs.getInt("invoice_id"));
                    invoice.put("code", rs.getString("code"));
                    invoice.put("roomCode", rs.getString("room_code"));
                    invoice.put("facilityName", rs.getString("facility_name"));
                    invoice.put("facilityCode", rs.getString("facility_code"));
                    invoice.put("electric", rs.getInt("electric"));
                    invoice.put("water", rs.getInt("water"));
                    invoice.put("totalAmount", rs.getDouble("total_amount"));
                    invoice.put("meterStatus", rs.getString("meter_status"));
                    invoice.put("ticketStatus", rs.getString("ticket_status"));
                    invoice.put("ticketId", rs.getObject("ticket_id") != null ? rs.getInt("ticket_id") : null);
                    invoice.put("operatorName", rs.getString("operator_name"));

                    java.sql.Date rDate = rs.getDate("reading_date");
                    if (rDate != null) {
                        java.time.LocalDate localDate = rDate.toLocalDate();
                        invoice.put("billingPeriod", String.format("%02d/%d", localDate.getMonthValue(), localDate.getYear()));
                    } else {
                        invoice.put("billingPeriod", "—");
                    }
                    invoice.put("facilityId", rs.getInt("facility_id"));
                    invoice.put("managerId", rs.getInt("manager_id"));
                }
            }
        } catch (Exception e) {
            logger.error("getInvoiceDetailsForSendOperator failed", e);
        }
        return invoice;
    }

    public List<Map<String, Object>> getActiveOperatorsForFacility(int facilityId) {
        List<Map<String, Object>> operators = new ArrayList<>();
        String opsSql = "SELECT u.user_id, u.full_name FROM dbo.users u " +
                "JOIN dbo.facilities f ON u.user_id = f.operator_id " +
                "WHERE u.role = 'OPERATOR' AND u.status = 'ACTIVE' AND u.deleted_at IS NULL " +
                "AND f.facility_id = ? AND f.deleted_at IS NULL " +
                "ORDER BY u.full_name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(opsSql)) {
            ps.setInt(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> op = new HashMap<>();
                    op.put("id", rs.getInt("user_id"));
                    op.put("fullName", rs.getString("full_name"));
                    operators.add(op);
                }
            }
        } catch (Exception e) {
            logger.error("getActiveOperatorsForFacility failed", e);
        }
        return operators;
    }

    public boolean sendOperatorRequestTransaction(String reqCode, int managerId, String title, String content, int operatorId, int meterId) {
        String insertReqSql = "INSERT INTO dbo.requests (code, sender_id, category, title, content, status, assigned_staff_id, created_at, updated_at) " +
                "VALUES (?, ?, 'UTILITY', ?, ?, 'PENDING', ?, GETDATE(), GETDATE())";
        String updateMeterSql = "UPDATE dbo.meter_readings SET status = 'REPORTED', updated_at = GETDATE() WHERE meter_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement psReq = conn.prepareStatement(insertReqSql)) {
                psReq.setString(1, reqCode);
                psReq.setInt(2, managerId);
                psReq.setString(3, title.trim());
                psReq.setString(4, content.trim());
                psReq.setInt(5, operatorId);
                psReq.executeUpdate();
            }

            try (PreparedStatement psMeter = conn.prepareStatement(updateMeterSql)) {
                psMeter.setInt(1, meterId);
                psMeter.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("sendOperatorRequestTransaction failed", e);
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    public Map<String, Object> getInvoiceDetailsForSendDebt(int invoiceId) {
        Map<String, Object> invoice = null;
        String sql = "SELECT i.invoice_id, i.code AS invoice_code, i.total_amount, i.due_date, " +
                "r.room_id, r.code AS room_code, f.facility_id, f.name AS facility_name, f.manager_id, " +
                "u.full_name AS tenant_name, u.phone AS tenant_phone " +
                "FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "LEFT JOIN dbo.users u ON r.tenant_id = u.user_id " +
                "WHERE i.invoice_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    invoice = new HashMap<>();
                    invoice.put("id", rs.getInt("invoice_id"));
                    invoice.put("code", rs.getString("invoice_code"));
                    invoice.put("totalAmount", rs.getDouble("total_amount"));
                    invoice.put("roomCode", rs.getString("room_code"));
                    invoice.put("roomId", rs.getInt("room_id"));
                    invoice.put("facilityId", rs.getInt("facility_id"));
                    invoice.put("facilityName", rs.getString("facility_name"));
                    invoice.put("managerId", rs.getInt("manager_id"));
                    invoice.put("tenantName", rs.getString("tenant_name") != null ? rs.getString("tenant_name") : "Chưa có");
                    invoice.put("tenantPhone", rs.getString("tenant_phone") != null ? rs.getString("tenant_phone") : "—");

                    java.sql.Date dDate = rs.getDate("due_date");
                    if (dDate != null) {
                        java.time.LocalDate localDate = dDate.toLocalDate();
                        invoice.put("dueDateLabel", String.format("%02d/%02d/%d", localDate.getDayOfMonth(), localDate.getMonthValue(), localDate.getYear()));
                        invoice.put("billingPeriod", String.format("%02d/%d", localDate.getMonthValue(), localDate.getYear()));
                        
                        long days = java.time.temporal.ChronoUnit.DAYS.between(localDate, java.time.LocalDate.now());
                        invoice.put("overdueDays", days > 0 ? days : 0);
                    } else {
                        invoice.put("dueDateLabel", "—");
                        invoice.put("billingPeriod", "—");
                        invoice.put("overdueDays", 0);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("getInvoiceDetailsForSendDebt failed", e);
        }
        return invoice;
    }

    public boolean sendDebtReminder(String code, String title, String content, int roomId, int createdBy) {
        String sql = "INSERT INTO dbo.notifications (code, title, content, target_type, facility_id, room_id, status, created_by, created_at, sent_at) " +
                "VALUES (?, ?, ?, 'ROOM', NULL, ?, 'SENT', ?, GETDATE(), GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, title.trim());
            ps.setString(3, content.trim());
            ps.setInt(4, roomId);
            ps.setInt(5, createdBy);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("sendDebtReminder failed", e);
            return false;
        }
    }
}
