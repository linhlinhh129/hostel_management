package com.quanlyphongtro.dao;

import com.quanlyphongtro.constant.StatusConstant;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDAO extends BaseDAO {

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getInt("invoice_id"));
        i.setCode(rs.getString("code"));
        i.setRoomId(rs.getInt("room_id"));
        i.setMeterId(getInteger(rs, "meter_id"));
        i.setDueDate(toLocalDate(rs, "due_date"));
        i.setStatus(rs.getString("status"));
        i.setTax(rs.getBigDecimal("tax"));
        i.setOtherFee(rs.getBigDecimal("other_fee"));
        i.setRoomFee(rs.getBigDecimal("room_fee"));
        i.setElectricityPrice(rs.getBigDecimal("electricity_price"));
        i.setWaterPrice(rs.getBigDecimal("water_price"));
        i.setInternetFee(rs.getBigDecimal("internet_fee"));
        i.setServiceFee(rs.getBigDecimal("service_fee"));
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setNote(rs.getString("note"));
        i.setCreatedBy(getInteger(rs, "created_by"));
        i.setCreatedAt(toLocalDateTime(rs, "created_at"));
        i.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        i.setDeletedAt(toLocalDateTime(rs, "deleted_at"));

        // Join columns (meters)
        if (hasColumn(rs, "old_electric")) {
            i.setOldElectricReading(getInteger(rs, "old_electric"));
            i.setNewElectricReading(getInteger(rs, "new_electric"));
            i.setOldWaterReading(getInteger(rs, "old_water"));
            i.setNewWaterReading(getInteger(rs, "new_water"));
            
            // Calculate usage amounts if possible, or read from a view if existed.
            // Since schema doesn't store computed amount, we can compute here for convenience
            if (i.getNewElectricReading() != null && i.getOldElectricReading() != null && i.getElectricityPrice() != null) {
                int used = i.getNewElectricReading() - i.getOldElectricReading();
                i.setElectricAmount(i.getElectricityPrice().multiply(new BigDecimal(used)));
            } else {
                i.setElectricAmount(BigDecimal.ZERO);
            }
            if (i.getNewWaterReading() != null && i.getOldWaterReading() != null && i.getWaterPrice() != null) {
                int used = i.getNewWaterReading() - i.getOldWaterReading();
                i.setWaterAmount(i.getWaterPrice().multiply(new BigDecimal(used)));
            } else {
                i.setWaterAmount(BigDecimal.ZERO);
            }

            // Derive billing period from meter reading date or due date
            if (hasColumn(rs, "reading_date") && rs.getDate("reading_date") != null) {
                LocalDate rd = toLocalDate(rs, "reading_date");
                i.setBillingPeriod("Tháng " + String.format("%02d/%d", rd.getMonthValue(), rd.getYear()));
            } else if (i.getDueDate() != null) {
                i.setBillingPeriod("Tháng " + String.format("%02d/%d", i.getDueDate().getMonthValue(), i.getDueDate().getYear()));
            }
        }
        return i;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        java.sql.ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();
        for (int x = 1; x <= columns; x++) {
            if (columnName.equalsIgnoreCase(rsmd.getColumnName(x))) {
                return true;
            }
        }
        return false;
    }

    public List<Invoice> findByRoomId(int roomId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.*, " +
                     "m.electric as new_electric, " +
                     "(SELECT TOP 1 m2.electric FROM dbo.meter_readings m2 WHERE m2.room_id = m.room_id AND m2.reading_date < m.reading_date ORDER BY m2.reading_date DESC) as old_electric, " +
                     "m.water as new_water, " +
                     "(SELECT TOP 1 m2.water FROM dbo.meter_readings m2 WHERE m2.room_id = m.room_id AND m2.reading_date < m.reading_date ORDER BY m2.reading_date DESC) as old_water, " +
                     "m.reading_date " +
                     "FROM dbo.invoices i " +
                     "LEFT JOIN dbo.meter_readings m ON i.meter_id = m.meter_id " +
                     "WHERE i.room_id = ? AND i.deleted_at IS NULL " +
                     "ORDER BY i.due_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByRoomId failed for roomId={}", roomId, e);
        }
        return list;
    }

    public Optional<Invoice> findByIdAndRoomId(int id, int roomId) {
        String sql = "SELECT i.*, " +
                     "m.electric as new_electric, " +
                     "(SELECT TOP 1 m2.electric FROM dbo.meter_readings m2 WHERE m2.room_id = m.room_id AND m2.reading_date < m.reading_date ORDER BY m2.reading_date DESC) as old_electric, " +
                     "m.water as new_water, " +
                     "(SELECT TOP 1 m2.water FROM dbo.meter_readings m2 WHERE m2.room_id = m.room_id AND m2.reading_date < m.reading_date ORDER BY m2.reading_date DESC) as old_water, " +
                     "m.reading_date " +
                     "FROM dbo.invoices i " +
                     "LEFT JOIN dbo.meter_readings m ON i.meter_id = m.meter_id " +
                     "WHERE i.invoice_id = ? AND i.room_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByIdAndRoomId failed for id={}, roomId={}", id, roomId, e);
        }
        return Optional.empty();
    }

    public BigDecimal getUnpaidTotalByRoomId(int roomId) {
        String sql = "SELECT SUM(total_amount) as total FROM dbo.invoices " +
                     "WHERE room_id = ? AND status IN (?, ?) AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ps.setString(2, StatusConstant.INVOICE_UNPAID);
            ps.setString(3, StatusConstant.INVOICE_OVERDUE);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            logger.error("getUnpaidTotalByRoomId failed for roomId={}", roomId, e);
        }
        return BigDecimal.ZERO;
    }

    public Optional<Invoice> getCurrentInvoiceByRoomId(int roomId) {
        String sql = "SELECT TOP 1 i.*, " +
                     "m.electric as new_electric, " +
                     "(SELECT TOP 1 m2.electric FROM dbo.meter_readings m2 WHERE m2.room_id = m.room_id AND m2.reading_date < m.reading_date ORDER BY m2.reading_date DESC) as old_electric, " +
                     "m.water as new_water, " +
                     "(SELECT TOP 1 m2.water FROM dbo.meter_readings m2 WHERE m2.room_id = m.room_id AND m2.reading_date < m.reading_date ORDER BY m2.reading_date DESC) as old_water, " +
                     "m.reading_date " +
                     "FROM dbo.invoices i " +
                     "LEFT JOIN dbo.meter_readings m ON i.meter_id = m.meter_id " +
                     "WHERE i.room_id = ? AND i.deleted_at IS NULL " +
                     "ORDER BY i.due_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("getCurrentInvoiceByRoomId failed for roomId={}", roomId, e);
        }
        return Optional.empty();
    }

    public boolean updateStatus(int invoiceId, String status) {
        String sql = "UPDATE dbo.invoices SET status = ?, updated_at = GETDATE() WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("updateStatus failed for invoiceId={}", invoiceId, e);
        }
        return false;
    }

    public boolean verifyInvoiceOwnership(int invoiceId, int tenantId) {
        String sql = "SELECT 1 FROM dbo.invoices i " +
                     "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                     "WHERE i.invoice_id = ? AND r.tenant_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("verifyInvoiceOwnership failed for invoiceId={}, tenantId={}", invoiceId, tenantId, e);
        }
        return false;
    }

    public BigDecimal calculateRealtimeLatePenalty(int invoiceId) {
        String sql = "SELECT total_amount, due_date FROM dbo.invoices WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal totalAmount = rs.getBigDecimal("total_amount");
                    LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                    LocalDate today = LocalDate.now();
                    if (dueDate != null && totalAmount != null && today.isAfter(dueDate)) {
                        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
                        // 0.05% penalty per day
                        BigDecimal penaltyRate = new BigDecimal("0.0005").multiply(new BigDecimal(daysLate));
                        return totalAmount.multiply(penaltyRate).setScale(0, java.math.RoundingMode.HALF_UP);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("calculateRealtimeLatePenalty failed for invoiceId={}", invoiceId, e);
        }
        return BigDecimal.ZERO;
    }
}
