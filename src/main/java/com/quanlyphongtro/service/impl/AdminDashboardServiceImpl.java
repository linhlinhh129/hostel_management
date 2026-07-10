package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.dao.RevenueDAO;
import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.RevenueActivityDTO;
import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.service.AdminDashboardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AdminDashboardServiceImpl implements AdminDashboardService {

    private static final Logger logger = LoggerFactory.getLogger(AdminDashboardServiceImpl.class);
    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final FacilityDAO     facilityDAO     = new FacilityDAO();
    private final PersonnelDAO    personnelDAO    = new PersonnelDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final AuditLogDAO     auditLogDAO     = new AuditLogDAO();
    private final RevenueDAO      revenueDAO      = new RevenueDAO();

    @Override
    public BigDecimal getMonthlyRevenue(String period) {
        try {
            BigDecimal val = revenueDAO.getMonthlyRevenueTotal(period);
            return val != null ? val : BigDecimal.ZERO;
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load monthly revenue for period={}", period, e);
            return BigDecimal.ZERO;
        }
    }

    @Override
    public int getTotalFacilities() {
        try { return facilityDAO.count("", ""); }
        catch (Exception e) { logger.warn("Dashboard: failed to load totalFacilities", e); return 0; }
    }

    @Override
    public int getActiveFacilities() {
        try { return facilityDAO.count("", "ACTIVE"); }
        catch (Exception e) { logger.warn("Dashboard: failed to load activeFacilities", e); return 0; }
    }

    @Override
    public int getTotalPersonnel() {
        try { return personnelDAO.countAll(); }
        catch (Exception e) { logger.warn("Dashboard: failed to load totalPersonnel", e); return 0; }
    }

    @Override
    public int getManagerCount() {
        try { return personnelDAO.countByRole("MANAGER"); }
        catch (Exception e) { logger.warn("Dashboard: failed to load managerCount", e); return 0; }
    }

    @Override
    public int getOperatorCount() {
        try { return personnelDAO.countByRole("OPERATOR"); }
        catch (Exception e) { logger.warn("Dashboard: failed to load operatorCount", e); return 0; }
    }

    @Override
    public int getTotalNotifications() {
        try { return notificationDAO.count(""); }
        catch (Exception e) { logger.warn("Dashboard: failed to load totalNotifications", e); return 0; }
    }

    @Override
    public int getTodayAuditLogs() {
        try { return auditLogDAO.countToday(); }
        catch (Exception e) { logger.warn("Dashboard: failed to load todayAuditLogs", e); return 0; }
    }

    @Override
    public List<FacilityRevenueStatDTO> getFacilityRevenueStats(String period) {
        try { return revenueDAO.getFacilityRevenues(period); }
        catch (Exception e) {
            logger.warn("Dashboard: failed to load facilityRevenueStats for period={}", period, e);
            return new ArrayList<>();
        }
    }

    @Override
    public List<RevenueActivityDTO> getRecentActivities() {
        List<RevenueActivityDTO> result = new ArrayList<>();
        try {
            List<AuditLog> logs = auditLogDAO.findRecent(5);
            for (AuditLog log : logs) {
                String actor = log.getCreatedByName() != null ? log.getCreatedByName() : "Hệ thống";
                String desc  = buildActionDescription(log);
                String time  = log.getCreatedAt() != null ? log.getCreatedAt().format(DTF) : "";
                result.add(new RevenueActivityDTO(actor, desc, time));
            }
        } catch (Exception e) {
            logger.warn("Dashboard: failed to load recentActivities", e);
        }
        return result;
    }

    // ── private helper ────────────────────────────────────────────────────

    private String buildActionDescription(AuditLog log) {
        if (log == null) return "";
        String action     = log.getAction()     != null ? log.getAction()     : "";
        String entityType = log.getEntityType() != null ? log.getEntityType() : "";

        String entityVN;
        switch (entityType) {
            case "facilities":     entityVN = "cơ sở";         break;
            case "rooms":          entityVN = "phòng";          break;
            case "users":          entityVN = "nhân sự";        break;
            case "notifications":  entityVN = "thông báo";      break;
            case "invoices":       entityVN = "hóa đơn";        break;
            case "payments":       entityVN = "thanh toán";     break;
            case "requests":       entityVN = "yêu cầu";        break;
            case "meter_readings": entityVN = "số điện nước";   break;
            default:               entityVN = "dữ liệu";        break;
        }

        return switch (action) {
            case "CREATE"             -> "Tạo mới " + entityVN;
            case "UPDATE"             -> "Cập nhật " + entityVN;
            case "ACTIVATE"           -> "Kích hoạt " + entityVN;
            case "DEACTIVATE"         -> "Vô hiệu hóa " + entityVN;
            case "CREATE_EMPLOYEE"    -> "Tạo nhân sự";
            case "UPDATE_EMPLOYEE"    -> "Cập nhật nhân sự";
            case "LOCK_EMPLOYEE"      -> "Khóa tài khoản nhân sự";
            case "UNLOCK_EMPLOYEE"    -> "Mở khóa tài khoản nhân sự";
            case "UPDATE_ELECTRICITY" -> "Cập nhật số điện";
            case "UPDATE_WATER"       -> "Cập nhật số nước";
            case "UPDATE_STATUS"      -> "Đổi trạng thái " + entityVN;
            case "UPDATE_AREA"        -> "Cập nhật diện tích phòng";
            default                   -> "Thao tác " + entityVN;
        };
    }
}
