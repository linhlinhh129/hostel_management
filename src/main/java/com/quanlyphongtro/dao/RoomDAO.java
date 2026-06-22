package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class RoomDAO extends BaseDAO {

    private Room mapRow(ResultSet rs) throws Exception {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setFacilityId(getInteger(rs, "facility_id"));
        room.setCode(rs.getString("code"));
        room.setArea(rs.getBigDecimal("area"));
        room.setStatus(rs.getString("status"));
        room.setTenantId(getInteger(rs, "tenant_id"));
        room.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        room.setContractStartDate(toLocalDate(rs, "contract_start_date"));
        room.setContractEndDate(toLocalDate(rs, "contract_end_date"));
        room.setCreatedAt(toLocalDateTime(rs, "created_at"));
        room.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        room.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        room.setRoomFee(rs.getBigDecimal("room_fee"));
        return room;
    }

    public Optional<Room> findById(int roomId) {
        String sql = "SELECT * FROM dbo.rooms WHERE room_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("RoomDAO.findById failed for roomId={}", roomId, e);
        }
        return Optional.empty();
    }

    public Optional<Room> findByCode(String code) {
        String sql = "SELECT * FROM dbo.rooms WHERE code = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("RoomDAO.findByCode failed for code={}", code, e);
        }
        return Optional.empty();
    }

    public Optional<Room> findByTenantId(int tenantId) {
        String sql = "SELECT * FROM dbo.rooms WHERE tenant_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("RoomDAO.findByTenantId failed for tenantId={}", tenantId, e);
        }
        return Optional.empty();
    }
}
