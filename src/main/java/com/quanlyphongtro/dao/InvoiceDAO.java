package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
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

    // --- Methods from HEAD (Tenant / Room specific) ---

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice invoice = new Invoice();
        invoice.setId(rs.getInt("invoice_id"));
        invoice.setCode(rs.getString("code"));
        invoice.setRoomId(getInteger(rs, "room_id"));
        invoice.setMeterId(getInteger(rs, "meter_id"));
        invoice.setDueDate(toLocalDate(rs, "due_date"));
        invoice.setStatus(rs.getString("status"));
        invoice.setTax(rs.getBigDecimal("tax"));
        invoice.setOtherFee(rs.getBigDecimal("other_fee"));
        invoice.setRoomFee(rs.getBigDecimal("room_fee"));
        invoice.setElectricityPrice(rs.getBigDecimal("electricity_price"));
        invoice.setWaterPrice(rs.getBigDecimal("water_price"));
        invoice.setInternetFee(rs.getBigDecimal("internet_fee"));
        invoice.setServiceFee(rs.getBigDecimal("service_fee"));
        invoice.setTotalAmount(rs.getBigDecimal("total_amount"));
        invoice.setNote(rs.getString("note"));
        invoice.setCreatedBy(getInteger(rs, "created_by"));
        invoice.setCreatedAt(toLocalDateTime(rs, "created_at"));
        invoice.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        invoice.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        
        // Cố gắng map các cột alias (thường có khi join với meter_readings)
        try {
            invoice.setNewElectricReading(rs.getInt("new_electric"));
            invoice.setOldElectricReading(rs.getInt("old_electric"));
            invoice.setNewWaterReading(rs.getInt("new_water"));
            invoice.setOldWaterReading(rs.getInt("old_water"));
            invoice.setBillingPeriod(rs.getString("billing_period"));
        } catch (SQLException ignore) {
            // Cột có thể không tồn tại trong các query đơn giản
        }
        
        return invoice;
    }

    public List<Invoice> findByRoomId(int roomId) {
        String sql = "SELECT i.*, " +
                     "  mr.electric AS new_electric, mr.water AS new_water, " +
                     "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, " +
                     "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, " +
                     "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period " +
                     "FROM invoices i " +
                     "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                     "WHERE i.room_id = ? AND i.deleted_at IS NULL " +
                     "ORDER BY i.created_at DESC";
        List<Invoice> list = new ArrayList<>();
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
                     "  mr.electric AS new_electric, mr.water AS new_water, " +
                     "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, " +
                     "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, " +
                     "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period " +
                     "FROM invoices i " +
                     "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
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
        String sql = "SELECT SUM(total_amount) FROM invoices WHERE room_id = ? AND status != 'PAID' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
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
                     "  mr.electric AS new_electric, mr.water AS new_water, " +
                     "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, " +
                     "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, " +
                     "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period " +
                     "FROM invoices i " +
                     "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                     "WHERE i.room_id = ? AND i.deleted_at IS NULL " +
                     "ORDER BY i.created_at DESC";
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
    
    

    public boolean verifyInvoiceOwnership(int invoiceId, int tenantId) {
        String sql = "SELECT 1 FROM invoices i JOIN rooms r ON i.room_id = r.room_id WHERE i.invoice_id = ? AND r.tenant_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("verifyInvoiceOwnership failed for invoiceId={}, tenantId={}", invoiceId, tenantId, e);
            return false;
        }
    }

    public BigDecimal calculateRealtimeLatePenalty(int invoiceId) {
        String sql = "SELECT total_amount, due_date FROM invoices WHERE invoice_id = ? AND status = 'OVERDUE' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal amount = rs.getBigDecimal("total_amount");
                    java.sql.Date dueDate = rs.getDate("due_date");
                    if (dueDate != null && amount != null) {
                        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate.toLocalDate(), LocalDate.now());
                        if (daysLate > 0) {
                            BigDecimal penaltyRatePerDay = new BigDecimal("0.005");
                            return amount.multiply(penaltyRatePerDay).multiply(new BigDecimal(daysLate));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("calculateRealtimeLatePenalty failed for invoiceId={}", invoiceId, e);
        }
        return BigDecimal.ZERO;
    }

    // --- Methods from buidinh (Manager specific) ---

    public List<InvoiceListItemDTO> findInvoices(int managerId, String keyword, String status, String billingPeriod, int offset, int limit) {
        List<InvoiceListItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.invoice_id, i.code, i.total_amount, i.due_date, i.status, r.code AS room_code, u.full_name AS tenant_name " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR u.full_name LIKE ?) ");
        }
        if (billingPeriod != null && billingPeriod.length() == 6) {
            sql.append("AND i.code LIKE ? "); // INV-RoomCode-YYYYMM
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
                 ps.setString(paramIndex++, kw);
             }
             if (billingPeriod != null && billingPeriod.length() == 6) {
                 ps.setString(paramIndex++, "%-" + billingPeriod);
             }
             ps.setInt(paramIndex++, offset);
             ps.setInt(paramIndex++, limit);
             
             try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     InvoiceListItemDTO dto = new InvoiceListItemDTO();
                     dto.setInvoiceId(rs.getInt("invoice_id"));
                     dto.setInvoiceCode(rs.getString("code"));
                     dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                     java.sql.Date d = rs.getDate("due_date");
                     if (d != null) dto.setDueDate(d.toString());
                     dto.setStatus(rs.getString("status"));
                     dto.setRoomCode(rs.getString("room_code"));
                     dto.setTenantName(rs.getString("tenant_name"));
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
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR u.full_name LIKE ?) ");
        }
        if (billingPeriod != null && billingPeriod.length() == 6) {
            sql.append("AND i.code LIKE ? "); 
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
                 ps.setString(paramIndex++, kw);
             }
             if (billingPeriod != null && billingPeriod.length() == 6) {
                 ps.setString(paramIndex++, "%-" + billingPeriod);
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
        String sql = "SELECT i.*, r.code AS room_code, u.full_name AS tenant_name, u.phone AS tenant_phone, u.email AS tenant_email, " +
                     "f.name AS facility_name, f.address AS facility_address, " +
                     "mr_curr.electric AS new_electric, mr_curr.water AS new_water, " +
                     "(SELECT TOP 1 electric FROM meter_readings mr_old WHERE mr_old.room_id = i.room_id AND mr_old.reading_date < mr_curr.reading_date ORDER BY mr_old.reading_date DESC) AS old_electric, " +
                     "(SELECT TOP 1 water FROM meter_readings mr_old WHERE mr_old.room_id = i.room_id AND mr_old.reading_date < mr_curr.reading_date ORDER BY mr_old.reading_date DESC) AS old_water " +
                     "FROM invoices i " +
                     "INNER JOIN rooms r ON i.room_id = r.room_id " +
                     "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                     "LEFT JOIN users u ON r.tenant_id = u.user_id " +
                     "LEFT JOIN meter_readings mr_curr ON i.meter_id = mr_curr.meter_id " +
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
                     dto.setStatus(rs.getString("status"));
                     
                     java.sql.Date d = rs.getDate("due_date");
                     if (d != null) dto.setDueDate(d.toString());
                     
                     java.sql.Timestamp created = rs.getTimestamp("created_at");
                     if (created != null) dto.setCreatedAt(created.toString());
                     
                     dto.setRoomCode(rs.getString("room_code"));
                     dto.setTenantName(rs.getString("tenant_name"));
                     dto.setTenantPhone(rs.getString("tenant_phone"));
                     dto.setTenantEmail(rs.getString("tenant_email"));
                     dto.setFacilityName(rs.getString("facility_name"));
                     dto.setFacilityAddress(rs.getString("facility_address"));
                     
                     dto.setRoomFee(rs.getBigDecimal("room_fee"));
                     dto.setElectricUnitPrice(rs.getBigDecimal("electricity_price"));
                     dto.setWaterUnitPrice(rs.getBigDecimal("water_price"));
                     dto.setInternetFee(rs.getBigDecimal("internet_fee"));
                     dto.setServiceFee(rs.getBigDecimal("service_fee"));
                     dto.setOtherFee(rs.getBigDecimal("other_fee"));
                     dto.setTaxRate(rs.getBigDecimal("tax"));
                     dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                     dto.setNote(rs.getString("note"));
                     
                     int ne = rs.getInt("new_electric");
                     int oe = rs.getInt("old_electric");
                     int nw = rs.getInt("new_water");
                     int ow = rs.getInt("old_water");
                     
                     dto.setElectricUsage(ne - oe);
                     dto.setWaterUsage(nw - ow);
                     
                     return dto;
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (code, room_id, meter_id, due_date, status, tax, other_fee, " +
                     "room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoice.getCode());
            ps.setInt(2, invoice.getRoomId());
            ps.setInt(3, invoice.getMeterId());
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
            ps.setInt(15, invoice.getCreatedBy());
            ps.executeUpdate();
        }
    }
    
    public void update(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET due_date = ?, tax = ?, other_fee = ?, total_amount = ?, note = ?, updated_at = GETDATE() " +
                     "WHERE invoice_id = ? AND deleted_at IS NULL";
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
        String sql = "UPDATE invoices SET status = ?, updated_at = GETDATE() WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
        }
    }
}
