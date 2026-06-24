package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.DatabaseUtil;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.util.AuditLogHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet(name = "ManagerNotificationsServlet", urlPatterns = {
        "/manager/notifications",
        "/manager/notifications/*"
})
public class ManagerNotificationsServlet extends BaseServlet {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            String action = req.getParameter("action");
            if ("report-incorrect".equals(action)) {
                handleReportIncorrect(req, resp);
            } else {
                handleList(req, resp);
            }
        } else if ("/create".equals(pathInfo)) {
            handleCreateForm(req, resp);
        } else if ("/send-operator".equals(pathInfo)) {
            handleSendOperatorForm(req, resp);
        } else {
            String idStr = pathInfo.substring(1);
            try {
                int notificationId = Integer.parseInt(idStr);
                handleDetail(notificationId, req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/create".equals(pathInfo)) {
            handleCreateSubmit(req, resp);
        } else if ("/send-operator".equals(pathInfo)) {
            handleSendOperatorSubmit(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("keyword");
        String facilityIdStr = req.getParameter("facilityId");
        Integer filterFacilityId = null;
        if (facilityIdStr != null && !facilityIdStr.trim().isEmpty()) {
            try {
                filterFacilityId = Integer.parseInt(facilityIdStr.trim());
            } catch (NumberFormatException e) {
                // ignore
            }
        }

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

        List<Map<String, Object>> notifications = new ArrayList<>();
        int totalCount = 0;

        StringBuilder whereClause = new StringBuilder(" WHERE n.deleted_at IS NULL AND (n.created_by = ? OR n.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL) OR n.room_id IN (SELECT r.room_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL))");
        List<Object> params = new ArrayList<>();
        params.add(currentUser.getId());
        params.add(currentUser.getId());
        params.add(currentUser.getId());

        if (filterFacilityId != null) {
            whereClause.append(" AND (n.facility_id = ? OR n.room_id IN (SELECT room_id FROM dbo.rooms WHERE facility_id = ? AND deleted_at IS NULL))");
            params.add(filterFacilityId);
            params.add(filterFacilityId);
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            whereClause.append(" AND (n.title LIKE ? OR n.content LIKE ?)");
            params.add("%" + keyword.trim() + "%");
            params.add("%" + keyword.trim() + "%");
        }

        String countSql = "SELECT COUNT(*) FROM dbo.notifications n" +
                " JOIN dbo.users u ON n.created_by = u.user_id " +
                " LEFT JOIN dbo.rooms r ON n.room_id = r.room_id AND r.deleted_at IS NULL " +
                " LEFT JOIN dbo.facilities f ON (n.facility_id = f.facility_id OR r.facility_id = f.facility_id) AND f.deleted_at IS NULL" + whereClause.toString();
        String selectSql = "SELECT n.*, u.full_name AS creator_name FROM dbo.notifications n " +
                "JOIN dbo.users u ON n.created_by = u.user_id " +
                "LEFT JOIN dbo.rooms r ON n.room_id = r.room_id AND r.deleted_at IS NULL " +
                "LEFT JOIN dbo.facilities f ON (n.facility_id = f.facility_id OR r.facility_id = f.facility_id) AND f.deleted_at IS NULL" + whereClause.toString() +
                " ORDER BY n.notification_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        List<Map<String, Object>> assignedFacilities = new ArrayList<>();
        String facilitySql = "SELECT facility_id, code, name FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Count
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalCount = rs.getInt(1);
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
                        Map<String, Object> notif = new HashMap<>();
                        notif.put("id", rs.getInt("notification_id"));
                        notif.put("code", rs.getString("code"));
                        notif.put("title", rs.getString("title"));
                        notif.put("recipientType", rs.getString("target_type"));
                        notif.put("createdByName", rs.getString("creator_name"));
                        notif.put("status", rs.getString("status"));
                        Timestamp cAt = rs.getTimestamp("created_at");
                        notif.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                        notifications.add(notif);
                    }
                }
            }

            // Load facilities for filter
            try (PreparedStatement ps = conn.prepareStatement(facilitySql)) {
                ps.setInt(1, currentUser.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> f = new HashMap<>();
                        f.put("id", rs.getInt("facility_id"));
                        f.put("code", rs.getString("code"));
                        f.put("name", rs.getString("name"));
                        assignedFacilities.add(f);
                    }
                }
            }

            // Load reported incorrect invoices
            List<Map<String, Object>> incorrectInvoices = new ArrayList<>();
            String incorrectSql = "SELECT i.invoice_id, i.code AS invoice_code, r.code AS room_code, f.name AS facility_name, f.code AS facility_code, " +
                    "mr.meter_id, mr.electric, mr.water, mr.reading_date, mr.status AS meter_status, i.total_amount " +
                    "FROM dbo.invoices i " +
                    "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                    "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                    "JOIN dbo.meter_readings mr ON i.meter_id = mr.meter_id " +
                    "WHERE f.manager_id = ? AND mr.status IN ('INCORRECT', 'REPORTED') AND i.deleted_at IS NULL AND mr.deleted_at IS NULL " +
                    "ORDER BY mr.reading_date DESC, i.invoice_id DESC";
            try (PreparedStatement psInc = conn.prepareStatement(incorrectSql)) {
                psInc.setInt(1, currentUser.getId());
                try (ResultSet rsInc = psInc.executeQuery()) {
                    while (rsInc.next()) {
                        Map<String, Object> item = new HashMap<>();
                        item.put("id", rsInc.getInt("invoice_id"));
                        item.put("code", rsInc.getString("invoice_code"));
                        item.put("roomCode", rsInc.getString("room_code"));
                        item.put("facilityName", rsInc.getString("facility_name"));
                        item.put("facilityCode", rsInc.getString("facility_code"));
                        item.put("electric", rsInc.getInt("electric"));
                        item.put("water", rsInc.getInt("water"));
                        item.put("meterStatus", rsInc.getString("meter_status"));
                        item.put("totalAmount", rsInc.getDouble("total_amount"));
                        
                        java.sql.Date rDate = rsInc.getDate("reading_date");
                        if (rDate != null) {
                            java.time.LocalDate localDate = rDate.toLocalDate();
                            item.put("billingPeriod", String.format("%02d/%d", localDate.getMonthValue(), localDate.getYear()));
                        } else {
                            item.put("billingPeriod", "—");
                        }
                        incorrectInvoices.add(item);
                    }
                }
            }
            req.setAttribute("incorrectInvoices", incorrectInvoices);
        } catch (Exception e) {
            logger.error("Failed to query notifications list", e);
        }

        int totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", notifications);
        pageObj.put("total", totalCount);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("keyword", keyword);
        req.setAttribute("assignedFacilities", assignedFacilities);
        req.setAttribute("filterFacilityId", filterFacilityId);

        req.getRequestDispatcher("/WEB-INF/views/manager/notifications/list.jsp").forward(req, resp);
    }

    private void handleCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Map<String, Object>> assignedFacilities = new ArrayList<>();
        String sql = "SELECT facility_id, code, name FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> f = new HashMap<>();
                    f.put("id", rs.getInt("facility_id"));
                    f.put("code", rs.getString("code"));
                    f.put("name", rs.getString("name"));
                    assignedFacilities.add(f);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load facilities for notification creation", e);
        }

        List<Map<String, Object>> assignedRooms = new ArrayList<>();
        String roomsSql = "SELECT r.room_id, r.code AS room_code, r.facility_id " +
                "FROM dbo.rooms r " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL " +
                "ORDER BY f.name, r.code";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(roomsSql)) {
            ps.setInt(1, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> r = new HashMap<>();
                    r.put("id", rs.getInt("room_id"));
                    r.put("code", rs.getString("room_code"));
                    r.put("facilityId", rs.getInt("facility_id"));
                    assignedRooms.add(r);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load rooms for notification creation", e);
        }

        req.setAttribute("assignedFacilities", assignedFacilities);
        req.setAttribute("assignedRooms", assignedRooms);
        req.getRequestDispatcher("/WEB-INF/views/manager/notifications/create.jsp").forward(req, resp);
    }

    private void handleCreateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String recipientType = req.getParameter("recipientType");
        String recipientIdStr = req.getParameter("recipientId");
        String facilityIdForRoomStr = req.getParameter("facilityId_for_room");

        Integer facilityIdForRoom = null;
        if (facilityIdForRoomStr != null && !facilityIdForRoomStr.trim().isEmpty()) {
            try {
                facilityIdForRoom = Integer.parseInt(facilityIdForRoomStr.trim());
            } catch (Exception e) {
                // ignore
            }
        }

        if (title == null || content == null || recipientType == null || title.trim().isEmpty() || content.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Vui lòng điền đầy đủ các trường bắt buộc.");
            Integer rId = null;
            try {
                if (recipientIdStr != null) rId = Integer.parseInt(recipientIdStr.trim());
            } catch (Exception e) {}
            req.setAttribute("dto", buildDto(title, content, recipientType, rId, facilityIdForRoom));
            handleCreateForm(req, resp);
            return;
        }

        Integer facilityId = null;
        Integer roomId = null;

        if ("FACILITY".equals(recipientType)) {
            if (recipientIdStr == null || recipientIdStr.trim().isEmpty()) {
                req.setAttribute("errorMessage", "Vui lòng chọn cơ sở nhận thông báo.");
                req.setAttribute("dto", buildDto(title, content, recipientType, null, null));
                handleCreateForm(req, resp);
                return;
            }
            try {
                facilityId = Integer.parseInt(recipientIdStr.trim());
            } catch (NumberFormatException e) {
                req.setAttribute("errorMessage", "Cơ sở nhận thông báo không hợp lệ.");
                req.setAttribute("dto", buildDto(title, content, recipientType, null, null));
                handleCreateForm(req, resp);
                return;
            }

            // Verify manager manages this facility
            boolean isFacilityAuthorized = false;
            String checkFacilitySql = "SELECT 1 FROM dbo.facilities WHERE facility_id = ? AND manager_id = ? AND deleted_at IS NULL";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(checkFacilitySql)) {
                ps.setInt(1, facilityId);
                ps.setInt(2, currentUser.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        isFacilityAuthorized = true;
                    }
                }
            } catch (Exception e) {
                logger.error("Error verifying facility access", e);
            }

            if (!isFacilityAuthorized) {
                req.setAttribute("errorMessage", "Bạn không có quyền gửi thông báo đến cơ sở này.");
                req.setAttribute("dto", buildDto(title, content, recipientType, facilityId, null));
                handleCreateForm(req, resp);
                return;
            }
        } else if ("ROOM".equals(recipientType)) {
            if (recipientIdStr == null || recipientIdStr.trim().isEmpty()) {
                req.setAttribute("errorMessage", "Vui lòng chọn phòng nhận thông báo.");
                req.setAttribute("dto", buildDto(title, content, recipientType, null, facilityIdForRoom));
                handleCreateForm(req, resp);
                return;
            }
            try {
                roomId = Integer.parseInt(recipientIdStr.trim());
            } catch (NumberFormatException e) {
                req.setAttribute("errorMessage", "Phòng nhận thông báo không hợp lệ.");
                req.setAttribute("dto", buildDto(title, content, recipientType, null, facilityIdForRoom));
                handleCreateForm(req, resp);
                return;
            }

            // Verify room exists and belongs to a facility managed by this manager
            boolean isRoomAuthorized = false;
            String checkRoomSql = "SELECT r.facility_id FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE r.room_id = ? AND f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(checkRoomSql)) {
                ps.setInt(1, roomId);
                ps.setInt(2, currentUser.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        isRoomAuthorized = true;
                        facilityIdForRoom = rs.getInt("facility_id");
                    }
                }
            } catch (Exception e) {
                logger.error("Failed to verify room ID", e);
            }

            if (!isRoomAuthorized) {
                req.setAttribute("errorMessage", "Bạn không có quyền gửi thông báo đến phòng này.");
                req.setAttribute("dto", buildDto(title, content, recipientType, roomId, facilityIdForRoom));
                handleCreateForm(req, resp);
                return;
            }
        } else {
            req.setAttribute("errorMessage", "Đối tượng nhận không hợp lệ.");
            req.setAttribute("dto", buildDto(title, content, recipientType, null, null));
            handleCreateForm(req, resp);
            return;
        }

        // Generate unique code
        com.quanlyphongtro.dao.NotificationDAO notificationDAO = new com.quanlyphongtro.dao.NotificationDAO();
        String code = notificationDAO.generateCode(recipientType);

        String sql = "INSERT INTO dbo.notifications (code, title, content, target_type, facility_id, room_id, status, created_by, created_at, sent_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'SENT', ?, GETDATE(), GETDATE())";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, title.trim());
            ps.setString(3, content.trim());
            ps.setString(4, recipientType);
            if (facilityId != null) ps.setInt(5, facilityId); else ps.setNull(5, java.sql.Types.INTEGER);
            if (roomId != null) ps.setInt(6, roomId); else ps.setNull(6, java.sql.Types.INTEGER);
            ps.setInt(7, currentUser.getId());
            ps.executeUpdate();
            
            int notifId = -1;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    notifId = rs.getInt(1);
                }
            }
            
            if (notifId > 0) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "notifications", notifId, "CREATE", null, title.trim(), currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after create notification", ex);
                }
            }
            
            setFlashMessage(req, "success", "Gửi thông báo thành công!");
        } catch (Exception e) {
            logger.error("Failed to insert notification", e);
            req.setAttribute("errorMessage", "Lỗi gửi thông báo: " + e.getMessage());
            req.setAttribute("dto", buildDto(title, content, recipientType, facilityId != null ? facilityId : roomId, facilityIdForRoom));
            handleCreateForm(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/manager/notifications");
    }

    private Map<String, Object> buildDto(String title, String content, String recipientType, Integer recipientId, Integer facilityId) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("title", title);
        dto.put("content", content);
        dto.put("recipientType", recipientType);
        dto.put("recipientId", recipientId);
        dto.put("facilityId", facilityId);
        return dto;
    }

    private void handleDetail(int notificationId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> notification = null;

        String sql = "SELECT n.*, u.full_name AS creator_name, " +
                "f.code AS facility_code, f.name AS facility_name, f.manager_id AS target_facility_manager_id, " +
                "r.code AS room_code, rf.manager_id AS target_room_facility_manager_id " +
                "FROM dbo.notifications n " +
                "JOIN dbo.users u ON n.created_by = u.user_id " +
                "LEFT JOIN dbo.facilities f ON n.facility_id = f.facility_id AND f.deleted_at IS NULL " +
                "LEFT JOIN dbo.rooms r ON n.room_id = r.room_id AND r.deleted_at IS NULL " +
                "LEFT JOIN dbo.facilities rf ON r.facility_id = rf.facility_id AND rf.deleted_at IS NULL " +
                "WHERE n.notification_id = ? AND n.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int creatorId = rs.getInt("created_by");
                    Integer targetFacilityManagerId = rs.getObject("target_facility_manager_id") != null ? rs.getInt("target_facility_manager_id") : null;
                    Integer targetRoomFacilityManagerId = rs.getObject("target_room_facility_manager_id") != null ? rs.getInt("target_room_facility_manager_id") : null;

                    boolean hasAccess = (creatorId == currentUser.getId()) 
                                     || (targetFacilityManagerId != null && targetFacilityManagerId == currentUser.getId())
                                     || (targetRoomFacilityManagerId != null && targetRoomFacilityManagerId == currentUser.getId());

                    if (!hasAccess) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền xem thông báo này.");
                        return;
                    }

                    notification = new HashMap<>();
                    notification.put("id", rs.getInt("notification_id"));
                    notification.put("code", rs.getString("code"));
                    notification.put("title", rs.getString("title"));
                    notification.put("content", rs.getString("content"));
                    String targetType = rs.getString("target_type");
                    notification.put("recipientType", targetType);
                    notification.put("createdByName", rs.getString("creator_name"));
                    notification.put("status", rs.getString("status"));
                    Timestamp cAt = rs.getTimestamp("created_at");
                    Timestamp sAt = rs.getTimestamp("sent_at");
                    notification.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                    notification.put("sentAt", sAt != null ? sAt.toLocalDateTime().toString().replace("T", " ") : "—");

                    // Set recipientName
                    if ("FACILITY".equals(targetType)) {
                        notification.put("recipientName", rs.getString("facility_name") + " (" + rs.getString("facility_code") + ")");
                    } else if ("ROOM".equals(targetType)) {
                        notification.put("recipientName", "Phòng " + rs.getString("room_code"));
                    } else {
                        notification.put("recipientName", "Toàn hệ thống");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query notification detail", e);
        }

        if (notification == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("notification", notification);
        req.getRequestDispatcher("/WEB-INF/views/manager/notifications/detail.jsp").forward(req, resp);
    }

    private void handleReportIncorrect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        if (invoiceIdStr == null || invoiceIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã hóa đơn.");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            
            // Verify manager owns this invoice
            boolean isAuthorized = false;
            int meterId = -1;
            String invoiceCode = "";
            String verifySql = "SELECT i.meter_id, i.code, f.manager_id FROM dbo.invoices i " +
                    "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                    "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                    "WHERE i.invoice_id = ? AND i.deleted_at IS NULL";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(verifySql)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int mId = rs.getInt("manager_id");
                        if (mId == currentUser.getId()) {
                            isAuthorized = true;
                            meterId = rs.getInt("meter_id");
                            invoiceCode = rs.getString("code");
                        }
                    }
                }
            }

            if (!isAuthorized) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền báo cáo hóa đơn này.");
                return;
            }

            if (meterId <= 0) {
                setFlashMessage(req, "danger", "Hóa đơn không liên kết với chỉ số điện nước hợp lệ.");
                resp.sendRedirect(req.getContextPath() + "/manager/notifications");
                return;
            }

            // Update meter reading status
            String updateSql = "UPDATE dbo.meter_readings SET status = 'INCORRECT', updated_at = GETDATE() WHERE meter_id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, meterId);
                ps.executeUpdate();
            }

            setFlashMessage(req, "success", "Đã báo cáo sai số điện nước cho hóa đơn " + invoiceCode + ". Vui lòng gửi thông báo cho Operator.");
            resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=incorrect-utility");

        } catch (Exception e) {
            logger.error("Failed to report incorrect invoice", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSendOperatorForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        if (invoiceIdStr == null || invoiceIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã hóa đơn.");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            Map<String, Object> invoice = null;

            String sql = "SELECT i.*, r.code AS room_code, f.code AS facility_code, f.name AS facility_name, f.manager_id, " +
                    "mr.electric, mr.water, mr.reading_date " +
                    "FROM dbo.invoices i " +
                    "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                    "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                    "JOIN dbo.meter_readings mr ON i.meter_id = mr.meter_id " +
                    "WHERE i.invoice_id = ? AND i.deleted_at IS NULL AND mr.deleted_at IS NULL";

            List<Map<String, Object>> operators = new ArrayList<>();

            try (Connection conn = DatabaseUtil.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, invoiceId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int managerId = rs.getInt("manager_id");
                            if (managerId != currentUser.getId()) {
                                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thực hiện thao tác này.");
                                return;
                            }

                            invoice = new HashMap<>();
                            invoice.put("id", rs.getInt("invoice_id"));
                            invoice.put("code", rs.getString("code"));
                            invoice.put("roomCode", rs.getString("room_code"));
                            invoice.put("facilityName", rs.getString("facility_name"));
                            invoice.put("facilityCode", rs.getString("facility_code"));
                            invoice.put("electric", rs.getInt("electric"));
                            invoice.put("water", rs.getInt("water"));
                            invoice.put("totalAmount", rs.getDouble("total_amount"));

                            java.sql.Date rDate = rs.getDate("reading_date");
                            if (rDate != null) {
                                java.time.LocalDate localDate = rDate.toLocalDate();
                                invoice.put("billingPeriod", String.format("%02d/%d", localDate.getMonthValue(), localDate.getYear()));
                            } else {
                                invoice.put("billingPeriod", "—");
                            }
                        }
                    }
                }

                if (invoice == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn.");
                    return;
                }

                // Query active operators
                String opsSql = "SELECT user_id, full_name FROM dbo.users WHERE role = 'OPERATOR' AND status = 'ACTIVE' AND deleted_at IS NULL ORDER BY full_name";
                try (PreparedStatement psOps = conn.prepareStatement(opsSql)) {
                    try (ResultSet rsOps = psOps.executeQuery()) {
                        while (rsOps.next()) {
                            Map<String, Object> op = new HashMap<>();
                            op.put("id", rsOps.getInt("user_id"));
                            op.put("fullName", rsOps.getString("full_name"));
                            operators.add(op);
                        }
                    }
                }
            }

            String defaultTitle = "Báo cáo sai số điện nước - Phòng " + invoice.get("roomCode");
            String defaultContent = "Kính gửi nhân viên vận hành,\n\nHóa đơn kỳ " + invoice.get("billingPeriod") + 
                    " của phòng " + invoice.get("roomCode") + " thuộc cơ sở " + invoice.get("facilityName") + 
                    " được phát hiện bị nhập sai chỉ số điện nước.\n\nThông tin hiện tại:\n" +
                    "- Chỉ số điện: " + invoice.get("electric") + " kWh\n" +
                    "- Chỉ số nước: " + invoice.get("water") + " m3\n\n" +
                    "Vui lòng kiểm tra thực tế, xác minh lại hình ảnh và cập nhật chỉ số chính xác.";

            req.setAttribute("invoice", invoice);
            req.setAttribute("operators", operators);
            req.setAttribute("defaultTitle", defaultTitle);
            req.setAttribute("defaultContent", defaultContent);

            req.getRequestDispatcher("/WEB-INF/views/manager/notifications/send_operator.jsp").forward(req, resp);

        } catch (Exception e) {
            logger.error("Failed to prepare send operator form", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSendOperatorSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        String operatorIdStr = req.getParameter("operatorId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        if (invoiceIdStr == null || operatorIdStr == null || title == null || content == null || 
                invoiceIdStr.trim().isEmpty() || operatorIdStr.trim().isEmpty() || title.trim().isEmpty() || content.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng nhập đầy đủ tất cả các trường.");
            resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=incorrect-utility");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            int operatorId = Integer.parseInt(operatorIdStr.trim());

            int meterId = -1;
            String facilityCode = "";
            boolean isAuthorized = false;

            // Verify manager and get details
            String checkSql = "SELECT i.meter_id, f.code AS facility_code, f.manager_id FROM dbo.invoices i " +
                    "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                    "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                    "WHERE i.invoice_id = ? AND i.deleted_at IS NULL";
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, invoiceId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int mId = rs.getInt("manager_id");
                        if (mId == currentUser.getId()) {
                            isAuthorized = true;
                            meterId = rs.getInt("meter_id");
                            facilityCode = rs.getString("facility_code");
                        }
                    }
                }
            }

            if (!isAuthorized) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }

            // Create unique request code: REQ-UTL-XXXX
            String reqCode = "REQ-UTL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Insert request
            String insertReqSql = "INSERT INTO dbo.requests (code, sender_id, category, title, content, status, assigned_staff_id, created_at, updated_at) " +
                    "VALUES (?, ?, 'UTILITY', ?, ?, 'PENDING', ?, GETDATE(), GETDATE())";
            
            // Update meter status to REPORTED
            String updateMeterSql = "UPDATE dbo.meter_readings SET status = 'REPORTED', updated_at = GETDATE() WHERE meter_id = ?";

            try (Connection conn = DatabaseUtil.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    try (PreparedStatement psReq = conn.prepareStatement(insertReqSql)) {
                        psReq.setString(1, reqCode);
                        psReq.setInt(2, currentUser.getId());
                        psReq.setString(3, title.trim());
                        psReq.setString(4, content.trim());
                        psReq.setInt(5, operatorId);
                        psReq.executeUpdate();
                    }

                    try (PreparedStatement psMeter = conn.prepareStatement(updateMeterSql)) {
                        psMeter.setInt(1, meterId);
                        psMeter.executeUpdate();
                    }

                    conn.commit();
                    setFlashMessage(req, "success", "Đã gửi thông báo yêu cầu sửa chỉ số điện nước cho Operator thành công!");
                } catch (Exception ex) {
                    conn.rollback();
                    throw ex;
                } finally {
                    conn.setAutoCommit(true);
                }
            }

            resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=incorrect-utility");

        } catch (Exception e) {
            logger.error("Failed to send operator request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
