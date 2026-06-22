package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;

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
}
