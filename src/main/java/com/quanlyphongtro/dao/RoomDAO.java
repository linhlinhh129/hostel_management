package com.quanlyphongtro.dao;
import java.math.BigDecimal;
import java.sql.Types;
import java.sql.Date;

import com.quanlyphongtro.dto.RoomOccupancyStatDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

    /**
     * Chỉ cập nhật diện tích (area) của phòng — dùng cho inline edit trên trang chi
     * tiết cơ sở.
     */
    public boolean updateArea(int roomId, BigDecimal area) {
        String sql = "UPDATE dbo.rooms SET area = ?, updated_at = GETDATE() WHERE room_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            if (area != null) {
                ps.setBigDecimal(1, area);
            } else {
                ps.setNull(1, Types.DECIMAL);
            }
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("RoomDAO.updateArea failed for roomId={}", roomId, e);
        }
        return false;
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
                ps.setNull(3, Types.DECIMAL);
            }
            ps.setString(4, room.getStatus());
            if (room.getTenantId() != null) {
                ps.setInt(5, room.getTenantId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setBigDecimal(6, room.getDepositAmount() != null ? room.getDepositAmount() : BigDecimal.ZERO);
            if (room.getContractStartDate() != null) {
                ps.setDate(7, Date.valueOf(room.getContractStartDate()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            if (room.getContractEndDate() != null) {
                ps.setDate(8, Date.valueOf(room.getContractEndDate()));
            } else {
                ps.setNull(8, Types.DATE);
            }
            ps.setBigDecimal(9, room.getRoomFee());
            ps.setInt(10, room.getId() != null && room.getId() > 0 ? room.getId() : room.getRoomId());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("update failed for roomId={}", room.getId(), e);
        }
        return false;
    }

    /**
     * Tìm chi tiết phòng kèm thông tin facility và tenant — dùng cho AdminRoomServlet.
     * Trả về Map<String, Object> để tiện set attribute sang JSP mà không cần thêm DTO mới.
     */
    public Optional<Map<String, Object>> findDetailForAdmin(int roomId) {
        String sql =
            "SELECT r.room_id, r.facility_id, r.code, r.area, r.status, " +
            "       r.room_fee, r.deposit_amount, r.created_at, r.updated_at, " +
            "       f.code   AS facility_code, " +
            "       f.name   AS facility_name, " +
            "       f.status AS facility_status, " +
            "       u.user_id   AS tenant_id, " +
            "       u.full_name AS tenant_name, " +
            "       u.username  AS tenant_code, " +
            "       u.phone     AS tenant_phone, " +
            "       u.email     AS tenant_email " +
            "FROM   dbo.rooms r " +
            "JOIN   dbo.facilities f ON f.facility_id = r.facility_id " +
            "LEFT JOIN dbo.users u   ON u.user_id     = r.tenant_id " +
            "WHERE  r.room_id = ? AND r.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();

                Map<String, Object> room = new HashMap<>();
                room.put("id",             rs.getInt("room_id"));
                room.put("facilityId",     rs.getInt("facility_id"));
                room.put("facilityCode",   rs.getString("facility_code"));
                room.put("facilityName",   rs.getString("facility_name"));
                room.put("facilityStatus", rs.getString("facility_status"));

                String code = rs.getString("code");
                room.put("code",          code);
                room.put("area",          rs.getDouble("area"));
                room.put("areaRaw",       rs.getObject("area"));
                room.put("status",        rs.getString("status"));
                room.put("roomFee",       rs.getObject("room_fee"));
                room.put("depositAmount", rs.getObject("deposit_amount"));

                Timestamp cAt = rs.getTimestamp("created_at");
                Timestamp uAt = rs.getTimestamp("updated_at");
                room.put("createdAt",     cAt != null ? cAt.toLocalDateTime().toString() : "—");
                room.put("updatedAt",     uAt != null ? uAt.toLocalDateTime().toString() : "—");
                room.put("createdAtAsDate", cAt);
                room.put("updatedAtAsDate", uAt);

                int tenantId = rs.getInt("tenant_id");
                room.put("tenantId",    rs.wasNull() ? null : tenantId);
                room.put("tenantName",  rs.getString("tenant_name"));
                room.put("tenantCode",  rs.getString("tenant_code"));
                room.put("tenantPhone", rs.getString("tenant_phone"));
                room.put("tenantEmail", rs.getString("tenant_email"));

                String floor = "—", roomNum = "—";
                if (code != null && code.length() >= 4) {
                    String last4 = code.substring(code.length() - 4);
                    if (last4.matches("\\d+")) {
                        floor   = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                        roomNum = String.valueOf(Integer.parseInt(last4.substring(2)));
                    }
                }
                room.put("floor",      floor);
                room.put("roomNumber", roomNum);

                return Optional.of(room);
            }
        } catch (Exception e) {
            logger.error("RoomDAO.findDetailForAdmin failed for roomId={}", roomId, e);
        }
        return Optional.empty();
    }

    /**
     * Cập nhật diện tích và giá phòng cùng lúc — dùng cho AdminRoomServlet.
     */
    public boolean updateAreaAndFee(int roomId, BigDecimal area, BigDecimal fee) {
        String sql = "UPDATE dbo.rooms SET area = ?, room_fee = ?, updated_at = GETDATE() " +
                     "WHERE room_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (area != null) ps.setBigDecimal(1, area); else ps.setNull(1, Types.DECIMAL);
            if (fee  != null) ps.setBigDecimal(2, fee);  else ps.setNull(2, Types.DECIMAL);
            ps.setInt(3, roomId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("RoomDAO.updateAreaAndFee failed for roomId={}", roomId, e);
        }
        return false;
    }

    /**
     * Thống kê tình trạng phòng toàn hệ thống cho Admin Dashboard.
     * 
     * @return RoomOccupancyStatDTO chứa tổng phòng, đang thuê, trống, tỷ lệ lấp đầy
     */
    public RoomOccupancyStatDTO getOccupancyStats() {
        String sql = "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN tenant_id IS NOT NULL THEN 1 ELSE 0 END) AS occupied, " +
                "SUM(CASE WHEN tenant_id IS NULL THEN 1 ELSE 0 END) AS available " +
                "FROM dbo.rooms WHERE deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total");
                int occupied = rs.getInt("occupied");
                int available = rs.getInt("available");
                return new RoomOccupancyStatDTO(total, occupied, available);
            }
        } catch (Exception e) {
            logger.error("RoomDAO.getOccupancyStats failed", e);
        }
        return new RoomOccupancyStatDTO(0, 0, 0);
    }

    public List<Map<String, Object>> getFacilitiesByManager(int managerId) {
        List<Map<String, Object>> facilities = new ArrayList<>();
        String sql = "SELECT f.*, " +
                "(SELECT COUNT(*) FROM dbo.rooms r WHERE r.facility_id = f.facility_id AND r.deleted_at IS NULL) AS total_rooms " +
                "FROM dbo.facilities f WHERE f.manager_id = ? AND f.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id", rs.getInt("facility_id"));
                    f.put("code", rs.getString("code"));
                    f.put("name", rs.getString("name"));
                    f.put("address", rs.getString("address"));
                    f.put("floorCount", rs.getInt("floor_count"));
                    f.put("status", rs.getString("status"));
                    f.put("totalRooms", rs.getInt("total_rooms"));
                    facilities.add(f);
                }
            }
        } catch (Exception e) {
            logger.error("getFacilitiesByManager failed for managerId={}", managerId, e);
        }
        return facilities;
    }

    public Integer getDefaultFacilityId(int managerId) {
        String sql = "SELECT TOP 1 facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL ORDER BY code ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("facility_id");
                }
            }
        } catch (Exception e) {
            logger.error("getDefaultFacilityId failed for managerId={}", managerId, e);
        }
        return null;
    }

    public Map<String, Object> verifyFacilityManager(int facilityId, int managerId) {
        String sql = "SELECT * FROM dbo.facilities WHERE facility_id = ? AND manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id", rs.getInt("facility_id"));
                    f.put("code", rs.getString("code"));
                    f.put("name", rs.getString("name"));
                    f.put("address", rs.getString("address"));
                    return f;
                }
            }
        } catch (Exception e) {
            logger.error("verifyFacilityManager failed", e);
        }
        return null;
    }

    public int countFacilityRooms(int facilityId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM dbo.rooms r WHERE r.facility_id = ? AND r.deleted_at IS NULL");
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND r.status = ?");
        }
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, facilityId);
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(2, status.trim());
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("countFacilityRooms failed for facilityId={}", facilityId, e);
        }
        return 0;
    }

    public List<Map<String, Object>> findFacilityRooms(int facilityId, String status, int offset, int limit) {
        List<Map<String, Object>> rooms = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT r.*, u.full_name AS tenant_name FROM dbo.rooms r " +
                "LEFT JOIN dbo.users u ON r.tenant_id = u.user_id " +
                "WHERE r.facility_id = ? AND r.deleted_at IS NULL");
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND r.status = ?");
        }
        sql.append(" ORDER BY r.room_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, facilityId);
            int idx = 2;
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(idx++, status.trim());
            }
            ps.setInt(idx++, offset);
            ps.setInt(idx++, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> room = new HashMap<>();
                    room.put("id", rs.getInt("room_id"));
                    String roomCode = rs.getString("code");
                    room.put("code", roomCode);
                    room.put("area", rs.getDouble("area"));
                    room.put("status", rs.getString("status"));
                    int tenantIdVal = rs.getInt("tenant_id");
                    if (rs.wasNull()) {
                        room.put("tenantId", null);
                    } else {
                        room.put("tenantId", tenantIdVal);
                    }
                    room.put("tenantName", rs.getString("tenant_name"));

                    // Parse floor and room number
                    String floorStr = "—";
                    String numberStr = "—";
                    if (roomCode != null && roomCode.length() >= 4) {
                        String last4 = roomCode.substring(roomCode.length() - 4);
                        if (last4.matches("\\d+")) {
                            floorStr = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                            numberStr = String.valueOf(Integer.parseInt(last4.substring(2)));
                        }
                    }
                    room.put("floor", floorStr);
                    room.put("roomNumber", numberStr);
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            logger.error("findFacilityRooms failed for facilityId={}", facilityId, e);
        }
        return rooms;
    }

    public Map<String, Object> findRoomDetail(int roomId) {
        String sql = "SELECT r.*, f.code AS facility_code, f.name AS facility_name, f.manager_id, u.full_name AS tenant_name, u.username AS tenant_code, u.phone AS tenant_phone " +
                "FROM dbo.rooms r " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "LEFT JOIN dbo.users u ON r.tenant_id = u.user_id " +
                "WHERE r.room_id = ? AND r.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> room = new HashMap<>();
                    room.put("id", rs.getInt("room_id"));
                    room.put("facilityId", rs.getInt("facility_id"));
                    room.put("facilityCode", rs.getString("facility_code"));
                    room.put("facilityName", rs.getString("facility_name"));
                    room.put("managerId", rs.getInt("manager_id"));
                    String roomCode = rs.getString("code");
                    room.put("code", roomCode);
                    room.put("area", rs.getDouble("area"));
                    room.put("status", rs.getString("status"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    Timestamp uAt = rs.getTimestamp("updated_at");
                    room.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString() : "—");
                    room.put("updatedAt", uAt != null ? uAt.toLocalDateTime().toString() : "—");
                    room.put("createdAtAsDate", cAt);
                    room.put("updatedAtAsDate", uAt);
                    
                    int tenantIdVal = rs.getInt("tenant_id");
                    if (rs.wasNull()) {
                        room.put("tenantId", null);
                    } else {
                        room.put("tenantId", tenantIdVal);
                    }
                    room.put("tenantName", rs.getString("tenant_name"));
                    room.put("tenantCode", rs.getString("tenant_code"));
                    room.put("tenantPhone", rs.getString("tenant_phone"));

                    // Parse floor and roomNumber
                    String floorStr = "—";
                    String numberStr = "—";
                    if (roomCode != null && roomCode.length() >= 4) {
                        String last4 = roomCode.substring(roomCode.length() - 4);
                        if (last4.matches("\\d+")) {
                            floorStr = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                            numberStr = String.valueOf(Integer.parseInt(last4.substring(2)));
                        }
                    }
                    room.put("floor", floorStr);
                    room.put("roomNumber", numberStr);

                    BigDecimal roomFee = rs.getBigDecimal("room_fee");
                    room.put("roomFee", roomFee);
                    return room;
                }
            }
        } catch (Exception e) {
            logger.error("findRoomDetail failed for roomId={}", roomId, e);
        }
        return null;
    }

    public Integer findActiveContractId(int roomId) {
        String sql = "SELECT TOP 1 contract_id FROM dbo.contracts WHERE room_id = ? AND status = 'ACTIVE' AND deleted_at IS NULL ORDER BY contract_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("contract_id");
                }
            }
        } catch (Exception e) {
            logger.error("findActiveContractId failed for roomId={}", roomId, e);
        }
        return null;
    }
}
