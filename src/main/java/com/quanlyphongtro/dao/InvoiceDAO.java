package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InvoiceDAO {

    public List<InvoiceListItemDTO> findInvoices(int managerId, String keyword, String status, String billingPeriod, int offset, int limit) {
        List<InvoiceListItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.invoice_id, i.code, i.due_date, i.status, i.total_amount, r.code AS room_code " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ?) ");
        }
        // Billing period is roughly Year-Month of due_date
        if (billingPeriod != null && !billingPeriod.trim().isEmpty()) {
            if (billingPeriod.length() == 6) {
                String year = billingPeriod.substring(0, 4);
                String month = billingPeriod.substring(4, 6);
                sql.append("AND YEAR(i.due_date) = ").append(year).append(" ");
                sql.append("AND MONTH(i.due_date) = ").append(month).append(" ");
            }
        }
        
        sql.append("ORDER BY i.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
             
             int paramIndex = 1;
             ps.setInt(paramIndex++, managerId);
             
             if (status != null && !status.trim().isEmpty()) {
                 ps.setString(paramIndex++, status);
             }
             if (keyword != null && !keyword.trim().isEmpty()) {
                 String kw = "%" + keyword + "%";
                 ps.setString(paramIndex++, kw);
                 ps.setString(paramIndex++, kw);
             }
             ps.setInt(paramIndex++, offset);
             ps.setInt(paramIndex++, limit);
             
             try (ResultSet rs = ps.executeQuery()) {
                 SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
                 while (rs.next()) {
                     InvoiceListItemDTO dto = new InvoiceListItemDTO();
                     dto.setInvoiceId(rs.getInt("invoice_id"));
                     dto.setInvoiceCode(rs.getString("code"));
                     dto.setRoomCode(rs.getString("room_code"));
                     
                     Date dueDate = rs.getDate("due_date");
                     if (dueDate != null) {
                         dto.setDueDate(dueDate.toString());
                         dto.setBillingPeriod(sdf.format(dueDate));
                     }
                     dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                     dto.setStatus(rs.getString("status"));
                     list.add(dto);
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public int countInvoices(int managerId, String keyword, String status, String billingPeriod) {
        int count = 0;
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ?) ");
        }
        if (billingPeriod != null && !billingPeriod.trim().isEmpty()) {
            if (billingPeriod.length() == 6) {
                String year = billingPeriod.substring(0, 4);
                String month = billingPeriod.substring(4, 6);
                sql.append("AND YEAR(i.due_date) = ").append(year).append(" ");
                sql.append("AND MONTH(i.due_date) = ").append(month).append(" ");
            }
        }
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
             
             int paramIndex = 1;
             ps.setInt(paramIndex++, managerId);
             
             if (status != null && !status.trim().isEmpty()) {
                 ps.setString(paramIndex++, status);
             }
             if (keyword != null && !keyword.trim().isEmpty()) {
                 String kw = "%" + keyword + "%";
                 ps.setString(paramIndex++, kw);
                 ps.setString(paramIndex++, kw);
             }
             
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     count = rs.getInt(1);
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public InvoiceDetailDTO findById(int managerId, int invoiceId) {
        String sql = "SELECT i.*, r.code AS room_code, u_create.full_name AS created_by_name, " +
                     "u_tenant.full_name AS tenant_name, u_tenant.phone AS tenant_phone, u_tenant.email AS tenant_email, " +
                     "m_new.electric AS new_electric, m_new.water AS new_water, " +
                     "(SELECT TOP 1 m_old.electric FROM meter_readings m_old WHERE m_old.room_id = i.room_id AND m_old.reading_date < m_new.reading_date ORDER BY m_old.reading_date DESC) AS old_electric, " +
                     "(SELECT TOP 1 m_old.water FROM meter_readings m_old WHERE m_old.room_id = i.room_id AND m_old.reading_date < m_new.reading_date ORDER BY m_old.reading_date DESC) AS old_water " +
                     "FROM invoices i " +
                     "INNER JOIN rooms r ON i.room_id = r.room_id " +
                     "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                     "LEFT JOIN meter_readings m_new ON i.meter_id = m_new.meter_id " +
                     "LEFT JOIN users u_create ON i.created_by = u_create.user_id " +
                     "LEFT JOIN users u_tenant ON r.tenant_id = u_tenant.user_id " +
                     "WHERE i.invoice_id = ? AND i.deleted_at IS NULL AND f.manager_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setInt(1, invoiceId);
             ps.setInt(2, managerId);
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     InvoiceDetailDTO dto = new InvoiceDetailDTO();
                     dto.setInvoiceId(rs.getInt("invoice_id"));
                     dto.setInvoiceCode(rs.getString("code"));
                     dto.setRoomCode(rs.getString("room_code"));
                     dto.setTenantName(rs.getString("tenant_name"));
                     dto.setTenantPhone(rs.getString("tenant_phone"));
                     dto.setTenantEmail(rs.getString("tenant_email"));
                     
                     Date dueDate = rs.getDate("due_date");
                     if (dueDate != null) {
                         dto.setDueDate(dueDate.toString());
                         dto.setBillingPeriod(new SimpleDateFormat("yyyyMM").format(dueDate));
                     }
                     
                     dto.setRoomFee(rs.getBigDecimal("room_fee"));
                     dto.setMeterId(rs.getObject("meter_id") != null ? rs.getInt("meter_id") : null);
                     
                     // Readings
                     dto.setNewElectricReading(rs.getObject("new_electric") != null ? rs.getInt("new_electric") : 0);
                     dto.setOldElectricReading(rs.getObject("old_electric") != null ? rs.getInt("old_electric") : 0);
                     dto.setElectricUsage(Math.max(0, dto.getNewElectricReading() - dto.getOldElectricReading()));
                     dto.setElectricUnitPrice(rs.getBigDecimal("electricity_price"));
                     if (dto.getElectricUnitPrice() != null) {
                         dto.setElectricAmount(dto.getElectricUnitPrice().multiply(new java.math.BigDecimal(dto.getElectricUsage())));
                     } else {
                         dto.setElectricAmount(java.math.BigDecimal.ZERO);
                     }
                     
                     dto.setNewWaterReading(rs.getObject("new_water") != null ? rs.getInt("new_water") : 0);
                     dto.setOldWaterReading(rs.getObject("old_water") != null ? rs.getInt("old_water") : 0);
                     dto.setWaterUsage(Math.max(0, dto.getNewWaterReading() - dto.getOldWaterReading()));
                     dto.setWaterUnitPrice(rs.getBigDecimal("water_price"));
                     if (dto.getWaterUnitPrice() != null) {
                         dto.setWaterAmount(dto.getWaterUnitPrice().multiply(new java.math.BigDecimal(dto.getWaterUsage())));
                     } else {
                         dto.setWaterAmount(java.math.BigDecimal.ZERO);
                     }
                     
                     dto.setServiceFee(rs.getBigDecimal("service_fee"));
                     dto.setInternetFee(rs.getBigDecimal("internet_fee"));
                     dto.setOtherFee(rs.getBigDecimal("other_fee"));
                     
                     // Subtotal
                     java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
                     if (dto.getRoomFee() != null) subtotal = subtotal.add(dto.getRoomFee());
                     if (dto.getElectricAmount() != null) subtotal = subtotal.add(dto.getElectricAmount());
                     if (dto.getWaterAmount() != null) subtotal = subtotal.add(dto.getWaterAmount());
                     if (dto.getServiceFee() != null) subtotal = subtotal.add(dto.getServiceFee());
                     if (dto.getInternetFee() != null) subtotal = subtotal.add(dto.getInternetFee());
                     if (dto.getOtherFee() != null) subtotal = subtotal.add(dto.getOtherFee());
                     dto.setSubtotal(subtotal);
                     
                     dto.setTaxRate(rs.getBigDecimal("tax"));
                     if (dto.getTaxRate() != null && dto.getTaxRate().compareTo(java.math.BigDecimal.ZERO) > 0) {
                         dto.setTaxAmount(subtotal.multiply(dto.getTaxRate()).divide(new java.math.BigDecimal("100")));
                     } else {
                         dto.setTaxAmount(java.math.BigDecimal.ZERO);
                     }
                     
                     dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                     dto.setStatus(rs.getString("status"));
                     dto.setNote(rs.getString("note"));
                     
                     Timestamp created = rs.getTimestamp("created_at");
                     if (created != null) dto.setCreatedAt(created.toString());
                     dto.setCreatedByName(rs.getString("created_by_name"));
                     
                     Timestamp updated = rs.getTimestamp("updated_at");
                     if (updated != null) dto.setUpdatedAt(updated.toString());
                     dto.setUpdatedByName(""); // Not available in schema
                     
                     return dto;
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public int insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (code, room_id, meter_id, due_date, status, tax, other_fee, room_fee, " +
                     "electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice.getCode());
            ps.setInt(2, invoice.getRoomId());
            if (invoice.getMeterId() != null) {
                ps.setInt(3, invoice.getMeterId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setDate(4, java.sql.Date.valueOf(invoice.getDueDate()));
            ps.setString(5, invoice.getStatus());
            ps.setBigDecimal(6, invoice.getTax());
            ps.setBigDecimal(7, invoice.getOtherFee());
            ps.setBigDecimal(8, invoice.getRoomFee());
            ps.setBigDecimal(9, invoice.getElectricityPrice());
            ps.setBigDecimal(10, invoice.getWaterPrice());
            ps.setBigDecimal(11, invoice.getInternetFee());
            ps.setBigDecimal(12, invoice.getServiceFee());
            ps.setBigDecimal(13, invoice.getTotalAmount());
            ps.setString(14, invoice.getNote());
            if (invoice.getCreatedBy() != null) {
                ps.setInt(15, invoice.getCreatedBy());
            } else {
                ps.setNull(15, Types.INTEGER);
            }
            
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public void update(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET due_date = ?, tax = ?, other_fee = ?, total_amount = ?, note = ?, updated_at = GETDATE() " +
                     "WHERE invoice_id = ? AND status != 'PAID'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(invoice.getDueDate()));
            ps.setBigDecimal(2, invoice.getTax());
            ps.setBigDecimal(3, invoice.getOtherFee());
            ps.setBigDecimal(4, invoice.getTotalAmount());
            ps.setString(5, invoice.getNote());
            ps.setInt(6, invoice.getInvoiceId());
            ps.executeUpdate();
        }
    }

    public void updateStatus(int invoiceId, String status) throws SQLException {
        String sql = "UPDATE invoices SET status = ?, updated_at = GETDATE() WHERE invoice_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
        }
    }
}
