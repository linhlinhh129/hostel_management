package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PaymentListItemDTO;
import com.quanlyphongtro.dto.PaymentDetailDTO;

import java.util.List;

public interface PaymentService {
    List<PaymentListItemDTO> findPayments(int managerId, String keyword, String status, int offset, int limit);
    int countPayments(int managerId, String keyword, String status);
    PaymentDetailDTO findById(int managerId, int paymentId);
    void approvePayment(int paymentId, int approvedBy);
    void rejectPayment(int paymentId, int rejectedBy);
}
