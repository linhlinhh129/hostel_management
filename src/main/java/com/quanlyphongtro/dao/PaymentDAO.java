package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.PaymentListItemDTO;
import com.quanlyphongtro.dto.PaymentDetailDTO;
import com.quanlyphongtro.util.DatabaseUtil;
import com.quanlyphongtro.dto.PaymentListItemDTO;
import com.quanlyphongtro.dto.PaymentDetailDTO;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO extends BaseDAO {

    public boolean insertPayment(String code, int invoiceId, int roomId, String status, LocalDate paymentDate, String method, BigDecimal amount, int createdBy) {
        String sql = "INSERT INTO dbo.payments (code, invoice_id, room_id, status, payment_date, payment_method, payment_amount, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setInt(2, invoiceId);
            ps.setInt(3, roomId);
            ps.setString(4, status);
            ps.setDate(5, Date.valueOf(paymentDate));
            ps.setString(6, method);
            ps.setBigDecimal(7, amount);
            ps.setInt(8, createdBy);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("insertPayment failed for code={}", code, e);
            return false;
        }
    }

    public boolean hasPendingPayment(int invoiceId) {
        String sql = "SELECT 1 FROM payments WHERE invoice_id = ? AND status = 'PENDING' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<PaymentListItemDTO> findPayments(int managerId, String keyword, String status, int offset, int limit) {
        List<PaymentListItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT p.payment_id, p.code, p.payment_amount, p.payment_date, p.payment_method, p.status, " +
            "r.code AS room_code, u.full_name AS tenant_name " +
            "FROM payments p " +
            "INNER JOIN rooms r ON p.room_id = r.room_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "WHERE p.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND p.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.code LIKE ? OR r.code LIKE ? OR u.full_name LIKE ?) ");
        }
        
        sql.append("ORDER BY p.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        
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
             ps.setInt(paramIndex++, offset);
             ps.setInt(paramIndex++, limit);
             
             try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     PaymentListItemDTO dto = new PaymentListItemDTO();
                     dto.setPaymentId(rs.getInt("payment_id"));
                     dto.setTransactionCode(rs.getString("code"));
                     dto.setAmount(rs.getBigDecimal("payment_amount"));
                     java.sql.Date d = rs.getDate("payment_date");
                     if (d != null) dto.setPaymentDate(d.toString());
                     dto.setPaymentMethod(rs.getString("payment_method"));
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
    
    public int countPayments(int managerId, String keyword, String status) {
        int count = 0;
        StringBuilder sql = new StringBuilder(
            "SELECT COUNT(1) " +
            "FROM payments p " +
            "INNER JOIN rooms r ON p.room_id = r.room_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "LEFT JOIN users u ON r.tenant_id = u.user_id " +
            "WHERE p.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND p.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.code LIKE ? OR r.code LIKE ? OR u.full_name LIKE ?) ");
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

    public PaymentDetailDTO findById(int managerId, int paymentId) {
        String sql = "SELECT p.payment_id, p.code, p.payment_amount, p.payment_date, p.payment_method, p.status, p.created_at, " +
                     "r.code AS room_code, u.full_name AS tenant_name, u.phone AS tenant_phone, u.email AS tenant_email, " +
                     "f.name AS facility_name, f.address AS facility_address, " +
                     "i.code AS invoice_code, i.due_date, i.total_amount AS invoice_total, i.note AS invoice_note " +
                     "FROM payments p " +
                     "INNER JOIN rooms r ON p.room_id = r.room_id " +
                     "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                     "LEFT JOIN users u ON r.tenant_id = u.user_id " +
                     "LEFT JOIN invoices i ON p.invoice_id = i.invoice_id " +
                     "WHERE p.payment_id = ? AND p.deleted_at IS NULL AND f.manager_id = ?";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             ps.setInt(1, paymentId);
             ps.setInt(2, managerId);
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     PaymentDetailDTO dto = new PaymentDetailDTO();
                     dto.setPaymentId(rs.getInt("payment_id"));
                     dto.setTransactionCode(rs.getString("code"));
                     dto.setAmount(rs.getBigDecimal("payment_amount"));
                     java.sql.Date d = rs.getDate("payment_date");
                     if (d != null) dto.setPaymentDate(d.toString());
                     dto.setPaymentMethod(rs.getString("payment_method"));
                     dto.setStatus(rs.getString("status"));
                     dto.setRoomCode(rs.getString("room_code"));
                     dto.setTenantName(rs.getString("tenant_name"));
                     dto.setTenantPhone(rs.getString("tenant_phone"));
                     dto.setTenantEmail(rs.getString("tenant_email"));
                     dto.setFacilityName(rs.getString("facility_name"));
                     dto.setFacilityAddress(rs.getString("facility_address"));
                     
                     java.sql.Timestamp created = rs.getTimestamp("created_at");
                     if (created != null) dto.setCreatedAt(created.toString());
                     
                     dto.setInvoiceCode(rs.getString("invoice_code"));
                     java.sql.Date dueD = rs.getDate("due_date");
                     if (dueD != null) dto.setDueDate(dueD.toString());
                     dto.setInvoiceTotal(rs.getBigDecimal("invoice_total"));
                     dto.setInvoiceNote(rs.getString("invoice_note"));
                     
                     return dto;
                 }
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void approvePayment(int paymentId, int approvedBy) throws SQLException {
        String updatePaymentSql = "UPDATE payments SET status = 'SUCCESS', updated_at = GETDATE() " +
                                  "WHERE payment_id = ? AND status = 'PENDING' AND " +
                                  "EXISTS (SELECT 1 FROM rooms r INNER JOIN facilities f ON r.facility_id = f.facility_id WHERE r.room_id = payments.room_id AND f.manager_id = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updatePaymentSql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, approvedBy);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                String updateInvoiceSql = "UPDATE invoices SET status = 'PAID', updated_at = GETDATE() WHERE invoice_id = (SELECT invoice_id FROM payments WHERE payment_id = ?)";
                try (PreparedStatement psInv = conn.prepareStatement(updateInvoiceSql)) {
                    psInv.setInt(1, paymentId);
                    psInv.executeUpdate();
                }
            }
        }
    }

    public void rejectPayment(int paymentId, int rejectedBy) throws SQLException {
        String updatePaymentSql = "UPDATE payments SET status = 'REJECTED', updated_at = GETDATE() " +
                                  "WHERE payment_id = ? AND status = 'PENDING' AND " +
                                  "EXISTS (SELECT 1 FROM rooms r INNER JOIN facilities f ON r.facility_id = f.facility_id WHERE r.room_id = payments.room_id AND f.manager_id = ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updatePaymentSql)) {
            ps.setInt(1, paymentId);
            ps.setInt(2, rejectedBy);
            ps.executeUpdate();
        }
    }
}
