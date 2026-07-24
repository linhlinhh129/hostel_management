package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.PersonnelService;
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
class AdminPersonnelServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private PersonnelService mockPersonnelService;

    private AdminPersonnelServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminPersonnelServlet();
        servlet.init();
        
        Field field = AdminPersonnelServlet.class.getDeclaredField("personnelService");
        field.setAccessible(true);
        field.set(servlet, mockPersonnelService);
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
    // # EARS [3.4 Thêm nhân sự: Admin nhập đầy đủ và hợp lệ các trường bắt buộc]
    void testDoPost_Create_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/create");
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        setupMockUser("ADMIN");
        
        when(request.getParameter("fullName")).thenReturn("John Doe");
        when(request.getParameter("email")).thenReturn("john@test.com");
        when(request.getParameter("role")).thenReturn("MANAGER");
        
        servlet.doPost(request, response);
        
        verify(mockPersonnelService).create(
            eq("John Doe"), eq("john@test.com"), any(), eq("MANAGER"), any(), any(), any(), any(), any(), eq(1), eq("http://localhost:8080/hostel/login")
        );
        verify(response).sendRedirect("/hostel/admin/personnel");
    }

    @Test
    // # EARS [3.5 Chỉnh sửa thông tin nhân sự: Admin cập nhật thông tin nhân sự với dữ liệu hợp lệ]
    void testDoPost_Update_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/123/edit");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(request.getParameter("fullName")).thenReturn("John Doe Updated");
        
        servlet.doPost(request, response);
        
        verify(mockPersonnelService).update(
            eq(123), eq("John Doe Updated"), any(), any(), any(), any(), any(), any(), any(), any()
        );
        verify(response).sendRedirect("/hostel/admin/personnel/123");
    }

    @Test
    // # EARS [3.1 Xem danh sách nhân sự: Admin truy cập màn hình quản lý nhân sự]
    void testDoGet_List_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        PageDTO<User> mockPage = new PageDTO<>();
        when(mockPersonnelService.list(anyString(), anyString(), anyString(), eq(1), eq(20))).thenReturn(mockPage);
        when(request.getRequestDispatcher("/WEB-INF/views/admin/personnel/list.jsp")).thenReturn(requestDispatcher);
        
        servlet.doGet(request, response);
        
        verify(request).setAttribute("page", mockPage);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.6 Khóa/Mở khóa tài khoản: Admin thực hiện khóa tài khoản nhân sự]
    void testDoPost_ToggleStatus_Success() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/123/status");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        servlet.doPost(request, response);
        
        verify(mockPersonnelService).toggleStatus(eq(123), eq(1));
        verify(response).sendRedirect("/hostel/admin/personnel/123");
    }

    // ==========================================
    // Phase 3: Error Cases
    // ==========================================

    @Test
    // # EARS [3.4 Thêm nhân sự: email không đúng định dạng email hợp lệ]
    void testDoPost_Create_ValidationException() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/create");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        doThrow(new ValidationException("Email không hợp lệ"))
            .when(mockPersonnelService).create(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyString());
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/personnel/create.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        
        verify(request).setAttribute("errorMessage", "Email không hợp lệ");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.6 Khóa/Mở khóa tài khoản: Admin thực hiện khóa tài khoản của chính mình]
    void testDoPost_CannotDeactivateSelf() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/1/status");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN"); // Admin ID = 1
        
        doThrow(new ValidationException("Không thể khóa tài khoản của chính mình"))
            .when(mockPersonnelService).toggleStatus(1, 1);
            
        servlet.doPost(request, response);
        verify(response).sendRedirect("/hostel/admin/personnel/1");
        // flash error is set
    }

    @Test
    // # EARS [3.7 Phân quyền: người dùng không phải Admin]
    void testAuth_Forbidden() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("MANAGER");
        
        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    // # EARS [3.2 Xem chi tiết nhân sự: nhân sự không tồn tại]
    void testDoGet_Detail_NotFound() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/999");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        when(mockPersonnelService.getById(999)).thenThrow(new NotFoundException("User not found"));
        
        servlet.doGet(request, response);
        
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // ==========================================
    // Phase 4: Boundary Values
    // ==========================================

    @Test
    // # EARS [3.4 Thêm nhân sự: số điện thoại đã tồn tại trong hệ thống]
    void testDoPost_UniqueConstraints() throws Exception {
        when(request.getRequestURI()).thenReturn("/hostel/admin/personnel/create");
        when(request.getContextPath()).thenReturn("/hostel");
        setupMockUser("ADMIN");
        
        doThrow(new ValidationException("Số điện thoại đã tồn tại"))
            .when(mockPersonnelService).create(any(), any(), any(), any(), any(), any(), any(), any(), any(), anyInt(), anyString());
            
        when(request.getRequestDispatcher("/WEB-INF/views/admin/personnel/create.jsp")).thenReturn(requestDispatcher);
        
        servlet.doPost(request, response);
        verify(request).setAttribute("errorMessage", "Số điện thoại đã tồn tại");
    }

    // ==========================================
    // Phase 5: Concurrent Scenarios
    // ==========================================

    @Test
    // # EARS [2.4 Concurrent Scenarios: Tranh chấp đồng thời]
    void testConcurrency_AssignManager() throws Exception {
        int numThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);
                    
                    when(req.getRequestURI()).thenReturn("/hostel/admin/personnel/123/status");
                    when(req.getContextPath()).thenReturn("/hostel");
                    
                    UserSessionDTO user = new UserSessionDTO();
                    user.setId(1);
                    user.setRole("ADMIN");
                    when(req.getSession(false)).thenReturn(sess);
                    when(sess.getAttribute("currentUser")).thenReturn(user);
                    
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
