package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.dao.RevenueDAO;
import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.RevenueActivityDTO;
import com.quanlyphongtro.model.AuditLog;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "AdminDashboardServlet", urlPatterns = "/admin/dashboard")
public class AdminDashboardServlet extends BaseServlet {

    private final FacilityDAO     facilityDAO     = new FacilityDAO();
    private final PersonnelDAO    personnelDAO    = new PersonnelDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuditLogDAO     auditLogDAO     = new AuditLogDAO();
    private final RevenueDAO      revenueDAO      = new RevenueDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        LocalDate now = LocalDate.now();
        String currentPeriod = String.format("%02d/%d", now.getMonthValue(), now.getYear());
        req.setAttribute("currentPeriodLabel", currentPeriod);

        // Revenue KPI
        BigDecimal monthlyRevenue = BigDecimal.ZERO;
        try {
            monthlyRevenue = revenueDAO.getMonthlyRevenueTotal(currentPeriod);
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load monthly revenue", e);
        }
        req.setAttribute("monthlyRevenue", monthlyRevenue);

        // Facility counts
        int totalFacilities  = 0;
        int activeFacilities = 0;
        try {
            totalFacilities  = facilityDAO.count("", "");
            activeFacilities = facilityDAO.count("", "ACTIVE");
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load facility counts", e);
        }
        req.setAttribute("totalFacilities",  totalFacilities);
        req.setAttribute("activeFacilities", activeFacilities);

        // Personnel counts
        int totalPersonnel = 0;
        int managerCount   = 0;
        int operatorCount  = 0;
        try {
            totalPersonnel = personnelDAO.countAll();
            managerCount   = personnelDAO.countByRole("MANAGER");
            operatorCount  = personnelDAO.countByRole("OPERATOR");
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load personnel counts", e);
        }
        req.setAttribute("totalPersonnel", totalPersonnel);
        req.setAttribute("managerCount",   managerCount);
        req.setAttribute("operatorCount",  operatorCount);

        // Notification count
        int totalNotifications = 0;
        try {
            totalNotifications = notificationDAO.count("");
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load notification count", e);
        }
        req.setAttribute("totalNotifications", totalNotifications);

        // Today's audit log count
        int todayAuditLogs = 0;
        try {
            todayAuditLogs = auditLogDAO.countToday();
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load today audit count", e);
        }
        req.setAttribute("todayAuditLogs", todayAuditLogs);

        // Facility revenue stats
        List<FacilityRevenueStatDTO> facilityRevenueStats = new ArrayList<>();
        try {
            facilityRevenueStats = revenueDAO.getFacilityRevenues(currentPeriod);
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load facility revenue stats", e);
        }
        req.setAttribute("facilityRevenueStats", facilityRevenueStats);

        // Recent activities (last 5 audit log entries)
        List<RevenueActivityDTO> recentActivities = new ArrayList<>();
        try {
            List<AuditLog> recentLogs = auditLogDAO.findRecent(5);
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (AuditLog log : recentLogs) {
                String actor = log.getCreatedByName() != null ? log.getCreatedByName() : "Hệ thống";
                String desc  = buildActionDescription(log);
                String time  = log.getCreatedAt() != null
                    ? log.getCreatedAt().format(dtf) : "";
                recentActivities.add(new RevenueActivityDTO(actor, desc, time));
            }
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load recent activities", e);
        }
        req.setAttribute("recentActivities", recentActivities);

        // Room occupancy stats (toàn hệ thống)
        int totalRooms    = 0;
        int occupiedRooms = 0;
        int availableRooms = 0;
        try {
            String sql = "SELECT " +
                "COUNT(*) AS total, " +
                "SUM(CASE WHEN tenant_id IS NOT NULL THEN 1 ELSE 0 END) AS occupied, " +
                "SUM(CASE WHEN tenant_id IS NULL THEN 1 ELSE 0 END) AS available " +
                "FROM dbo.rooms WHERE deleted_at IS NULL";
            try (java.sql.Connection conn = com.quanlyphongtro.util.DatabaseUtil.getConnection();
                 java.sql.PreparedStatement ps = conn.prepareStatement(sql);
                 java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalRooms     = rs.getInt("total");
                    occupiedRooms  = rs.getInt("occupied");
                    availableRooms = rs.getInt("available");
                }
            }
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load room occupancy", e);
        }
        int occupancyRate = totalRooms == 0 ? 0 : (occupiedRooms * 100) / totalRooms;
        req.setAttribute("totalRooms",     totalRooms);
        req.setAttribute("occupiedRooms",  occupiedRooms);
        req.setAttribute("availableRooms", availableRooms);
        req.setAttribute("occupancyRate",  occupancyRate);

        req.getRequestDispatcher("/WEB-INF/views/admin/dashboard.jsp").forward(req, resp);
    }

    private String buildActionDescription(AuditLog log) {
        if (log == null) return "";
        String action     = log.getAction()     != null ? log.getAction()     : "";
        String entityType = log.getEntityType() != null ? log.getEntityType() : "";
        
        String entityTypeVN;
        switch (entityType) {
            case "facilities":    entityTypeVN = "cơ sở"; break;
            case "rooms":         entityTypeVN = "phòng"; break;
            case "users":         entityTypeVN = "nhân sự"; break;
            case "notifications": entityTypeVN = "thông báo"; break;
            case "invoices":      entityTypeVN = "hóa đơn"; break;
            case "payments":      entityTypeVN = "thanh toán"; break;
            case "requests":      entityTypeVN = "yêu cầu"; break;
            case "meter_readings":entityTypeVN = "số điện nước"; break;
            default:              entityTypeVN = "dữ liệu"; break;
        }

        return switch (action) {
            case "CREATE"             -> "Tạo mới " + entityTypeVN;
            case "UPDATE"             -> "Cập nhật " + entityTypeVN;
            case "ACTIVATE"           -> "Kích hoạt " + entityTypeVN;
            case "DEACTIVATE"         -> "Vô hiệu hóa " + entityTypeVN;
            case "CREATE_EMPLOYEE"    -> "Tạo nhân sự";
            case "UPDATE_EMPLOYEE"    -> "Cập nhật nhân sự";
            case "LOCK_EMPLOYEE"      -> "Khóa tài khoản nhân sự";
            case "UNLOCK_EMPLOYEE"    -> "Mở khóa tài khoản nhân sự";
            case "UPDATE_ELECTRICITY" -> "Cập nhật số điện";
            case "UPDATE_WATER"       -> "Cập nhật số nước";
            case "UPDATE_STATUS"      -> "Đổi trạng thái " + entityTypeVN;
            case "UPDATE_AREA"        -> "Cập nhật diện tích phòng";
            default                   -> "Thao tác " + entityTypeVN;
        };
    }
}
