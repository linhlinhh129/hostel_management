package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.SystemRevenueDTO;
import com.quanlyphongtro.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RevenueDAO extends BaseDAO {

    /**
     * Parse "MM/yyyy" to int[]{month, year}.
     */
    private int[] parsePeriod(String period) {
        if (period == null || period.isBlank()) {
            LocalDate now = LocalDate.now();
            return new int[]{now.getMonthValue(), now.getYear()};
        }
        try {
            String[] parts = period.trim().split("/");
            return new int[]{Integer.parseInt(parts[0]), Integer.parseInt(parts[1])};
        } catch (Exception e) {
            LocalDate now = LocalDate.now();
            return new int[]{now.getMonthValue(), now.getYear()};
        }
    }

    /**
     * Get system-level revenue KPIs for a given period (format "MM/yyyy").
     */
    public SystemRevenueDTO getSystemRevenue(String period) {
        int[] my = parsePeriod(period);
        int month = my[0], year = my[1];

        String sql = "SELECT " +
            "SUM(CASE WHEN status = 'PAID' THEN total_amount ELSE 0 END) AS total_revenue, " +
            "SUM(CASE WHEN status IN ('UNPAID', 'OVERDUE') THEN total_amount ELSE 0 END) AS total_outstanding, " +
            "COUNT(CASE WHEN status = 'PAID'   THEN 1 END) AS paid_count, " +
            "COUNT(CASE WHEN status = 'UNPAID' THEN 1 END) AS unpaid_count, " +
            "COUNT(CASE WHEN status = 'OVERDUE' THEN 1 END) AS overdue_count, " +
            "COUNT(*) AS total_count " +
            "FROM dbo.invoices " +
            "WHERE deleted_at IS NULL AND MONTH(created_at) = ? AND YEAR(created_at) = ?";

        SystemRevenueDTO dto = new SystemRevenueDTO();
        dto.setTotalRevenue(BigDecimal.ZERO);
        dto.setTotalOutstanding(BigDecimal.ZERO);
        dto.setTotalBilledAmount(BigDecimal.ZERO);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal rev = rs.getBigDecimal("total_revenue");
                    dto.setTotalRevenue(rev != null ? rev : BigDecimal.ZERO);
                    BigDecimal out = rs.getBigDecimal("total_outstanding");
                    dto.setTotalOutstanding(out != null ? out : BigDecimal.ZERO);
                    dto.setTotalBilledAmount(dto.getTotalRevenue().add(dto.getTotalOutstanding()));
                    dto.setPaidCount(rs.getInt("paid_count"));
                    dto.setUnpaidCount(rs.getInt("unpaid_count"));
                    dto.setOverdueCount(rs.getInt("overdue_count"));
                    int total = rs.getInt("total_count");
                    if (total > 0) {
                        dto.setCollectionRate((int) Math.round(100.0 * dto.getPaidCount() / total));
                    }
                }
            }
        } catch (Exception e) {
            logger.error("RevenueDAO.getSystemRevenue failed for period={}", period, e);
        }

        return dto;
    }

    private static final String FACILITY_REVENUE_SQL =
        "SELECT f.facility_id, f.code AS facility_code, f.name AS facility_name, " +
        "  COALESCE(SUM(CASE WHEN i.status = 'PAID' THEN i.total_amount ELSE 0 END), 0) AS total_revenue, " +
        "  COALESCE(SUM(CASE WHEN i.status IN ('UNPAID', 'OVERDUE') THEN i.total_amount ELSE 0 END), 0) AS total_outstanding, " +
        "  COUNT(CASE WHEN i.status = 'PAID'   THEN 1 END) AS paid_count, " +
        "  COUNT(CASE WHEN i.status = 'UNPAID' THEN 1 END) AS unpaid_count, " +
        "  COUNT(CASE WHEN i.status = 'OVERDUE' THEN 1 END) AS overdue_count, " +
        "  COUNT(i.invoice_id) AS total_count " +
        "FROM dbo.facilities f " +
        "LEFT JOIN dbo.rooms r ON r.facility_id = f.facility_id AND r.deleted_at IS NULL " +
        "LEFT JOIN dbo.invoices i ON i.room_id = r.room_id AND i.deleted_at IS NULL " +
        "  AND MONTH(i.created_at) = ? AND YEAR(i.created_at) = ? " +
        "WHERE f.deleted_at IS NULL AND f.status = 'ACTIVE' " +
        "GROUP BY f.facility_id, f.code, f.name " +
        "ORDER BY total_revenue DESC";

    public List<FacilityRevenueStatDTO> getFacilityRevenues(String period) {
        int[] my = parsePeriod(period);
        return queryFacilityRevenues(my[0], my[1], -1, -1);
    }

    public List<FacilityRevenueStatDTO> getFacilityRevenuesPaged(String period, int page, int pageSize) {
        int[] my = parsePeriod(period);
        return queryFacilityRevenues(my[0], my[1], page, pageSize);
    }

    private List<FacilityRevenueStatDTO> queryFacilityRevenues(int month, int year, int page, int pageSize) {
        List<FacilityRevenueStatDTO> list = new ArrayList<>();
        String sql = FACILITY_REVENUE_SQL;
        if (page > 0 && pageSize > 0) {
            sql = FACILITY_REVENUE_SQL.replace("ORDER BY total_revenue DESC",
                "ORDER BY total_revenue DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            if (page > 0 && pageSize > 0) {
                ps.setInt(3, (page - 1) * pageSize);
                ps.setInt(4, pageSize);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FacilityRevenueStatDTO dto = new FacilityRevenueStatDTO();
                    dto.setFacilityId(rs.getInt("facility_id"));
                    dto.setFacilityCode(rs.getString("facility_code"));
                    dto.setFacilityName(rs.getString("facility_name"));
                    BigDecimal rev = rs.getBigDecimal("total_revenue");
                    dto.setTotalRevenue(rev != null ? rev : BigDecimal.ZERO);
                    BigDecimal out = rs.getBigDecimal("total_outstanding");
                    dto.setTotalOutstanding(out != null ? out : BigDecimal.ZERO);
                    dto.setTotalBilledAmount(dto.getTotalRevenue().add(dto.getTotalOutstanding()));
                    dto.setPaidCount(rs.getInt("paid_count"));
                    dto.setUnpaidCount(rs.getInt("unpaid_count"));
                    dto.setOverdueCount(rs.getInt("overdue_count"));
                    int total = rs.getInt("total_count");
                    if (total > 0) {
                        dto.setCollectionRate((int) Math.round(100.0 * dto.getPaidCount() / total));
                    }
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("RevenueDAO.queryFacilityRevenues failed month={} year={}", month, year, e);
        }
        return list;
    }

    /**
     * Đếm tổng số facility ACTIVE để tính số trang phân trang cho by-facility view.
     * <p>
     * Lưu ý: {@code period} được nhận vào để đồng bộ với interface nhưng
     * <b>không được dùng để filter</b> — đây là thiết kế có chủ đích.
     * by-facility luôn hiển thị <b>tất cả facility ACTIVE</b>, kể cả
     * những facility không có hóa đơn trong kỳ (revenue = 0).
     * Truy vấn {@code getFacilityRevenuesPaged} dùng LEFT JOIN để đảm bảo
     * điều này, nên tổng số trang cũng phải tính trên toàn bộ facility ACTIVE.
     */
    public int countActiveFacilities() {
        String sql = "SELECT COUNT(DISTINCT f.facility_id) " +
            "FROM dbo.facilities f " +
            "WHERE f.deleted_at IS NULL AND f.status = 'ACTIVE'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            logger.error("RevenueDAO.countActiveFacilities failed", e);
        }
        return 0;
    }

    /**
     * Returns revenue stats for the last N months (for by-period view).
     * Single query, no N+1, no growth rate calculation.
     */
    public List<FacilityRevenueStatDTO> getRevenueTrend(int months) {
        List<FacilityRevenueStatDTO> list = new ArrayList<>();
        LocalDate now = LocalDate.now();
        LocalDate startDate = now.minusMonths(months - 1).withDayOfMonth(1);

        String sql = "SELECT " +
            "MONTH(created_at) AS m, YEAR(created_at) AS y, " +
            "COALESCE(SUM(CASE WHEN status = 'PAID'    THEN total_amount ELSE 0 END), 0) AS total_revenue, " +
            "COALESCE(SUM(CASE WHEN status IN ('UNPAID', 'OVERDUE') THEN total_amount ELSE 0 END), 0) AS total_outstanding, " +
            "COUNT(CASE WHEN status = 'PAID'    THEN 1 END) AS paid_count, " +
            "COUNT(CASE WHEN status = 'UNPAID'  THEN 1 END) AS unpaid_count, " +
            "COUNT(CASE WHEN status = 'OVERDUE' THEN 1 END) AS overdue_count " +
            "FROM dbo.invoices " +
            "WHERE deleted_at IS NULL AND created_at >= ? " +
            "GROUP BY YEAR(created_at), MONTH(created_at)";

        java.util.Map<String, FacilityRevenueStatDTO> map = new java.util.HashMap<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(startDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String period = String.format("%02d/%d", rs.getInt("m"), rs.getInt("y"));
                    FacilityRevenueStatDTO dto = new FacilityRevenueStatDTO();
                    dto.setFacilityCode(period);
                    BigDecimal rev = rs.getBigDecimal("total_revenue");
                    dto.setTotalRevenue(rev != null ? rev : BigDecimal.ZERO);
                    BigDecimal out = rs.getBigDecimal("total_outstanding");
                    dto.setTotalOutstanding(out != null ? out : BigDecimal.ZERO);
                    dto.setTotalBilledAmount(dto.getTotalRevenue().add(dto.getTotalOutstanding()));
                    dto.setPaidCount(rs.getInt("paid_count"));
                    dto.setUnpaidCount(rs.getInt("unpaid_count"));
                    dto.setOverdueCount(rs.getInt("overdue_count"));
                    map.put(period, dto);
                }
            }
        } catch (Exception e) {
            logger.error("RevenueDAO.getRevenueTrend failed", e);
        }

        for (int i = 0; i < months; i++) {
            LocalDate d = now.minusMonths(i);
            String period = String.format("%02d/%d", d.getMonthValue(), d.getYear());
            FacilityRevenueStatDTO dto = map.get(period);
            if (dto == null) {
                dto = new FacilityRevenueStatDTO();
                dto.setFacilityCode(period);
                dto.setTotalRevenue(BigDecimal.ZERO);
                dto.setTotalOutstanding(BigDecimal.ZERO);
                dto.setTotalBilledAmount(BigDecimal.ZERO);
                dto.setPaidCount(0);
                dto.setUnpaidCount(0);
                dto.setOverdueCount(0);
            }
            list.add(dto);
        }
        return list;
    }

    /**
     * Returns total paid revenue for a period "MM/yyyy" (for dashboard KPI).
     */
    public BigDecimal getMonthlyRevenueTotal(String period) {
        int[] my = parsePeriod(period);
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM dbo.invoices " +
            "WHERE deleted_at IS NULL AND status = 'PAID' " +
            "AND MONTH(created_at) = ? AND YEAR(created_at) = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, my[0]);
            ps.setInt(2, my[1]);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal val = rs.getBigDecimal(1);
                    return val != null ? val : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            logger.error("RevenueDAO.getMonthlyRevenueTotal failed for period={}", period, e);
        }
        return BigDecimal.ZERO;
    }
}
