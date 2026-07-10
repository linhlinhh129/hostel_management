package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.RevenueActivityDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service tổng hợp dữ liệu cho Admin Dashboard.
 * Gom các lời gọi nhiều DAO vào một tầng duy nhất, giữ Servlet sạch.
 */
public interface AdminDashboardService {

    /** Tổng doanh thu tháng (hóa đơn PAID). */
    BigDecimal getMonthlyRevenue(String period);

    /** Tổng số cơ sở (tất cả trạng thái). */
    int getTotalFacilities();

    /** Số cơ sở đang ACTIVE. */
    int getActiveFacilities();

    /** Tổng nhân sự (MANAGER + OPERATOR). */
    int getTotalPersonnel();

    /** Số nhân sự vai trò MANAGER. */
    int getManagerCount();

    /** Số nhân sự vai trò OPERATOR. */
    int getOperatorCount();

    /** Tổng thông báo. */
    int getTotalNotifications();

    /** Số nhật ký hôm nay. */
    int getTodayAuditLogs();

    /** Doanh thu theo từng cơ sở trong tháng. */
    List<FacilityRevenueStatDTO> getFacilityRevenueStats(String period);

    /** 5 hoạt động gần đây từ Audit Log. */
    List<RevenueActivityDTO> getRecentActivities();
}
