package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerTicketsServlet", urlPatterns = {
        "/manager/tickets",
        "/manager/tickets/*"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
        maxFileSize = 1024 * 1024 * 10,       // 10MB
        maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class ManagerTicketsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            handleList(req, resp);
        } else {
            String idStr = pathInfo.substring(1);
            try {
                int ticketId = Integer.parseInt(idStr);
                handleDetail(ticketId, req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || "/".equals(pathInfo)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String[] parts = pathInfo.split("/");
        if (parts.length == 3) {
            try {
                int ticketId = Integer.parseInt(parts[1]);
                String action = parts[2];
                if ("receive".equals(action)) {
                    handleReceive(ticketId, req, resp);
                } else if ("assign".equals(action)) {
                    handleAssign(ticketId, req, resp);
                } else if ("reject".equals(action)) {
                    handleReject(ticketId, req, resp);
                } else if ("schedule".equals(action)) {
                    handleSchedule(ticketId, req, resp);
                } else if ("complete".equals(action)) {
                    handleComplete(ticketId, req, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
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

        String type = req.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "TENANT";
        } else {
            type = type.trim().toUpperCase();
        }

        String keyword = req.getParameter("keyword");
        String status = req.getParameter("status");
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

        List<Map<String, Object>> tickets = new ArrayList<>();
        int totalTickets = 0;

        StringBuilder whereClause = new StringBuilder(
                " WHERE f.manager_id = ? AND req.deleted_at IS NULL AND u.role = ?");
        List<Object> params = new ArrayList<>();
        params.add(currentUser.getId());
        params.add(type);

        if ("TENANT".equals(type)) {
            whereClause.append(" AND req.assigned_staff_id = ?");
            params.add(currentUser.getId());
        }

        if (status != null && !status.trim().isEmpty()) {
            whereClause.append(" AND req.status = ?");
            params.add(status.trim());
        }

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeParam = "%" + keyword.trim() + "%";
            whereClause.append(" AND (req.title LIKE ? OR req.code LIKE ?)");
            params.add(likeParam);
            params.add(likeParam);
        }

        String countSql = "SELECT COUNT(*) FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL"
                + whereClause.toString();

        String selectSql = "SELECT req.*, u.full_name AS sender_name, u.role AS sender_role, r.room_id, r.code AS room_code, f.name AS facility_name "
                +
                "FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL"
                + whereClause.toString() +
                " ORDER BY req.request_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Count
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalTickets = rs.getInt(1);
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
                        Map<String, Object> ticket = new HashMap<>();
                        ticket.put("id", rs.getInt("request_id"));
                        ticket.put("code", rs.getString("code"));
                        ticket.put("category", rs.getString("category"));
                        ticket.put("title", rs.getString("title"));
                        ticket.put("senderName", rs.getString("sender_name"));
                        ticket.put("senderRole", rs.getString("sender_role"));
                        ticket.put("roomId", rs.getInt("room_id"));
                        ticket.put("roomCode", rs.getString("room_code"));
                        ticket.put("facilityName", rs.getString("facility_name"));
                        Timestamp cAt = rs.getTimestamp("created_at");
                        ticket.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                        ticket.put("status", rs.getString("status"));
                        tickets.add(ticket);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query tickets list", e);
        }

        int totalPages = totalTickets > 0 ? (int) Math.ceil((double) totalTickets / pageSize) : 1;

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", tickets);
        pageObj.put("total", totalTickets);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("keyword", keyword);
        req.setAttribute("filterStatus", status);
        req.setAttribute("filterType", type);

        req.getRequestDispatcher("/WEB-INF/views/manager/tickets/list.jsp").forward(req, resp);
    }

    private void handleDetail(int ticketId, HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> ticket = null;
        List<Map<String, Object>> operators = new ArrayList<>();

        String ticketSql = "SELECT req.*, u.full_name AS sender_name, u.role AS sender_role, u.phone AS sender_phone, "
                +
                "r.room_id, r.code AS room_code, f.facility_id, f.name AS facility_name, f.manager_id, o.full_name AS assigned_operator_name "
                +
                "FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL "
                +
                "LEFT JOIN dbo.users o ON req.assigned_staff_id = o.user_id " +
                "WHERE req.request_id = ? AND req.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(ticketSql)) {
                ps.setInt(1, ticketId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int managerId = rs.getInt("manager_id");
                        if (managerId != currentUser.getId()) {
                            resp.sendError(HttpServletResponse.SC_FORBIDDEN,
                                    "Bạn không quản lý cơ sở của yêu cầu này.");
                            return;
                        }

                        ticket = new HashMap<>();
                        ticket.put("id", rs.getInt("request_id"));
                        ticket.put("code", rs.getString("code"));
                        ticket.put("category", rs.getString("category"));
                        ticket.put("title", rs.getString("title"));
                        ticket.put("content", rs.getString("content"));
                        ticket.put("status", rs.getString("status"));
                        ticket.put("senderId", rs.getInt("sender_id"));
                        ticket.put("senderName", rs.getString("sender_name"));
                        ticket.put("senderRole", rs.getString("sender_role"));
                        ticket.put("senderPhone", rs.getString("sender_phone"));
                        ticket.put("roomId", rs.getInt("room_id"));
                        ticket.put("roomCode", rs.getString("room_code"));
                        ticket.put("facilityId", rs.getInt("facility_id"));
                        ticket.put("facilityName", rs.getString("facility_name"));
                        Timestamp cAt = rs.getTimestamp("created_at");
                        Timestamp uAt = rs.getTimestamp("updated_at");
                        ticket.put("createdAt", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ") : "");
                        ticket.put("updatedAt", uAt != null ? uAt.toLocalDateTime().toString().replace("T", " ") : "");
                        ticket.put("assignedOperatorName", rs.getString("assigned_operator_name"));
                        ticket.put("rejectionReason", rs.getString("rejection_reason"));
                        ticket.put("attachmentUrls1", rs.getString("attachment_urls1"));
                        ticket.put("attachmentUrls2", rs.getString("attachment_urls2"));
                        Timestamp appointAt = rs.getTimestamp("appoint_schedule");
                        if (appointAt != null) {
                            ticket.put("appointSchedule", appointAt.toLocalDateTime().toString().replace("T", " "));
                            ticket.put("appointScheduleFormatted", appointAt.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm - dd/MM/yyyy")));
                        }

                        // Generate history timeline
                        List<Map<String, Object>> historyList = new ArrayList<>();
                        String sender = rs.getString("sender_name");
                        String senderRole = rs.getString("sender_role");
                        String status = rs.getString("status");

                        if ("OPERATOR".equals(senderRole)) {
                            // KEEP ORIGINAL LOGIC FOR OPERATOR TICKETS
                            Map<String, Object> h1 = new HashMap<>();
                            h1.put("action", "Gửi yêu cầu");
                            h1.put("performedAt", ticket.get("createdAt"));
                            h1.put("performedBy", sender);
                            h1.put("note", "Khởi tạo yêu cầu hỗ trợ hệ thống.");
                            historyList.add(h1);

                            if (!"NEW".equals(status) && !"PENDING".equals(status)) {
                                Map<String, Object> h2 = new HashMap<>();
                                h2.put("action", "Tiếp nhận yêu cầu");
                                h2.put("performedAt", ticket.get("updatedAt"));
                                h2.put("performedBy", "Ban Quản lý");
                                h2.put("note", "Đã tiếp nhận và đưa vào hàng chờ xử lý.");
                                historyList.add(h2);

                                if ("ASSIGNED".equals(status) || "IN_PROGRESS".equals(status) || "DONE".equals(status)
                                        || "RESOLVED".equals(status)) {
                                    Map<String, Object> h3 = new HashMap<>();
                                    h3.put("action", "Phân công xử lý");
                                    h3.put("performedAt", ticket.get("updatedAt"));
                                    h3.put("performedBy", "Ban Quản lý");
                                    h3.put("note", "Phân công cho nhân viên: " + rs.getString("assigned_operator_name"));
                                    historyList.add(h3);
                                }

                                if ("DONE".equals(status) || "RESOLVED".equals(status)) {
                                    Map<String, Object> h4 = new HashMap<>();
                                    h4.put("action", "Hoàn thành yêu cầu");
                                    h4.put("performedAt", ticket.get("updatedAt"));
                                    h4.put("performedBy", rs.getString("assigned_operator_name"));
                                    h4.put("note", "Đã sửa chữa / hoàn thành xử lý sự cố.");
                                    historyList.add(h4);
                                }

                                if ("REJECTED".equals(status)) {
                                    Map<String, Object> h5 = new HashMap<>();
                                    h5.put("action", "Từ chối yêu cầu");
                                    h5.put("performedAt", ticket.get("updatedAt"));
                                    h5.put("performedBy", "Ban Quản lý");
                                    h5.put("note", "Lý do: " + rs.getString("rejection_reason"));
                                    historyList.add(h5);
                                }
                            }
                        } else {
                            // NEW SIMPLIFIED LOGIC FOR RESIDENT/TENANT TICKETS
                            Map<String, Object> h1 = new HashMap<>();
                            h1.put("action", "Gửi yêu cầu");
                            h1.put("performedAt", ticket.get("createdAt"));
                            h1.put("performedBy", sender);
                            h1.put("note", "Khởi tạo yêu cầu hỗ trợ hệ thống.");
                            historyList.add(h1);

                            if ("RECEIVED".equals(status)) {
                                Map<String, Object> h2 = new HashMap<>();
                                h2.put("action", "Tiếp nhận yêu cầu");
                                h2.put("performedAt", ticket.get("updatedAt"));
                                h2.put("performedBy", "Ban Quản lý");
                                h2.put("note", "Đã tiếp nhận yêu cầu.");
                                historyList.add(h2);
                            } else if ("IN_PROGRESS".equals(status)) {
                                Map<String, Object> h2 = new HashMap<>();
                                h2.put("action", "Đang xử lý");
                                h2.put("performedAt", ticket.get("updatedAt"));
                                h2.put("performedBy", "Ban Quản lý");
                                String sched = (String) ticket.get("appointScheduleFormatted");
                                if (sched != null && !sched.isEmpty()) {
                                    h2.put("note", "Lịch hẹn xử lý: " + sched);
                                } else {
                                    h2.put("note", "Đang tiến hành xử lý yêu cầu.");
                                }
                                historyList.add(h2);
                            } else if ("DONE".equals(status) || "RESOLVED".equals(status)) {
                                if (ticket.get("appointScheduleFormatted") != null) {
                                    Map<String, Object> h2 = new HashMap<>();
                                    h2.put("action", "Đang xử lý");
                                    h2.put("performedAt", ticket.get("appointSchedule"));
                                    h2.put("performedBy", "Ban Quản lý");
                                    h2.put("note", "Lịch hẹn xử lý: " + ticket.get("appointScheduleFormatted"));
                                    historyList.add(h2);
                                }
                                Map<String, Object> h3 = new HashMap<>();
                                h3.put("action", "Đã hoàn thành");
                                h3.put("performedAt", ticket.get("updatedAt"));
                                h3.put("performedBy", "Ban Quản lý");
                                String notes = rs.getString("rejection_reason");
                                h3.put("note", (notes != null && !notes.trim().isEmpty()) ? notes : "Yêu cầu đã được xử lý hoàn tất.");
                                historyList.add(h3);
                            } else if ("REJECTED".equals(status)) {
                                Map<String, Object> h2 = new HashMap<>();
                                h2.put("action", "Từ chối yêu cầu");
                                h2.put("performedAt", ticket.get("updatedAt"));
                                h2.put("performedBy", "Ban Quản lý");
                                String reason = rs.getString("rejection_reason");
                                h2.put("note", "Lý do: " + ((reason != null && !reason.trim().isEmpty()) ? reason : "Không có lý do cụ thể."));
                                historyList.add(h2);
                            } else if ("CANCELLED".equals(status)) {
                                Map<String, Object> h2 = new HashMap<>();
                                h2.put("action", "Đã hủy yêu cầu");
                                h2.put("performedAt", ticket.get("updatedAt"));
                                h2.put("performedBy", sender);
                                h2.put("note", "Cư dân đã chủ động hủy yêu cầu.");
                                historyList.add(h2);
                            }
                        }
                        ticket.put("history", historyList);
                    }
                }
            }

            if (ticket == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Query operators list assigned to the same facility
            int facilityId = ticket != null && ticket.get("facilityId") != null ? (Integer) ticket.get("facilityId")
                    : -1;
            String opsSql;
            if (facilityId > 0) {
                opsSql = "SELECT u.user_id, u.full_name FROM dbo.users u " +
                        "JOIN dbo.facilities f ON u.user_id = f.operator_id " +
                        "WHERE u.role = 'OPERATOR' AND u.status = 'ACTIVE' AND u.deleted_at IS NULL " +
                        "AND f.facility_id = ? AND f.deleted_at IS NULL " +
                        "ORDER BY u.full_name";
            } else {
                opsSql = "SELECT u.user_id, u.full_name FROM dbo.users u " +
                        "JOIN dbo.facilities f ON u.user_id = f.operator_id " +
                        "WHERE u.role = 'OPERATOR' AND u.status = 'ACTIVE' AND u.deleted_at IS NULL " +
                        "AND f.manager_id = ? AND f.deleted_at IS NULL " +
                        "ORDER BY u.full_name";
            }
            try (PreparedStatement ps = conn.prepareStatement(opsSql)) {
                if (facilityId > 0) {
                    ps.setInt(1, facilityId);
                } else {
                    ps.setInt(1, currentUser.getId());
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> op = new HashMap<>();
                        op.put("id", rs.getInt("user_id"));
                        op.put("fullName", rs.getString("full_name"));
                        operators.add(op);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query ticket detail", e);
        }

        req.setAttribute("ticket", ticket);
        req.setAttribute("operators", operators);
        req.getRequestDispatcher("/WEB-INF/views/manager/tickets/detail.jsp").forward(req, resp);
    }

    private void handleReceive(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String sql = "UPDATE dbo.requests SET status = 'RECEIVED', updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ticketId);
            ps.executeUpdate();
            setFlashMessage(req, "success", "Tiếp nhận yêu cầu thành công!");
        } catch (Exception e) {
            logger.error("Failed to receive ticket={}", ticketId, e);
            setFlashMessage(req, "danger", "Lỗi tiếp nhận yêu cầu: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleAssign(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String operatorIdStr = req.getParameter("operatorId");
        if (operatorIdStr == null || operatorIdStr.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng chọn nhân sự.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        int operatorId = Integer.parseInt(operatorIdStr);
        String sql = "UPDATE dbo.requests SET status = 'ASSIGNED', assigned_staff_id = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            ps.setInt(2, ticketId);
            ps.executeUpdate();
            setFlashMessage(req, "success", "Phân công xử lý yêu cầu thành công!");
        } catch (Exception e) {
            logger.error("Failed to assign ticket={}", ticketId, e);
            setFlashMessage(req, "danger", "Lỗi phân công: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleReject(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reason = req.getParameter("reason");
        if (reason == null || reason.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng nhập lý do từ chối.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        String sql = "UPDATE dbo.requests SET status = 'REJECTED', rejection_reason = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, reason.trim());
            ps.setInt(2, ticketId);
            ps.executeUpdate();
            setFlashMessage(req, "success", "Từ chối yêu cầu thành công!");
        } catch (Exception e) {
            logger.error("Failed to reject ticket={}", ticketId, e);
            setFlashMessage(req, "danger", "Lỗi từ chối yêu cầu: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleSchedule(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String appointmentDateStr = req.getParameter("appointmentDate");
        String sql = "UPDATE dbo.requests SET status = 'IN_PROGRESS', appoint_schedule = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (appointmentDateStr != null && !appointmentDateStr.trim().isEmpty()) {
                java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(appointmentDateStr.trim());
                ps.setTimestamp(1, Timestamp.valueOf(ldt));
            } else {
                ps.setNull(1, java.sql.Types.TIMESTAMP);
            }
            ps.setInt(2, ticketId);
            ps.executeUpdate();
            setFlashMessage(req, "success", "Bắt đầu xử lý yêu cầu thành công!");
        } catch (Exception e) {
            logger.error("Failed to start processing ticket={}", ticketId, e);
            setFlashMessage(req, "danger", "Lỗi cập nhật trạng thái: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private void handleComplete(int ticketId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String notes = req.getParameter("notes");
        if (notes == null || notes.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Ghi chú hoàn thành không được để trống.");
            resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
            return;
        }

        List<String> fileNames = new ArrayList<>();
        try {
            String uploadPath = req.getServletContext().getRealPath("") + java.io.File.separator + "uploads" + java.io.File.separator + "requests";
            java.io.File uploadDir = new java.io.File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            for (Part part : req.getParts()) {
                if ("after_images".equals(part.getName()) && part.getSize() > 0) {
                    String fileName = java.util.UUID.randomUUID().toString() + "_" + getFileName(part);
                    part.write(uploadPath + java.io.File.separator + fileName);
                    fileNames.add("/uploads/requests/" + fileName);
                }
            }
        } catch (Exception e) {
            logger.error("Error uploading completion images", e);
        }

        String attachmentUrls2 = fileNames.isEmpty() ? null : String.join(",", fileNames);

        String sql = "UPDATE dbo.requests SET status = 'DONE', rejection_reason = ?, attachment_urls2 = ?, updated_at = GETDATE() WHERE request_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, notes.trim());
            if (attachmentUrls2 != null) {
                ps.setString(2, attachmentUrls2);
            } else {
                ps.setNull(2, java.sql.Types.VARCHAR);
            }
            ps.setInt(3, ticketId);
            ps.executeUpdate();
            setFlashMessage(req, "success", "Xác nhận hoàn thành yêu cầu thành công!");
        } catch (Exception e) {
            logger.error("Failed to complete ticket={}", ticketId, e);
            setFlashMessage(req, "danger", "Lỗi cập nhật hoàn thành: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tickets/" + ticketId);
    }

    private String getFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
