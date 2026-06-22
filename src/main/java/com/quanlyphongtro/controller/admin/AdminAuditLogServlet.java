package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.model.AuditLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminAuditLogServlet",
        urlPatterns = {"/admin/audit-logs", "/admin/audit-logs/*"})
public class AdminAuditLogServlet extends BaseServlet {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    private static final int PAGE_SIZE = 10;
    private static final String BASE_PATH = "/admin/audit-logs";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/audit-logs/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        try {
            if (path == null || path.isEmpty() || path.equals("/")) {
                showList(req, resp);
            } else if (path.matches("/\\d+")) {
                showDetail(req, resp, extractId(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminAuditLogServlet doGet error", e);
            handleException(req, resp, e);
            req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
        }
    }

    // ─── GET handlers ────────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filterActor      = nullToEmpty(req.getParameter("actor"));
        String filterEntityType = nullToEmpty(req.getParameter("entityType"));
        String filterAction     = nullToEmpty(req.getParameter("action"));
        String filterDateFrom   = nullToEmpty(req.getParameter("dateFrom"));
        String filterDateTo     = nullToEmpty(req.getParameter("dateTo"));
        int page = parseIntOrDefault(req.getParameter("page"), 1);

        int total = auditLogDAO.count(
            filterActor, filterEntityType, filterAction, filterDateFrom, filterDateTo);
        List<AuditLog> auditLogs = auditLogDAO.findAll(
            filterActor, filterEntityType, filterAction, filterDateFrom, filterDateTo,
            page, PAGE_SIZE);

        boolean hasNextPage = (long) page * PAGE_SIZE < total;

        req.setAttribute("auditLogs", auditLogs);
        req.setAttribute("currentPage", page);
        req.setAttribute("hasNextPage", hasNextPage);
        req.setAttribute("totalCount", total);
        req.setAttribute("filterActor", filterActor);
        req.setAttribute("filterEntityType", filterEntityType);
        req.setAttribute("filterAction", filterAction);
        req.setAttribute("filterDateFrom", filterDateFrom);
        req.setAttribute("filterDateTo", filterDateTo);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        AuditLog log = auditLogDAO.findById(id)
            .orElseThrow(NotFoundException::new);
        req.setAttribute("auditLog", log);
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String getSubPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String subPath = uri.substring(ctx.length() + BASE_PATH.length());
        return subPath.isEmpty() ? "/" : subPath;
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(1));
    }

    private String nullToEmpty(String s) { return s == null ? "" : s.trim(); }
    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
