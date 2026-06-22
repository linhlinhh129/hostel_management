package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class FacilityDAO extends BaseDAO {

    private Facility mapRow(ResultSet rs) throws Exception {
        Facility f = new Facility();
        f.setFacilityId(rs.getInt("facility_id"));
        f.setCode(rs.getString("code"));
        f.setName(rs.getString("name"));
        f.setAddress(rs.getString("address"));
        f.setFloorCount(getInteger(rs, "floor_count"));
        f.setRoomsPerFloor(getInteger(rs, "rooms_per_floor"));
        f.setStatus(rs.getString("status"));
        f.setManagerId(getInteger(rs, "manager_id"));
        f.setElectricityPrice(rs.getBigDecimal("electricity_price"));
        f.setWaterPrice(rs.getBigDecimal("water_price"));
        f.setInternetFee(rs.getBigDecimal("internet_fee"));
        f.setServiceFee(rs.getBigDecimal("service_fee"));
        f.setCreatedAt(toLocalDateTime(rs, "created_at"));
        f.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        f.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        return f;
    }

    public Optional<Facility> findById(int facilityId) {
        String sql = "SELECT * FROM dbo.facilities WHERE facility_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findById failed for facilityId={}", facilityId, e);
        }
        return Optional.empty();
    }

    public Optional<Facility> findByCode(String code) {
        String sql = "SELECT * FROM dbo.facilities WHERE code = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findByCode failed for code={}", code, e);
        }
        return Optional.empty();
    }

    /**
     * Lấy cơ sở mà một manager (user_id) đang phụ trách.
     * Theo schema: facilities.manager_id = user_id (unique index).
     */
    public Optional<Facility> findByManagerId(int managerId) {
        String sql = "SELECT * FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findByManagerId failed for managerId={}", managerId, e);
        }
        return Optional.empty();
    }
    public boolean update(Facility f) {
        String sql = "UPDATE dbo.facilities SET code = ?, name = ?, address = ?, floor_count = ?, rooms_per_floor = ?, status = ?, manager_id = ?, electricity_price = ?, water_price = ?, internet_fee = ?, service_fee = ?, updated_at = GETDATE() WHERE facility_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getName());
            ps.setString(3, f.getAddress());
            ps.setObject(4, f.getFloorCount());
            ps.setObject(5, f.getRoomsPerFloor());
            ps.setString(6, f.getStatus());
            ps.setObject(7, f.getManagerId());
            ps.setBigDecimal(8, f.getElectricityPrice());
            ps.setBigDecimal(9, f.getWaterPrice());
            ps.setBigDecimal(10, f.getInternetFee());
            ps.setBigDecimal(11, f.getServiceFee());
            ps.setInt(12, f.getFacilityId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("FacilityDAO.update failed for facilityId={}", f.getFacilityId(), e);
        }
        return false;
    }
}
