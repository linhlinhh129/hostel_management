package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.NotificationService;
import com.quanlyphongtro.service.impl.NotificationServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminNotificationServlet",
        urlPatterns = {"/admin/notifications", "/admin/notifications/*"})
public class AdminNotificationServlet extends BaseServlet {

    private final NotificationService notificationService = new NotificationServiceImpl();

    private static final int PAGE_SIZE = 10;
    private static final String BASE_PATH = "/admin/notifications";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/notifications/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        try {
            if (path == null || path.isEmpty() || path.equals("/")) {
                showList(req, resp);
            } else if (path.equals("/create")) {
                req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
            } else if (path.matches("/\\d+")) {
                showDetail(req, resp, extractId(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminNotificationServlet doGet error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        if (!path.equals("/create")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try {
            doCreate(req, resp);
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("AdminNotificationServlet doPost error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    // ─── GET handlers ────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = nullToEmpty(req.getParameter("keyword"));
        int page = parseIntOrDefault(req.getParameter("page"), 1);

        PageDTO<Notification> page_ = notificationService.getAdminNotifications(keyword, page, PAGE_SIZE);

        req.setAttribute("page",    page_);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        Notification notification = notificationService.getAdminNotificationById(id)
            .orElseThrow(NotFoundException::new);
        req.setAttribute("notification", notification);
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    // ─── POST handlers ───────────────────────────────────────────────────

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String title         = trim(req.getParameter("title"));
        String content       = trim(req.getParameter("content"));

        UserSessionDTO currentUser = getCurrentUser(req);
        Integer createdBy = currentUser != null ? currentUser.getId() : null;

        // Validation + business logic đã nằm trong Service
        notificationService.createAdminNotification(title, content, createdBy);

        setFlashMessage(req, "success", "Đã gửi thông báo thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────

    private String getSubPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String sub = uri.substring(ctx.length() + BASE_PATH.length());
        return sub.isEmpty() ? "/" : sub;
    }

    private int extractId(String path) { return Integer.parseInt(path.substring(1)); }
    private String trim(String s)        { return s == null ? "" : s.trim(); }
    private String nullToEmpty(String s) { return s == null ? "" : s.trim(); }
    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
