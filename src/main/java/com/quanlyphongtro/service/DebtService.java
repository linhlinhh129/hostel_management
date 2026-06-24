package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.DebtListItemDTO;
import com.quanlyphongtro.dto.DebtDetailDTO;

import java.util.List;
import java.util.Optional;

public interface DebtService {
    List<DebtListItemDTO> getDebts(int managerId, String keyword, String status, int page, int pageSize);
    int getTotalPages(int managerId, String keyword, String status, int pageSize);
    Optional<DebtDetailDTO> getDebtDetail(int managerId, int invoiceId);
}
