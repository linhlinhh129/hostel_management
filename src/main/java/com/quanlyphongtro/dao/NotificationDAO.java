package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
}
