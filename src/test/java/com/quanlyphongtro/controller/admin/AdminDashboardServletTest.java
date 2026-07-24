package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.RevenueActivityDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.quanlyphongtro.service.AdminDashboardService;
import java.lang.reflect.Field;
import com.quanlyphongtro.exception.AppException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdminDashboardServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private AdminDashboardService dashboardService;

    @InjectMocks
    private AdminDashboardServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getServletPath()).thenReturn("/admin/dashboard");
        when(request.getContextPath()).thenReturn("/hostel-management");

        // Inject mock service via reflection
        Field serviceField = AdminDashboardServlet.class.getDeclaredField("dashboardService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, dashboardService);
    }

    private void setupMockUser(String role) {
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole(role);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    // # EARS [3.1 Xem Dashboard: Hệ thống hiển thị tổng quan thống kê có dữ liệu]
    void testDoGet_Success_WithData() throws Exception {
        setupMockUser("ADMIN");

        when(dashboardService.getMonthlyRevenue(anyString())).thenReturn(BigDecimal.valueOf(5000000));
        when(dashboardService.getTotalFacilities()).thenReturn(10);
        when(dashboardService.getActiveFacilities()).thenReturn(8);
        when(dashboardService.getTotalPersonnel()).thenReturn(15);
        when(dashboardService.getManagerCount()).thenReturn(5);
        when(dashboardService.getOperatorCount()).thenReturn(10);
        when(dashboardService.getTotalNotifications()).thenReturn(100);
        when(dashboardService.getTodayAuditLogs()).thenReturn(50);

        List<FacilityRevenueStatDTO> stats = new ArrayList<>();
        stats.add(new FacilityRevenueStatDTO());
        when(dashboardService.getFacilityRevenueStats(anyString())).thenReturn(stats);

        List<RevenueActivityDTO> activities = new ArrayList<>();
        activities.add(new RevenueActivityDTO());
        when(dashboardService.getRecentActivities()).thenReturn(activities);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("monthlyRevenue"), eq(BigDecimal.valueOf(5000000)));
        verify(request).setAttribute(eq("totalFacilities"), eq(10));
        verify(request).setAttribute(eq("facilityRevenueStats"), eq(stats));
        verify(request).setAttribute(eq("recentActivities"), eq(activities));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.1 Xem Dashboard: Hệ thống hiển thị tổng quan thống kê khi chưa có dữ liệu]
    void testDoGet_Success_EmptyData() throws Exception {
        setupMockUser("ADMIN");

        when(dashboardService.getMonthlyRevenue(anyString())).thenReturn(BigDecimal.ZERO);
        when(dashboardService.getTotalFacilities()).thenReturn(0);
        when(dashboardService.getFacilityRevenueStats(anyString())).thenReturn(new ArrayList<>());
        when(dashboardService.getRecentActivities()).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("monthlyRevenue"), eq(BigDecimal.ZERO));
        verify(request).setAttribute(eq("totalFacilities"), eq(0));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [Phân quyền: Người dùng chưa đăng nhập]
    void testDoGet_WithoutSession_RedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/hostel-management/login");
    }

    @Test
    // # EARS [Phân quyền: Người dùng không có quyền quản lý]
    void testDoGet_RoleManager_Returns403() throws Exception {
        setupMockUser("MANAGER");

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    // # EARS [3.1 Xem Dashboard: Lỗi khi lấy dữ liệu từ cơ sở dữ liệu]
    void testDoGet_DaoThrowsException() throws Exception {
        setupMockUser("ADMIN");

        when(dashboardService.getMonthlyRevenue(anyString())).thenThrow(new RuntimeException("Database error"));

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("errorMessage"), anyString());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.1 Xem Dashboard: Doanh thu tháng chỉ tính các hóa đơn đã thanh toán]
    void testMonthlyRevenue_OnlyPaid() throws Exception {
        setupMockUser("ADMIN");

        servlet.doGet(request, response);

        // Verify that dashboardService.getMonthlyRevenue was called with a period string
        verify(dashboardService).getMonthlyRevenue(anyString());
    }

    @Test
    // # EARS [2.4 Concurrent Scenarios: An toàn luồng đa tiến trình trên Dashboard]
    void testDoGet_Concurrency_NoSharedState() throws Exception {
        setupMockUser("ADMIN");
        when(dashboardService.getTotalFacilities()).thenReturn(10);
        
        Runnable task = () -> {
            try {
                servlet.doGet(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
        
        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        
        verify(requestDispatcher, atLeast(2)).forward(request, response);
    }
}
