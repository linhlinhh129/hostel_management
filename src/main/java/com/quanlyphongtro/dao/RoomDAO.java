package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RoomDAO extends BaseDAO {

    private Room mapRow(ResultSet rs) throws SQLException {
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
        room.setRoomFee(rs.getBigDecimal("room_fee"));
        room.setCreatedAt(toLocalDateTime(rs, "created_at"));
        room.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        room.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        return room;
    }

    private Facility mapFacility(ResultSet rs) throws SQLException {
        Facility f = new Facility();
        f.setId(rs.getInt("facility_id"));
        f.setCode(rs.getString("f_code"));
        f.setName(rs.getString("f_name"));
        f.setAddress(rs.getString("f_address"));
        f.setFloorCount(rs.getInt("f_floor_count"));
        f.setRoomsPerFloor(rs.getInt("f_rooms_per_floor"));
        f.setStatus(rs.getString("f_status"));
        f.setManagerId(getInteger(rs, "f_manager_id"));
        f.setElectricityPrice(rs.getBigDecimal("f_electricity_price"));
        f.setWaterPrice(rs.getBigDecimal("f_water_price"));
        f.setInternetFee(rs.getBigDecimal("f_internet_fee"));
        f.setServiceFee(rs.getBigDecimal("f_service_fee"));
        return f;

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

    public Optional<Facility> findFacilityByRoomId(int roomId) {
        String sql = "SELECT f.facility_id, f.code as f_code, f.name as f_name, f.address as f_address, " +
                     "f.floor_count as f_floor_count, f.rooms_per_floor as f_rooms_per_floor, " +
                     "f.status as f_status, f.manager_id as f_manager_id, " +
                     "f.electricity_price as f_electricity_price, f.water_price as f_water_price, " +
                     "f.internet_fee as f_internet_fee, f.service_fee as f_service_fee " +
                     "FROM dbo.facilities f " +
                     "JOIN dbo.rooms r ON f.facility_id = r.facility_id " +
                     "WHERE r.room_id = ? AND f.deleted_at IS NULL AND r.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapFacility(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findFacilityByRoomId failed for roomId={}", roomId, e);
        }
        return Optional.empty();
    }

    public boolean update(Room room) {
        String sql = "UPDATE dbo.rooms SET facility_id = ?, code = ?, area = ?, status = ?, tenant_id = ?, " +
                     "deposit_amount = ?, contract_start_date = ?, contract_end_date = ?, room_fee = ?, " +
                     "updated_at = GETDATE() WHERE room_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, room.getFacilityId());
            ps.setString(2, room.getCode());
            if (room.getArea() != null) {
                ps.setBigDecimal(3, room.getArea());
            } else {
                ps.setNull(3, java.sql.Types.DECIMAL);
            }
            ps.setString(4, room.getStatus());
            if (room.getTenantId() != null) {
                ps.setInt(5, room.getTenantId());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setBigDecimal(6, room.getDepositAmount() != null ? room.getDepositAmount() : java.math.BigDecimal.ZERO);
            if (room.getContractStartDate() != null) {
                ps.setDate(7, java.sql.Date.valueOf(room.getContractStartDate()));
            } else {
                ps.setNull(7, java.sql.Types.DATE);
            }
            if (room.getContractEndDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(room.getContractEndDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setBigDecimal(9, room.getRoomFee());
            ps.setInt(10, room.getId() != null && room.getId() > 0 ? room.getId() : room.getRoomId());
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("update failed for roomId={}", room.getId(), e);
        }
        return false;
    }
}
