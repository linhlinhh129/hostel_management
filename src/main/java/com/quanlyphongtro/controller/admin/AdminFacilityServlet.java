package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.util.AuditLogHelper;
import com.quanlyphongtro.util.DatabaseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;

@WebServlet(name = "AdminFacilityServlet", urlPatterns = {"/admin/facilities", "/admin/facilities/*"})
public class AdminFacilityServlet extends BaseServlet {

    private final FacilityDAO facilityDAO = new FacilityDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    private static final int PAGE_SIZE = 10;
    private static final String BASE_PATH = "/admin/facilities";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/facilities/";

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
            } else if (path.matches("/\\d+/edit")) {
                showEdit(req, resp, extractIdFromPrefix(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
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
            } else if (path.matches("/\\d+/edit")) {
                doEdit(req, resp, extractIdFromPrefix(path));
            } else if (path.matches("/\\d+/activate")) {
                doActivate(req, resp, extractIdFromPrefix(path));
            } else if (path.matches("/\\d+/deactivate")) {
                doDeactivate(req, resp, extractIdFromPrefix(path));
            } else if (path.matches("/\\d+/rooms/\\d+/area")) {
                doUpdateRoomArea(req, resp, path);
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            // Nếu lỗi đến từ route update area → redirect về detail
            if (path.matches("/\\d+/rooms/\\d+/area")) {
                String[] parts = path.split("/");
                int facilityId = Integer.parseInt(parts[1]);
                setFlashMessage(req, "error", e.getMessage());
                try { resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + facilityId); } catch (Exception ignored) {}
            } else {
                req.setAttribute("errorMessage", e.getMessage());
                try {
                    req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
                } catch (Exception ex) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminFacilityServlet doPost error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    // ─── GET handlers ────────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword = nullToEmpty(req.getParameter("keyword"));
        String status  = nullToEmpty(req.getParameter("status"));
        int page = parseIntOrDefault(req.getParameter("page"), 1);

        int total = facilityDAO.count(keyword, status);
        List<Facility> items = facilityDAO.findAll(keyword, status, page, PAGE_SIZE);

        req.setAttribute("page", new PageDTO<>(items, page, PAGE_SIZE, total));
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        Facility facility = facilityDAO.findById(id)
            .orElseThrow(NotFoundException::new);
        req.setAttribute("facility", facility);

        // Chỉ query rooms khi cơ sở đã ACTIVE hoặc INACTIVE (đã sinh phòng)
        if (!"DRAFT".equals(facility.getStatus())) {
            req.setAttribute("rooms", facilityDAO.findRoomsByFacilityId(id));
        }

        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        Facility facility = facilityDAO.findById(id)
            .orElseThrow(NotFoundException::new);
        req.setAttribute("facility", facility);
        req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
    }

    // ─── POST handlers ───────────────────────────────────────────────────────

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String code         = trim(req.getParameter("code"));
        String name         = trim(req.getParameter("name"));
        String address      = trim(req.getParameter("address"));
        String floorStr     = trim(req.getParameter("floorCount"));
        String roomStr      = trim(req.getParameter("roomsPerFloor"));
        String elecStr      = trim(req.getParameter("electricityPrice"));
        String waterStr     = trim(req.getParameter("waterPrice"));
        String internetStr  = trim(req.getParameter("internetFee"));
        String serviceStr   = trim(req.getParameter("serviceFee"));

        // Validation
        if (code.isEmpty())    throw new ValidationException("Mã cơ sở không được để trống.");
        if (name.isEmpty())    throw new ValidationException("Tên cơ sở không được để trống.");
        if (address.isEmpty()) throw new ValidationException("Địa chỉ không được để trống.");

        if (!code.matches("[A-Za-z]{2,10}")) {
            throw new ValidationException("Mã cơ sở chỉ gồm 2-10 chữ cái A-Z.");
        }
        code = code.toUpperCase();

        int floorCount, roomsPerFloor;
        try {
            floorCount = Integer.parseInt(floorStr);
            if (floorCount < 1 || floorCount > 99)
                throw new ValidationException("Số tầng phải từ 1 đến 99.");
        } catch (NumberFormatException e) {
            throw new ValidationException("Số tầng không hợp lệ.");
        }
        try {
            roomsPerFloor = Integer.parseInt(roomStr);
            if (roomsPerFloor < 1 || roomsPerFloor > 99)
                throw new ValidationException("Số phòng mỗi tầng phải từ 1 đến 99.");
        } catch (NumberFormatException e) {
            throw new ValidationException("Số phòng mỗi tầng không hợp lệ.");
        }

        if (facilityDAO.countByCode(code, null) > 0) {
            throw new ValidationException("Mã cơ sở '" + code + "' đã tồn tại.");
        }

        Facility f = new Facility();
        f.setCode(code);
        f.setName(name);
        f.setAddress(address);
        f.setFloorCount(floorCount);
        f.setRoomsPerFloor(roomsPerFloor);
        f.setStatus("DRAFT");
        f.setElectricityPrice(parseBigDecimal(elecStr));
        f.setWaterPrice(parseBigDecimal(waterStr));
        f.setInternetFee(parseBigDecimal(internetStr));
        f.setServiceFee(parseBigDecimal(serviceStr));

        int newId = facilityDAO.insert(f);

        UserSessionDTO currentUser = getCurrentUser(req);
        try {
            AuditLogHelper.log(auditLogDAO, req, "facilities", newId,
                "CREATE", null, code, currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after facility create id={}", newId, ex);
        }

        setFlashMessage(req, "success", "Tạo cơ sở '" + name + "' thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH);
    }

    private void doEdit(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        Facility existing = facilityDAO.findById(id)
            .orElseThrow(NotFoundException::new);

        String name        = trim(req.getParameter("name"));
        String address     = trim(req.getParameter("address"));
        String elecStr     = trim(req.getParameter("electricityPrice"));
        String waterStr    = trim(req.getParameter("waterPrice"));
        String internetStr = trim(req.getParameter("internetFee"));
        String serviceStr  = trim(req.getParameter("serviceFee"));

        if (name.isEmpty())    throw new ValidationException("Tên cơ sở không được để trống.");
        if (address.isEmpty()) throw new ValidationException("Địa chỉ không được để trống.");

        existing.setName(name);
        existing.setAddress(address);
        existing.setElectricityPrice(parseBigDecimal(elecStr));
        existing.setWaterPrice(parseBigDecimal(waterStr));
        existing.setInternetFee(parseBigDecimal(internetStr));
        existing.setServiceFee(parseBigDecimal(serviceStr));

        facilityDAO.update(existing);

        UserSessionDTO currentUser = getCurrentUser(req);
        try {
            AuditLogHelper.log(auditLogDAO, req, "facilities", id,
                "UPDATE", existing.getCode(), name, currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after facility edit id={}", id, ex);
        }

        setFlashMessage(req, "success", "Cập nhật cơ sở thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doActivate(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        Facility facility = facilityDAO.findById(id)
            .orElseThrow(NotFoundException::new);

        if (!"DRAFT".equals(facility.getStatus())) {
            setFlashMessage(req, "error", "Chỉ có thể kích hoạt cơ sở ở trạng thái DRAFT.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
            return;
        }

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);
            facilityDAO.updateStatus(id, "ACTIVE", conn);
            facilityDAO.generateRooms(id, facility.getCode(),
                facility.getFloorCount(), facility.getRoomsPerFloor(), conn);
            conn.commit();
        } catch (Exception e) {
            DatabaseUtil.rollbackQuietly(conn);
            throw e;
        } finally {
            DatabaseUtil.closeQuietly(conn);
        }

        UserSessionDTO currentUser = getCurrentUser(req);
        try {
            AuditLogHelper.log(auditLogDAO, req, "facilities", id,
                "ACTIVATE", "DRAFT", "ACTIVE", currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after facility activate id={}", id, ex);
        }

        setFlashMessage(req, "success", "Kích hoạt cơ sở thành công. Phòng đã được tạo tự động.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doDeactivate(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        Facility facility = facilityDAO.findById(id)
            .orElseThrow(NotFoundException::new);

        if (!"ACTIVE".equals(facility.getStatus())) {
            setFlashMessage(req, "error", "Chỉ có thể vô hiệu hóa cơ sở đang ACTIVE.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
            return;
        }

        facilityDAO.updateStatus(id, "INACTIVE");

        UserSessionDTO currentUser = getCurrentUser(req);
        try {
            AuditLogHelper.log(auditLogDAO, req, "facilities", id,
                "DEACTIVATE", "ACTIVE", "INACTIVE", currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after facility deactivate id={}", id, ex);
        }

        setFlashMessage(req, "success", "Đã vô hiệu hóa cơ sở.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doUpdateRoomArea(HttpServletRequest req, HttpServletResponse resp, String path)
            throws Exception {
        // path: /{facilityId}/rooms/{roomId}/area
        String[] parts = path.split("/");
        int facilityId = Integer.parseInt(parts[1]);
        int roomId     = Integer.parseInt(parts[3]);

        String areaStr = trim(req.getParameter("area"));
        BigDecimal area = null;
        if (!areaStr.isEmpty()) {
            try {
                area = new BigDecimal(areaStr.replace(",", "."));
                if (area.compareTo(BigDecimal.ZERO) < 0) {
                    throw new ValidationException("Diện tích không được âm.");
                }
            } catch (NumberFormatException e) {
                throw new ValidationException("Diện tích không hợp lệ.");
            }
        }

        boolean updated = roomDAO.updateArea(roomId, area);
        if (!updated) {
            setFlashMessage(req, "error", "Không tìm thấy phòng hoặc cập nhật thất bại.");
        } else {
            setFlashMessage(req, "success", "Đã cập nhật diện tích phòng.");
        }
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + facilityId);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String getSubPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String subPath = uri.substring(ctx.length() + BASE_PATH.length());
        return subPath.isEmpty() ? "/" : subPath;
    }

    private int extractId(String path) {
        // path like "/123"
        return Integer.parseInt(path.substring(1));
    }

    private int extractIdFromPrefix(String path) {
        // path like "/123/edit" or "/123/activate"
        String[] parts = path.split("/");
        return Integer.parseInt(parts[1]);
    }

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private String nullToEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    private BigDecimal parseBigDecimal(String s) {
        if (s == null || s.isBlank()) return BigDecimal.ZERO;
        try { return new BigDecimal(s.replace(",", ".")); } catch (Exception e) { return BigDecimal.ZERO; }
    }
}
