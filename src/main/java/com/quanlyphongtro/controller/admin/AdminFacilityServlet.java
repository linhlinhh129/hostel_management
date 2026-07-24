package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.FacilityFormDTO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.service.FacilityService;
import com.quanlyphongtro.service.impl.FacilityServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "AdminFacilityServlet", urlPatterns = {"/admin/facilities", "/admin/facilities/*"})
public class AdminFacilityServlet extends BaseServlet {

    private final FacilityService facilityService = new FacilityServiceImpl();

    private static final int    PAGE_SIZE = 10;
    private static final String BASE_PATH = "/admin/facilities";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/facilities/";

    // ── Routing ───────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = subPath(req);
        try {
            if (isRoot(path)) {
                showList(req, resp);
            } else if (path.equals("/create")) {
                req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
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
            logger.error("AdminFacilityServlet doGet error", e);
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
                doEdit(req, resp, idPrefix(path));
            } else if (path.matches("/\\d+/activate")) {
                doActivate(req, resp, idPrefix(path));
            } else if (path.matches("/\\d+/deactivate")) {
                doDeactivate(req, resp, idPrefix(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            if (path.matches("/\\d+/edit")) {
                // Re-render edit form with error
                int editId = idPrefix(path);
                try {
                    req.setAttribute("errorMessage", e.getMessage());
                    req.setAttribute("facility", facilityService.getById(editId));
                    req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
                } catch (Exception ex) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                req.setAttribute("dto", FacilityFormDTO.of(req));
                req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminFacilityServlet doPost error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    // ── GET handlers ──────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = e(req.getParameter("keyword"));
        String status  = e(req.getParameter("status"));
        int page = intOrDefault(req.getParameter("page"), 1);

        PageDTO<Facility> page_ = facilityService.list(keyword, status, page, PAGE_SIZE);

        req.setAttribute("page", page_);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        Facility facility = facilityService.getById(id);
        req.setAttribute("facility", facility);
        if (!"DRAFT".equals(facility.getStatus())) {
            req.setAttribute("rooms", facilityService.getRooms(id));
        }
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        req.setAttribute("facility", facilityService.getById(id));
        req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
    }

    // ── POST handlers ─────────────────────────────────────────────────────

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        facilityService.create(
            req.getParameter("code"),
            req.getParameter("name"),
            req.getParameter("address"),
            req.getParameter("floorCount"),
            req.getParameter("roomsPerFloor")
        );
        setFlashMessage(req, "success", "Tạo cơ sở thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH);
    }

    private void doEdit(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        facilityService.update(id,
            req.getParameter("code"),
            req.getParameter("name"),
            req.getParameter("address"),
            req.getParameter("floorCount"),
            req.getParameter("roomsPerFloor")
        );
        setFlashMessage(req, "success", "Cập nhật cơ sở thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doActivate(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        try {
            facilityService.activate(id);
            setFlashMessage(req, "success", "Kích hoạt cơ sở thành công. Phòng đã được tạo tự động.");
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doDeactivate(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        try {
            facilityService.deactivate(id);
            setFlashMessage(req, "success", "Đã vô hiệu hóa cơ sở.");
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
