package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.DashboardService;
import com.quanlyphongtro.service.impl.DashboardServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

import com.quanlyphongtro.service.ContractService;
import com.quanlyphongtro.service.impl.ContractServiceImpl;

@WebServlet(name = "ManagerDashboardServlet", urlPatterns = "/manager/dashboard")
public class ManagerDashboardServlet extends BaseServlet {

    private final DashboardService dashboardService = new DashboardServiceImpl();
    private final ContractService contractService = new ContractServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (!"MANAGER".equals(currentUser.getRole()) && !"ADMIN".equals(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        int managerId = currentUser.getId();
        try {
            Map<String, Object> stats = dashboardService.getManagerDashboardStats(managerId);

            if (stats != null) {
                req.setAttribute("facilityName",     stats.get("facilityName"));
                req.setAttribute("facilityCode",     stats.get("facilityCode"));
                req.setAttribute("facilityStatus",   stats.get("facilityStatus"));

                req.setAttribute("totalRooms",       stats.get("totalRooms"));
                req.setAttribute("occupiedRooms",    stats.get("occupiedRooms"));
                req.setAttribute("vacantRooms",      stats.get("vacantRooms"));
                req.setAttribute("totalTenants",     stats.get("totalTenants"));
                req.setAttribute("totalDependents",  stats.get("totalDependents"));
                req.setAttribute("pendingTickets",   stats.get("pendingTickets"));
                req.setAttribute("sentNotifications",stats.get("sentNotifications"));
                req.setAttribute("occupancyRate",    stats.get("occupancyRate"));

                req.setAttribute("activeContracts",  stats.get("activeContracts"));
                req.setAttribute("unpaidInvoices",   stats.get("unpaidInvoices"));
                req.setAttribute("overdueInvoices",  stats.get("overdueInvoices"));
                req.setAttribute("pendingPayments",  stats.get("pendingPayments"));
                req.setAttribute("monthlyRevenue",   stats.get("monthlyRevenue"));
                req.setAttribute("totalOutstanding", stats.get("totalOutstanding"));

                req.setAttribute("ticketCountNew",   stats.get("ticketCountNew"));
                req.setAttribute("ticketCountInProgress", stats.get("ticketCountInProgress"));
                req.setAttribute("ticketCountDone",   stats.get("ticketCountDone"));
                req.setAttribute("ticketCountRejected", stats.get("ticketCountRejected"));

                req.setAttribute("recentTickets",    stats.get("recentTickets"));
            }

            int expiringContractsCount = 0;
            try {
                expiringContractsCount = contractService.countExpiringContracts(managerId);
            } catch (Exception e) {
                logger.error("Failed to count expiring contracts for managerId=" + managerId, e);
            }
            req.setAttribute("expiringContractsCount", expiringContractsCount);

            req.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Error loading Manager Dashboard for managerId=" + managerId, e);
            req.setAttribute("errorMessage", "Không thể tải dữ liệu trang Dashboard. Vui lòng thử lại sau.");
            req.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(req, resp);
        }
    }
}
