package com.quanlyphongtro.dao;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.RoundingMode;
// Filter debts strictly by OVERDUE status

import com.quanlyphongtro.dto.DebtListItemDTO;
import com.quanlyphongtro.dto.DebtDetailDTO;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DebtDAO extends BaseDAO {

    public List<DebtListItemDTO> findDebts(int managerId, String keyword, String status, int offset, int limit) {
        List<DebtListItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.invoice_id, i.code AS invoice_code, r.room_id, r.code AS room_code, " +
            "u.user_id AS tenant_id, u.full_name AS tenant_name, u.phone AS tenant_phone, " +
            "f.facility_id, f.code AS facility_code, f.name AS facility_name, " +
            "i.total_amount, i.room_fee, i.due_date, i.status, " +
            "(SELECT COALESCE(SUM(payment_amount), 0) FROM payments WHERE invoice_id = i.invoice_id AND status = 'SUCCESS' AND deleted_at IS NULL) AS paid_amount, " +
            "(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? AND (i.status = 'OVERDUE' OR (i.status = 'UNPAID' AND i.due_date < CAST(GETDATE() AS DATE))) "
        );

        if (status != null && !status.trim().isEmpty()) {
            if (status.equals("UNPAID")) {
                sql.append("AND i.status = 'UNPAID' AND i.due_date >= CAST(GETDATE() AS DATE) ");
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR u.full_name LIKE ?) ");
        }
        
        sql.append("ORDER BY i.due_date ASC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
             
            int paramIndex = 1;
            ps.setInt(paramIndex++, managerId);
            

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DebtListItemDTO dto = new DebtListItemDTO();
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    dto.setInvoiceCode(rs.getString("invoice_code"));
                    dto.setRoomId(rs.getInt("room_id"));
                    dto.setRoomCode(rs.getString("room_code"));
                    dto.setTenantId(rs.getInt("tenant_id"));
                    dto.setTenantName(rs.getString("tenant_name"));
                    dto.setTenantPhone(rs.getString("tenant_phone"));
                    dto.setFacilityId(rs.getInt("facility_id"));
                    dto.setFacilityCode(rs.getString("facility_code"));
                    dto.setFacilityName(rs.getString("facility_name"));
                    BigDecimal baseTotal = rs.getBigDecimal("total_amount");
                    String invoiceStatus = rs.getString("status");
                    Date dueDate = rs.getDate("due_date");
                    BigDecimal roomFee = rs.getBigDecimal("room_fee");
                    
                    if (!"PAID".equals(invoiceStatus) && dueDate != null && roomFee != null) {
                        LocalDate dueLocalDate = dueDate.toLocalDate();
                        LocalDate endDate = LocalDate.now();
                        Date pendingDate = rs.getDate("pending_payment_date");
                        if (pendingDate != null) {
                            endDate = pendingDate.toLocalDate();
                        }
                        if (endDate.isAfter(dueLocalDate)) {
                            long daysLate = ChronoUnit.DAYS.between(dueLocalDate, endDate);
                            BigDecimal lateFee = roomFee.multiply(new BigDecimal("0.01"))
                                                        .multiply(new BigDecimal(daysLate))
                                                        .setScale(0, RoundingMode.HALF_UP);
                            if (baseTotal != null) {
                                baseTotal = baseTotal.add(lateFee);
                            } else {
                                baseTotal = lateFee;
                            }
                            dto.setLateFeePreview(lateFee);
                            dto.setOverdueDays((int) daysLate);
                        } else {
                            dto.setLateFeePreview(BigDecimal.ZERO);
                            dto.setOverdueDays(0);
                        }
                    } else {
                        dto.setLateFeePreview(BigDecimal.ZERO);
                        dto.setOverdueDays(0);
                    }
                    dto.setInvoiceTotalAmount(baseTotal);
                    dto.setRoomFee(roomFee);
                    
                    if (dueDate != null) {
                        dto.setDueDate(dueDate.toLocalDate());
                        dto.setBillingPeriod(new SimpleDateFormat("yyyyMM").format(dueDate));
                    }
                    
                    dto.setStatus(invoiceStatus);
                    dto.setPaidAmount(rs.getBigDecimal("paid_amount"));
                    
                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding debts", e);
        }
        return list;
    }

    public int countDebts(int managerId, String keyword, String status) {
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? AND (i.status = 'OVERDUE' OR (i.status = 'UNPAID' AND i.due_date < CAST(GETDATE() AS DATE))) "
        );

        if (status != null && !status.trim().isEmpty()) {
            if (status.equals("UNPAID")) {
                sql.append("AND i.status = 'UNPAID' AND i.due_date >= CAST(GETDATE() AS DATE) ");
            }
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR u.full_name LIKE ?) ");
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
             
            int paramIndex = 1;
            ps.setInt(paramIndex++, managerId);
            

            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            logger.error("Error counting debts", e);
        }
        return 0;
    }

    public Optional<DebtDetailDTO> findDebtDetail(int managerId, int invoiceId) {
        String sql = "SELECT i.invoice_id, i.code AS invoice_code, r.room_id, r.code AS room_code, " +
            "COALESCE(u.user_id, c.tenant_id) AS tenant_id, COALESCE(u.full_name, c.tenant_full_name) AS tenant_name, COALESCE(u.phone, c.tenant_phone) AS tenant_phone, u.email AS tenant_email, " +
            "f.facility_id, f.code AS facility_code, f.name AS facility_name, " +
            "c.start_date AS contract_start_date, c.end_date AS contract_end_date, " +
            "i.room_fee, " +
            "m_new.electric AS new_electric, " +
            "(SELECT TOP 1 m_old.electric FROM meter_readings m_old WHERE m_old.room_id = i.room_id AND m_old.reading_date < m_new.reading_date ORDER BY m_old.reading_date DESC) AS old_electric, " +
            "m_new.water AS new_water, " +
            "(SELECT TOP 1 m_old.water FROM meter_readings m_old WHERE m_old.room_id = i.room_id AND m_old.reading_date < m_new.reading_date ORDER BY m_old.reading_date DESC) AS old_water, " +
            "i.electricity_price, i.water_price, " +
            "i.service_fee, i.internet_fee, i.other_fee, i.total_amount, " +
            "i.due_date, i.status, i.note, i.created_at, i.created_by, i.updated_at, " +
            "(SELECT COALESCE(SUM(payment_amount), 0) FROM payments WHERE invoice_id = i.invoice_id AND status = 'SUCCESS' AND deleted_at IS NULL) AS paid_amount, " +
            "(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "LEFT JOIN meter_readings m_new ON i.meter_id = m_new.meter_id " +
            "LEFT JOIN contracts c ON c.contract_id = (SELECT TOP 1 contract_id FROM contracts WHERE room_id = i.room_id ORDER BY CASE WHEN CAST(i.created_at AS DATE) BETWEEN start_date AND end_date THEN 0 ELSE 1 END, CASE WHEN status = 'ACTIVE' THEN 0 ELSE 1 END, CASE WHEN deleted_at IS NULL THEN 0 ELSE 1 END, created_at DESC) " +
            "LEFT JOIN users u ON COALESCE(c.tenant_id, r.tenant_id) = u.user_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND i.invoice_id = ? AND f.manager_id = ? AND (i.status = 'OVERDUE' OR (i.status = 'UNPAID' AND i.due_date < CAST(GETDATE() AS DATE)))";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DebtDetailDTO dto = new DebtDetailDTO();
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    dto.setInvoiceCode(rs.getString("invoice_code"));
                    dto.setRoomId(rs.getInt("room_id"));
                    dto.setRoomCode(rs.getString("room_code"));
                    
                    dto.setTenantId(rs.getInt("tenant_id"));
                    dto.setTenantName(rs.getString("tenant_name"));
                    dto.setTenantPhone(rs.getString("tenant_phone"));
                    dto.setTenantEmail(rs.getString("tenant_email"));
                    
                    dto.setFacilityId(rs.getInt("facility_id"));
                    dto.setFacilityCode(rs.getString("facility_code"));
                    dto.setFacilityName(rs.getString("facility_name"));
                    
                    Date cStart = rs.getDate("contract_start_date");
                    Date cEnd = rs.getDate("contract_end_date");
                    if (cStart != null && cEnd != null) {
                        java.text.SimpleDateFormat sdfDate = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        dto.setContractPeriod(sdfDate.format(cStart) + " - " + sdfDate.format(cEnd));
                    } else {
                        dto.setContractPeriod("Chưa có hợp đồng");
                    }
                    
                    dto.setRoomFee(rs.getBigDecimal("room_fee"));
                    
                    int newElec = rs.getObject("new_electric") != null ? rs.getInt("new_electric") : 0;
                    int oldElec = rs.getObject("old_electric") != null ? rs.getInt("old_electric") : 0;
                    int elecUsage = Math.max(0, newElec - oldElec);
                    BigDecimal elecPrice = rs.getBigDecimal("electricity_price");
                    
                    dto.setNewElectricReading(newElec);
                    dto.setOldElectricReading(oldElec);
                    dto.setElectricUsage(elecUsage);
                    dto.setElectricUnitPrice(elecPrice);
                    BigDecimal elecAmount = BigDecimal.ZERO;
                    if (elecPrice != null) {
                        elecAmount = elecPrice.multiply(new BigDecimal(elecUsage));
                    }
                    dto.setElectricAmount(elecAmount);
                    
                    int newWater = rs.getObject("new_water") != null ? rs.getInt("new_water") : 0;
                    int oldWater = rs.getObject("old_water") != null ? rs.getInt("old_water") : 0;
                    int waterUsage = Math.max(0, newWater - oldWater);
                    BigDecimal waterPrice = rs.getBigDecimal("water_price");
                    
                    dto.setNewWaterReading(newWater);
                    dto.setOldWaterReading(oldWater);
                    dto.setWaterUsage(waterUsage);
                    dto.setWaterUnitPrice(waterPrice);
                    BigDecimal waterAmount = BigDecimal.ZERO;
                    if (waterPrice != null) {
                        waterAmount = waterPrice.multiply(new BigDecimal(waterUsage));
                    }
                    dto.setWaterAmount(waterAmount);
                    
                    dto.setServiceFee(rs.getBigDecimal("service_fee"));
                    dto.setInternetFee(rs.getBigDecimal("internet_fee"));
                    dto.setOtherFee(rs.getBigDecimal("other_fee"));

                    // Tính phí chậm nộp runtime (1%/ngày × tiền phòng × số ngày quá hạn)
                    BigDecimal lateFee = BigDecimal.ZERO;
                    Date dueDateSql = rs.getDate("due_date");
                    if (dueDateSql != null && dto.getRoomFee() != null) {
                        LocalDate dueLocalDate = dueDateSql.toLocalDate();
                        LocalDate endDate = LocalDate.now();
                        Date pendingDate = rs.getDate("pending_payment_date");
                        if (pendingDate != null) {
                            endDate = pendingDate.toLocalDate();
                        }
                        if (endDate.isAfter(dueLocalDate)) {
                            long daysLate = ChronoUnit.DAYS.between(dueLocalDate, endDate);
                            lateFee = dto.getRoomFee()
                                        .multiply(new BigDecimal("0.01"))
                                        .multiply(new BigDecimal(daysLate))
                                        .setScale(0, RoundingMode.HALF_UP);
                            dto.setOverdueDays((int) daysLate);
                        } else {
                            dto.setOverdueDays(0);
                        }
                    }
                    dto.setLateFeePreview(lateFee);
                    // otherFee giữ nguyên giá trị từ DB, không cộng lateFee vào

                    BigDecimal subtotal = BigDecimal.ZERO;
                    if (dto.getRoomFee() != null) subtotal = subtotal.add(dto.getRoomFee());
                    if (dto.getElectricAmount() != null) subtotal = subtotal.add(dto.getElectricAmount());
                    if (dto.getWaterAmount() != null) subtotal = subtotal.add(dto.getWaterAmount());
                    if (dto.getServiceFee() != null) subtotal = subtotal.add(dto.getServiceFee());
                    if (dto.getInternetFee() != null) subtotal = subtotal.add(dto.getInternetFee());
                    if (dto.getOtherFee() != null) subtotal = subtotal.add(dto.getOtherFee());
                    
                    BigDecimal originalSubtotal = subtotal;
                    
                    // Cộng phí chậm nộp vào subtotal theo đặc tả mới
                    if (lateFee != null && lateFee.compareTo(BigDecimal.ZERO) > 0) {
                        subtotal = subtotal.add(lateFee);
                    }
                    dto.setSubtotal(subtotal);
                    
                    // invoiceTotalAmount = subtotal
                    BigDecimal totalAmount = subtotal;
                    dto.setInvoiceTotalAmount(totalAmount);
                    dto.setPaidAmount(rs.getBigDecimal("paid_amount"));
                    
                    // Số còn nợ = tổng - đã thanh toán
                    BigDecimal paid = dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO;
                    dto.setDebtAmount(totalAmount.subtract(paid).max(BigDecimal.ZERO));
                    
                    Date dueDate = rs.getDate("due_date");
                    if (dueDate != null) {
                        dto.setDueDate(dueDate.toLocalDate());
                        dto.setBillingPeriod(new SimpleDateFormat("yyyyMM").format(dueDate));
                    }
                    
                    dto.setStatus(rs.getString("status"));
                    dto.setNote(rs.getString("note"));
                    
                    Timestamp created = rs.getTimestamp("created_at");
                    if (created != null) dto.setCreatedAt(created.toLocalDateTime());
                    dto.setCreatedBy(getInteger(rs, "created_by"));
                    Timestamp updated = rs.getTimestamp("updated_at");
                    if (updated != null) dto.setUpdatedAt(updated.toLocalDateTime());

                    return Optional.of(dto);
                }
            }
        } catch (Exception e) {
            logger.error("Error finding debt detail", e);
        }
        return Optional.empty();
    }
}
