package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.InvoiceDAO;
import com.quanlyphongtro.dao.MeterReadingDAO;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.model.MeterReading;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.util.AuditLogHelper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InvoiceServiceImpl implements InvoiceService {
    private final InvoiceDAO invoiceDAO = new InvoiceDAO();
    private final MeterReadingDAO meterReadingDAO = new MeterReadingDAO();
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

        BigDecimal manualOtherFee = new BigDecimal(otherFeeStr != null && !otherFeeStr.isEmpty() ? otherFeeStr : "0");
        if (manualOtherFee.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Phí khác không được nhỏ hơn 0.");

        // Cộng tiền nợ cũ (chưa thanh toán) của phòng vào phí khác
        BigDecimal previousDebt = invoiceDAO.getUnpaidDebtByRoomCode(roomCode, managerId);
        BigDecimal otherFee = manualOtherFee.add(previousDebt != null ? previousDebt : BigDecimal.ZERO);

        InvoiceDAO.InvoiceRoomSnapshot roomSnap = invoiceDAO.getRoomSnapshotForInvoice(roomCode, managerId);
        if (roomSnap == null) {
            throw new IllegalArgumentException("Phòng không tồn tại hoặc bạn không có quyền quản lý phòng này.");
        }
        if (!"OCCUPIED".equals(roomSnap.status) || !roomSnap.hasTenant) {
            throw new IllegalArgumentException("Không thể tạo hóa đơn cho phòng trống hoặc phòng chưa có người thuê.");
        }

        String year = billingPeriod.substring(0, 4);
        String month = billingPeriod.substring(4, 6);
        
        MeterReading currentMeter = meterReadingDAO.getReadingByMonth(roomSnap.roomId, Integer.parseInt(year), Integer.parseInt(month));
        if (currentMeter == null) {
            throw new IllegalArgumentException("Không tìm thấy chỉ số điện nước của phòng trong kỳ hạn " + billingPeriod);
        }
        if (dueDate.isBefore(currentMeter.getReadingDate())) {
            throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày chốt số điện nước của kỳ hạn này (" + currentMeter.getReadingDate() + ").");
        }

        String invoiceCode = "INV-" + roomCode + "-" + billingPeriod;
        if (invoiceDAO.checkInvoiceCodeExists(invoiceCode)) {
            throw new IllegalArgumentException("Phòng đã có hóa đơn trong kỳ hạn này (" + invoiceCode + ").");
        }

        MeterReading oldMeter = meterReadingDAO.getPreviousReadingByDate(roomSnap.roomId, currentMeter.getReadingDate());
        int oldElectric = oldMeter != null ? oldMeter.getElectric() : 0;
        int oldWater = oldMeter != null ? oldMeter.getWater() : 0;

        if (currentMeter.getElectric() < oldElectric) throw new IllegalArgumentException("Chỉ số điện mới nhỏ hơn chỉ số điện cũ.");
        if (currentMeter.getWater() < oldWater) throw new IllegalArgumentException("Chỉ số nước mới nhỏ hơn chỉ số nước cũ.");

        int electricUsage = currentMeter.getElectric() - oldElectric;
        int waterUsage = currentMeter.getWater() - oldWater;

        BigDecimal electricAmount = roomSnap.electricityPrice.multiply(new BigDecimal(electricUsage));
        BigDecimal waterAmount = roomSnap.waterPrice.multiply(new BigDecimal(waterUsage));

        BigDecimal subtotal = roomSnap.roomFee.add(electricAmount).add(waterAmount).add(roomSnap.serviceFee).add(roomSnap.internetFee).add(otherFee);
        BigDecimal taxAmount = subtotal.multiply(taxRate).divide(new BigDecimal("100"));
        BigDecimal totalAmount = subtotal.add(taxAmount);

        Invoice invoice = new Invoice();
        invoice.setCode(invoiceCode);
        invoice.setRoomId(roomSnap.roomId);
        invoice.setMeterId(currentMeter.getMeterId());
        invoice.setDueDate(dueDate);
        invoice.setStatus("UNPAID");
        invoice.setTax(taxRate);
        invoice.setOtherFee(otherFee);
        invoice.setRoomFee(roomSnap.roomFee);
        invoice.setElectricityPrice(roomSnap.electricityPrice);
        invoice.setWaterPrice(roomSnap.waterPrice);
        invoice.setInternetFee(roomSnap.internetFee);
        invoice.setServiceFee(roomSnap.serviceFee);
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

        InvoiceDAO.InvoicePriceSnapshot snap = invoiceDAO.getInvoicePriceSnapshot(invoiceId);
        if (snap != null) {
            if (snap.readingDate != null && dueDate.isBefore(snap.readingDate)) {
                throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày chốt số điện nước (" + snap.readingDate + ").");
            }
            if (snap.dueDate != null) {
                if (!dueDate.equals(snap.dueDate) && dueDate.isBefore(LocalDate.now())) {
                    throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày hiện tại.");
                }
            } else if (dueDate.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Hạn thanh toán không thể trước ngày hiện tại.");
            }
        }

        BigDecimal electricAmount = snap != null ? snap.electricityPrice.multiply(new BigDecimal(dto.getElectricUsage())) : BigDecimal.ZERO;
        BigDecimal waterAmount = snap != null ? snap.waterPrice.multiply(new BigDecimal(dto.getWaterUsage())) : BigDecimal.ZERO;

        BigDecimal snapRoom = snap != null ? snap.roomFee : BigDecimal.ZERO;
        BigDecimal snapInt = snap != null ? snap.internetFee : BigDecimal.ZERO;
        BigDecimal snapSvc = snap != null ? snap.serviceFee : BigDecimal.ZERO;

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

        Integer meterId = invoiceDAO.getMeterIdByInvoiceId(invoiceId);
        String meterStatus = null;
        if (meterId != null) {
            meterStatus = meterReadingDAO.getMeterStatus(meterId);
        }

        invoiceDAO.softDeleteInvoiceWithMeter(invoiceId, meterId, meterStatus);

        try {
            AuditLogHelper.log(auditLogDAO, null, "invoices", invoiceId, "DELETE", dto.getStatus(), "Soft Deleted", managerId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public BigDecimal getUnpaidDebtByRoomCode(String roomCode, int managerId) {
        return invoiceDAO.getUnpaidDebtByRoomCode(roomCode, managerId);
    }
}
