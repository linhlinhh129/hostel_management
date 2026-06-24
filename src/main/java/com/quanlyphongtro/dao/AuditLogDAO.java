package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuditLogDAO extends BaseDAO {

    /**
     * Ghi audit log vào bảng audit_logs.
     *
     * @param entityType loại entity, ví dụ "INVOICE"
     * @param entityId   id của entity
     * @param action     hành động, ví dụ "CREATE" / "UPDATE"
     * @param oldValue   giá trị cũ (JSON hoặc string mô tả)
     * @param newValue   giá trị mới
     * @param ipAddress  địa chỉ IP của request
     * @param createdBy  user_id thực hiện thao tác
     */
    public void log(String entityType, int entityId, String action,
                    String oldValue, String newValue,
                    String ipAddress, Integer createdBy) {
        String sql = """
                INSERT INTO dbo.audit_logs
                    (entity_type, entity_id, action, old_value, new_value,
                     ip_address, created_by, created_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entityType);
            ps.setInt(2, entityId);
            ps.setString(3, action);
            ps.setString(4, oldValue);
            ps.setString(5, newValue);
            ps.setString(6, ipAddress);
            if (createdBy != null) {
                ps.setInt(7, createdBy);
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("AuditLogDAO.log failed for entityType={}, entityId={}, action={}",
                    entityType, entityId, action, e);
        }
    }

    public void logWithComment(String entityType, int entityId, String action,
                               String oldValue, String newValue,
                               String ipAddress, Integer createdBy, String comment) {
        String sql = """
                INSERT INTO dbo.audit_logs
                    (entity_type, entity_id, action, old_value, new_value,
                     ip_address, created_by, created_at, comment)
                VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), ?)
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entityType);
            ps.setInt(2, entityId);
            ps.setString(3, action);
            ps.setString(4, oldValue);
            ps.setString(5, newValue);
            ps.setString(6, ipAddress);
            if (createdBy != null) {
                ps.setInt(7, createdBy);
            } else {
                ps.setNull(7, Types.INTEGER);
            }
            ps.setString(8, comment);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("AuditLogDAO.logWithComment failed for entityType={}, entityId={}, action={}",
                    entityType, entityId, action, e);
        }
    }

    public java.util.List<com.quanlyphongtro.dto.ServicePriceHistoryDTO> getPriceHistories(int facilityId, String priceType, int page, int size) {
        java.util.List<com.quanlyphongtro.dto.ServicePriceHistoryDTO> result = new java.util.ArrayList<>();
        String actionType = "UPDATE_" + priceType;
        String sql = """
                SELECT a.audit_log_id, a.old_value, a.new_value, a.comment, a.created_at, a.created_by, u.full_name, f.code as facility_code
                FROM dbo.audit_logs a
                LEFT JOIN dbo.users u ON a.created_by = u.user_id
                JOIN dbo.facilities f ON a.entity_id = f.facility_id
                WHERE a.entity_type = 'SERVICE_PRICE'
                  AND a.entity_id = ?
                  AND a.action = ?
                ORDER BY a.created_at DESC
                OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setString(2, actionType);
            ps.setInt(3, (page - 1) * size);
            ps.setInt(4, size);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.quanlyphongtro.dto.ServicePriceHistoryDTO dto = new com.quanlyphongtro.dto.ServicePriceHistoryDTO();
                    dto.setHistoryId(rs.getInt("audit_log_id"));
                    dto.setFacilityId(facilityId);
                    dto.setFacilityCode(rs.getString("facility_code"));
                    dto.setPriceType(priceType);
                    
                    String oldValStr = rs.getString("old_value");
                    String newValStr = rs.getString("new_value");
                    dto.setOldPrice(oldValStr != null ? new java.math.BigDecimal(oldValStr) : null);
                    dto.setNewPrice(newValStr != null ? new java.math.BigDecimal(newValStr) : null);
                    
                    dto.setNote(rs.getString("comment"));
                    
                    java.sql.Timestamp createdTs = rs.getTimestamp("created_at");
                    if (createdTs != null) {
                        dto.setChangedAt(createdTs.toLocalDateTime().toString());
                    }
                    
                    dto.setChangedBy(rs.getInt("created_by"));
                    if (rs.wasNull()) dto.setChangedBy(null);
                    
                    dto.setChangedByName(rs.getString("full_name"));
                    
                    result.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("AuditLogDAO.getPriceHistories failed for facilityId={}, priceType={}", facilityId, priceType, e);
        }
        return result;
    }

    private AuditLog mapRow(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setId(rs.getInt("audit_log_id"));
        log.setEntityType(rs.getString("entity_type"));
        log.setEntityId(getInteger(rs, "entity_id"));
        log.setAction(rs.getString("action"));
        log.setOldValue(rs.getString("old_value"));
        log.setNewValue(rs.getString("new_value"));
        log.setIpAddress(rs.getString("ip_address"));
        log.setComment(rs.getString("comment"));
        log.setCreatedBy(getInteger(rs, "created_by"));
        log.setCreatedAt(toLocalDateTime(rs, "created_at"));
        try {
            log.setCreatedByName(rs.getString("created_by_name"));
        } catch (SQLException ignored) {
            // column not present
        }
        return log;
    }

    private static final String BASE_SELECT =
        "SELECT al.*, u.full_name AS created_by_name " +
        "FROM dbo.audit_logs al " +
        "LEFT JOIN dbo.users u ON u.user_id = al.created_by";

    public List<AuditLog> findAll(String actor, String entityType, String action,
                                   String dateFrom, String dateTo,
                                   int page, int pageSize) {
        return findAll(actor, null, entityType, action, dateFrom, dateTo, page, pageSize);
    }

    public List<AuditLog> findAll(String actor, String role, String entityType, String action,
                                   String dateFrom, String dateTo,
                                   int page, int pageSize) {
        List<AuditLog> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(BASE_SELECT + " WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (role != null && !role.isBlank()) {
            sql.append(" AND u.role = ?");
            params.add(role.trim());
        }
        if (actor != null && !actor.isBlank()) {
            sql.append(" AND u.full_name LIKE ?");
            params.add("%" + actor.trim() + "%");
        }
        if (entityType != null && !entityType.isBlank()) {
            sql.append(" AND al.entity_type = ?");
            params.add(entityType.trim());
        }
        if (action != null && !action.isBlank()) {
            sql.append(" AND al.action = ?");
            params.add(action.trim());
        }
        if (dateFrom != null && !dateFrom.isBlank()) {
            sql.append(" AND CAST(al.created_at AS DATE) >= ?");
            params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.isBlank()) {
            sql.append(" AND CAST(al.created_at AS DATE) <= ?");
            params.add(dateTo.trim());
        }
        sql.append(" ORDER BY al.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("AuditLogDAO.findAll failed", e);
        }
        // Lookup entity name cho từng log (batch per entity type)
        enrichEntityNames(list);
        return list;
    }

    public int count(String actor, String entityType, String action,
                     String dateFrom, String dateTo) {
        return count(actor, null, entityType, action, dateFrom, dateTo);
    }

    public int count(String actor, String role, String entityType, String action,
                     String dateFrom, String dateTo) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM dbo.audit_logs al " +
            "LEFT JOIN dbo.users u ON u.user_id = al.created_by " +
            "WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (role != null && !role.isBlank()) {
            sql.append(" AND u.role = ?");
            params.add(role.trim());
        }
        if (actor != null && !actor.isBlank()) {
            sql.append(" AND u.full_name LIKE ?");
            params.add("%" + actor.trim() + "%");
        }
        if (entityType != null && !entityType.isBlank()) {
            sql.append(" AND al.entity_type = ?");
            params.add(entityType.trim());
        }
        if (action != null && !action.isBlank()) {
            sql.append(" AND al.action = ?");
            params.add(action.trim());
        }
        if (dateFrom != null && !dateFrom.isBlank()) {
            sql.append(" AND CAST(al.created_at AS DATE) >= ?");
            params.add(dateFrom.trim());
        }
        if (dateTo != null && !dateTo.isBlank()) {
            sql.append(" AND CAST(al.created_at AS DATE) <= ?");
            params.add(dateTo.trim());
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("AuditLogDAO.count failed", e);
        }
        return 0;
    }

    public Optional<AuditLog> findById(int id) {
        String sql = BASE_SELECT + " WHERE al.audit_log_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AuditLog log = mapRow(rs);
                    // Lookup tên entity để hiển thị bên cạnh ID
                    log.setEntityName(lookupEntityName(log.getEntityType(), log.getEntityId()));
                    return Optional.of(log);
                }
            }
        } catch (Exception e) {
            logger.error("AuditLogDAO.findById failed for id={}", id, e);
        }
        return Optional.empty();
    }

    /**
     * Batch lookup entityName cho danh sách log.
     * Gom nhóm theo entityType, dùng IN query để tránh N+1.
     */
    private void enrichEntityNames(List<AuditLog> logs) {
        if (logs == null || logs.isEmpty()) return;

        // Nhóm id theo từng entityType
        java.util.Map<String, java.util.List<Integer>> groups = new java.util.LinkedHashMap<>();
        for (AuditLog log : logs) {
            if (log.getEntityType() == null || log.getEntityId() == null) continue;
            groups.computeIfAbsent(log.getEntityType(), k -> new java.util.ArrayList<>())
                  .add(log.getEntityId());
        }

        // Query từng nhóm
        java.util.Map<String, java.util.Map<Integer, String>> nameCache = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, java.util.List<Integer>> entry : groups.entrySet()) {
            String type = entry.getKey();
            java.util.List<Integer> ids = entry.getValue();
            String nameCol, idCol, table;
            switch (type) {
                case "users":         nameCol = "full_name"; idCol = "user_id";         table = "dbo.users";         break;
                case "facilities":    nameCol = "name";      idCol = "facility_id";     table = "dbo.facilities";    break;
                case "rooms":         nameCol = "code";      idCol = "room_id";         table = "dbo.rooms";         break;
                case "notifications": nameCol = "title";     idCol = "notification_id"; table = "dbo.notifications"; break;
                case "invoices":      nameCol = "code";      idCol = "invoice_id";      table = "dbo.invoices";      break;
                case "payments":      nameCol = "code";      idCol = "payment_id";      table = "dbo.payments";      break;
                default: continue;
            }
            // Deduplicate
            java.util.Set<Integer> uniqueIds = new java.util.LinkedHashSet<>(ids);
            StringBuilder placeholders = new StringBuilder();
            for (int i = 0; i < uniqueIds.size(); i++) {
                if (i > 0) placeholders.append(',');
                placeholders.append('?');
            }
            String q = "SELECT " + idCol + ", " + nameCol + " FROM " + table
                     + " WHERE " + idCol + " IN (" + placeholders + ")";
            java.util.Map<Integer, String> map = new java.util.HashMap<>();
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(q)) {
                int idx = 1;
                for (Integer id : uniqueIds) ps.setInt(idx++, id);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) map.put(rs.getInt(1), rs.getString(2));
                }
            } catch (Exception e) {
                logger.warn("enrichEntityNames failed for type={}", type, e);
            }
            nameCache.put(type, map);
        }

        // Gán vào từng log
        for (AuditLog log : logs) {
            java.util.Map<Integer, String> m = nameCache.get(log.getEntityType());
            if (m != null && log.getEntityId() != null) {
                log.setEntityName(m.get(log.getEntityId()));
            }
        }
    }

    /**
     * Tra cứu tên hiển thị của entity dựa vào entityType và entityId.
     * Trả về null nếu không tìm thấy hoặc entityType không được hỗ trợ.
     */
    private String lookupEntityName(String entityType, Integer entityId) {
        if (entityType == null || entityId == null) return null;
        String sql;
        switch (entityType) {
            case "users":
                sql = "SELECT full_name FROM dbo.users WHERE user_id = ?";
                break;
            case "facilities":
                sql = "SELECT name FROM dbo.facilities WHERE facility_id = ?";
                break;
            case "rooms":
                sql = "SELECT code FROM dbo.rooms WHERE room_id = ?";
                break;
            case "notifications":
                sql = "SELECT title FROM dbo.notifications WHERE notification_id = ?";
                break;
            case "invoices":
                sql = "SELECT code FROM dbo.invoices WHERE invoice_id = ?";
                break;
            case "payments":
                sql = "SELECT code FROM dbo.payments WHERE payment_id = ?";
                break;
            default:
                return null;
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, entityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString(1);
            }
        } catch (Exception e) {
            logger.warn("AuditLogDAO.lookupEntityName failed for type={} id={}", entityType, entityId, e);
        }
        return null;
    }

    public void insert(AuditLog log) {
        String sql = "INSERT INTO dbo.audit_logs " +
            "(entity_type, entity_id, action, old_value, new_value, ip_address, comment, " +
            " created_by, created_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, log.getEntityType());
            if (log.getEntityId() != null) ps.setInt(2, log.getEntityId());
            else ps.setNull(2, Types.INTEGER);
            ps.setString(3, log.getAction());
            ps.setString(4, log.getOldValue());
            ps.setString(5, log.getNewValue());
            ps.setString(6, log.getIpAddress());
            ps.setString(7, log.getComment());
            if (log.getCreatedBy() != null) ps.setInt(8, log.getCreatedBy());
            else ps.setNull(8, Types.INTEGER);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("AuditLogDAO.insert failed", e);
        }
    }

    public int countToday() {
        String sql = "SELECT COUNT(*) FROM dbo.audit_logs WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            logger.error("AuditLogDAO.countToday failed", e);
        }
        return 0;
    }

    public List<AuditLog> findRecent(int limit) {
        List<AuditLog> list = new ArrayList<>();
        String sql = BASE_SELECT +
            " ORDER BY al.created_at DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("AuditLogDAO.findRecent failed", e);
        }
        return list;
    }
}
