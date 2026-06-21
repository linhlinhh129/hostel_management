package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.InvoiceDAO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.service.InvoiceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    public List<Invoice> getInvoicesByRoomId(int roomId) {
        return invoiceDAO.findByRoomId(roomId);
    }

    @Override
    public Optional<Invoice> getInvoiceById(int id, int roomId) {
        return invoiceDAO.findByIdAndRoomId(id, roomId);
    }

    @Override
    public BigDecimal getUnpaidTotal(int roomId) {
        return invoiceDAO.getUnpaidTotalByRoomId(roomId);
    }

    @Override
    public Optional<Invoice> getCurrentInvoice(int roomId) {
        return invoiceDAO.getCurrentInvoiceByRoomId(roomId);
    }
}
