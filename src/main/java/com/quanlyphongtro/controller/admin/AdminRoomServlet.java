package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Admin room detail — read-only view.
 * URL: /admin/rooms/{roomId}
 *
 * Admin can view any room regardless of facility manager.
 * Admin cannot create/update tenants from this page.
 */
@WebServlet(name = "AdminRoomServlet", urlPatterns = {"/admin/rooms", "/admin/rooms/*"})
public class AdminRoomServlet extends BaseServlet {

    private static final String VIEW_BASE = "/WEB-INF/views/admin/rooms/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo(); // e.g. "/12"
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String idStr = pathInfo.substring(1);
        int roomId;
        try {
            roomId = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Map<String, Object> room = loadRoom(roomId);
        if (room == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("room", room);
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && pathInfo.matches("/\\d+/update")) {
            int roomId = Integer.parseInt(pathInfo.split("/")[1]);
            
            Map<String, Object> room = loadRoom(roomId);
            if (room != null && "INACTIVE".equals(room.get("facilityStatus"))) {
                setFlashMessage(req, "error", "Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng.");
                resp.sendRedirect(req.getContextPath() + "/admin/rooms/" + roomId);
                return;
            }
            
            String areaStr = req.getParameter("area");
            String feeStr = req.getParameter("roomFee");

            BigDecimal area = null;
            BigDecimal fee = null;

            try {
                if (areaStr != null && !areaStr.isBlank()) {
                    area = new BigDecimal(areaStr.replace(",", "."));
                    if (area.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Diện tích không được âm.");
                }
                if (feeStr != null && !feeStr.isBlank()) {
                    fee = new BigDecimal(feeStr.replace(",", "."));
                    if (fee.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Giá phòng không được âm.");
                }

                String sql = "UPDATE dbo.rooms SET area = ?, room_fee = ?, updated_at = GETDATE() WHERE room_id = ? AND deleted_at IS NULL";
                try (Connection conn = DatabaseUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    if (area != null) ps.setBigDecimal(1, area); else ps.setNull(1, Types.DECIMAL);
                    if (fee != null) ps.setBigDecimal(2, fee); else ps.setNull(2, Types.DECIMAL);
                    ps.setInt(3, roomId);
                    ps.executeUpdate();
                }

                setFlashMessage(req, "success", "Cập nhật thông tin phòng thành công.");
            } catch (Exception e) {
                logger.error("AdminRoomServlet.doPost error updating room {}", roomId, e);
                setFlashMessage(req, "error", "Lỗi: " + e.getMessage());
            }
            resp.sendRedirect(req.getContextPath() + "/admin/rooms/" + roomId);
            return;
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // ── private helpers ────────────────────────────────────────────────────

    private Map<String, Object> loadRoom(int roomId) {
        String sql =
            "SELECT r.room_id, r.facility_id, r.code, r.area, r.status, " +
            "       r.room_fee, r.deposit_amount, " +
            "       r.created_at, r.updated_at, " +
            "       f.code  AS facility_code, " +
            "       f.name  AS facility_name, " +
            "       f.status AS facility_status, " +
            "       u.user_id   AS tenant_id, " +
            "       u.full_name AS tenant_name, " +
            "       u.username  AS tenant_code, " +
            "       u.phone     AS tenant_phone, " +
            "       u.email     AS tenant_email " +
            "FROM dbo.rooms r " +
            "JOIN  dbo.facilities f ON f.facility_id = r.facility_id " +
            "LEFT JOIN dbo.users u   ON u.user_id     = r.tenant_id " +
            "WHERE r.room_id = ? AND r.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;

                Map<String, Object> room = new HashMap<>();
                room.put("id",           rs.getInt("room_id"));
                room.put("facilityId",   rs.getInt("facility_id"));
                room.put("facilityCode", rs.getString("facility_code"));
                room.put("facilityName", rs.getString("facility_name"));
                room.put("facilityStatus", rs.getString("facility_status"));

                String code = rs.getString("code");
                room.put("code", code);

                room.put("area",          rs.getDouble("area"));
                room.put("areaRaw",       rs.getObject("area"));   // null if not set
                room.put("status",        rs.getString("status"));
                room.put("roomFee",       rs.getObject("room_fee"));
                room.put("depositAmount", rs.getObject("deposit_amount"));

                Timestamp cAt = rs.getTimestamp("created_at");
                Timestamp uAt = rs.getTimestamp("updated_at");
                room.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString() : "—");
                room.put("updatedAt", uAt != null ? uAt.toLocalDateTime().toString() : "—");

                // Tenant — may be null
                int tenantId = rs.getInt("tenant_id");
                room.put("tenantId",    rs.wasNull() ? null : tenantId);
                room.put("tenantName",  rs.getString("tenant_name"));
                room.put("tenantCode",  rs.getString("tenant_code"));
                room.put("tenantPhone", rs.getString("tenant_phone"));
                room.put("tenantEmail", rs.getString("tenant_email"));

                // Parse floor / room-number from code (e.g. HN0102 → floor=1, room=2)
                String floor = "—", roomNum = "—";
                if (code != null && code.length() >= 4) {
                    String last4 = code.substring(code.length() - 4);
                    if (last4.matches("\\d+")) {
                        floor   = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                        roomNum = String.valueOf(Integer.parseInt(last4.substring(2)));
                    }
                }
                room.put("floor",      floor);
                room.put("roomNumber", roomNum);

                return room;
            }
        } catch (Exception e) {
            logger.error("AdminRoomServlet.loadRoom failed for roomId={}", roomId, e);
            return null;
        }
    }
}
