package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "ManagerDashboardServlet", urlPatterns = "/manager/dashboard")
public class ManagerDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setAttribute("facilityName", "Cơ sở được phân công");
        req.setAttribute("facilityCode", "—");

        // KPI — placeholder
        req.setAttribute("totalRooms",         8);
        req.setAttribute("occupiedRooms",       5);
        req.setAttribute("vacantRooms",         2);
        req.setAttribute("totalTenants",        5);
        req.setAttribute("totalDependents",     3);
        req.setAttribute("pendingTickets",      1);
        req.setAttribute("sentNotifications",   3);
        req.setAttribute("occupancyRate",       63);

        // Ticket stats
        req.setAttribute("ticketCountNew",        1);
        req.setAttribute("ticketCountInProgress", 1);
        req.setAttribute("ticketCountDone",       1);
        req.setAttribute("ticketCountRejected",   1);

        req.setAttribute("recentTickets", java.util.Collections.emptyList());

        req.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(req, resp);
    }
}
