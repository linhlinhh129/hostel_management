package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "AdminNotificationServlet",
        urlPatterns = {"/admin/notifications", "/admin/notifications/*"})
public class AdminNotificationServlet extends BaseServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();

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
                showCreate(req, resp);
            } else if (path.matches("/\\d+")) {
                showDetail(req, resp, extractId(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminNotificationServlet doGet error", e);
            handleException(req, resp, e);
            req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        try {
            if (path.equals("/create")) {
                doCreate(req, resp);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            try {
                req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
            } catch (Exception ex) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminNotificationServlet doPost error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    // ─── GET handlers ────────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = nullToEmpty(req.getParameter("keyword"));
        int page = parseIntOrDefault(req.getParameter("page"), 1);

        int total = notificationDAO.count(keyword);
        List<Notification> items = notificationDAO.findAll(keyword, page, PAGE_SIZE);

        req.setAttribute("page", new PageDTO<>(items, page, PAGE_SIZE, total));
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        Notification notification = notificationDAO.findById(id)
            .orElseThrow(NotFoundException::new);
        req.setAttribute("notification", notification);
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    // ─── POST handlers ───────────────────────────────────────────────────────

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String title         = trim(req.getParameter("title"));
        String content       = trim(req.getParameter("content"));
        String recipientType = trim(req.getParameter("recipientType"));

        // Validation
        if (title.isEmpty())
            throw new ValidationException("Tiêu đề không được để trống.");
        if (title.length() > 255)
            throw new ValidationException("Tiêu đề không được vượt quá 255 ký tự.");
        if (content.isEmpty())
            throw new ValidationException("Nội dung không được để trống.");
        if (content.length() > 1000)
            throw new ValidationException("Nội dung không được vượt quá 1000 ký tự.");
        if (!"ALL".equals(recipientType)) {
            throw new ValidationException("Loại người nhận phải là 'ALL'.");
        }

        UserSessionDTO currentUser = getCurrentUser(req);
        String code = notificationDAO.generateCode("ALL");

        Notification n = new Notification();
        n.setCode(code);
        n.setTitle(title);
        n.setContent(content);
        n.setTargetType("ALL");
        n.setStatus("SENT");
        n.setCreatedBy(currentUser != null ? currentUser.getId() : null);

        notificationDAO.insert(n);

        setFlashMessage(req, "success", "Đã gửi thông báo thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH);
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

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private String nullToEmpty(String s) { return s == null ? "" : s.trim(); }
    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
