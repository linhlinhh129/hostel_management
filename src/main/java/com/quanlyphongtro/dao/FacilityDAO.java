package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FacilityDAO extends BaseDAO {

    private Facility mapRow(ResultSet rs) throws SQLException {
        Facility f = new Facility();
        f.setId(rs.getInt("facility_id"));
        f.setCode(rs.getString("code"));
        f.setName(rs.getString("name"));
        f.setAddress(rs.getString("address"));
        f.setFloorCount(getIntOrZero(rs, "floor_count"));
        f.setRoomsPerFloor(getIntOrZero(rs, "rooms_per_floor"));
        f.setStatus(rs.getString("status"));
        f.setManagerId(getInteger(rs, "manager_id"));
        f.setOperatorId(getInteger(rs, "operator_id"));
        f.setElectricityPrice(rs.getBigDecimal("electricity_price"));
        f.setWaterPrice(rs.getBigDecimal("water_price"));
        f.setInternetFee(rs.getBigDecimal("internet_fee"));
        f.setServiceFee(rs.getBigDecimal("service_fee"));
        f.setCreatedAt(toLocalDateTime(rs, "created_at"));
        f.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        f.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        try { f.setManagerName(rs.getString("manager_name")); } catch (SQLException ignored) {}
        try { f.setOperatorName(rs.getString("operator_name")); } catch (SQLException ignored) {}
        return f;
    }

    private String buildBaseSelect() {
        return "SELECT f.*, " +
               "       mgr.full_name AS manager_name, " +
               "       opr.full_name AS operator_name " +
               "FROM dbo.facilities f " +
               "LEFT JOIN dbo.users mgr ON mgr.user_id = f.manager_id " +
               "LEFT JOIN dbo.users opr ON opr.user_id = f.operator_id " +
               "WHERE f.deleted_at IS NULL";
    }

    public List<Facility> findAll(String keyword, String status, int page, int pageSize) {
        List<Facility> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(buildBaseSelect());
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (f.code LIKE ? OR f.name LIKE ? OR f.address LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND f.status = ?");
            params.add(status.trim());
        }
        sql.append(" ORDER BY f.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findAll failed", e);
        }
        return list;
    }

    public int count(String keyword, String status) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(*) FROM dbo.facilities f WHERE f.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (f.code LIKE ? OR f.name LIKE ? OR f.address LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND f.status = ?");
            params.add(status.trim());
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.count failed", e);
        }
        return 0;
    }

    public Optional<Facility> findById(int id) {
        String sql = buildBaseSelect() + " AND f.facility_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findById failed for id={}", id, e);
        }
        return Optional.empty();
    }
    
    public Optional<Facility> findByCode(String code) {
        String sql = buildBaseSelect() + " AND f.code = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
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
        String sql = buildBaseSelect() + " AND f.manager_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findByManagerId failed for managerId={}", managerId, e);
        }
        return Optional.empty();
    }

    /**
     * Lấy cơ sở mà một operator (user_id) đang vận hành.
     */
    public Optional<Facility> findByOperatorId(int operatorId) {
        String sql = buildBaseSelect() + " AND f.operator_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findByOperatorId failed for operatorId={}", operatorId, e);
        }
        return Optional.empty();
    }

    public List<Facility> findActiveList() {
        List<Facility> list = new ArrayList<>();
        String sql = buildBaseSelect() + " AND f.status = 'ACTIVE' ORDER BY f.name";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) {
            logger.error("FacilityDAO.findActiveList failed", e);
        }
        return list;
    }

    public int insert(Facility f) {
        String sql = "INSERT INTO dbo.facilities " +
            "(code, name, address, floor_count, rooms_per_floor, status, manager_id, operator_id, " +
            " electricity_price, water_price, internet_fee, service_fee, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getName());
            ps.setString(3, f.getAddress());
            ps.setInt(4, f.getFloorCount() == null ? 0 : f.getFloorCount());
            ps.setInt(5, f.getRoomsPerFloor() == null ? 0 : f.getRoomsPerFloor());
            ps.setString(6, f.getStatus() != null ? f.getStatus() : "DRAFT");
            if (f.getManagerId() != null) ps.setInt(7, f.getManagerId()); else ps.setNull(7, Types.INTEGER);
            if (f.getOperatorId() != null) ps.setInt(8, f.getOperatorId()); else ps.setNull(8, Types.INTEGER);
            ps.setBigDecimal(9,  f.getElectricityPrice());
            ps.setBigDecimal(10, f.getWaterPrice());
            ps.setBigDecimal(11, f.getInternetFee());
            ps.setBigDecimal(12, f.getServiceFee());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) { logger.error("FacilityDAO.insert failed", e); }
        return -1;
    }

    public boolean update(Facility f) {
        String sql = "UPDATE dbo.facilities SET " +
            "code = ?, name = ?, address = ?, floor_count = ?, rooms_per_floor = ?, " +
            "status = ?, manager_id = ?, operator_id = ?, " +
            "electricity_price = ?, water_price = ?, internet_fee = ?, " +
            "service_fee = ?, updated_at = GETDATE() " +
            "WHERE facility_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, f.getCode());
            ps.setString(2, f.getName());
            ps.setString(3, f.getAddress());
            ps.setInt(4, f.getFloorCount() == null ? 0 : f.getFloorCount());
            ps.setInt(5, f.getRoomsPerFloor() == null ? 0 : f.getRoomsPerFloor());
            ps.setString(6, f.getStatus());
            if (f.getManagerId() != null) ps.setInt(7, f.getManagerId()); else ps.setNull(7, Types.INTEGER);
            if (f.getOperatorId() != null) ps.setInt(8, f.getOperatorId()); else ps.setNull(8, Types.INTEGER);
            ps.setBigDecimal(9,  f.getElectricityPrice());
            ps.setBigDecimal(10, f.getWaterPrice());
            ps.setBigDecimal(11, f.getInternetFee());
            ps.setBigDecimal(12, f.getServiceFee());
            ps.setInt(13, f.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) { logger.error("FacilityDAO.update failed for id={}", f.getId(), e); }
        return false;
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE dbo.facilities SET status = ?, updated_at = GETDATE() WHERE facility_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("FacilityDAO.updateStatus failed for id={}", id, e);
        }
    }

    /**
     * Update status using a provided connection (for use within an existing transaction).
     */
    public void updateStatus(int id, String status, Connection conn) throws SQLException {
        String sql = "UPDATE dbo.facilities SET status = ?, updated_at = GETDATE() WHERE facility_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    /**
     * Generates rooms for the facility in the same transaction.
     * Room code format: facilityCode + floor(2-digit) + room(2-digit)
     */
    public void generateRooms(int facilityId, String facilityCode, int floors, int roomsPerFloor,
                               Connection conn) throws SQLException {
        String sql = "INSERT INTO dbo.rooms (facility_id, code, status, room_fee, deposit_amount) " +
                     "VALUES (?, ?, 'AVAILABLE', 0, 0)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            for (int floor = 1; floor <= floors; floor++) {
                for (int room = 1; room <= roomsPerFloor; room++) {
                    String roomCode = facilityCode
                        + String.format("%02d", floor)
                        + String.format("%02d", room);
                    ps.setInt(1, facilityId);
                    ps.setString(2, roomCode);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
        }
    }

    public int countByCode(String code, Integer excludeId) {
        String sql = excludeId != null
            ? "SELECT COUNT(*) FROM dbo.facilities WHERE UPPER(code) = UPPER(?) AND facility_id <> ? AND deleted_at IS NULL"
            : "SELECT COUNT(*) FROM dbo.facilities WHERE UPPER(code) = UPPER(?) AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.countByCode failed", e);
        }
        return 0;
    }

    public boolean existsActiveManager(int managerId, Integer excludeFacilityId) {
        String sql = excludeFacilityId != null
            ? "SELECT COUNT(*) FROM dbo.facilities WHERE manager_id = ? AND facility_id <> ? AND deleted_at IS NULL AND status <> 'DRAFT'"
            : "SELECT COUNT(*) FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL AND status <> 'DRAFT'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            if (excludeFacilityId != null) ps.setInt(2, excludeFacilityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.existsActiveManager failed", e);
        }
        return false;
    }

    /**
     * Lấy danh sách phòng đã sinh theo cơ sở, sắp xếp theo code.
     */
    public List<Room> findRoomsByFacilityId(int facilityId) {
        List<Room> list = new ArrayList<>();
        String sql =
            "SELECT r.room_id, r.facility_id, r.code, r.area, r.status, r.tenant_id, " +
            "       r.deposit_amount, r.contract_start_date, r.contract_end_date, " +
            "       r.created_at, r.updated_at, r.deleted_at, r.room_fee, " +
            "       u.full_name AS tenant_name " +
            "FROM dbo.rooms r " +
            "LEFT JOIN dbo.users u ON u.user_id = r.tenant_id " +
            "WHERE r.facility_id = ? AND r.deleted_at IS NULL " +
            "ORDER BY r.code";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Room r = new Room();
                    r.setId(rs.getInt("room_id"));
                    r.setFacilityId(rs.getInt("facility_id"));
                    r.setCode(rs.getString("code"));
                    r.setArea(rs.getBigDecimal("area"));
                    r.setStatus(rs.getString("status"));
                    r.setTenantId(getInteger(rs, "tenant_id"));
                    r.setDepositAmount(rs.getBigDecimal("deposit_amount"));
                    r.setContractStartDate(toLocalDate(rs, "contract_start_date"));
                    r.setContractEndDate(toLocalDate(rs, "contract_end_date"));
                    r.setCreatedAt(toLocalDateTime(rs, "created_at"));
                    r.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
                    r.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
                    r.setRoomFee(rs.getBigDecimal("room_fee"));
                    r.setTenantName(rs.getString("tenant_name"));
                    list.add(r);
                }
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.findRoomsByFacilityId failed for facilityId={}", facilityId, e);
        }
        return list;
    }

    /**
     * Đếm số phòng đang có người thuê (tenant_id IS NOT NULL) trong một cơ sở.
     */
    public int countOccupiedRooms(int facilityId) {
        String sql = "SELECT COUNT(*) FROM dbo.rooms " +
                     "WHERE facility_id = ? AND tenant_id IS NOT NULL AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("FacilityDAO.countOccupiedRooms failed for facilityId={}", facilityId, e);
        }
        return 0;
    }

    /**
     * Chuyển toàn bộ phòng của cơ sở sang trạng thái INACTIVE.
     */
    public void deactivateAllRooms(int facilityId) {
        String sql = "UPDATE dbo.rooms SET status = 'INACTIVE', updated_at = GETDATE() " +
                     "WHERE facility_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("FacilityDAO.deactivateAllRooms failed for facilityId={}", facilityId, e);
        }
    }
}
