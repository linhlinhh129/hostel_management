package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.DebtDAO;
import com.quanlyphongtro.dto.DebtListItemDTO;
import com.quanlyphongtro.dto.DebtDetailDTO;
import com.quanlyphongtro.service.DebtService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class DebtServiceImpl implements DebtService {
    
    private final DebtDAO debtDAO = new DebtDAO();

    @Override
    public List<DebtListItemDTO> getDebts(int managerId, String keyword, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<DebtListItemDTO> list = debtDAO.findDebts(managerId, keyword, status, offset, pageSize);
        
        LocalDate today = LocalDate.now();
        
        for (DebtListItemDTO dto : list) {
            // Calculate debt amount = MAX(0, totalAmount - paidAmount)
            BigDecimal totalAmount = dto.getInvoiceTotalAmount() != null ? dto.getInvoiceTotalAmount() : BigDecimal.ZERO;
            BigDecimal paidAmount = dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal debtAmount = totalAmount.subtract(paidAmount);
            if (debtAmount.compareTo(BigDecimal.ZERO) < 0) {
                debtAmount = BigDecimal.ZERO;
            }
            dto.setDebtAmount(debtAmount);
            
            if (dto.getDueDate() != null) {
                if (today.isAfter(dto.getDueDate())) {
                    if ("UNPAID".equals(dto.getStatus())) {
                        dto.setStatus("OVERDUE");
                    }
                } else if (!today.isAfter(dto.getDueDate())) {
                    if ("OVERDUE".equals(dto.getStatus())) {
                        dto.setStatus("UNPAID");
                    }
                }
            }
        }
        
        return list;
    }

    @Override
    public int getTotalPages(int managerId, String keyword, String status, int pageSize) {
        int totalRows = debtDAO.countDebts(managerId, keyword, status);
        return (int) Math.ceil((double) totalRows / pageSize);
    }

    @Override
    public Optional<DebtDetailDTO> getDebtDetail(int managerId, int invoiceId) {
        Optional<DebtDetailDTO> opt = debtDAO.findDebtDetail(managerId, invoiceId);
        if (opt.isPresent()) {
            DebtDetailDTO dto = opt.get();
            LocalDate today = LocalDate.now();
            
            // Calculate debt amount
            BigDecimal totalAmount = dto.getInvoiceTotalAmount() != null ? dto.getInvoiceTotalAmount() : BigDecimal.ZERO;
            BigDecimal paidAmount = dto.getPaidAmount() != null ? dto.getPaidAmount() : BigDecimal.ZERO;
            BigDecimal debtAmount = totalAmount.subtract(paidAmount);
            if (debtAmount.compareTo(BigDecimal.ZERO) < 0) {
                debtAmount = BigDecimal.ZERO;
            }
            dto.setDebtAmount(debtAmount);
            
            if (dto.getDueDate() != null) {
                if (today.isAfter(dto.getDueDate())) {
                    if ("UNPAID".equals(dto.getStatus())) {
                        dto.setStatus("OVERDUE");
                    }
                } else if (!today.isAfter(dto.getDueDate())) {
                    if ("OVERDUE".equals(dto.getStatus())) {
                        dto.setStatus("UNPAID");
                    }
                }
            }
        }
        return opt;
    }

    @Override
    public void sendRemindNotification(int managerId, int invoiceId) throws Exception {
        Optional<DebtDetailDTO> optDebt = getDebtDetail(managerId, invoiceId);
        if (optDebt.isEmpty()) {
            throw new Exception("Không tìm thấy công nợ hoặc không thuộc quyền quản lý");
        }
        DebtDetailDTO debt = optDebt.get();
        if ("PAID".equals(debt.getStatus())) {
            throw new Exception("Hóa đơn này đã được thanh toán");
        }
        
        com.quanlyphongtro.dao.NotificationDAO notificationDAO = new com.quanlyphongtro.dao.NotificationDAO();
        String title = "Nhắc nhở thanh toán hóa đơn " + debt.getInvoiceCode();
        String content = String.format("Bạn có một hóa đơn chưa thanh toán. Kỳ: %s, Hạn thanh toán: %s, Số tiền cần thanh toán: %,.0f đ. Vui lòng thanh toán sớm để tránh bị phạt phí trả chậm.",
            debt.getBillingPeriod(), debt.getDueDate(), debt.getDebtAmount());
            
        notificationDAO.insertNotificationAndGetId(
            "INVOICE_REMINDER", title, content, "ROOM", debt.getFacilityId(), debt.getRoomId(), managerId
        );
    }
}
