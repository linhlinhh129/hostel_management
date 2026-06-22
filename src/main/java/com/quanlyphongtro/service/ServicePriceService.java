package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.ServicePriceDTO;
import com.quanlyphongtro.dto.ServicePriceHistoryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface ServicePriceService {
    List<ServicePriceDTO> getCurrentPrices(int managerId);
    boolean updatePrice(int managerId, String priceType, BigDecimal newPrice, String note);
    List<ServicePriceHistoryDTO> getPriceHistory(int managerId, String priceType, int page, int size);
}
