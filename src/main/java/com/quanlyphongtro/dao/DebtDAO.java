package com.quanlyphongtro.dao;

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
            "(SELECT COALESCE(SUM(payment_amount), 0) FROM payments WHERE invoice_id = i.invoice_id AND status = 'SUCCESS' AND deleted_at IS NULL) AS paid_amount " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? AND i.status IN ('UNPAID', 'OVERDUE') "
        );

        if (status != null && !status.trim().isEmpty()) {
            if (status.equals("UNPAID")) {
                sql.append("AND i.status = 'UNPAID' AND i.due_date >= CAST(GETDATE() AS DATE) ");
            } else if (status.equals("OVERDUE")) {
                sql.append("AND (i.status = 'OVERDUE' OR (i.status = 'UNPAID' AND i.due_date < CAST(GETDATE() AS DATE))) ");
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
                    dto.setInvoiceTotalAmount(rs.getBigDecimal("total_amount"));
                    dto.setRoomFee(rs.getBigDecimal("room_fee"));
                    
                    Date dueDate = rs.getDate("due_date");
                    if (dueDate != null) {
                        dto.setDueDate(dueDate.toLocalDate());
                        dto.setBillingPeriod(new java.text.SimpleDateFormat("yyyyMM").format(dueDate));
                    }
                    
                    dto.setStatus(rs.getString("status"));
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
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? AND i.status IN ('UNPAID', 'OVERDUE') "
        );

        if (status != null && !status.trim().isEmpty()) {
            if (status.equals("UNPAID")) {
                sql.append("AND i.status = 'UNPAID' AND i.due_date >= CAST(GETDATE() AS DATE) ");
            } else if (status.equals("OVERDUE")) {
                sql.append("AND (i.status = 'OVERDUE' OR (i.status = 'UNPAID' AND i.due_date < CAST(GETDATE() AS DATE))) ");
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
        String sql = "SELECT i.invoice_id, i.code AS invoice_code, r.code AS room_code, " +
            "u.user_id AS tenant_id, u.full_name AS tenant_name, u.phone AS tenant_phone, u.email AS tenant_email, " +
            "f.facility_id, f.code AS facility_code, f.name AS facility_name, " +
            "i.room_fee, " +
            "m_new.electric AS new_electric, " +
            "(SELECT TOP 1 m_old.electric FROM meter_readings m_old WHERE m_old.room_id = i.room_id AND m_old.reading_date < m_new.reading_date ORDER BY m_old.reading_date DESC) AS old_electric, " +
            "m_new.water AS new_water, " +
            "(SELECT TOP 1 m_old.water FROM meter_readings m_old WHERE m_old.room_id = i.room_id AND m_old.reading_date < m_new.reading_date ORDER BY m_old.reading_date DESC) AS old_water, " +
            "i.electricity_price, i.water_price, " +
            "i.service_fee, i.internet_fee, i.other_fee, i.tax AS tax_rate, i.total_amount, " +
            "i.due_date, i.status, i.note, i.created_at, i.created_by, i.updated_at, " +
            "(SELECT COALESCE(SUM(payment_amount), 0) FROM payments WHERE invoice_id = i.invoice_id AND status = 'SUCCESS' AND deleted_at IS NULL) AS paid_amount " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "LEFT JOIN meter_readings m_new ON i.meter_id = m_new.meter_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND i.invoice_id = ? AND f.manager_id = ? AND i.status IN ('UNPAID', 'OVERDUE')";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    DebtDetailDTO dto = new DebtDetailDTO();
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    dto.setInvoiceCode(rs.getString("invoice_code"));
                    dto.setRoomCode(rs.getString("room_code"));
                    
                    dto.setTenantId(rs.getInt("tenant_id"));
                    dto.setTenantName(rs.getString("tenant_name"));
                    dto.setTenantPhone(rs.getString("tenant_phone"));
                    dto.setTenantEmail(rs.getString("tenant_email"));
                    
                    dto.setFacilityId(rs.getInt("facility_id"));
                    dto.setFacilityCode(rs.getString("facility_code"));
                    dto.setFacilityName(rs.getString("facility_name"));
                    
                    dto.setRoomFee(rs.getBigDecimal("room_fee"));
                    
                    int newElec = rs.getObject("new_electric") != null ? rs.getInt("new_electric") : 0;
                    int oldElec = rs.getObject("old_electric") != null ? rs.getInt("old_electric") : 0;
                    int elecUsage = Math.max(0, newElec - oldElec);
                    java.math.BigDecimal elecPrice = rs.getBigDecimal("electricity_price");
                    
                    dto.setNewElectricReading(newElec);
                    dto.setOldElectricReading(oldElec);
                    dto.setElectricUsage(elecUsage);
                    dto.setElectricUnitPrice(elecPrice);
                    java.math.BigDecimal elecAmount = java.math.BigDecimal.ZERO;
                    if (elecPrice != null) {
                        elecAmount = elecPrice.multiply(new java.math.BigDecimal(elecUsage));
                    }
                    dto.setElectricAmount(elecAmount);
                    
                    int newWater = rs.getObject("new_water") != null ? rs.getInt("new_water") : 0;
                    int oldWater = rs.getObject("old_water") != null ? rs.getInt("old_water") : 0;
                    int waterUsage = Math.max(0, newWater - oldWater);
                    java.math.BigDecimal waterPrice = rs.getBigDecimal("water_price");
                    
                    dto.setNewWaterReading(newWater);
                    dto.setOldWaterReading(oldWater);
                    dto.setWaterUsage(waterUsage);
                    dto.setWaterUnitPrice(waterPrice);
                    java.math.BigDecimal waterAmount = java.math.BigDecimal.ZERO;
                    if (waterPrice != null) {
                        waterAmount = waterPrice.multiply(new java.math.BigDecimal(waterUsage));
                    }
                    dto.setWaterAmount(waterAmount);
                    
                    dto.setServiceFee(rs.getBigDecimal("service_fee"));
                    dto.setInternetFee(rs.getBigDecimal("internet_fee"));
                    dto.setOtherFee(rs.getBigDecimal("other_fee"));
                    
                    java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
                    if (dto.getRoomFee() != null) subtotal = subtotal.add(dto.getRoomFee());
                    if (dto.getElectricAmount() != null) subtotal = subtotal.add(dto.getElectricAmount());
                    if (dto.getWaterAmount() != null) subtotal = subtotal.add(dto.getWaterAmount());
                    if (dto.getServiceFee() != null) subtotal = subtotal.add(dto.getServiceFee());
                    if (dto.getInternetFee() != null) subtotal = subtotal.add(dto.getInternetFee());
                    if (dto.getOtherFee() != null) subtotal = subtotal.add(dto.getOtherFee());
                    dto.setSubtotal(subtotal);
                    
                    java.math.BigDecimal taxRate = rs.getBigDecimal("tax_rate");
                    dto.setTaxRate(taxRate);
                    if (taxRate != null && taxRate.compareTo(java.math.BigDecimal.ZERO) > 0) {
                        dto.setTaxAmount(subtotal.multiply(taxRate).divide(new java.math.BigDecimal("100")));
                    } else {
                        dto.setTaxAmount(java.math.BigDecimal.ZERO);
                    }
                    
                    dto.setInvoiceTotalAmount(rs.getBigDecimal("total_amount"));
                    dto.setPaidAmount(rs.getBigDecimal("paid_amount"));
                    
                    Date dueDate = rs.getDate("due_date");
                    if (dueDate != null) {
                        dto.setDueDate(dueDate.toLocalDate());
                        dto.setBillingPeriod(new java.text.SimpleDateFormat("yyyyMM").format(dueDate));
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
        } catch (SQLException e) {
            logger.error("Error finding debt detail", e);
        }
        return Optional.empty();
    }
}
