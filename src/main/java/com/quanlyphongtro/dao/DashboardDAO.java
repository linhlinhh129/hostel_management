package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO extends BaseDAO {

    public Map<String, Object> getManagerDashboardStats(int managerId) {
        Map<String, Object> stats = new HashMap<>();

        String facilityName = "—";
        String facilityCode = "—";
        String facilityStatus = "—";

        int totalRooms = 0;
        int occupiedRooms = 0;
        int vacantRooms = 0;
        int totalTenants = 0;
        int totalDependents = 0;
        int pendingTickets = 0;
        int sentNotifications = 0;
        int occupancyRate = 0;

        int activeContracts = 0;
        int unpaidInvoices = 0;
        int overdueInvoices = 0;
        int pendingPayments = 0;
        java.math.BigDecimal monthlyRevenue = java.math.BigDecimal.ZERO;
        java.math.BigDecimal totalOutstanding = java.math.BigDecimal.ZERO;

        int ticketCountNew = 0;
        int ticketCountInProgress = 0;
        int ticketCountDone = 0;
        int ticketCountRejected = 0;

        List<Map<String, Object>> recentTickets = new ArrayList<>();

        String facilitySql = "SELECT TOP 1 name, code, status FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";

        String totalRoomsSql = "SELECT COUNT(*) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        
        String occupiedRoomsSql = "SELECT COUNT(*) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.tenant_id IS NOT NULL AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        
        String vacantRoomsSql = "SELECT COUNT(*) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.tenant_id IS NULL AND r.deleted_at IS NULL AND f.deleted_at IS NULL";

        String totalTenantsSql = "SELECT COUNT(DISTINCT r.tenant_id) FROM dbo.rooms r JOIN dbo.facilities f ON r.facility_id = f.facility_id JOIN dbo.users u ON r.tenant_id = u.user_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL AND u.deleted_at IS NULL";

        String totalDependentsSql = "SELECT COUNT(*) FROM dbo.dependents d JOIN dbo.rooms r ON d.tenant_id = r.tenant_id JOIN dbo.facilities f ON r.facility_id = f.facility_id WHERE f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL AND d.deleted_at IS NULL";

        String pendingTicketsSql = "SELECT COUNT(*) FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL " +
                "WHERE f.manager_id = ? AND req.deleted_at IS NULL AND req.status IN ('NEW', 'PENDING', 'RECEIVED', 'ASSIGNED', 'IN_PROGRESS') " +
                "AND (u.role = 'OPERATOR' OR (u.role = 'TENANT' AND req.assigned_staff_id = f.manager_id))";

        String sentNotificationsSql = "SELECT COUNT(*) FROM dbo.notifications WHERE created_by = ? AND deleted_at IS NULL";

        String activeContractsSql = "SELECT COUNT(*) FROM dbo.contracts c " +
                "JOIN dbo.rooms r ON c.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND c.deleted_at IS NULL AND c.status = 'ACTIVE'";

        String unpaidInvoicesSql = "SELECT COUNT(*) FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND i.deleted_at IS NULL AND i.status = 'UNPAID'";

        String overdueInvoicesSql = "SELECT COUNT(*) FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND i.deleted_at IS NULL AND i.status = 'OVERDUE'";

        String pendingPaymentsSql = "SELECT COUNT(*) FROM dbo.payments p " +
                "JOIN dbo.rooms r ON p.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND p.deleted_at IS NULL AND p.status = 'PENDING'";

        String monthlyRevenueSql = "SELECT SUM(i.total_amount) FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND i.deleted_at IS NULL AND i.status = 'PAID' " +
                "AND MONTH(i.created_at) = MONTH(GETDATE()) AND YEAR(i.created_at) = YEAR(GETDATE())";

        String totalOutstandingSql = "SELECT SUM(i.total_amount) FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "WHERE f.manager_id = ? AND i.deleted_at IS NULL AND i.status IN ('UNPAID', 'OVERDUE')";

        String ticketStatsSql = "SELECT req.status, COUNT(*) AS count FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL " +
                "WHERE f.manager_id = ? AND req.deleted_at IS NULL " +
                "AND (u.role = 'OPERATOR' OR (u.role = 'TENANT' AND req.assigned_staff_id = f.manager_id)) " +
                "GROUP BY req.status";

        String recentTicketsSql = "SELECT req.request_id, req.code, req.title, req.status, req.created_at, r.code AS room_code, u.role AS sender_role " +
                "FROM dbo.requests req " +
                "JOIN dbo.users u ON req.sender_id = u.user_id " +
                "LEFT JOIN dbo.rooms r ON (u.role = 'TENANT' AND u.user_id = r.tenant_id AND r.deleted_at IS NULL) " +
                "LEFT JOIN dbo.facilities f ON ((u.role = 'TENANT' AND r.facility_id = f.facility_id) OR (u.role = 'OPERATOR' AND req.code LIKE 'REQ-' + f.code + '%')) AND f.deleted_at IS NULL " +
                "WHERE f.manager_id = ? AND req.deleted_at IS NULL " +
                "AND (u.role = 'OPERATOR' OR (u.role = 'TENANT' AND req.assigned_staff_id = f.manager_id)) " +
                "ORDER BY req.request_id DESC OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // 1. Facility Info
            try (PreparedStatement ps = conn.prepareStatement(facilitySql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        facilityName   = rs.getString("name");
                        facilityCode   = rs.getString("code");
                        facilityStatus = rs.getString("status");
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

            // 4b. Active contracts & financial stats
            try (PreparedStatement ps = conn.prepareStatement(activeContractsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) activeContracts = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(unpaidInvoicesSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) unpaidInvoices = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(overdueInvoicesSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) overdueInvoices = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(pendingPaymentsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) pendingPayments = rs.getInt(1);
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(monthlyRevenueSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        java.math.BigDecimal val = rs.getBigDecimal(1);
                        if (val != null) monthlyRevenue = val;
                    }
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(totalOutstandingSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        java.math.BigDecimal val = rs.getBigDecimal(1);
                        if (val != null) totalOutstanding = val;
                    }
                }
            }

            // 5. Ticket stats by status
            try (PreparedStatement ps = conn.prepareStatement(ticketStatsSql)) {
                ps.setInt(1, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String status = rs.getString("status");
                        int count = rs.getInt("count");
                        if ("NEW".equals(status) || "PENDING".equals(status)) {
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
                        if ("NEW".equals(status) || "PENDING".equals(status)) {
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

        stats.put("facilityName", facilityName);
        stats.put("facilityCode", facilityCode);
        stats.put("facilityStatus", facilityStatus);
        stats.put("totalRooms", totalRooms);
        stats.put("occupiedRooms", occupiedRooms);
        stats.put("vacantRooms", vacantRooms);
        stats.put("totalTenants", totalTenants);
        stats.put("totalDependents", totalDependents);
        stats.put("pendingTickets", pendingTickets);
        stats.put("sentNotifications", sentNotifications);
        stats.put("occupancyRate", occupancyRate);
        stats.put("activeContracts", activeContracts);
        stats.put("unpaidInvoices", unpaidInvoices);
        stats.put("overdueInvoices", overdueInvoices);
        stats.put("pendingPayments", pendingPayments);
        stats.put("monthlyRevenue", monthlyRevenue);
        stats.put("totalOutstanding", totalOutstanding);
        stats.put("ticketCountNew", ticketCountNew);
        stats.put("ticketCountInProgress", ticketCountInProgress);
        stats.put("ticketCountDone", ticketCountDone);
        stats.put("ticketCountRejected", ticketCountRejected);
        stats.put("recentTickets", recentTickets);

        return stats;
    }
}
