package com.quanlyphongtro.dao;

<<<<<<< HEAD
import com.quanlyphongtro.model.Facility;
=======
>>>>>>> feature/invoiceManagement-buidinh
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
<<<<<<< HEAD
import java.sql.SQLException;
=======
>>>>>>> feature/invoiceManagement-buidinh
import java.util.Optional;

public class RoomDAO extends BaseDAO {

<<<<<<< HEAD
    private Room mapRoom(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setId(rs.getInt("room_id"));
        room.setFacilityId(rs.getInt("facility_id"));
=======
    private Room mapRow(ResultSet rs) throws Exception {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setFacilityId(getInteger(rs, "facility_id"));
>>>>>>> feature/invoiceManagement-buidinh
        room.setCode(rs.getString("code"));
        room.setArea(rs.getBigDecimal("area"));
        room.setStatus(rs.getString("status"));
        room.setTenantId(getInteger(rs, "tenant_id"));
        room.setDepositAmount(rs.getBigDecimal("deposit_amount"));
        room.setContractStartDate(toLocalDate(rs, "contract_start_date"));
        room.setContractEndDate(toLocalDate(rs, "contract_end_date"));
<<<<<<< HEAD
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
=======
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
>>>>>>> feature/invoiceManagement-buidinh
    }

    public Optional<Room> findByTenantId(int tenantId) {
        String sql = "SELECT * FROM dbo.rooms WHERE tenant_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
<<<<<<< HEAD
                    return Optional.of(mapRoom(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByTenantId failed for tenantId={}", tenantId, e);
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
=======
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("RoomDAO.findByTenantId failed for tenantId={}", tenantId, e);
>>>>>>> feature/invoiceManagement-buidinh
        }
        return Optional.empty();
    }
}
