package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        return n;
    }

    public List<Notification> findForTenant(int roomId, int facilityId) {
        List<Notification> list = new ArrayList<>();
        String sql = "SELECT * FROM dbo.notifications " +
                     "WHERE status = 'SENT' AND deleted_at IS NULL " +
                     "AND (target_type = 'ALL' OR (target_type = 'FACILITY' AND facility_id = ?) OR (target_type = 'ROOM' AND room_id = ?)) " +
                     "ORDER BY sent_at DESC, created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findForTenant failed for roomId={}, facilityId={}", roomId, facilityId, e);
        }
        return list;
    }

    public Optional<Notification> findByIdForTenant(int id, int roomId, int facilityId) {
        String sql = "SELECT * FROM dbo.notifications " +
                     "WHERE notification_id = ? AND status = 'SENT' AND deleted_at IS NULL " +
                     "AND (target_type = 'ALL' OR (target_type = 'FACILITY' AND facility_id = ?) OR (target_type = 'ROOM' AND room_id = ?))";
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
        // Simple unread count: count notifications sent after the user's last login/read time.
        // For a more robust unread tracking, we'd need a user_notification tracking table.
        // For now, if lastReadTime is null, return all as unread, else count newer ones.
        if (lastReadTime == null) return findForTenant(roomId, facilityId).size();

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
}
