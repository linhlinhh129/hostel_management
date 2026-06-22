package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MeterReadingDAO {

    /**
     * Lấy danh sách trạng thái chốt sổ điện nước của các phòng đang có người thuê.
     * TUYỆT ĐỐI CHỈ SỬ DỤNG LỆNH SELECT, KHÔNG CHỈNH SỬA DATABASE.
     */
    public List<MeterStatusDTO> getMeterStatusList(int currentMonth, int currentYear, String facility, String roomCode) {
        List<MeterStatusDTO> list = new ArrayList<>();
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("    r.room_id AS roomId, ");
        sqlBuilder.append("    r.code AS roomCode, ");
        sqlBuilder.append("    prev_mr.electric AS previousElectricReading, ");
        sqlBuilder.append("    prev_mr.water AS previousWaterReading, ");
        sqlBuilder.append("    curr_mr.electric AS currentElectricReading, ");
        sqlBuilder.append("    curr_mr.water AS currentWaterReading, ");
        sqlBuilder.append("    curr_mr.updated_at AS updatedAt, ");
        sqlBuilder.append("    CASE ");
        sqlBuilder.append("        WHEN curr_mr.meter_id IS NOT NULL THEN 'DA_CAP_NHAT' ");
        sqlBuilder.append("        ELSE 'CHUA_CAP_NHAT' ");
        sqlBuilder.append("    END AS status ");
        sqlBuilder.append("FROM rooms r ");
        sqlBuilder.append("LEFT JOIN meter_readings curr_mr ");
        sqlBuilder.append("    ON r.room_id = curr_mr.room_id ");
        sqlBuilder.append("    AND MONTH(curr_mr.reading_date) = ? ");
        sqlBuilder.append("    AND YEAR(curr_mr.reading_date) = ? ");
        sqlBuilder.append("    AND curr_mr.deleted_at IS NULL ");
        sqlBuilder.append("OUTER APPLY ( ");
        sqlBuilder.append("    SELECT TOP 1 electric, water ");
        sqlBuilder.append("    FROM meter_readings ");
        sqlBuilder.append("    WHERE room_id = r.room_id ");
        sqlBuilder.append("      AND (YEAR(reading_date) < ? OR (YEAR(reading_date) = ? AND MONTH(reading_date) < ?)) ");
        sqlBuilder.append("      AND deleted_at IS NULL ");
        sqlBuilder.append("    ORDER BY reading_date DESC ");
        sqlBuilder.append(") prev_mr ");
        sqlBuilder.append("WHERE r.deleted_at IS NULL ");
        
        if (roomCode != null && !roomCode.trim().isEmpty()) {
            sqlBuilder.append(" AND r.code LIKE ? ");
        } else if (facility != null && !facility.trim().isEmpty()) {
            if (facility.equals("Cơ sở A - Cầu Giấy")) {
                sqlBuilder.append(" AND r.code LIKE 'HN01%' ");
            } else if (facility.equals("Cơ sở B - Đống Đa")) {
                sqlBuilder.append(" AND r.code LIKE 'HN02%' ");
            } else if (facility.equals("Cơ sở C - Thanh Xuân")) {
                sqlBuilder.append(" AND r.code LIKE 'HN03%' ");
            }
        }
        
        sqlBuilder.append("ORDER BY r.code ASC");

        String sql = sqlBuilder.toString();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, currentMonth);
            ps.setInt(2, currentYear);
            ps.setInt(3, currentYear);
            ps.setInt(4, currentYear);
            ps.setInt(5, currentMonth);

            if (roomCode != null && !roomCode.trim().isEmpty()) {
                ps.setString(6, "%" + roomCode.trim() + "%");
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MeterStatusDTO dto = new MeterStatusDTO();
                    dto.setRoomId(rs.getInt("roomId"));
                    dto.setRoomCode(rs.getString("roomCode"));
                    
                    int prevElec = rs.getInt("previousElectricReading");
                    if (!rs.wasNull()) dto.setPreviousElectricReading(prevElec);
                    
                    int prevWater = rs.getInt("previousWaterReading");
                    if (!rs.wasNull()) dto.setPreviousWaterReading(prevWater);
                    
                    int currElec = rs.getInt("currentElectricReading");
                    if (!rs.wasNull()) dto.setCurrentElectricReading(currElec);
                    
                    int currWater = rs.getInt("currentWaterReading");
                    if (!rs.wasNull()) dto.setCurrentWaterReading(currWater);
                    
                    dto.setUpdatedAt(rs.getTimestamp("updatedAt"));
                    dto.setStatus(rs.getString("status"));
                    
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public boolean insertMeterReading(int roomId, int electric, int water, String electricImg, String waterImg, int createdBy) {
        String sql = "INSERT INTO meter_readings (room_id, electric, water, reading_date, status, created_by, electric_img, water_img) " +
                     "VALUES (?, ?, ?, GETDATE(), 'UPDATED', ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, electric);
            ps.setInt(3, water);
            if (createdBy > 0) {
                ps.setInt(4, createdBy);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, electricImg);
            ps.setString(6, waterImg);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MeterStatusDTO getPreviousReadingByRoomCode(String roomCode) {
        String sql = "SELECT TOP 1 r.room_id AS roomId, r.code AS roomCode, mr.electric, mr.water " +
                     "FROM rooms r " +
                     "LEFT JOIN meter_readings mr ON r.room_id = mr.room_id AND mr.deleted_at IS NULL " +
                     "WHERE r.code = ? AND r.deleted_at IS NULL " +
                     "ORDER BY mr.reading_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MeterStatusDTO dto = new MeterStatusDTO();
                    dto.setRoomId(rs.getInt("roomId"));
                    dto.setRoomCode(rs.getString("roomCode"));
                    int elec = rs.getInt("electric");
                    if (!rs.wasNull()) dto.setPreviousElectricReading(elec);
                    int water = rs.getInt("water");
                    if (!rs.wasNull()) dto.setPreviousWaterReading(water);
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
