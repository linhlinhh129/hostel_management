package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.RoomService;
import com.quanlyphongtro.service.impl.RoomServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/**
 * Admin room detail — read-only view + inline update area/fee.
 * URL: /admin/rooms/{roomId}        GET  — xem chi tiết
 * URL: /admin/rooms/{roomId}/update POST — cập nhật area và fee
 */
@WebServlet(name = "AdminRoomServlet", urlPatterns = {"/admin/rooms", "/admin/rooms/*"})
public class AdminRoomServlet extends BaseServlet {

    private final RoomService roomService = new RoomServiceImpl();

    private static final String VIEW_BASE = "/WEB-INF/views/admin/rooms/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int roomId;
        try {
            roomId = Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Map<String, Object> room = roomService.getDetailForAdmin(roomId);
            req.setAttribute("room", room);
            req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || !pathInfo.matches("/\\d+/update")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        int roomId = Integer.parseInt(pathInfo.split("/")[1]);

        try {
            roomService.updateAreaAndFee(roomId,
                    req.getParameter("area"),
                    req.getParameter("roomFee"));
            setFlashMessage(req, "success", "Cập nhật thông tin phòng thành công.");
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (ValidationException e) {
            setFlashMessage(req, "error", e.getMessage());
        } catch (Exception e) {
            logger.error("AdminRoomServlet.doPost error updating room {}", roomId, e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
        }

        resp.sendRedirect(req.getContextPath() + "/admin/rooms/" + roomId);
    }
}
