package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Kỳ hiện tại
        LocalDate now = LocalDate.now();
        String period = now.format(DateTimeFormatter.ofPattern("MM/yyyy", new Locale("vi")));
        req.setAttribute("currentPeriodLabel", period);

        // KPI — placeholder (sẽ thay bằng DB query khi implement từng module)
        req.setAttribute("monthlyRevenue",     0);
        req.setAttribute("totalFacilities",    2);
        req.setAttribute("activeFacilities",   2);
        req.setAttribute("totalPersonnel",     4);
        req.setAttribute("managerCount",       2);
        req.setAttribute("operatorCount",      2);
        req.setAttribute("totalNotifications", 4);
        req.setAttribute("todayAuditLogs",     3);

        // Widgets — empty lists (dashboard vẫn render được với empty-state)
        req.setAttribute("facilityRevenueStats", java.util.Collections.emptyList());
        req.setAttribute("recentActivities",     java.util.Collections.emptyList());

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }
}
