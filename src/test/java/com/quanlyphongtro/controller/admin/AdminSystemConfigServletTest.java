package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.SystemConfigService;
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
class AdminSystemConfigServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private SystemConfigService mockConfigService;

    private AdminSystemConfigServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminSystemConfigServlet();
        servlet.init();
        
        Field field = AdminSystemConfigServlet.class.getDeclaredField("configService");
        field.setAccessible(true);
        field.set(servlet, mockConfigService);
    }

    private void setupMockUser() {
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    // ==========================================
    // Phase 2: Happy Path
    // ==========================================

    @Test
    // # EARS [3.1 Xem cấu hình: Admin truy cập màn hình Cấu hình hệ thống]
    void testDoGet_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config");
        setupMockUser();
        when(request.getRequestDispatcher("/WEB-INF/views/admin/system-config.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        verify(mockConfigService).getUIEmailConfig();
        verify(mockConfigService).getUIVNPayConfig();
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.3 Cấu hình Email: Admin nhập đầy đủ và đúng định dạng]
    void testDoPost_UpdateEmail_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config/email");
        setupMockUser();
        when(request.getContextPath()).thenReturn("/hostel");
        
        when(request.getParameter("host")).thenReturn("smtp.gmail.com");
        when(request.getParameter("port")).thenReturn("587");
        when(request.getParameter("username")).thenReturn("test");
        when(request.getParameter("password")).thenReturn("123");

        servlet.doPost(request, response);

        verify(mockConfigService).updateEmailConfig(eq("smtp.gmail.com"), eq("587"), eq("test"), eq("123"), any(), eq(1));
        verify(response).sendRedirect("/hostel/admin/system-config?success=email_updated");
    }

    // ==========================================
    // Phase 3: Error Cases
    // ==========================================

    @Test
    // # EARS [3.3 Cấu hình Email: Admin bỏ trống trường bắt buộc]
    void testDoPost_UpdateEmail_MissingFields() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config/email");
        setupMockUser();
        
        when(request.getParameter("host")).thenReturn("");
        when(request.getParameter("port")).thenReturn("587");
        
        doThrow(new ValidationException("Thiếu thông tin bắt buộc"))
            .when(mockConfigService).updateEmailConfig(anyString(), anyString(), any(), any(), any(), eq(1));
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/system-config.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Thiếu thông tin bắt buộc");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.3 Cấu hình Email: cổng (port) nhập sai định dạng]
    void testDoPost_UpdateEmail_InvalidPortFormat() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config/email");
        setupMockUser();
        
        when(request.getParameter("host")).thenReturn("smtp.gmail.com");
        when(request.getParameter("port")).thenReturn("abc"); // Invalid format
        
        doThrow(new ValidationException("Port phải là số nguyên"))
            .when(mockConfigService).updateEmailConfig(anyString(), anyString(), any(), any(), any(), eq(1));
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/system-config.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Port phải là số nguyên");
        verify(requestDispatcher).forward(request, response);
    }

    // ==========================================
    // Phase 4: Boundary Values
    // ==========================================

    @Test
    // # EARS [Phân quyền: Người dùng chưa đăng nhập]
    void testDoPost_WithoutSession_RedirectsToLogin() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config/email");
        when(request.getSession(false)).thenReturn(null);
        
        servlet.doPost(request, response);
        
        verify(response).sendRedirect("/hostel-management/login");
    }

    @Test
    // # EARS [Phân quyền: Người dùng không phải Admin]
    void testDoPost_RoleManager_Returns403() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config/email");
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole("MANAGER"); // Not Admin
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(user);
        
        servlet.doPost(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    // # EARS [3.3 Cấu hình Email: nhập khoảng trắng qua mặt Validation]
    void testDoPost_UpdateEmail_WhitespaceFields() throws Exception {
        when(request.getServletPath()).thenReturn("/admin/system-config/email");
        setupMockUser();
        
        when(request.getParameter("host")).thenReturn("    "); // Only whitespace
        when(request.getParameter("port")).thenReturn("587");
        
        doThrow(new ValidationException("Host không được để trống"))
            .when(mockConfigService).updateEmailConfig(anyString(), anyString(), any(), any(), any(), eq(1));
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/system-config.jsp")).thenReturn(requestDispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Host không được để trống");
    }

    // ==========================================
    // Phase 5: Concurrent Scenarios
    // ==========================================

    @Test
    // # EARS [2.4 Concurrent Scenarios: Tranh chấp đồng thời cập nhật cấu hình]
    void testDoPost_ConcurrentUpdates() throws Exception {
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
                    
                    when(req.getServletPath()).thenReturn("/admin/system-config/email");
                    when(req.getContextPath()).thenReturn("/hostel");
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(1);
                    when(req.getSession(false)).thenReturn(sess);
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    
                    when(req.getParameter("host")).thenReturn("smtp.gmail.com");
                    when(req.getParameter("port")).thenReturn("587");
                    when(req.getParameter("username")).thenReturn("test");
                    when(req.getParameter("password")).thenReturn("123");

                    servlet.doPost(req, res);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertTrue(latch.await(5, TimeUnit.SECONDS), "Threads did not complete in time");
        assertEquals(numThreads, successCount.get(), "All threads should process successfully");
        executor.shutdown();
    }
}
