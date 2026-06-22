package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.InvoiceDAO;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.List;

public class InvoiceServiceImpl implements InvoiceService {
    private InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    public List<InvoiceListItemDTO> getInvoices(int managerId, String keyword, String status, String billingPeriod, int page, int size) {
        int offset = (page - 1) * size;
        return invoiceDAO.findInvoices(managerId, keyword, status, billingPeriod, offset, size);
    }

    @Override
    public int countInvoices(int managerId, String keyword, String status, String billingPeriod) {
        return invoiceDAO.countInvoices(managerId, keyword, status, billingPeriod);
    }

    @Override
    public InvoiceDetailDTO getInvoiceDetail(int managerId, int invoiceId) {
        InvoiceDetailDTO dto = invoiceDAO.findById(managerId, invoiceId);
        if (dto == null) {
            throw new IllegalArgumentException("Hóa đơn không tồn tại hoặc bạn không có quyền xem.");
        }
        return dto;
    }

    @Override
    public void createInvoice(int managerId, String roomCode, String billingPeriod, String dueDateStr, String taxRateStr, String otherFeeStr, String note, int createdBy) throws Exception {
        if (roomCode == null || roomCode.isEmpty()) throw new IllegalArgumentException("Mã phòng không được để trống.");
        if (billingPeriod == null || billingPeriod.length() != 6) throw new IllegalArgumentException("Kỳ hóa đơn không hợp lệ. Format YYYYMM.");
        
        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateStr);
        } catch (Exception e) {
            throw new IllegalArgumentException("Hạn thanh toán không hợp lệ.");
        }
        if (dueDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Hạn thanh toán không được nhỏ hơn ngày hiện tại.");
        }

        BigDecimal taxRate = new BigDecimal(taxRateStr != null && !taxRateStr.isEmpty() ? taxRateStr : "0");
        if (taxRate.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Thuế không được nhỏ hơn 0.");

        BigDecimal otherFee = new BigDecimal(otherFeeStr != null && !otherFeeStr.isEmpty() ? otherFeeStr : "0");
        if (otherFee.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Phí khác không được nhỏ hơn 0.");

        int roomId = 0;
        BigDecimal roomFee = BigDecimal.ZERO;
        BigDecimal electricityPrice = BigDecimal.ZERO;
        BigDecimal waterPrice = BigDecimal.ZERO;
        BigDecimal serviceFee = BigDecimal.ZERO;
        BigDecimal internetFee = BigDecimal.ZERO;
        
        String roomSql = "SELECT r.room_id, r.room_fee, f.electricity_price, f.water_price, f.service_fee, f.internet_fee " +
                         "FROM rooms r INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                         "WHERE r.code = ? AND f.manager_id = ? AND r.deleted_at IS NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(roomSql)) {
            ps.setString(1, roomCode);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    roomId = rs.getInt("room_id");
                    roomFee = rs.getBigDecimal("room_fee") != null ? rs.getBigDecimal("room_fee") : BigDecimal.ZERO;
                    electricityPrice = rs.getBigDecimal("electricity_price") != null ? rs.getBigDecimal("electricity_price") : BigDecimal.ZERO;
                    waterPrice = rs.getBigDecimal("water_price") != null ? rs.getBigDecimal("water_price") : BigDecimal.ZERO;
                    serviceFee = rs.getBigDecimal("service_fee") != null ? rs.getBigDecimal("service_fee") : BigDecimal.ZERO;
                    internetFee = rs.getBigDecimal("internet_fee") != null ? rs.getBigDecimal("internet_fee") : BigDecimal.ZERO;
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

        // Re-query original values to calculate precise total
        String snapshotSql = "SELECT room_fee, electricity_price, water_price, internet_fee, service_fee FROM invoices WHERE invoice_id = ?";
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
    }

    @Override
    public void updateStatus(int managerId, int invoiceId, String status) throws Exception {
        // Verify existence and authorization
        InvoiceDetailDTO dto = getInvoiceDetail(managerId, invoiceId);
        
        // Allowed statuses: UNPAID, PAID, OVERDUE
        if (!"UNPAID".equals(status) && !"PAID".equals(status) && !"OVERDUE".equals(status)) {
            throw new IllegalArgumentException("Trạng thái không hợp lệ.");
        }

        invoiceDAO.updateStatus(invoiceId, status);
    }
}
