package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.FacilityRevenueStatDTO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.SystemRevenueDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.RevenueService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminRevenueServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private RevenueService mockRevenueService;

    private AdminRevenueServlet servlet;
    private String currentPeriod;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminRevenueServlet();
        servlet.init();

        Field field = AdminRevenueServlet.class.getDeclaredField("revenueService");
        field.setAccessible(true);
        field.set(servlet, mockRevenueService);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        
        LocalDate now = LocalDate.now();
        currentPeriod = String.format("%02d/%d", now.getMonthValue(), now.getYear());
    }

    private void setupMockUser(String role) {
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole(role);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Admin truy cập màn hình index tổng hợp doanh thu]
    void testDoGet_Index_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue");

        SystemRevenueDTO sysRev = new SystemRevenueDTO();
        when(mockRevenueService.getSystemRevenue(currentPeriod)).thenReturn(sysRev);
        when(mockRevenueService.getFacilityRevenues(currentPeriod)).thenReturn(new ArrayList<>());
        when(mockRevenueService.getRevenueTrend(6)).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(request).setAttribute("systemRevenue", sysRev);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Phân trang danh sách cơ sở]
    void testDoGet_ByFacility_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue/by-facility");
        when(request.getParameter("page")).thenReturn("2");

        when(mockRevenueService.countFacilitiesWithRevenue(currentPeriod)).thenReturn(15);
        List<FacilityRevenueStatDTO> items = new ArrayList<>();
        when(mockRevenueService.getFacilityRevenuesPaged(currentPeriod, 2, 10)).thenReturn(items);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("page"), any(PageDTO.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Xem dữ liệu theo kỳ giảm dần]
    void testDoGet_ByPeriod_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue/by-period");
        when(request.getParameter("months")).thenReturn("6");

        List<FacilityRevenueStatDTO> trends = new ArrayList<>();
        when(mockRevenueService.getRevenueTrend(6)).thenReturn(trends);

        servlet.doGet(request, response);

        verify(request).setAttribute("periodRevenues", trends);
        verify(request).setAttribute("selectedMonths", 6);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Chọn khoảng thời gian hợp lệ YYYY-MM tự chuyển thành MM/yyyy]
    void testPeriodParsing_YYYYMM_to_MMyyyy() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue");
        when(request.getParameter("period")).thenReturn("2026-07");

        servlet.doGet(request, response);

        verify(mockRevenueService).getSystemRevenue("07/2026");
        verify(request).setAttribute("selectedPeriod", "07/2026");
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Ngày bắt đầu lớn hơn ngày kết thúc / lỗi từ service]
    void testDoGet_InvalidDateRange() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue");
        when(request.getParameter("period")).thenReturn("00/0000");

        doThrow(new ValidationException("INVALID_DATE_RANGE")).when(mockRevenueService).getSystemRevenue(anyString());

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response); 
    }

    @Test
    // # EARS [3.2 Phân quyền: Từ chối truy cập báo cáo nếu không phải ADMIN]
    void testAuth_Unauthorized_Forbidden() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue/by-unknown");
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Không có dữ liệu doanh thu trả về rỗng không văng NullPointer]
    void testDoGet_EmptyData() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue");
        
        SystemRevenueDTO emptyDTO = new SystemRevenueDTO();
        emptyDTO.setTotalRevenue(BigDecimal.ZERO);
        when(mockRevenueService.getSystemRevenue(currentPeriod)).thenReturn(emptyDTO);

        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Tham số period bị thiếu thì lấy tháng hiện tại]
    void testDoGet_MissingPeriodParameter() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue");
        when(request.getParameter("period")).thenReturn(null);

        servlet.doGet(request, response);

        verify(mockRevenueService).getSystemRevenue(currentPeriod);
    }

    @Test
    // # EARS [3.1 Xem báo cáo doanh thu: Phân trang trang số âm hoặc mã rác]
    void testDoGet_PaginationBoundary() throws Exception {
        setupMockUser("ADMIN");
        when(request.getRequestURI()).thenReturn("/hostel/admin/revenue/by-facility");
        when(request.getParameter("page")).thenReturn("abc"); 

        servlet.doGet(request, response);

        verify(mockRevenueService).getFacilityRevenuesPaged(currentPeriod, 1, 10);
    }

    // Phase 5: Concurrent Scenarios

    @Test
    // # EARS [2.4 Concurrent Scenarios: Tranh chấp đọc báo cáo đồng thời nhiều threads]
    void testConcurrency_ReadReports() throws Exception {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getRequestURI()).thenReturn("/hostel/admin/revenue");
                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getSession(false)).thenReturn(sess);
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(1);
                    u.setRole("ADMIN");
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    
                    when(req.getParameter("period")).thenReturn("2026-10");
                    when(req.getRequestDispatcher(anyString())).thenReturn(mock(RequestDispatcher.class));

                    servlet.doGet(req, res);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(numThreads, successCount.get());
        verify(mockRevenueService, times(numThreads)).getSystemRevenue("10/2026"); 
    }
}
