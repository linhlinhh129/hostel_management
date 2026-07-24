package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.InvoiceDAO;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.util.DatabaseUtil;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.util.AuditLogHelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    public List<Invoice> getInvoicesByRoomId(int roomId) {
        return invoiceDAO.findByRoomId(roomId);
    }

    @Override
    public Optional<Invoice> getInvoiceById(int invoiceId, int roomId) {
        return invoiceDAO.findByIdAndRoomId(invoiceId, roomId);
    }

    @Override
    public BigDecimal getUnpaidTotal(int roomId) {
        return invoiceDAO.getUnpaidTotalByRoomId(roomId);
    }

    @Override
    public Optional<Invoice> getCurrentInvoice(int roomId) {
        return invoiceDAO.getCurrentInvoiceByRoomId(roomId);
    }

    @Override
    public List<InvoiceListItemDTO> getInvoices(int managerId, String keyword, String status, String billingPeriod, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return invoiceDAO.findInvoices(managerId, keyword, status, billingPeriod, offset, pageSize);
    }

    @Override
    public int countInvoices(int managerId, String keyword, String status, String billingPeriod) {
        return invoiceDAO.countInvoices(managerId, keyword, status, billingPeriod);
    }

    @Override
    public InvoiceDetailDTO getInvoiceDetail(int managerId, int invoiceId) throws Exception {
        InvoiceDetailDTO dto = invoiceDAO.findById(managerId, invoiceId);
        if (dto == null) {
            throw new Exception("Không tìm thấy hóa đơn hoặc bạn không có quyền xem.");
        }
        return dto;
    }

    @Override
    public void createInvoice(int managerId, String roomCode, String billingPeriod, String dueDateStr, String taxRateStr, String otherFeeStr, String note, int createdBy) throws Exception {
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Hạn thanh toán không hợp lệ.");
        }
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày hiện tại.");
        }

        BigDecimal taxRate = new BigDecimal(taxRateStr != null && !taxRateStr.isEmpty() ? taxRateStr : "0");
        if (taxRate.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Thuế không được nhỏ hơn 0.");

        BigDecimal otherFee = new BigDecimal(otherFeeStr != null && !otherFeeStr.isEmpty() ? otherFeeStr : "0");
        if (otherFee.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Phí khác không được nhỏ hơn 0.");

        int roomId = 0;
        BigDecimal roomFee = BigDecimal.ZERO;
        BigDecimal electricityPrice = BigDecimal.ZERO;
        BigDecimal waterPrice = BigDecimal.ZERO;
        BigDecimal internetFee = BigDecimal.ZERO;
        BigDecimal serviceFee = BigDecimal.ZERO;

        String checkRoomSql = "SELECT r.room_id, r.status, r.tenant_id, r.room_fee, f.electricity_price, f.water_price, f.internet_fee, f.service_fee " +
                              "FROM rooms r INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                              "WHERE r.code = ? AND f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkRoomSql)) {
            ps.setString(1, roomCode);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String status = rs.getString("status");
                    rs.getInt("tenant_id");
                    boolean hasTenant = !rs.wasNull();

                    if (!"OCCUPIED".equals(status) || !hasTenant) {
                        throw new IllegalArgumentException("Không thể tạo hóa đơn cho phòng trống hoặc phòng chưa có người thuê.");
                    }

                    roomId = rs.getInt("room_id");
                    roomFee = rs.getBigDecimal("room_fee") != null ? rs.getBigDecimal("room_fee") : BigDecimal.ZERO;
                    electricityPrice = rs.getBigDecimal("electricity_price") != null ? rs.getBigDecimal("electricity_price") : BigDecimal.ZERO;
                    waterPrice = rs.getBigDecimal("water_price") != null ? rs.getBigDecimal("water_price") : BigDecimal.ZERO;
                    internetFee = rs.getBigDecimal("internet_fee") != null ? rs.getBigDecimal("internet_fee") : BigDecimal.ZERO;
                    serviceFee = rs.getBigDecimal("service_fee") != null ? rs.getBigDecimal("service_fee") : BigDecimal.ZERO;
                } else {
                    throw new IllegalArgumentException("Phòng không tồn tại hoặc bạn không có quyền quản lý phòng này.");
                }
            }
        }

        String year = billingPeriod.substring(0, 4);
        String month = billingPeriod.substring(4, 6);
        int meterId = 0;
        int newElectric = 0;
        int newWater = 0;
        LocalDate readingDate = null;
        
        String meterSql = "SELECT meter_id, electric, water, reading_date FROM meter_readings " +
                          "WHERE room_id = ? AND YEAR(reading_date) = ? AND MONTH(reading_date) = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(meterSql)) {
            ps.setInt(1, roomId);
            ps.setInt(2, Integer.parseInt(year));
            ps.setInt(3, Integer.parseInt(month));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    meterId = rs.getInt("meter_id");
                    newElectric = rs.getInt("electric");
                    newWater = rs.getInt("water");
                    readingDate = rs.getDate("reading_date").toLocalDate();
                    if (dueDate.isBefore(readingDate)) {
                        throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày chốt số điện nước của kỳ hạn này (" + readingDate + ").");
                    }
                } else {
                    throw new IllegalArgumentException("Không tìm thấy chỉ số điện nước của phòng trong kỳ hạn " + billingPeriod);
                }
            }
        }

        String invoiceCode = "INV-" + roomCode + "-" + billingPeriod;
        String checkCodeSql = "SELECT invoice_id FROM invoices WHERE code = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkCodeSql)) {
            ps.setString(1, invoiceCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new IllegalArgumentException("Phòng đã có hóa đơn trong kỳ hạn này (" + invoiceCode + ").");
                }
            }
        }

        int oldElectric = 0;
        int oldWater = 0;
        String oldMeterSql = "SELECT TOP 1 electric, water FROM meter_readings " +
                             "WHERE room_id = ? AND reading_date < ? AND deleted_at IS NULL ORDER BY reading_date DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(oldMeterSql)) {
            ps.setInt(1, roomId);
            ps.setDate(2, java.sql.Date.valueOf(readingDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    oldElectric = rs.getInt("electric");
                    oldWater = rs.getInt("water");
                }
            }
        }

        if (newElectric < oldElectric) throw new IllegalArgumentException("Chỉ số điện mới nhỏ hơn chỉ số điện cũ.");
        if (newWater < oldWater) throw new IllegalArgumentException("Chỉ số nước mới nhỏ hơn chỉ số nước cũ.");

        int electricUsage = newElectric - oldElectric;
        int waterUsage = newWater - oldWater;

        BigDecimal electricAmount = electricityPrice.multiply(new BigDecimal(electricUsage));
        BigDecimal waterAmount = waterPrice.multiply(new BigDecimal(waterUsage));
        
        BigDecimal subtotal = roomFee.add(electricAmount).add(waterAmount).add(serviceFee).add(internetFee).add(otherFee);
        BigDecimal taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"));
        BigDecimal totalAmount = subtotal.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setCode(invoiceCode);
        invoice.setRoomId(roomId);
        invoice.setMeterId(meterId);
        invoice.setDueDate(dueDate);
        invoice.setStatus("UNPAID");
        invoice.setTax(taxRate);
        invoice.setOtherFee(otherFee);
        invoice.setRoomFee(roomFee);
        invoice.setElectricityPrice(electricityPrice);
        invoice.setWaterPrice(waterPrice);
        invoice.setInternetFee(internetFee);
        invoice.setServiceFee(serviceFee);
        invoice.setTotalAmount(totalAmount);
        invoice.setNote(note);
        invoice.setCreatedBy(createdBy);

        invoiceDAO.insert(invoice);
        
        try {
            AuditLogHelper.log(auditLogDAO, null, "invoices", invoice.getInvoiceId(), "CREATE", null, invoiceCode, createdBy);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateInvoice(int managerId, int invoiceId, String dueDateStr, String taxRateStr, String otherFeeStr, String note) throws Exception {
        InvoiceDetailDTO dto = getInvoiceDetail(managerId, invoiceId);
        if ("PAID".equalsIgnoreCase(dto.getStatus())) {
            throw new IllegalArgumentException("Không được chỉnh sửa hóa đơn đã thanh toán.");
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Hạn thanh toán không hợp lệ.");
        }

        BigDecimal taxRate = new BigDecimal(taxRateStr != null && !taxRateStr.isEmpty() ? taxRateStr : "0");
        if (taxRate.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Thuế không được nhỏ hơn 0.");

        BigDecimal otherFee = new BigDecimal(otherFeeStr != null && !otherFeeStr.isEmpty() ? otherFeeStr : "0");
        if (otherFee.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Phí khác không được nhỏ hơn 0.");

        String snapshotSql = "SELECT i.room_fee, i.electricity_price, i.water_price, i.internet_fee, i.service_fee, i.due_date, mr.reading_date " +
                             "FROM invoices i LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id WHERE i.invoice_id = ?";
        BigDecimal snapRoom = BigDecimal.ZERO, snapElec = BigDecimal.ZERO, snapWater = BigDecimal.ZERO, snapInt = BigDecimal.ZERO, snapSvc = BigDecimal.ZERO;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(snapshotSql)) {
             ps.setInt(1, invoiceId);
             try (ResultSet rs = ps.executeQuery()) {
                 if (rs.next()) {
                     snapRoom = rs.getBigDecimal("room_fee") != null ? rs.getBigDecimal("room_fee") : BigDecimal.ZERO;
                     snapElec = rs.getBigDecimal("electricity_price") != null ? rs.getBigDecimal("electricity_price") : BigDecimal.ZERO;
                     snapWater = rs.getBigDecimal("water_price") != null ? rs.getBigDecimal("water_price") : BigDecimal.ZERO;
                     snapInt = rs.getBigDecimal("internet_fee") != null ? rs.getBigDecimal("internet_fee") : BigDecimal.ZERO;
                     snapSvc = rs.getBigDecimal("service_fee") != null ? rs.getBigDecimal("service_fee") : BigDecimal.ZERO;

                     java.sql.Date existingDue = rs.getDate("due_date");
                     java.sql.Date rdDate = rs.getDate("reading_date");
                     LocalDate readingDate = rdDate != null ? rdDate.toLocalDate() : null;

                     if (readingDate != null && dueDate.isBefore(readingDate)) {
                         throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày chốt số điện nước (" + readingDate + ").");
                     }

                     if (existingDue != null) {
                         LocalDate existingDueLocalDate = existingDue.toLocalDate();
                         if (!dueDate.equals(existingDueLocalDate) && dueDate.isBefore(LocalDate.now())) {
                             throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày hiện tại.");
                         }
                     } else if (dueDate.isBefore(LocalDate.now())) {
                         throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày hiện tại.");
                     }
                 }
             }
        }
        
        BigDecimal electricAmount = snapElec.multiply(new BigDecimal(dto.getElectricUsage()));
        BigDecimal waterAmount = snapWater.multiply(new BigDecimal(dto.getWaterUsage()));
        
        BigDecimal newSubtotal = snapRoom.add(electricAmount).add(waterAmount).add(snapInt).add(snapSvc).add(otherFee);
        BigDecimal newTaxAmount = newSubtotal.multiply(taxRate).divide(new BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal newTotal = newSubtotal.add(newTaxAmount);
        
        Invoice invoiceToUpdate = new Invoice();
        invoiceToUpdate.setInvoiceId(invoiceId);
        invoiceToUpdate.setDueDate(dueDate);
        invoiceToUpdate.setTax(taxRate);
        invoiceToUpdate.setOtherFee(otherFee);
        invoiceToUpdate.setTotalAmount(newTotal);
        invoiceToUpdate.setNote(note);

        invoiceDAO.update(invoiceToUpdate);
        
        try {
            AuditLogHelper.log(auditLogDAO, null, "invoices", invoiceId, "UPDATE", "Old Total: " + dto.getTotalAmount(), "New Total: " + newTotal, managerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateStatus(int managerId, int invoiceId, String status) throws Exception {
        InvoiceDetailDTO dto = getInvoiceDetail(managerId, invoiceId);
        if (!"UNPAID".equals(status) && !"PAID".equals(status) && !"OVERDUE".equals(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }
        invoiceDAO.updateStatus(invoiceId, status);
        
        try {
            AuditLogHelper.log(auditLogDAO, null, "invoices", invoiceId, "UPDATE", dto.getStatus(), status, managerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteInvoice(int managerId, int invoiceId) throws Exception {
        InvoiceDetailDTO dto = getInvoiceDetail(managerId, invoiceId);
        if ("PAID".equalsIgnoreCase(dto.getStatus())) {
            throw new IllegalArgumentException("Không thể xóa hóa đơn đã thanh toán.");
        }

        Integer meterId = null;
        String checkMeterSql = "SELECT meter_id FROM invoices WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkMeterSql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int mId = rs.getInt("meter_id");
                    if (!rs.wasNull()) {
                        meterId = mId;
                    }
                }
            }
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Soft-delete the invoice
            String deleteInvoiceSql = "UPDATE invoices SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE invoice_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteInvoiceSql)) {
                ps.setInt(1, invoiceId);
                ps.executeUpdate();
            }

            // 2. If associated meter reading status is 'INCORRECT' or 'REPORTED', soft-delete the meter reading as well
            if (meterId != null) {
                String checkMeterStatusSql = "SELECT status FROM meter_readings WHERE meter_id = ? AND deleted_at IS NULL";
                String meterStatus = null;
                try (PreparedStatement ps = conn.prepareStatement(checkMeterStatusSql)) {
                    ps.setInt(1, meterId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            meterStatus = rs.getString("status");
                        }
                    }
                }

                if ("INCORRECT".equals(meterStatus) || "REPORTED".equals(meterStatus)) {
                    String deleteMeterSql = "UPDATE meter_readings SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE meter_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(deleteMeterSql)) {
                        ps.setInt(1, meterId);
                        ps.executeUpdate();
                    }
                }
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (java.sql.SQLException ignored) {}
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (java.sql.SQLException ignored) {}
            }
        }

        try {
            AuditLogHelper.log(auditLogDAO, null, "invoices", invoiceId, "DELETE", dto.getStatus(), "Soft Deleted", managerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
