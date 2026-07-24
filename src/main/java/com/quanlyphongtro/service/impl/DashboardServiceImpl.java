package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.ManagerDashboardDAO;
import com.quanlyphongtro.service.DashboardService;

import java.util.Map;

public class DashboardServiceImpl implements DashboardService {
    private final ManagerDashboardDAO dashboardDAO = new ManagerDashboardDAO();

    @Override
    public Map<String, Object> getManagerDashboardStats(int managerId) {
        return dashboardDAO.getManagerDashboardStats(managerId);
    }
}
