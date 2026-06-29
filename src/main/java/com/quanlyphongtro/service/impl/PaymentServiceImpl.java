package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.PaymentDAO;
import com.quanlyphongtro.dto.PaymentDetailDTO;
import com.quanlyphongtro.dto.PaymentListItemDTO;
import com.quanlyphongtro.service.PaymentService;

import java.sql.SQLException;
import java.util.List;

public class PaymentServiceImpl implements PaymentService {
    
    private final PaymentDAO paymentDAO = new PaymentDAO();

    @Override
    public List<PaymentListItemDTO> findPayments(int managerId, String keyword, String status, String fromDate, String toDate, String month, String year, int offset, int limit) {
        return paymentDAO.findPayments(managerId, keyword, status, fromDate, toDate, month, year, offset, limit);
    }

    @Override
    public int countPayments(int managerId, String keyword, String status, String fromDate, String toDate, String month, String year) {
        return paymentDAO.countPayments(managerId, keyword, status, fromDate, toDate, month, year);
    }

    @Override
    public PaymentDetailDTO findById(int managerId, int paymentId) {
        return paymentDAO.findById(managerId, paymentId);
    }

    @Override
    public void approvePayment(int paymentId, int approvedBy) {
        try {
            paymentDAO.approvePayment(paymentId, approvedBy);
        } catch (SQLException e) {
            throw new RuntimeException("Error approving payment: " + e.getMessage(), e);
        }
    }

    @Override
    public void rejectPayment(int paymentId, int rejectedBy) {
        try {
            paymentDAO.rejectPayment(paymentId, rejectedBy);
        } catch (SQLException e) {
            throw new RuntimeException("Error rejecting payment: " + e.getMessage(), e);
        }
    }
}
