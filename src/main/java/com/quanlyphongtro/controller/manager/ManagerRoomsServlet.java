package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.RoomDetailDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.RoomService;
import com.quanlyphongtro.service.impl.RoomServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerRoomsServlet", urlPatterns = {
        "/manager/rooms",
        "/manager/rooms/*",
        "/manager/facilities/*"
})
public class ManagerRoomsServlet extends BaseServlet {

    private final RoomService roomService = new RoomServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri = req.getRequestURI();

        if (uri.contains("/manager/facilities/")) {
            try {
                int startIdx = uri.indexOf("/manager/facilities/") + "/manager/facilities/".length();
                int endIdx = uri.indexOf("/rooms", startIdx);
                if (endIdx == -1) {
                    endIdx = uri.length();
                }
                String facIdStr = uri.substring(startIdx, endIdx);
                if (facIdStr.endsWith("/")) {
                    facIdStr = facIdStr.substring(0, facIdStr.length() - 1);
                }
                int facilityId = Integer.parseInt(facIdStr);
                handleFacilityRooms(facilityId, req, resp);
            } catch (Exception e) {
                logger.error("Failed to parse facility ID in rooms list", e);
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || "/".equals(pathInfo)) {
                String showGrid = req.getParameter("showGrid");
                if ("true".equals(showGrid)) {
                    handleFacilityGrid(req, resp);
                } else {
                    redirectToDefaultFacility(req, resp);
                }
            } else {
                // Renders detail: /manager/rooms/{id}
                String idStr = pathInfo.substring(1);
                try {
                    int roomId = Integer.parseInt(idStr);
                    handleRoomDetail(roomId, req, resp);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        }
    }

    private void handleFacilityGrid(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Map<String, Object>> facilities = roomService.getFacilitiesByManager(currentUser.getId());

        req.setAttribute("facilities", facilities);
        req.setAttribute("facilityId", null); // Set empty to trigger Mode 1 in list.jsp
        req.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp").forward(req, resp);
    }

    private void redirectToDefaultFacility(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Integer firstFacilityId = roomService.getDefaultFacilityId(currentUser.getId());

        if (firstFacilityId != null) {
            resp.sendRedirect(req.getContextPath() + "/manager/facilities/" + firstFacilityId + "/rooms");
        } else {
            handleFacilityGrid(req, resp);
        }
    }

    private void handleFacilityRooms(int facilityId, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> facility = roomService.verifyFacilityManager(facilityId, currentUser.getId());
        if (facility == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không quản lý cơ sở này.");
            return;
        }

        String filterStatus = req.getParameter("status");
        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (Exception e) {
                page = 1;
            }
        }
        int pageSize = 10;

        PageResult<RoomDTO> pageObj = roomService.getFacilityRoomsPage(facilityId, filterStatus, page, pageSize);

        req.setAttribute("page", pageObj);
        req.setAttribute("facilityId", facilityId);
        req.setAttribute("currentFacility", facility);
        req.setAttribute("filterStatus", filterStatus);

        req.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp").forward(req, resp);
    }

    private void handleRoomDetail(int roomId, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        try {
            RoomDetailDTO room = roomService.getRoomDetail(roomId, currentUser.getId());
            if (room == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            Integer activeContractId = room.getActiveContractId();
            if (activeContractId != null) {
                req.setAttribute("activeContractId", activeContractId);
            }

            req.setAttribute("room", room);
            req.getRequestDispatcher("/WEB-INF/views/manager/rooms/detail.jsp").forward(req, resp);
        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to query room details", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
