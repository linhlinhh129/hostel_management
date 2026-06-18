package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "TenantDashboardServlet", urlPatterns = "/tenant/dashboard")
public class TenantDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Thông tin phòng — placeholder
        req.setAttribute("roomCode",    "—");
        req.setAttribute("facilityName","Cơ sở của bạn");

        // KPI — placeholder
        req.setAttribute("unpaidAmount",        0);
        req.setAttribute("dueDateLabel",        null);
        req.setAttribute("unreadNotifications", 0);
        req.setAttribute("pendingTickets",      0);

        req.setAttribute("latestNotifications", java.util.Collections.emptyList());
        req.setAttribute("currentInvoice",      null);

        req.getRequestDispatcher("/WEB-INF/views/tenant/dashboard.jsp").forward(req, resp);
    }
}
