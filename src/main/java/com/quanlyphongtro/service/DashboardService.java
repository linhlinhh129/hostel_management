package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.DashboardDAO;
import com.quanlyphongtro.dto.DashboardSummaryDTO;

public class DashboardService {
    private DashboardDAO dashboardDAO;

    public DashboardService() {
        this.dashboardDAO = new DashboardDAO();
    }

    public DashboardSummaryDTO getDashboardSummary(int operatorId) {
        DashboardSummaryDTO summary = new DashboardSummaryDTO();
        
        summary.setPendingRequestsCount(dashboardDAO.getPendingRequestsCount(operatorId));
        summary.setInProgressRequestsCount(dashboardDAO.getInProgressRequestsCount(operatorId));
        summary.setIncidentsReportedCount(dashboardDAO.getIncidentsReportedCount(operatorId));
        summary.setTotalRooms(dashboardDAO.getTotalRooms());
        summary.setUpdatedMeterReadingsCount(dashboardDAO.getUpdatedMeterReadingsCount());
        summary.setTodaysAppointments(dashboardDAO.getTodaysAppointments(operatorId));
        
        return summary;
    }
}
