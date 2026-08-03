package com.quanlyphongtro.dao;
import java.sql.Types;
import java.time.LocalDate;
import java.sql.Date;

import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.model.MeterReading;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MeterReadingDAO extends BaseDAO {

    public List<MeterStatusDTO> getMeterStatusList(int currentMonth, int currentYear, String facility, String roomCode, Integer operatorId) {
        List<MeterStatusDTO> list = new ArrayList<>();
        
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT ");
        sqlBuilder.append("    r.room_id AS roomId, ");
        sqlBuilder.append("    r.code AS roomCode, ");
        sqlBuilder.append("    prev_mr.electric AS previousElectricReading, ");
        sqlBuilder.append("    prev_mr.water AS previousWaterReading, ");
        sqlBuilder.append("    curr_mr.electric AS currentElectricReading, ");
        sqlBuilder.append("    curr_mr.water AS currentWaterReading, ");
        sqlBuilder.append("    curr_mr.electric_usage AS electricUsage, ");
        sqlBuilder.append("    curr_mr.water_usage AS waterUsage, ");
        sqlBuilder.append("    curr_mr.updated_at AS updatedAt, ");
        sqlBuilder.append("    curr_mr.meter_id AS meterId, ");
        sqlBuilder.append("    curr_mr.electric_img AS electricImg, ");
        sqlBuilder.append("    curr_mr.water_img AS waterImg, ");
        sqlBuilder.append("    curr_mr.electric_status AS electricStatus, ");
        sqlBuilder.append("    curr_mr.electric_old_final AS electricOldFinal, ");
        sqlBuilder.append("    curr_mr.electric_new_start AS electricNewStart, ");
        sqlBuilder.append("    curr_mr.water_status AS waterStatus, ");
        sqlBuilder.append("    curr_mr.water_old_final AS waterOldFinal, ");
        sqlBuilder.append("    curr_mr.water_new_start AS waterNewStart, ");
        sqlBuilder.append("    u.full_name AS updatedByName, ");
        sqlBuilder.append("    CASE WHEN curr_mr.meter_id IS NOT NULL THEN 'DA_CAP_NHAT' ELSE 'CHUA_CAP_NHAT' END AS status, ");
        sqlBuilder.append("    CASE WHEN inv.status = 'PAID' THEN 1 ELSE 0 END AS invoicePaid ");
        sqlBuilder.append("FROM rooms r ");
        sqlBuilder.append("INNER JOIN facilities f ON r.facility_id = f.facility_id ");
        sqlBuilder.append("LEFT JOIN meter_readings curr_mr ");
        sqlBuilder.append("    ON r.room_id = curr_mr.room_id ");
        sqlBuilder.append("    AND MONTH(curr_mr.reading_date) = ? ");
        sqlBuilder.append("    AND YEAR(curr_mr.reading_date) = ? ");
        sqlBuilder.append("    AND curr_mr.deleted_at IS NULL ");
        sqlBuilder.append("LEFT JOIN users u ON curr_mr.created_by = u.user_id ");
        sqlBuilder.append("LEFT JOIN invoices inv ON inv.meter_id = curr_mr.meter_id AND inv.deleted_at IS NULL ");
        sqlBuilder.append("OUTER APPLY ( ");
        sqlBuilder.append("    SELECT TOP 1 electric, water ");
        sqlBuilder.append("    FROM meter_readings ");
        sqlBuilder.append("    WHERE room_id = r.room_id ");
        sqlBuilder.append("      AND (YEAR(reading_date) < ? OR (YEAR(reading_date) = ? AND MONTH(reading_date) < ?)) ");
        sqlBuilder.append("      AND deleted_at IS NULL ");
        sqlBuilder.append("    ORDER BY reading_date DESC ");
        sqlBuilder.append(") prev_mr ");
        sqlBuilder.append("WHERE r.deleted_at IS NULL ");
        
        if (operatorId != null) {
            sqlBuilder.append(" AND f.operator_id = ? ");
        }
        if (roomCode != null && !roomCode.trim().isEmpty()) {
            sqlBuilder.append(" AND r.code LIKE ? ");
        }
        if (facility != null && !facility.trim().isEmpty()) {
            sqlBuilder.append(" AND (f.name + ' (' + f.code + ')' = ?) ");
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

            int paramIndex = 6;
            if (operatorId != null) {
                ps.setInt(paramIndex++, operatorId);
            }
            if (roomCode != null && !roomCode.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + roomCode.trim() + "%");
            }
            if (facility != null && !facility.trim().isEmpty()) {
                ps.setString(paramIndex++, facility.trim());
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
                    
                    int elecUsage = rs.getInt("electricUsage");
                    if (!rs.wasNull()) dto.setElectricUsage(elecUsage);
                    
                    int waterUsage = rs.getInt("waterUsage");
                    if (!rs.wasNull()) dto.setWaterUsage(waterUsage);
                    
                    dto.setElectricStatus(rs.getString("electricStatus"));
                    int eOld = rs.getInt("electricOldFinal"); if (!rs.wasNull()) dto.setElectricOldFinal(eOld);
                    int eNew = rs.getInt("electricNewStart"); if (!rs.wasNull()) dto.setElectricNewStart(eNew);
                    
                    dto.setWaterStatus(rs.getString("waterStatus"));
                    int wOld = rs.getInt("waterOldFinal"); if (!rs.wasNull()) dto.setWaterOldFinal(wOld);
                    int wNew = rs.getInt("waterNewStart"); if (!rs.wasNull()) dto.setWaterNewStart(wNew);
                    
                    dto.setUpdatedAt(rs.getTimestamp("updatedAt"));
                    dto.setStatus(rs.getString("status"));
                    
                    int mId = rs.getInt("meterId");
                    if (!rs.wasNull()) dto.setMeterId(mId);
                    
                    dto.setElectricImg(rs.getString("electricImg"));
                    dto.setWaterImg(rs.getString("waterImg"));
                    dto.setUpdatedByName(rs.getString("updatedByName"));
                    dto.setInvoicePaid(rs.getInt("invoicePaid") == 1);
                    
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return list;
    }

    public boolean insertMeterReading(MeterStatusDTO dto, int createdBy) {
        String sql = "INSERT INTO meter_readings (room_id, electric, water, reading_date, status, created_by, electric_img, water_img, " +
                     "electric_usage, water_usage, electric_status, electric_old_final, electric_new_start, electric_max_limit, " +
                     "water_status, water_old_final, water_new_start, water_max_limit) " +
                     "VALUES (?, ?, ?, GETDATE(), 'UPDATED', ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dto.getRoomId());
            ps.setInt(2, dto.getCurrentElectricReading());
            ps.setInt(3, dto.getCurrentWaterReading());
            if (createdBy > 0) {
                ps.setInt(4, createdBy);
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, dto.getElectricImg());
            ps.setString(6, dto.getWaterImg());
            
            ps.setInt(7, dto.getElectricUsage());
            ps.setInt(8, dto.getWaterUsage());
            
            ps.setString(9, dto.getElectricStatus() != null ? dto.getElectricStatus() : "NORMAL");
            if (dto.getElectricOldFinal() != null) ps.setInt(10, dto.getElectricOldFinal()); else ps.setNull(10, Types.INTEGER);
            if (dto.getElectricNewStart() != null) ps.setInt(11, dto.getElectricNewStart()); else ps.setNull(11, Types.INTEGER);
            if (dto.getElectricMaxLimit() != null) ps.setInt(12, dto.getElectricMaxLimit()); else ps.setNull(12, Types.INTEGER);
            
            ps.setString(13, dto.getWaterStatus() != null ? dto.getWaterStatus() : "NORMAL");
            if (dto.getWaterOldFinal() != null) ps.setInt(14, dto.getWaterOldFinal()); else ps.setNull(14, Types.INTEGER);
            if (dto.getWaterNewStart() != null) ps.setInt(15, dto.getWaterNewStart()); else ps.setNull(15, Types.INTEGER);
            if (dto.getWaterMaxLimit() != null) ps.setInt(16, dto.getWaterMaxLimit()); else ps.setNull(16, Types.INTEGER);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MeterReading getReadingByMonth(int roomId, int year, int month) {
        String sql = "SELECT * FROM meter_readings " +
                     "WHERE room_id = ? AND YEAR(reading_date) = ? AND MONTH(reading_date) = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public MeterReading getPreviousReadingByDate(int roomId, LocalDate readingDate) {
        String sql = "SELECT TOP 1 * FROM meter_readings " +
                     "WHERE room_id = ? AND reading_date < ? AND deleted_at IS NULL ORDER BY reading_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(readingDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMeterStatus(int meterId) {
        String sql = "SELECT status FROM meter_readings WHERE meter_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, meterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MeterStatusDTO getPreviousReadingByRoomCode(String roomCode) {
        String sql = "SELECT TOP 1 r.room_id AS roomId, r.code AS roomCode, mr.electric, mr.water, mr.electric_img, mr.water_img " +
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
                    dto.setPreviousElectricImg(rs.getString("electric_img"));
                    dto.setPreviousWaterImg(rs.getString("water_img"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MeterStatusDTO getReadingForEdit(int meterId) {
        String sql = "SELECT " +
                     "    m.meter_id AS meterId, " +
                     "    m.room_id AS roomId, " +
                     "    r.code AS roomCode, " +
                     "    m.electric AS currentElectricReading, " +
                     "    m.water AS currentWaterReading, " +
                     "    m.electric_img AS electricImg, " +
                     "    m.water_img AS waterImg, " +
                     "    m.electric_status AS electricStatus, " +
                     "    m.electric_old_final AS electricOldFinal, " +
                     "    m.electric_new_start AS electricNewStart, " +
                     "    m.electric_max_limit AS electricMaxLimit, " +
                     "    m.water_status AS waterStatus, " +
                     "    m.water_old_final AS waterOldFinal, " +
                     "    m.water_new_start AS waterNewStart, " +
                     "    m.water_max_limit AS waterMaxLimit, " +
                     "    (SELECT TOP 1 electric FROM meter_readings prev " +
                     "     WHERE prev.room_id = m.room_id AND prev.reading_date < m.reading_date AND prev.deleted_at IS NULL " +
                     "     ORDER BY prev.reading_date DESC) as previousElectricReading, " +
                     "    (SELECT TOP 1 water FROM meter_readings prev " +
                     "     WHERE prev.room_id = m.room_id AND prev.reading_date < m.reading_date AND prev.deleted_at IS NULL " +
                     "     ORDER BY prev.reading_date DESC) as previousWaterReading, " +
                     "    (SELECT TOP 1 status FROM invoices i WHERE i.meter_id = m.meter_id AND i.deleted_at IS NULL) as invoice_status " +
                     "FROM meter_readings m " +
                     "JOIN rooms r ON m.room_id = r.room_id " +
                     "WHERE m.meter_id = ? AND m.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, meterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MeterStatusDTO dto = new MeterStatusDTO();
                    dto.setMeterId(rs.getInt("meterId"));
                    dto.setRoomId(rs.getInt("roomId"));
                    dto.setRoomCode(rs.getString("roomCode"));
                    dto.setCurrentElectricReading(rs.getInt("currentElectricReading"));
                    dto.setCurrentWaterReading(rs.getInt("currentWaterReading"));
                    dto.setElectricImg(rs.getString("electricImg"));
                    dto.setWaterImg(rs.getString("waterImg"));
                    
                    dto.setElectricStatus(rs.getString("electricStatus"));
                    int elecOldF = rs.getInt("electricOldFinal"); if (!rs.wasNull()) dto.setElectricOldFinal(elecOldF);
                    int elecNewS = rs.getInt("electricNewStart"); if (!rs.wasNull()) dto.setElectricNewStart(elecNewS);
                    int elecMaxL = rs.getInt("electricMaxLimit"); if (!rs.wasNull()) dto.setElectricMaxLimit(elecMaxL);

                    dto.setWaterStatus(rs.getString("waterStatus"));
                    int waterOldF = rs.getInt("waterOldFinal"); if (!rs.wasNull()) dto.setWaterOldFinal(waterOldF);
                    int waterNewS = rs.getInt("waterNewStart"); if (!rs.wasNull()) dto.setWaterNewStart(waterNewS);
                    int waterMaxL = rs.getInt("waterMaxLimit"); if (!rs.wasNull()) dto.setWaterMaxLimit(waterMaxL);
                    
                    int prevElec = rs.getInt("previousElectricReading");
                    if (!rs.wasNull()) dto.setPreviousElectricReading(prevElec);
                    
                    int prevWater = rs.getInt("previousWaterReading");
                    if (!rs.wasNull()) dto.setPreviousWaterReading(prevWater);
                    
                    String invoiceStatus = rs.getString("invoice_status");
                    dto.setInvoicePaid("PAID".equals(invoiceStatus));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private MeterReading mapRow(ResultSet rs) throws Exception {
        MeterReading m = new MeterReading();
        m.setMeterId(rs.getInt("meter_id"));
        m.setRoomId(getInteger(rs, "room_id"));
        m.setElectric(getInteger(rs, "electric"));
        m.setWater(getInteger(rs, "water"));
        m.setReadingDate(toLocalDate(rs, "reading_date"));
        m.setStatus(rs.getString("status"));
        m.setCreatedBy(getInteger(rs, "created_by"));
        m.setCreatedAt(toLocalDateTime(rs, "created_at"));
        m.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        m.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        m.setWaterImg(rs.getString("water_img"));
        m.setElectricImg(rs.getString("electric_img"));
        m.setElectricUsage(getInteger(rs, "electric_usage"));
        m.setWaterUsage(getInteger(rs, "water_usage"));
        
        if (hasColumn(rs, "electric_status")) m.setElectricStatus(rs.getString("electric_status"));
        if (hasColumn(rs, "electric_old_final")) m.setElectricOldFinal(getInteger(rs, "electric_old_final"));
        if (hasColumn(rs, "electric_new_start")) m.setElectricNewStart(getInteger(rs, "electric_new_start"));
        
        if (hasColumn(rs, "water_status")) m.setWaterStatus(rs.getString("water_status"));
        if (hasColumn(rs, "water_old_final")) m.setWaterOldFinal(getInteger(rs, "water_old_final"));
        if (hasColumn(rs, "water_new_start")) m.setWaterNewStart(getInteger(rs, "water_new_start"));
        
        return m;
    }

    public List<MeterReading> findLatestTwoByRoomId(int roomId) {
        String sql = "SELECT TOP 2 * FROM dbo.meter_readings WHERE room_id = ? AND deleted_at IS NULL ORDER BY reading_date DESC, meter_id DESC";
        List<MeterReading> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("MeterReadingDAO.findLatestTwoByRoomId failed for roomId={}", roomId, e);
        }
        return list;
    }

    /**
     * Lấy chỉ số điện nước của tháng hiện tại (billing_period dạng YYYYMM)
     * và tháng trước đó để tính tiêu thụ trong kỳ.
     */
    public List<MeterReading> findByRoomAndBillingPeriod(int roomId, String billingPeriod) {
        int year  = Integer.parseInt(billingPeriod.substring(0, 4));
        int month = Integer.parseInt(billingPeriod.substring(4, 6));

        LocalDate periodStart = LocalDate.of(year, month, 1);
        LocalDate periodEnd   = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

        LocalDate prevEnd   = periodStart.minusDays(1);
        LocalDate prevStart = prevEnd.withDayOfMonth(1);

        MeterReading current = findLatestInRange(roomId, periodStart, periodEnd);
        MeterReading prev    = findLatestInRange(roomId, prevStart, prevEnd);

        List<MeterReading> result = new ArrayList<>();
        result.add(current);
        result.add(prev);
        return result;
    }

    public MeterReading findLatestInRange(int roomId, LocalDate from, LocalDate to) {
        String sql = "SELECT TOP 1 * " +
                     "FROM dbo.meter_readings " +
                     "WHERE room_id = ? AND reading_date >= ? AND reading_date <= ? AND deleted_at IS NULL " +
                     "ORDER BY reading_date DESC, meter_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (Exception e) {
            logger.error("MeterReadingDAO.findLatestInRange failed for roomId={}", roomId, e);
        }
        return null;
    }

    public Integer checkCurrentMonthReadingExists(int roomId, int month, int year) {
        String sql = "SELECT TOP 1 meter_id FROM meter_readings " +
                     "WHERE room_id = ? AND MONTH(reading_date) = ? AND YEAR(reading_date) = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("meter_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Kiểm tra xem hóa đơn của phòng trong tháng/năm chỉ định đã được thanh toán chưa.
     * Dùng để ngăn nhân viên vận hành cập nhật lại chỉ số điện nước sau khi hóa đơn đã PAID.
     */
    public boolean isInvoicePaidForMonth(int roomId, int month, int year) {
        String sql = "SELECT TOP 1 i.status FROM invoices i " +
                     "INNER JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                     "WHERE mr.room_id = ? " +
                     "  AND MONTH(mr.reading_date) = ? " +
                     "  AND YEAR(mr.reading_date) = ? " +
                     "  AND mr.deleted_at IS NULL " +
                     "  AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, month);
            ps.setInt(3, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return "PAID".equals(rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMeterReading(MeterStatusDTO dto) {
        String sql = "UPDATE meter_readings SET electric = ?, water = ?, electric_img = ?, water_img = ?, status = 'UPDATED', updated_at = GETDATE(), " +
                     "electric_usage = ?, water_usage = ?, electric_status = ?, electric_old_final = ?, electric_new_start = ?, electric_max_limit = ?, " +
                     "water_status = ?, water_old_final = ?, water_new_start = ?, water_max_limit = ? " +
                     "WHERE meter_id = ?";
        String updateInvoiceSql = 
            "UPDATE i SET " +
            "  total_amount = i.room_fee + " +
            "    (? * i.electricity_price) + " +
            "    (? * i.water_price) + " +
            "    COALESCE(i.service_fee, 0) + COALESCE(i.internet_fee, 0) + COALESCE(i.other_fee, 0), " +
            "  updated_at = GETDATE() " +
            "FROM invoices i " +
            "WHERE i.meter_id = ? AND i.status != 'PAID' AND i.deleted_at IS NULL";

        String updateReqSql =
            "UPDATE req SET status = 'DONE', updated_at = GETDATE() " +
            "FROM dbo.requests req " +
            "JOIN dbo.rooms r ON (req.title LIKE N'%' + RTRIM(r.code) OR req.content LIKE N'%' + RTRIM(r.code)) " +
            "JOIN dbo.meter_readings mr ON mr.room_id = r.room_id " +
            "WHERE mr.meter_id = ? AND req.category IN ('UTILITY', 'WATER', 'ELECTRIC') AND req.status != 'DONE' AND req.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection()) {
            boolean updated = false;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, dto.getCurrentElectricReading());
                ps.setInt(2, dto.getCurrentWaterReading());
                ps.setString(3, dto.getElectricImg());
                ps.setString(4, dto.getWaterImg());
                
                ps.setInt(5, dto.getElectricUsage());
                ps.setInt(6, dto.getWaterUsage());
                
                ps.setString(7, dto.getElectricStatus() != null ? dto.getElectricStatus() : "NORMAL");
                if (dto.getElectricOldFinal() != null) ps.setInt(8, dto.getElectricOldFinal()); else ps.setNull(8, Types.INTEGER);
                if (dto.getElectricNewStart() != null) ps.setInt(9, dto.getElectricNewStart()); else ps.setNull(9, Types.INTEGER);
                if (dto.getElectricMaxLimit() != null) ps.setInt(10, dto.getElectricMaxLimit()); else ps.setNull(10, Types.INTEGER);
                
                ps.setString(11, dto.getWaterStatus() != null ? dto.getWaterStatus() : "NORMAL");
                if (dto.getWaterOldFinal() != null) ps.setInt(12, dto.getWaterOldFinal()); else ps.setNull(12, Types.INTEGER);
                if (dto.getWaterNewStart() != null) ps.setInt(13, dto.getWaterNewStart()); else ps.setNull(13, Types.INTEGER);
                if (dto.getWaterMaxLimit() != null) ps.setInt(14, dto.getWaterMaxLimit()); else ps.setNull(14, Types.INTEGER);
                
                ps.setInt(15, dto.getMeterId());
                updated = ps.executeUpdate() > 0;
            }
            if (updated) {
                try (PreparedStatement psInv = conn.prepareStatement(updateInvoiceSql)) {
                    psInv.setInt(1, dto.getElectricUsage());
                    psInv.setInt(2, dto.getWaterUsage());
                    psInv.setInt(3, dto.getMeterId());
                    psInv.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                try (PreparedStatement psReq = conn.prepareStatement(updateReqSql)) {
                    psReq.setInt(1, dto.getMeterId());
                    psReq.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public MeterStatusDTO getReadingBeforeCurrentMonth(String roomCode, int currentMonth, int currentYear) {
        String sql = "SELECT TOP 1 r.room_id AS roomId, r.code AS roomCode, mr.electric, mr.water, mr.electric_img, mr.water_img " +
                     "FROM rooms r " +
                     "LEFT JOIN meter_readings mr ON r.room_id = mr.room_id AND mr.deleted_at IS NULL " +
                     "  AND (YEAR(mr.reading_date) < ? OR (YEAR(mr.reading_date) = ? AND MONTH(mr.reading_date) < ?)) " +
                     "WHERE r.code = ? AND r.deleted_at IS NULL " +
                     "ORDER BY mr.reading_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentYear);
            ps.setInt(2, currentYear);
            ps.setInt(3, currentMonth);
            ps.setString(4, roomCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    MeterStatusDTO dto = new MeterStatusDTO();
                    dto.setRoomId(rs.getInt("roomId"));
                    dto.setRoomCode(rs.getString("roomCode"));
                    int elec = rs.getInt("electric");
                    if (!rs.wasNull()) dto.setPreviousElectricReading(elec);
                    int water = rs.getInt("water");
                    if (!rs.wasNull()) dto.setPreviousWaterReading(water);
                    dto.setPreviousElectricImg(rs.getString("electric_img"));
                    dto.setPreviousWaterImg(rs.getString("water_img"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
