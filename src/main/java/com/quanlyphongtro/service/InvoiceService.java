package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.model.Invoice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    // Methods for Tenant/Room
    List<Invoice> getInvoicesByRoomId(int roomId);
    Optional<Invoice> getInvoiceById(int id, int roomId);
    BigDecimal getUnpaidTotal(int roomId);
    Optional<Invoice> getCurrentInvoice(int roomId);
    
    // Methods for Manager
    List<InvoiceListItemDTO> getInvoices(int managerId, String keyword, String status, String billingPeriod, int page, int size);
    int countInvoices(int managerId, String keyword, String status, String billingPeriod);
    InvoiceDetailDTO getInvoiceDetail(int managerId, int invoiceId);
    
    void createInvoice(int managerId, String roomCode, String billingPeriod, String dueDate, String taxRate, String otherFee, String note, int createdBy) throws Exception;
    void updateInvoice(int managerId, int invoiceId, String dueDateStr, String taxRateStr, String otherFeeStr, String note) throws Exception;
    void updateStatus(int managerId, int invoiceId, String status) throws Exception;
}
