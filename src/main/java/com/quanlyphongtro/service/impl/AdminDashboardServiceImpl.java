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

    // ── Cache Implementation ────────────────────────────────────────────────
    private long lastCacheTime = 0;
    private String lastPeriod = "";
    
    private BigDecimal monthlyRevenue = BigDecimal.ZERO;
    private int totalFacilities;
    private int activeFacilities;
    private int totalPersonnel;
    private int managerCount;
    private int operatorCount;
    private int totalNotifications;
    private int todayAuditLogs;
    private List<FacilityRevenueStatDTO> facilityRevenueStats = new ArrayList<>();
    private List<RevenueActivityDTO> recentActivities = new ArrayList<>();
    
    private final Object cacheLock = new Object();

    private void refreshCacheIfNeeded(String period) {
        String p = period == null ? "" : period;
        long now = System.currentTimeMillis();
        if (now - lastCacheTime < 60_000 && lastPeriod.equals(p)) {
            return;
        }
        synchronized (cacheLock) {
            if (System.currentTimeMillis() - lastCacheTime < 60_000 && lastPeriod.equals(p)) {
                return;
            }
            lastPeriod = p;
            
            try {
                BigDecimal val = revenueDAO.getMonthlyRevenueTotal(p);
                monthlyRevenue = val != null ? val : BigDecimal.ZERO;
            } catch (Exception e) { logger.warn("Dashboard: failed to load monthly revenue", e); monthlyRevenue = BigDecimal.ZERO; }
            
            try { totalFacilities = facilityDAO.count("", ""); }
            catch (Exception e) { logger.warn("Dashboard: failed to load totalFacilities", e); totalFacilities = 0; }
            
            try { activeFacilities = facilityDAO.count("", "ACTIVE"); }
            catch (Exception e) { logger.warn("Dashboard: failed to load activeFacilities", e); activeFacilities = 0; }
            
            try { totalPersonnel = personnelDAO.countAll(); }
            catch (Exception e) { logger.warn("Dashboard: failed to load totalPersonnel", e); totalPersonnel = 0; }
            
            try { managerCount = personnelDAO.countByRole("MANAGER"); }
            catch (Exception e) { logger.warn("Dashboard: failed to load managerCount", e); managerCount = 0; }
            
            try { operatorCount = personnelDAO.countByRole("OPERATOR"); }
            catch (Exception e) { logger.warn("Dashboard: failed to load operatorCount", e); operatorCount = 0; }
            
            try { totalNotifications = notificationDAO.count(""); }
            catch (Exception e) { logger.warn("Dashboard: failed to load totalNotifications", e); totalNotifications = 0; }
            
            try { todayAuditLogs = auditLogDAO.countToday(); }
            catch (Exception e) { logger.warn("Dashboard: failed to load todayAuditLogs", e); todayAuditLogs = 0; }
            
            try { facilityRevenueStats = revenueDAO.getFacilityRevenues(p); }
            catch (Exception e) { logger.warn("Dashboard: failed to load facilityRevenueStats", e); facilityRevenueStats = new ArrayList<>(); }
            
            List<RevenueActivityDTO> activities = new ArrayList<>();
            try {
                List<AuditLog> logs = auditLogDAO.findRecent(5);
                for (AuditLog log : logs) {
                    String actor = log.getCreatedByName() != null ? log.getCreatedByName() : "Hệ thống";
                    String desc  = buildActionDescription(log);
                    String time  = log.getCreatedAt() != null ? log.getCreatedAt().format(DTF) : "";
                    activities.add(new RevenueActivityDTO(actor, desc, time));
                }
            } catch (Exception e) { logger.warn("Dashboard: failed to load recentActivities", e); }
            recentActivities = activities;
            
            lastCacheTime = System.currentTimeMillis();
        }
    }

    @Override
    public BigDecimal getMonthlyRevenue(String period) {
        refreshCacheIfNeeded(period);
        return monthlyRevenue;
    }

    @Override
    public int getTotalFacilities() {
        refreshCacheIfNeeded(lastPeriod);
        return totalFacilities;
    }

    @Override
    public int getActiveFacilities() {
        refreshCacheIfNeeded(lastPeriod);
        return activeFacilities;
    }

    @Override
    public int getTotalPersonnel() {
        refreshCacheIfNeeded(lastPeriod);
        return totalPersonnel;
    }

    @Override
    public int getManagerCount() {
        refreshCacheIfNeeded(lastPeriod);
        return managerCount;
    }

    @Override
    public int getOperatorCount() {
        refreshCacheIfNeeded(lastPeriod);
        return operatorCount;
    }

    @Override
    public int getTotalNotifications() {
        refreshCacheIfNeeded(lastPeriod);
        return totalNotifications;
    }

    @Override
    public int getTodayAuditLogs() {
        refreshCacheIfNeeded(lastPeriod);
        return todayAuditLogs;
    }

    @Override
    public List<FacilityRevenueStatDTO> getFacilityRevenueStats(String period) {
        refreshCacheIfNeeded(period);
        return facilityRevenueStats;
    }

    @Override
    public List<RevenueActivityDTO> getRecentActivities() {
        refreshCacheIfNeeded(lastPeriod);
        return recentActivities;
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
