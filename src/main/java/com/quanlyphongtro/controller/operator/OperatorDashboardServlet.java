package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.controller.BaseServlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@WebServlet(name = "OperatorDashboardServlet", urlPatterns = "/operator/dashboard")
public class OperatorDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String period = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MM/yyyy", new Locale("vi")));
        req.setAttribute("billingPeriodLabel", period);
        req.setAttribute("facilityCode", "—");
        req.setAttribute("facilityName", "Cơ sở được phân công");

        // KPI — placeholder
        req.setAttribute("totalRooms",         8);
        req.setAttribute("updatedMeterRooms",   6);
        req.setAttribute("pendingMeterRooms",   2);
        req.setAttribute("pendingTickets",      1);
        req.setAttribute("meterUpdateProgress", 75);

        // Ticket stats
        req.setAttribute("ticketCountNew",        1);
        req.setAttribute("ticketCountInProgress", 1);
        req.setAttribute("ticketCountDone",       1);

        req.setAttribute("pendingMeterRoomList", java.util.Collections.emptyList());

        req.getRequestDispatcher("/WEB-INF/views/operator/dashboard.jsp").forward(req, resp);
    }
}
