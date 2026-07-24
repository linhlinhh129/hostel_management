package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.NotificationService;
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
import java.util.Collections;
import java.util.Optional;
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
class AdminNotificationServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private NotificationService mockNotificationService;

    private AdminNotificationServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminNotificationServlet();
        servlet.init();
        
        Field field = AdminNotificationServlet.class.getDeclaredField("notificationService");
        field.setAccessible(true);
        field.set(servlet, mockNotificationService);
    }

    private void setupMockUser(String role) {
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole(role);
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(request.getContextPath()).thenReturn("/hostel");
        lenient().when(request.getRequestDispatcher(org.mockito.ArgumentMatchers.anyString())).thenReturn(requestDispatcher);
        lenient().when(session.getAttribute("currentUser")).thenReturn(user);
    }

    // ==========================================
    // Phase 2: Happy Path
    // ==========================================

    @Test
    // # EARS [3.1 Tạo thông báo: Admin gửi biểu mẫu tạo thông báo với dữ liệu hợp lệ]
    void testDoPost_Create_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications/create");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("recipientType")).thenReturn("ALL");
        
        servlet.doPost(request, response);
        
        verify(mockNotificationService).createAdminNotification(eq("Test Title"), eq("Test Content"), eq(1));
        verify(response).sendRedirect("/hostel/admin/notifications");
    }

    @Test
    // # EARS [3.2 Danh sách thông báo: Admin truy cập màn hình danh sách thông báo]
    void testDoGet_List_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(request.getParameter("page")).thenReturn("1");
        when(request.getParameter("keyword")).thenReturn("Test");
        
        PageDTO<Notification> mockPage = new PageDTO<>();
        when(mockNotificationService.getAdminNotifications("Test", 1, 10)).thenReturn(mockPage);
        when(request.getRequestDispatcher("/WEB-INF/views/admin/notifications/list.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute("page", mockPage);
        verify(request).setAttribute("keyword", "Test");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.3 Chi tiết thông báo: Admin chọn một thông báo từ danh sách]
    void testDoGet_Detail_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications/123");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        Notification mockNotification = new Notification();
        when(mockNotificationService.getAdminNotificationById(123)).thenReturn(Optional.of(mockNotification));
        when(request.getRequestDispatcher("/WEB-INF/views/admin/notifications/detail.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute("notification", mockNotification);
        verify(requestDispatcher).forward(request, response);
    }

    // ==========================================
    // Phase 3: Error Cases
    // ==========================================

    @Test
    // # EARS [3.1 Tạo thông báo: Admin nhập tiêu đề trống hoặc nội dung trống]
    void testDoPost_Create_EmptyFields() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications/create");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(request.getParameter("title")).thenReturn("");
        when(request.getParameter("content")).thenReturn("");
        
        doThrow(new ValidationException("Tiêu đề không được để trống"))
            .when(mockNotificationService).createAdminNotification(anyString(), anyString(), anyInt());
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/notifications/create.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        
        verify(request).setAttribute("errorMessage", "Tiêu đề không được để trống");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.3 Chi tiết thông báo: Admin truy cập thông báo không tồn tại]
    void testDoGet_Detail_NotFound() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications/999");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(mockNotificationService.getAdminNotificationById(999)).thenReturn(Optional.empty());
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    // # EARS [3.5 Phân quyền: người dùng chưa được xác thực]
    void testDoGet_Unauthorized() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications");
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getSession(false)).thenReturn(null); // No session
        
        servlet.doGet(request, response);
        
        verify(response).sendRedirect("/hostel/login");
    }

    @Test
    // # EARS [3.5 Phân quyền: người dùng không có quyền quản lý thông báo]
    void testDoGet_Forbidden() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("MANAGER"); // Not Admin
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    // ==========================================
    // Phase 4: Boundary Values
    // ==========================================

    @Test
    // # EARS [3.2 Danh sách thông báo: phân trang biên]
    void testDoGet_List_PaginationLimits() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(request.getParameter("page")).thenReturn("-5"); // Invalid page
        
        when(mockNotificationService.getAdminNotifications("", 1, 10)).thenReturn(new PageDTO<>());
        when(request.getRequestDispatcher("/WEB-INF/views/admin/notifications/list.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        // Should fallback to default parseIntOrDefault which is 1
        verify(mockNotificationService).getAdminNotifications(eq(""), eq(1), eq(10));
    }

    @Test
    // # EARS [3.1 Tạo thông báo: tiêu đề vượt quá 255 ký tự]
    void testDoPost_Create_TitleLengthBoundary() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/notifications/create");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        String longTitle = "a".repeat(256);
        when(request.getParameter("title")).thenReturn(longTitle);
        
        doThrow(new ValidationException("Tiêu đề không vượt quá 255 ký tự"))
            .when(mockNotificationService).createAdminNotification(anyString(), any(), anyInt());
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/notifications/create.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        verify(request).setAttribute("errorMessage", "Tiêu đề không vượt quá 255 ký tự");
    }

    // ==========================================
    // Phase 5: Concurrent Scenarios
    // ==========================================

    @Test
    // # EARS [2.4 Concurrent Scenarios: An toàn luồng đa tiến trình]
    void testDoPost_ConcurrentRequests() throws Exception {
        int numThreads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);
                    
                    when(req.getRequestURI()).thenReturn("/hostel/admin/notifications/create");
                    when(req.getContextPath()).thenReturn("/hostel");
                    
                    UserSessionDTO user = new UserSessionDTO();
                    user.setId(1);
                    user.setRole("ADMIN");
                    when(req.getSession(false)).thenReturn(sess);
                    when(sess.getAttribute("currentUser")).thenReturn(user);
                    
                    when(req.getParameter("title")).thenReturn("Title");
                    when(req.getParameter("content")).thenReturn("Content");
                    
                    servlet.doPost(req, res);
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
        executor.shutdown();
    }
}
