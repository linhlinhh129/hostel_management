package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.*;
import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.RevenueActivityDTO;
import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.service.impl.AdminDashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit test cho AdminDashboardServiceImpl.
 *
 * Tất cả method của service wrap DAO call trong try/catch,
 * nên test cả 2 path:
 *   1. DAO trả dữ liệu hợp lệ → service trả đúng giá trị.
 *   2. DAO ném exception → service trả về giá trị mặc định an toàn (0 / [] / ZERO).
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminDashboardServiceImpl — Admin Dashboard")
class AdminDashboardServiceImplTest {

    private FacilityDAO     mockFacilityDAO;
    private PersonnelDAO    mockPersonnelDAO;
    private NotificationDAO mockNotificationDAO;
    private AuditLogDAO     mockAuditLogDAO;
    private RevenueDAO      mockRevenueDAO;

    private AdminDashboardServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockFacilityDAO     = mock(FacilityDAO.class);
        mockPersonnelDAO    = mock(PersonnelDAO.class);
        mockNotificationDAO = mock(NotificationDAO.class);
        mockAuditLogDAO     = mock(AuditLogDAO.class);
        mockRevenueDAO      = mock(RevenueDAO.class);

        service = new AdminDashboardServiceImpl();
        injectField("facilityDAO",     mockFacilityDAO);
        injectField("personnelDAO",     mockPersonnelDAO);
        injectField("notificationDAO",  mockNotificationDAO);
        injectField("auditLogDAO",      mockAuditLogDAO);
        injectField("revenueDAO",       mockRevenueDAO);
    }

    private void injectField(String fieldName, Object mock) throws Exception {
        Field f = AdminDashboardServiceImpl.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(service, mock);
    }

    private AuditLog buildAuditLog(String action, String entityType, String createdByName) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setCreatedByName(createdByName);
        log.setCreatedAt(LocalDateTime.of(2025, 6, 15, 10, 30, 0));
        return log;
    }

    // =========================================================
    // getMonthlyRevenue()
    // =========================================================
    @Nested
    @DisplayName("getMonthlyRevenue()")
    class GetMonthlyRevenue {

        @Test
        @DisplayName("trả về doanh thu đúng khi DAO có dữ liệu")
        void returnsRevenueFromDAO() {
            when(mockRevenueDAO.getMonthlyRevenueTotal("06/2025"))
                    .thenReturn(new BigDecimal("50000000"));

            BigDecimal result = service.getMonthlyRevenue("06/2025");

            assertThat(result).isEqualByComparingTo(new BigDecimal("50000000"));
        }

        @Test
        @DisplayName("trả về ZERO khi DAO trả null")
        void daoReturnsNull_returnsZero() {
            when(mockRevenueDAO.getMonthlyRevenueTotal("06/2025")).thenReturn(null);

            BigDecimal result = service.getMonthlyRevenue("06/2025");

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("trả về ZERO khi DAO ném exception (defensive fallback)")
        void daoThrowsException_returnsZero() {
            when(mockRevenueDAO.getMonthlyRevenueTotal(any()))
                    .thenThrow(new RuntimeException("DB lỗi"));

            BigDecimal result = service.getMonthlyRevenue("06/2025");

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // =========================================================
    // getTotalFacilities() / getActiveFacilities()
    // =========================================================
    @Nested
    @DisplayName("getTotalFacilities() / getActiveFacilities()")
    class FacilityCounts {

        @Test
        @DisplayName("getTotalFacilities trả về số cơ sở đúng")
        void totalFacilities_returnsCount() {
            when(mockFacilityDAO.count("", "")).thenReturn(8);

            assertThat(service.getTotalFacilities()).isEqualTo(8);
        }

        @Test
        @DisplayName("getActiveFacilities trả về số cơ sở ACTIVE")
        void activeFacilities_returnsActiveCount() {
            when(mockFacilityDAO.count("", "ACTIVE")).thenReturn(5);

            assertThat(service.getActiveFacilities()).isEqualTo(5);
        }

        @Test
        @DisplayName("getTotalFacilities trả về 0 khi DAO ném exception")
        void totalFacilities_daoError_returnsZero() {
            when(mockFacilityDAO.count("", "")).thenThrow(new RuntimeException("timeout"));

            assertThat(service.getTotalFacilities()).isEqualTo(0);
        }

        @Test
        @DisplayName("getActiveFacilities trả về 0 khi DAO ném exception")
        void activeFacilities_daoError_returnsZero() {
            when(mockFacilityDAO.count("", "ACTIVE")).thenThrow(new RuntimeException("timeout"));

            assertThat(service.getActiveFacilities()).isEqualTo(0);
        }
    }

    // =========================================================
    // getTotalPersonnel() / getManagerCount() / getOperatorCount()
    // =========================================================
    @Nested
    @DisplayName("Personnel counts")
    class PersonnelCounts {

        @Test
        @DisplayName("getTotalPersonnel trả về tổng nhân sự")
        void totalPersonnel_returnsCount() {
            when(mockPersonnelDAO.countAll()).thenReturn(12);

            assertThat(service.getTotalPersonnel()).isEqualTo(12);
        }

        @Test
        @DisplayName("getManagerCount trả về số MANAGER")
        void managerCount_returnsCount() {
            when(mockPersonnelDAO.countByRole("MANAGER")).thenReturn(4);

            assertThat(service.getManagerCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("getOperatorCount trả về số OPERATOR")
        void operatorCount_returnsCount() {
            when(mockPersonnelDAO.countByRole("OPERATOR")).thenReturn(6);

            assertThat(service.getOperatorCount()).isEqualTo(6);
        }

        @Test
        @DisplayName("getTotalPersonnel trả về 0 khi DAO ném exception")
        void totalPersonnel_daoError_returnsZero() {
            when(mockPersonnelDAO.countAll()).thenThrow(new RuntimeException());

            assertThat(service.getTotalPersonnel()).isEqualTo(0);
        }
    }

    // =========================================================
    // getTodayAuditLogs() / getTotalNotifications()
    // =========================================================
    @Nested
    @DisplayName("Audit & Notification counts")
    class AuditAndNotificationCounts {

        @Test
        @DisplayName("getTodayAuditLogs trả về số nhật ký hôm nay")
        void todayAuditLogs_returnsCount() {
            when(mockAuditLogDAO.countToday()).thenReturn(17);

            assertThat(service.getTodayAuditLogs()).isEqualTo(17);
        }

        @Test
        @DisplayName("getTotalNotifications trả về tổng thông báo")
        void totalNotifications_returnsCount() {
            when(mockNotificationDAO.count("")).thenReturn(33);

            assertThat(service.getTotalNotifications()).isEqualTo(33);
        }

        @Test
        @DisplayName("getTodayAuditLogs trả về 0 khi DAO ném exception")
        void todayAuditLogs_daoError_returnsZero() {
            when(mockAuditLogDAO.countToday()).thenThrow(new RuntimeException());

            assertThat(service.getTodayAuditLogs()).isEqualTo(0);
        }
    }

    // =========================================================
    // getFacilityRevenueStats()
    // =========================================================
    @Nested
    @DisplayName("getFacilityRevenueStats()")
    class FacilityRevenueStats {

        @Test
        @DisplayName("trả về danh sách thống kê từ DAO")
        void returnsStatsFromDAO() {
            FacilityRevenueStatDTO stat = new FacilityRevenueStatDTO();
            stat.setFacilityName("Cơ sở A");
            stat.setTotalRevenue(new BigDecimal("12000000"));
            when(mockRevenueDAO.getFacilityRevenues("06/2025")).thenReturn(List.of(stat));

            List<FacilityRevenueStatDTO> result = service.getFacilityRevenueStats("06/2025");

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getFacilityName()).isEqualTo("Cơ sở A");
        }

        @Test
        @DisplayName("trả về list rỗng khi DAO ném exception")
        void daoError_returnsEmptyList() {
            when(mockRevenueDAO.getFacilityRevenues(any())).thenThrow(new RuntimeException());

            List<FacilityRevenueStatDTO> result = service.getFacilityRevenueStats("06/2025");

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getRecentActivities() — business logic buildActionDescription
    // =========================================================
    @Nested
    @DisplayName("getRecentActivities() — buildActionDescription")
    class RecentActivities {

        @Test
        @DisplayName("ánh xạ đúng: action=CREATE + entityType=invoices")
        void create_invoices_mapsCorrectly() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("CREATE", "invoices", "Admin A")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getActorName()).isEqualTo("Admin A");
            assertThat(result.get(0).getActionDescription()).isEqualTo("Tạo mới hóa đơn");
        }

        @Test
        @DisplayName("ánh xạ đúng: action=UPDATE + entityType=rooms")
        void update_rooms_mapsCorrectly() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("UPDATE", "rooms", "Manager B")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActionDescription()).isEqualTo("Cập nhật phòng");
        }

        @Test
        @DisplayName("ánh xạ đúng: action=DELETE + entityType=facilities → 'Thao tác cơ sở'")
        void delete_facilities_mapsToDefaultAction() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("DELETE", "facilities", "Admin")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActionDescription()).isEqualTo("Thao tác cơ sở");
        }

        @Test
        @DisplayName("ánh xạ đúng: action=LOCK_EMPLOYEE → 'Khóa tài khoản nhân sự'")
        void lockEmployee_mapsCorrectly() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("LOCK_EMPLOYEE", "users", "System")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActionDescription()).isEqualTo("Khóa tài khoản nhân sự");
        }

        @Test
        @DisplayName("ánh xạ đúng: action=CREATE_EMPLOYEE → 'Tạo nhân sự'")
        void createEmployee_mapsCorrectly() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("CREATE_EMPLOYEE", "users", null)));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            // actor null → dùng "Hệ thống"
            assertThat(result.get(0).getActorName()).isEqualTo("Hệ thống");
            assertThat(result.get(0).getActionDescription()).isEqualTo("Tạo nhân sự");
        }

        @Test
        @DisplayName("actor null → dùng 'Hệ thống' làm actorName")
        void nullCreatedByName_defaultsToSystem() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("UPDATE", "invoices", null)));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActorName()).isEqualTo("Hệ thống");
        }

        @Test
        @DisplayName("ánh xạ entityType không xác định → 'dữ liệu'")
        void unknownEntityType_mapsToData() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("CREATE", "unknown_table", "Admin")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActionDescription()).isEqualTo("Tạo mới dữ liệu");
        }

        @Test
        @DisplayName("ánh xạ action=UPDATE_ELECTRICITY → 'Cập nhật số điện'")
        void updateElectricity_mapsCorrectly() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("UPDATE_ELECTRICITY", "meter_readings", "Op A")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActionDescription()).isEqualTo("Cập nhật số điện");
        }

        @Test
        @DisplayName("ánh xạ action=ACTIVATE + entityType=notifications → 'Kích hoạt thông báo'")
        void activate_notifications_mapsCorrectly() {
            when(mockAuditLogDAO.findRecent(5))
                    .thenReturn(List.of(buildAuditLog("ACTIVATE", "notifications", "Admin")));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result.get(0).getActionDescription()).isEqualTo("Kích hoạt thông báo");
        }

        @Test
        @DisplayName("trả về list rỗng khi DAO ném exception")
        void daoError_returnsEmptyList() {
            when(mockAuditLogDAO.findRecent(5)).thenThrow(new RuntimeException("DB lỗi"));

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về tối đa 5 hoạt động gần đây")
        void returnsAtMostFiveActivities() {
            List<AuditLog> logs = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                logs.add(buildAuditLog("UPDATE", "rooms", "Actor " + i));
            }
            when(mockAuditLogDAO.findRecent(5)).thenReturn(logs);

            List<RevenueActivityDTO> result = service.getRecentActivities();

            assertThat(result).hasSize(5);
        }
    }
}
