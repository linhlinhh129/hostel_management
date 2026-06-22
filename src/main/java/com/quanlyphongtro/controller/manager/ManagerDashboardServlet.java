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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerDashboardServlet", urlPatterns = "/manager/dashboard")
public class ManagerDashboardServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        int managerId = currentUser.getId();

        String facilityName = "Chưa phân công";
        String facilityCode = "—";

        int totalRooms = 0;
        int occupiedRooms = 0;
        int vacantRooms = 0;
        int totalTenants = 0;
        int totalDependents = 0;
        int pendingTickets = 0;
        int sentNotifications = 0;
        int occupancyRate = 0;

        int ticketCountNew = 0;
        int ticketCountInProgress = 0;
        int ticketCountDone = 0;
        int ticketCountRejected = 0;

        List<Map<String, Object>> recentTickets = new ArrayList<>();

        String facilitySql = "SELECT TOP 1 name, code FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";

        String totalRoomsSql = "SELECT COUNT(*) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        
        String occupiedRoomsSql = "SELECT COUNT(*) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.tenant_id IS NOT NULL AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        
        String vacantRoomsSql = "SELECT COUNT(*) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.tenant_id IS NULL AND r.deleted_at IS NULL AND f.deleted_at IS NULL";

        String totalTenantsSql = "SELECT COUNT(DISTINCT r.tenant_id) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id JOIN dbo.users u ON r.tenant_id = u.user_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL AND u.deleted_at IS NULL";

        String totalDependentsSql = "SELECT COUNT(*) FROM dbo.dependents d JOIN dbo.rooms r ON d.tenant_id = r.tenant_id JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL AND d.deleted_at IS NULL";

        String pendingTicketsSql = "SELECT COUNT(*) FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL " +
                "WHERE f.manager_id = ? AND req.deleted_at IS NULL AND req.status IN ('NEW', 'RECEIVED', 'ASSIGNED', 'IN_PROGRESS')";

        String sentNotificationsSql = "SELECT COUNT(*) FROM dbo.notifications WHERE created_by = ? AND deleted_at IS NULL";

        String ticketStatsSql = "SELECT req.status, COUNT(*) AS count FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL " +
                "WHERE f.manager_id = ? AND req.deleted_at IS NULL " +
                "GROUP BY req.status";

        String recentTicketsSql = "SELECT req.request_id, req.code, req.title, req.status, req.created_at, r.code AS room_code, u.role AS sender_role " +
                "FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL " +
                "WHERE f.manager_id = ? AND req.deleted_at IS NULL " +
                "ORDER BY req.request_id DESC OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 1. Facility Info
            try (PreparedStatement ps = conn.prepareStatement(facilitySql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        facilityName = rs.getString("name");
                        facilityCode = rs.getString("code");
                    }
                }
            }

            // 2. Room stats
            try (PreparedStatement ps = conn.prepareStatement(totalRoomsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalRooms = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(occupiedRoomsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) occupiedRooms = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(vacantRoomsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) vacantRooms = rs.getInt(1);
                }
            }

            // 3. Tenants & Dependents stats
            try (PreparedStatement ps = conn.prepareStatement(totalTenantsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalTenants = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(totalDependentsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) totalDependents = rs.getInt(1);
                }
            }

            // 4. Pending tickets & Sent Notifications stats
            try (PreparedStatement ps = conn.prepareStatement(pendingTicketsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) pendingTickets = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(sentNotificationsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) sentNotifications = rs.getInt(1);
                }
            }

            // 5. Ticket stats by status
            try (PreparedStatement ps = conn.prepareStatement(ticketStatsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String status = rs.getString("status");
                        int count = rs.getInt("count");
                        if ("NEW".equals(status)) {
                            ticketCountNew += count;
                        } else if ("RECEIVED".equals(status) || "ASSIGNED".equals(status) || "IN_PROGRESS".equals(status)) {
                            ticketCountInProgress += count;
                        } else if ("RESOLVED".equals(status) || "DONE".equals(status)) {
                            ticketCountDone += count;
                        } else if ("REJECTED".equals(status)) {
                            ticketCountRejected += count;
                        }
                    }
                }
            }

            // 6. Recent Tickets list
            try (PreparedStatement ps = conn.prepareStatement(recentTicketsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> ticket = new HashMap<>();
                        ticket.put("id", rs.getInt("request_id"));
                        ticket.put("code", rs.getString("code"));
                        ticket.put("title", rs.getString("title"));
                        
                        String rCode = rs.getString("room_code");
                        ticket.put("roomCode", rCode != null ? rCode : "Sự cố trọ");

                        Timestamp cAt = rs.getTimestamp("created_at");
                        ticket.put("createdDateLabel", cAt != null ? cAt.toLocalDateTime().toString().replace("T", " ").substring(0, 16) : "");

                        String status = rs.getString("status");
                        String statusLabel = status;
                        String statusBadgeClass = "badge-neutral";
                        if ("NEW".equals(status)) {
                            statusLabel = "Mới";
                            statusBadgeClass = "badge-info";
                        } else if ("RECEIVED".equals(status)) {
                            statusLabel = "Tiếp nhận";
                            statusBadgeClass = "badge-accent";
                        } else if ("ASSIGNED".equals(status)) {
                            statusLabel = "Đã phân công";
                            statusBadgeClass = "badge-warning";
                        } else if ("IN_PROGRESS".equals(status)) {
                            statusLabel = "Đang xử lý";
                            statusBadgeClass = "badge-warning";
                        } else if ("RESOLVED".equals(status) || "DONE".equals(status)) {
                            statusLabel = "Hoàn thành";
                            statusBadgeClass = "badge-success";
                        } else if ("REJECTED".equals(status)) {
                            statusLabel = "Từ chối";
                            statusBadgeClass = "badge-danger";
                        }

                        ticket.put("statusLabel", statusLabel);
                        ticket.put("statusBadgeClass", statusBadgeClass);
                        recentTickets.add(ticket);
                    }
                }
            }

            // Calculate occupancy rate
            if (totalRooms > 0) {
                occupancyRate = (occupiedRooms * 100) / totalRooms;
            }

        } catch (Exception e) {
            logger.error("Failed to load manager dashboard statistics for manager={}", managerId, e);
        }

        req.setAttribute("facilityName", facilityName);
        req.setAttribute("facilityCode", facilityCode);

        req.setAttribute("totalRooms", totalRooms);
        req.setAttribute("occupiedRooms", occupiedRooms);
        req.setAttribute("vacantRooms", vacantRooms);
        req.setAttribute("totalTenants", totalTenants);
        req.setAttribute("totalDependents", totalDependents);
        req.setAttribute("pendingTickets", pendingTickets);
        req.setAttribute("sentNotifications", sentNotifications);
        req.setAttribute("occupancyRate", occupancyRate);

        req.setAttribute("ticketCountNew", ticketCountNew);
        req.setAttribute("ticketCountInProgress", ticketCountInProgress);
        req.setAttribute("ticketCountDone", ticketCountDone);
        req.setAttribute("ticketCountRejected", ticketCountRejected);

        req.setAttribute("recentTickets", recentTickets);

        req.getRequestDispatcher("/WEB-INF/views/manager/dashboard.jsp").forward(req, resp);
    }
}
