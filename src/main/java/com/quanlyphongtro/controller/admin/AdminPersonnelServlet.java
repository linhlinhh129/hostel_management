package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.PersonnelService;
import com.quanlyphongtro.service.impl.PersonnelServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminPersonnelServlet",
        urlPatterns = {"/admin/personnel", "/admin/personnel/*"})
public class AdminPersonnelServlet extends BaseServlet {

    private final PersonnelService personnelService = new PersonnelServiceImpl();

    private static final int    PAGE_SIZE = 20;
    private static final String BASE_PATH = "/admin/personnel";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/personnel/";

    // ── Routing ───────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = subPath(req);
        try {
            if (isRoot(path)) {
                showList(req, resp);
            } else if (path.equals("/create")) {
                showCreate(req, resp);
            } else if (path.matches("/\\d+")) {
                showDetail(req, resp, idOf(path));
            } else if (path.matches("/\\d+/edit")) {
                showEdit(req, resp, idPrefix(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminPersonnelServlet doGet error", e);
            handleException(req, resp, e);
            req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = subPath(req);
        try {
            if (path.equals("/create")) {
                doCreate(req, resp);
            } else if (path.matches("/\\d+/edit")) {
                doUpdate(req, resp, idPrefix(path));
            } else if (path.matches("/\\d+/status")) {
                doToggleStatus(req, resp, idPrefix(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            if (path.equals("/create")) {
                req.setAttribute("managerFacilities",  personnelService.findFacilitiesForManager(null));
                req.setAttribute("operatorFacilities", personnelService.findFacilitiesForOperator(null));
                req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
            } else if (path.matches("/\\d+/edit")) {
                int editId = idPrefix(path);
                try {
                    User user = personnelService.getById(editId);
                    req.setAttribute("user", user);
                    req.setAttribute("currentFacilityId", personnelService.findFacilityIdForUser(editId));
                    req.setAttribute("managerFacilities",  personnelService.findFacilitiesForManager(
                            "MANAGER".equals(user.getRole()) ? editId : null));
                    req.setAttribute("operatorFacilities", personnelService.findFacilitiesForOperator(
                            "OPERATOR".equals(user.getRole()) ? editId : null));
                    req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
                } catch (Exception ex) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                setFlashMessage(req, "error", e.getMessage());
                resp.sendRedirect(req.getContextPath() + BASE_PATH);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminPersonnelServlet doPost error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    // ── GET handlers ──────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = e(req.getParameter("keyword"));
        String role    = e(req.getParameter("role"));
        String status  = e(req.getParameter("status"));
        int page = intOrDefault(req.getParameter("page"), 1);

        PageDTO<User> page_ = personnelService.list(keyword, role, status, page, PAGE_SIZE);

        req.setAttribute("page", page_);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedRole", role);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("managerFacilities",  personnelService.findFacilitiesForManager(null));
        req.setAttribute("operatorFacilities", personnelService.findFacilitiesForOperator(null));
        req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        req.setAttribute("user", personnelService.getById(id));
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        User user = personnelService.getById(id);
        req.setAttribute("user", user);
        req.setAttribute("currentFacilityId", personnelService.findFacilityIdForUser(id));
        req.setAttribute("managerFacilities",  personnelService.findFacilitiesForManager(
                "MANAGER".equals(user.getRole()) ? id : null));
        req.setAttribute("operatorFacilities", personnelService.findFacilitiesForOperator(
                "OPERATOR".equals(user.getRole()) ? id : null));
        req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
    }

    // ── POST handlers ─────────────────────────────────────────────────────

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        UserSessionDTO me = getCurrentUser(req);
        int createdBy = (me != null && me.getId() != null) ? me.getId() : 0;

        String loginLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/login";

        personnelService.create(
            req.getParameter("fullName"),
            req.getParameter("email"),
            req.getParameter("phone"),
            req.getParameter("role"),
            req.getParameter("identityNumber"),
            req.getParameter("dob"),
            req.getParameter("gender"),
            req.getParameter("permanentAddress"),
            req.getParameter("facilityId"),
            createdBy,
            loginLink
        );

        setFlashMessage(req, "success", "Tạo nhân sự thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        personnelService.update(id,
            req.getParameter("fullName"),
            req.getParameter("email"),
            req.getParameter("phone"),
            req.getParameter("role"),
            req.getParameter("identityNumber"),
            req.getParameter("dob"),
            req.getParameter("gender"),
            req.getParameter("permanentAddress"),
            req.getParameter("facilityId")
        );
        setFlashMessage(req, "success", "Cập nhật nhân sự thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doToggleStatus(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        UserSessionDTO me = getCurrentUser(req);
        int currentUserId = (me != null && me.getId() != null) ? me.getId() : -1;

        try {
            personnelService.toggleStatus(id, currentUserId);
            setFlashMessage(req, "success", "Đã cập nhật trạng thái tài khoản nhân sự.");
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String subPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String sub = uri.substring(req.getContextPath().length() + BASE_PATH.length());
        return sub.isEmpty() ? "/" : sub;
    }

    private boolean isRoot(String path) {
        return path == null || path.isEmpty() || path.equals("/");
    }

    private int idOf(String path) {
        return Integer.parseInt(path.substring(1));
    }

    private int idPrefix(String path) {
        return Integer.parseInt(path.split("/")[1]);
    }

    private String e(String s) { return s == null ? "" : s.trim(); }

    private int intOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }
}
