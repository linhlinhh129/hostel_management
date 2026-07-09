package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.DashboardDAO;
import com.quanlyphongtro.service.DashboardService;

import java.util.Map;

public class DashboardServiceImpl implements DashboardService {
    private final DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    public Map<String, Object> getManagerDashboardStats(int managerId) {
        return dashboardDAO.getManagerDashboardStats(managerId);
    }
}
