package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RevenueDAO;
import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.SystemRevenueDTO;
import com.quanlyphongtro.service.RevenueService;
import java.util.List;

public class RevenueServiceImpl implements RevenueService {

    private final RevenueDAO revenueDAO = new RevenueDAO();

    @Override
    public SystemRevenueDTO getSystemRevenue(String period) {
        return revenueDAO.getSystemRevenue(period);
    }

    @Override
    public List<FacilityRevenueStatDTO> getFacilityRevenues(String period) {
        return revenueDAO.getFacilityRevenues(period);
    }

    @Override
    public List<FacilityRevenueStatDTO> getFacilityRevenuesPaged(String period, int page, int pageSize) {
        return revenueDAO.getFacilityRevenuesPaged(period, page, pageSize);
    }

    @Override
    public int countFacilitiesWithRevenue(String period) {
        return revenueDAO.countFacilitiesWithRevenue(period);
    }

    @Override
    public List<FacilityRevenueStatDTO> getRevenueTrend(int months) {
        return revenueDAO.getRevenueTrend(months);
    }
}
