package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.MeterReading;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MeterReadingDAO extends BaseDAO {

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
        return m;
    }

    /**
     * Lấy 2 bản ghi chỉ số điện nước mới nhất theo reading_date DESC cho một phòng.
     * Index 0 = mới nhất (new), index 1 = cũ hơn (old).
     */
    public List<MeterReading> findLatestTwoByRoomId(int roomId) {
        String sql = """
                SELECT TOP 2 *
                FROM dbo.meter_readings
                WHERE room_id = ? AND deleted_at IS NULL
                ORDER BY reading_date DESC, meter_id DESC
                """;
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
     *
     * Trả về list đúng 2 phần tử [meterCurrent, meterPrev]:
     *  - meterCurrent: bản ghi mới nhất trong tháng billingPeriod (YYYYMM)
     *  - meterPrev:    bản ghi mới nhất trong tháng liền trước
     * Nếu không có dữ liệu tháng nào thì phần tử tương ứng là null trong list.
     *
     * Luôn trả về list 2 phần tử để caller dễ xử lý (null-safe).
     */
    public List<MeterReading> findByRoomAndBillingPeriod(int roomId, String billingPeriod) {
        // Parse billingPeriod YYYYMM thành năm/tháng
        int year  = Integer.parseInt(billingPeriod.substring(0, 4));
        int month = Integer.parseInt(billingPeriod.substring(4, 6));

        // Tháng hiện tại: từ ngày 1 đến cuối tháng
        java.time.LocalDate periodStart = java.time.LocalDate.of(year, month, 1);
        java.time.LocalDate periodEnd   = periodStart.withDayOfMonth(periodStart.lengthOfMonth());

        // Tháng trước: từ ngày 1 tháng trước đến cuối tháng trước
        java.time.LocalDate prevEnd   = periodStart.minusDays(1);
        java.time.LocalDate prevStart = prevEnd.withDayOfMonth(1);

        MeterReading current = findLatestInRange(roomId, periodStart, periodEnd);
        MeterReading prev    = findLatestInRange(roomId, prevStart, prevEnd);

        List<MeterReading> result = new ArrayList<>();
        result.add(current); // có thể null
        result.add(prev);    // có thể null
        return result;
    }

    /**
     * Lấy bản ghi mới nhất của một phòng trong khoảng ngày [from, to].
     * Trả về null nếu không có dữ liệu.
     */
    public MeterReading findLatestInRange(int roomId,
                                           java.time.LocalDate from,
                                           java.time.LocalDate to) {
        String sql = """
                SELECT TOP 1 *
                FROM dbo.meter_readings
                WHERE room_id = ?
                  AND reading_date >= ?
                  AND reading_date <= ?
                  AND deleted_at IS NULL
                ORDER BY reading_date DESC, meter_id DESC
                """;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, java.sql.Date.valueOf(from));
            ps.setDate(3, java.sql.Date.valueOf(to));
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
}
