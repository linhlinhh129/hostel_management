package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Invoice;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<Invoice> getInvoicesByRoomId(int roomId);
    Optional<Invoice> getInvoiceById(int id, int roomId);
    BigDecimal getUnpaidTotal(int roomId);
    Optional<Invoice> getCurrentInvoice(int roomId);
}
