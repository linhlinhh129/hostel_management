package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.time.LocalDate;

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
}
