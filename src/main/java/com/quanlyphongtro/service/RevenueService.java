package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.SystemRevenueDTO;
import java.util.List;

public interface RevenueService {
    SystemRevenueDTO getSystemRevenue(String period);
    List<FacilityRevenueStatDTO> getFacilityRevenues(String period);
    List<FacilityRevenueStatDTO> getFacilityRevenuesPaged(String period, int page, int pageSize);
    int countFacilitiesWithRevenue(String period);
    List<FacilityRevenueStatDTO> getRevenueTrend(int months);
}
