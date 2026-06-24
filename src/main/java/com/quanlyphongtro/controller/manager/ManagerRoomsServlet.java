package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerRoomsServlet", urlPatterns = {
        "/manager/rooms",
        "/manager/rooms/*",
        "/manager/facilities/*"
})
public class ManagerRoomsServlet extends BaseServlet {

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
                handleFacilityGrid(req, resp);
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

    private void handleFacilityGrid(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Map<String, Object>> facilities = new ArrayList<>();

        String sql = "SELECT f.*, " +
                "(SELECT COUNT(*) FROM dbo.rooms r WHERE r.facility_id = f.facility_id AND r.deleted_at IS NULL) AS total_rooms " +
                "FROM dbo.facilities f WHERE f.manager_id = ? AND f.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id", rs.getInt("facility_id"));
                    f.put("code", rs.getString("code"));
                    f.put("name", rs.getString("name"));
                    f.put("address", rs.getString("address"));
                    f.put("floorCount", rs.getInt("floor_count"));
                    f.put("status", rs.getString("status"));
                    f.put("totalRooms", rs.getInt("total_rooms"));
                    facilities.add(f);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load facilities managed by manager={}", currentUser.getId(), e);
        }

        if (facilities.size() > 0) {
            int firstFacilityId = (Integer) facilities.get(0).get("id");
            resp.sendRedirect(req.getContextPath() + "/manager/facilities/" + firstFacilityId + "/rooms");
            return;
        }

        req.setAttribute("facilities", facilities);
        req.setAttribute("facilityId", null); // Set empty to trigger Mode 1 in list.jsp
        req.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp").forward(req, resp);
    }

    private void handleFacilityRooms(int facilityId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        // Verify manager access to facility
        Map<String, Object> facility = null;
        List<Map<String, Object>> facilities = new ArrayList<>();
        String verifySql = "SELECT * FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setInt(1, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id", rs.getInt("facility_id"));
                    f.put("code", rs.getString("code"));
                    f.put("name", rs.getString("name"));
                    f.put("address", rs.getString("address"));
                    facilities.add(f);
                    
                    if (rs.getInt("facility_id") == facilityId) {
                        facility = f;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to verify facility managed by manager", e);
        }

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

        List<Map<String, Object>> rooms = new ArrayList<>();
        int totalRooms = 0;

        StringBuilder whereClause = new StringBuilder(" WHERE r.facility_id = ? AND r.deleted_at IS NULL");
        List<Object> params = new ArrayList<>();
        params.add(facilityId);

        if (filterStatus != null && !filterStatus.trim().isEmpty()) {
            whereClause.append(" AND r.status = ?");
            params.add(filterStatus.trim());
        }

        String countSql = "SELECT COUNT(*) FROM dbo.rooms r" + whereClause.toString();
        String selectSql = "SELECT r.*, u.full_name AS tenant_name FROM dbo.rooms r " +
                "LEFT JOIN dbo.users u ON r.tenant_id = u.user_id" + whereClause.toString() +
                " ORDER BY r.room_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Count
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalRooms = rs.getInt(1);
                    }
                }
            }

            // Items
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                int i = 0;
                for (; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.setInt(i + 1, (page - 1) * pageSize);
                ps.setInt(i + 2, pageSize);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> room = new HashMap<>();
                        room.put("id", rs.getInt("room_id"));
                        String roomCode = rs.getString("code");
                        room.put("code", roomCode);
                        room.put("area", rs.getDouble("area"));
                        room.put("status", rs.getString("status"));
                        room.put("tenantId", rs.getInt("tenant_id"));
                        room.put("tenantName", rs.getString("tenant_name"));

                        // Parse floor and room number from code (e.g. HN0102)
                        String floorStr = "—";
                        String numberStr = "—";
                        if (roomCode != null && roomCode.length() >= 4) {
                            String last4 = roomCode.substring(roomCode.length() - 4);
                            if (last4.matches("\\d+")) {
                                floorStr = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                                numberStr = String.valueOf(Integer.parseInt(last4.substring(2)));
                            }
                        }
                        room.put("floor", floorStr);
                        room.put("roomNumber", numberStr);

                        rooms.add(room);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query rooms list of facility", e);
        }

        int totalPages = totalRooms > 0 ? (int) Math.ceil((double) totalRooms / pageSize) : 1;

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", rooms);
        pageObj.put("total", totalRooms);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("facilityId", facilityId);
        req.setAttribute("currentFacility", facility);
        req.setAttribute("facilities", facilities);
        req.setAttribute("filterStatus", filterStatus);

        req.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp").forward(req, resp);
    }

    private void handleRoomDetail(int roomId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> room = null;

        String sql = "SELECT r.*, f.code AS facility_code, f.name AS facility_name, f.manager_id, u.full_name AS tenant_name, u.username AS tenant_code, u.phone AS tenant_phone " +
                "FROM dbo.rooms r " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "LEFT JOIN dbo.users u ON r.tenant_id = u.user_id " +
                "WHERE r.room_id = ? AND r.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int managerId = rs.getInt("manager_id");
                    if (managerId != currentUser.getId()) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không quản lý cơ sở chứa phòng này.");
                        return;
                    }

                    room = new HashMap<>();
                    room.put("id", rs.getInt("room_id"));
                    room.put("facilityId", rs.getInt("facility_id"));
                    room.put("facilityCode", rs.getString("facility_code"));
                    room.put("facilityName", rs.getString("facility_name"));
                    String roomCode = rs.getString("code");
                    room.put("code", roomCode);
                    room.put("area", rs.getDouble("area"));
                    room.put("status", rs.getString("status"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    Timestamp uAt = rs.getTimestamp("updated_at");
                    room.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString() : "—");
                    room.put("updatedAt", uAt != null ? uAt.toLocalDateTime().toString() : "—");
                    room.put("tenantId", rs.getInt("tenant_id"));
                    if (rs.wasNull()) {
                        room.put("tenantId", null);
                    }
                    room.put("tenantName", rs.getString("tenant_name"));
                    room.put("tenantCode", rs.getString("tenant_code"));
                    room.put("tenantPhone", rs.getString("tenant_phone"));

                    // Parse floor and roomNumber
                    String floorStr = "—";
                    String numberStr = "—";
                    if (roomCode != null && roomCode.length() >= 4) {
                        String last4 = roomCode.substring(roomCode.length() - 4);
                        if (last4.matches("\\d+")) {
                            floorStr = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                            numberStr = String.valueOf(Integer.parseInt(last4.substring(2)));
                        }
                    }
                    room.put("floor", floorStr);
                    room.put("roomNumber", numberStr);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query room details", e);
        }

        if (room == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("room", room);
        req.getRequestDispatcher("/WEB-INF/views/manager/rooms/detail.jsp").forward(req, resp);
    }
}
