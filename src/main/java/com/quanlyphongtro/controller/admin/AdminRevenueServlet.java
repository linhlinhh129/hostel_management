package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.SystemRevenueDTO;
import com.quanlyphongtro.service.RevenueService;
import com.quanlyphongtro.service.impl.RevenueServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@WebServlet(name = "AdminRevenueServlet",
        urlPatterns = {"/admin/revenue", "/admin/revenue/*"})
public class AdminRevenueServlet extends BaseServlet {

    private final RevenueService revenueService = new RevenueServiceImpl();

    private static final int PAGE_SIZE = 10;
    private static final String BASE_PATH = "/admin/revenue";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/revenue/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        try {
            if (path == null || path.isEmpty() || path.equals("/")) {
                showIndex(req, resp);
            } else if (path.equals("/by-facility")) {
                showByFacility(req, resp);
            } else if (path.equals("/by-period")) {
                showByPeriod(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("AdminRevenueServlet doGet error", e);
            handleException(req, resp, e);
            req.getRequestDispatcher(VIEW_BASE + "index.jsp").forward(req, resp);
        }
    }

    // ─── GET handlers ────────────────────────────────────────────────────────

    private void showIndex(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String selectedPeriod = resolvePeriod(req.getParameter("period"));

        SystemRevenueDTO systemRevenue = revenueService.getSystemRevenue(selectedPeriod);
        List<FacilityRevenueStatDTO> facilityRevenues = revenueService.getFacilityRevenues(selectedPeriod);
        List<FacilityRevenueStatDTO> periodRevenues = revenueService.getRevenueTrend(6);

        req.setAttribute("systemRevenue",    systemRevenue);
        req.setAttribute("facilityRevenues", facilityRevenues);
        req.setAttribute("periodRevenues",   periodRevenues);
        req.setAttribute("selectedPeriod",   selectedPeriod);
        req.getRequestDispatcher(VIEW_BASE + "index.jsp").forward(req, resp);
    }

    private void showByFacility(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String selectedPeriod = resolvePeriod(req.getParameter("period"));
        int page = parseIntOrDefault(req.getParameter("page"), 1);

        int total = revenueService.countActiveFacilities();
        List<FacilityRevenueStatDTO> items =
            revenueService.getFacilityRevenuesPaged(selectedPeriod, page, PAGE_SIZE);

        req.setAttribute("page", new PageDTO<>(items, page, PAGE_SIZE, total));
        req.setAttribute("facilityRevenues", items);
        req.setAttribute("selectedPeriod", selectedPeriod);
        req.getRequestDispatcher(VIEW_BASE + "by-facility.jsp").forward(req, resp);
    }

    private void showByPeriod(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int selectedMonths = parseIntOrDefault(req.getParameter("months"), 12);
        List<FacilityRevenueStatDTO> revenueTrend = revenueService.getRevenueTrend(selectedMonths);

        req.setAttribute("periodRevenues",  revenueTrend);
        req.setAttribute("selectedMonths",  selectedMonths);
        req.getRequestDispatcher(VIEW_BASE + "by-period.jsp").forward(req, resp);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Parses the period parameter from either:
     *  - HTML input[type=month] format "YYYY-MM"
     *  - Display format "MM/yyyy"
     * Returns "MM/yyyy" format for DAO consumption.
     */
    private String resolvePeriod(String raw) {
        if (raw == null || raw.isBlank()) {
            LocalDate now = LocalDate.now();
            return String.format("%02d/%d", now.getMonthValue(), now.getYear());
        }
        raw = raw.trim();
        // "YYYY-MM" from <input type="month">
        if (raw.matches("\\d{4}-\\d{2}")) {
            try {
                YearMonth ym = YearMonth.parse(raw);
                return String.format("%02d/%d", ym.getMonthValue(), ym.getYear());
            } catch (Exception e) { /* fall through */ }
        }
        // Already "MM/yyyy"
        if (raw.matches("\\d{2}/\\d{4}")) return raw;
        // Fallback to current month
        LocalDate now = LocalDate.now();
        return String.format("%02d/%d", now.getMonthValue(), now.getYear());
    }

    private String getSubPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String subPath = uri.substring(ctx.length() + BASE_PATH.length());
        return subPath.isEmpty() ? "/" : subPath;
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
