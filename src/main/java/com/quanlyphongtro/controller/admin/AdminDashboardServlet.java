package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.service.AdminDashboardService;
import com.quanlyphongtro.service.impl.AdminDashboardServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    private final AdminDashboardService dashboardService = new AdminDashboardServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            LocalDate now = LocalDate.now();
            String currentPeriod = String.format("%02d/%d", now.getMonthValue(), now.getYear());
    
            req.setAttribute("currentPeriodLabel",   currentPeriod);
            req.setAttribute("monthlyRevenue",        dashboardService.getMonthlyRevenue(currentPeriod));
            req.setAttribute("totalFacilities",       dashboardService.getTotalFacilities());
            req.setAttribute("activeFacilities",      dashboardService.getActiveFacilities());
            req.setAttribute("totalPersonnel",        dashboardService.getTotalPersonnel());
            req.setAttribute("managerCount",          dashboardService.getManagerCount());
            req.setAttribute("operatorCount",         dashboardService.getOperatorCount());
            req.setAttribute("totalNotifications",    dashboardService.getTotalNotifications());
            req.setAttribute("todayAuditLogs",        dashboardService.getTodayAuditLogs());
            req.setAttribute("facilityRevenueStats",  dashboardService.getFacilityRevenueStats(currentPeriod));
            req.setAttribute("recentActivities",      dashboardService.getRecentActivities());
    
            req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            System.err.println("Unexpected error in AdminDashboardServlet: " + e.getMessage());
            e.printStackTrace();
            handleException(req, resp, e);
        }
    }
}
