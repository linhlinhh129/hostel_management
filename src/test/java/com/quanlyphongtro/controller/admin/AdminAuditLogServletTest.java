package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.model.AuditLog;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.service.AuditLogService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AdminAuditLogServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private AuditLogService mockAuditLogService;

    private AdminAuditLogServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminAuditLogServlet();
        Field field = AdminAuditLogServlet.class.getDeclaredField("auditLogService");
        field.setAccessible(true);
        field.set(servlet, mockAuditLogService);
    }

    // ==========================================
    // Phase 2: Happy Path
    // ==========================================

    @Test
    // # EARS [3.1 Xem danh sách Audit Logs: Admin mở màn hình danh sách lịch sử hệ thống mặc định]
    void testDoGet_List_SuccessWithoutFilters() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        when(request.getParameter("page")).thenReturn(null);

        when(mockAuditLogService.count(any(), any(), any(), any(), any(), any())).thenReturn(5);
        when(mockAuditLogService.list(any(), any(), any(), any(), any(), any(), eq(1), anyInt()))
                .thenReturn(Collections.emptyList());
        when(request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/list.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("currentPage", 1);
        verify(request).setAttribute("totalCount", 5);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.3 Lọc danh sách Audit Logs: lọc theo Action và Entity]
    void testDoGet_List_WithActionAndEntityFilters() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        when(request.getParameter("action")).thenReturn("CREATE");
        when(request.getParameter("entityType")).thenReturn("users");
        
        when(request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/list.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(mockAuditLogService).list(any(), any(), eq("users"), eq("CREATE"), any(), any(), eq(1), anyInt());
    }

    @Test
    // # EARS [3.2 Xem chi tiết Audit Log: mở log ID hợp lệ]
    void testDoGet_Detail_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs/123");
        when(request.getContextPath()).thenReturn("/hostel-management");
        
        AuditLog mockLog = new AuditLog();
        when(mockAuditLogService.getById(123)).thenReturn(mockLog);
        when(request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/detail.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("auditLog", mockLog);
        verify(requestDispatcher).forward(request, response);
    }

    // ==========================================
    // Phase 3: Error Cases
    // ==========================================

    @Test
    // # EARS [3.3 Lọc danh sách Audit Logs: chọn khoảng thời gian không hợp lệ (Edge case 6)]
    void testDoGet_List_InvalidDateRangeSafeHandling() throws Exception {
        // fromDate > toDate should not crash but return empty safely (or be handled gracefully)
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        when(request.getParameter("dateFrom")).thenReturn("2026-01-02");
        when(request.getParameter("dateTo")).thenReturn("2026-01-01");
        
        // Mock service handles this by returning 0 / empty
        when(mockAuditLogService.count(any(), any(), any(), any(), eq("2026-01-02"), eq("2026-01-01"))).thenReturn(0);
        when(request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/list.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);
        verify(request).setAttribute("totalCount", 0);
    }

    @Test
    // # EARS [3.2 Xem chi tiết Audit Log: log không tồn tại]
    void testDoGet_Detail_NotFound() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs/999");
        when(request.getContextPath()).thenReturn("/hostel-management");
        
        when(mockAuditLogService.getById(999)).thenThrow(new NotFoundException("Audit log not found"));
        
        // Expecting 404 response
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    // # EARS [Phân quyền: Người dùng chưa đăng nhập]
    void testDoGet_WithoutSession_RedirectsToLogin() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doGet(request, response);
        verify(response).sendRedirect("/hostel-management/login");
    }

    @Test
    // # EARS [Phân quyền: Người dùng không phải Admin]
    void testDoGet_RoleManager_Returns403() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        
        HttpSession mockSession = mock(HttpSession.class);
        com.quanlyphongtro.dto.UserSessionDTO user = new com.quanlyphongtro.dto.UserSessionDTO();
        user.setId(1);
        user.setRole("MANAGER"); // Not Admin
        
        when(request.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("currentUser")).thenReturn(user);
        
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    // ==========================================
    // Phase 4: Boundary Values
    // ==========================================

    @Test
    // # EARS [3.1 Xem danh sách Audit Logs: phân trang biên (Edge case 4)]
    void testDoGet_List_PageLessThanOne() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        when(request.getParameter("page")).thenReturn("-5"); // boundary negative
        
        when(request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/list.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        // Should pass -5 as parsed
        verify(mockAuditLogService).list(any(), any(), any(), any(), any(), any(), eq(-5), anyInt());
    }

    @Test
    // # EARS [3.3 Lọc danh sách Audit Logs: tham số tìm kiếm chứa khoảng trắng]
    void testDoGet_List_WhitespaceFilters() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
        when(request.getContextPath()).thenReturn("/hostel-management");
        when(request.getParameter("action")).thenReturn("   ");
        
        when(request.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/list.jsp")).thenReturn(requestDispatcher);

        servlet.doGet(request, response);

        // The action parameter should be trimmed to empty string
        verify(mockAuditLogService).list(any(), any(), any(), eq(""), any(), any(), eq(1), anyInt());
    }

    // ==========================================
    // Phase 5: Concurrent Scenarios
    // ==========================================

    @Test
    // # EARS [2.4 Concurrent Scenarios: Tranh chấp đồng thời]
    void testDoGet_ConcurrentRequests() throws Exception {
        // Create 50 threads trying to hit doGet simultaneously to check for Thread Safety
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    RequestDispatcher rd = mock(RequestDispatcher.class);
                    
                    when(req.getRequestURI()).thenReturn("/hostel-management/admin/audit-logs");
                    when(req.getContextPath()).thenReturn("/hostel-management");
                    when(req.getParameter("page")).thenReturn("1");
                    when(req.getRequestDispatcher("/WEB-INF/views/admin/audit-logs/list.jsp")).thenReturn(rd);

                    servlet.doGet(req, res);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Threads did not complete in time");
        assertEquals(numThreads, successCount.get(), "All requests should process without exception");
        executor.shutdown();
    }
}
