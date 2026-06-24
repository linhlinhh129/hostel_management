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
            
            // Calculate overdue days
            int overdueDays = 0;
            if (dto.getDueDate() != null && today.isAfter(dto.getDueDate())) {
                overdueDays = (int) ChronoUnit.DAYS.between(dto.getDueDate(), today);
                if ("UNPAID".equals(dto.getStatus())) {
                    dto.setStatus("OVERDUE");
                }
            }
            dto.setOverdueDays(overdueDays);
            
            // Calculate late fee preview
            BigDecimal lateFee = BigDecimal.ZERO;
            if ("OVERDUE".equals(dto.getStatus()) && overdueDays > 0) {
                BigDecimal roomFee = dto.getRoomFee() != null ? dto.getRoomFee() : BigDecimal.ZERO;
                lateFee = roomFee.multiply(new BigDecimal("0.01")).multiply(new BigDecimal(overdueDays));
            }
            dto.setLateFeePreview(lateFee);
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
            
            // Calculate overdue days
            int overdueDays = 0;
            if (dto.getDueDate() != null && today.isAfter(dto.getDueDate())) {
                overdueDays = (int) ChronoUnit.DAYS.between(dto.getDueDate(), today);
                if ("UNPAID".equals(dto.getStatus())) {
                    dto.setStatus("OVERDUE");
                }
            }
            dto.setOverdueDays(overdueDays);
            
            // Calculate late fee preview
            BigDecimal lateFee = BigDecimal.ZERO;
            if ("OVERDUE".equals(dto.getStatus()) && overdueDays > 0) {
                BigDecimal roomFee = dto.getRoomFee() != null ? dto.getRoomFee() : BigDecimal.ZERO;
                lateFee = roomFee.multiply(new BigDecimal("0.01")).multiply(new BigDecimal(overdueDays));
            }
            dto.setLateFeePreview(lateFee);
        }
        return opt;
    }
}
