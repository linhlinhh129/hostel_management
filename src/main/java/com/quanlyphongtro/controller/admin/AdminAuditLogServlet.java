package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.service.AuditLogService;
import com.quanlyphongtro.service.impl.AuditLogServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminAuditLogServlet",
        urlPatterns = {"/admin/audit-logs", "/admin/audit-logs/*"})
public class AdminAuditLogServlet extends BaseServlet {

    private final AuditLogService auditLogService = new AuditLogServiceImpl();

    private static final int    PAGE_SIZE = 10;
    private static final String BASE_PATH = "/admin/audit-logs";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/audit-logs/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = subPath(req);
        try {
            if (isRoot(path)) {
                showList(req, resp);
            } else if (path.matches("/\\d+")) {
                showDetail(req, resp, Integer.parseInt(path.substring(1)));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminAuditLogServlet doGet error", e);
            handleException(req, resp, e);
            return;
        }
    }

    // ── GET handlers ──────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String filterActor      = e(req.getParameter("actor"));
        String filterRole       = e(req.getParameter("role"));
        String filterEntityType = e(req.getParameter("entityType"));
        String filterAction     = e(req.getParameter("action"));
        String filterDateFrom   = e(req.getParameter("dateFrom"));
        String filterDateTo     = e(req.getParameter("dateTo"));
        int page = intOrDefault(req.getParameter("page"), 1);
        if (page < 1) page = 1;

        int total = auditLogService.count(
                filterActor, filterRole, filterEntityType, filterAction, filterDateFrom, filterDateTo);
        List<AuditLog> auditLogs = auditLogService.list(
                filterActor, filterRole, filterEntityType, filterAction, filterDateFrom, filterDateTo,
                page, PAGE_SIZE);

        req.setAttribute("auditLogs",        auditLogs);
        req.setAttribute("currentPage",      page);
        req.setAttribute("hasNextPage",      (long) page * PAGE_SIZE < total);
        req.setAttribute("totalCount",       total);
        req.setAttribute("filterActor",      filterActor);
        req.setAttribute("filterRole",       filterRole);
        req.setAttribute("filterEntityType", filterEntityType);
        req.setAttribute("filterAction",     filterAction);
        req.setAttribute("filterDateFrom",   filterDateFrom);
        req.setAttribute("filterDateTo",     filterDateTo);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        AuditLog log = auditLogService.getById(id);
        req.setAttribute("auditLog", log);
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String subPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String sub = uri.substring(req.getContextPath().length() + BASE_PATH.length());
        return sub.isEmpty() ? "/" : sub;
    }

    private boolean isRoot(String p) { return p == null || p.isEmpty() || p.equals("/"); }
    private String e(String s)       { return s == null ? "" : s.trim(); }
    private int intOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception ex) { return def; }
    }
}
