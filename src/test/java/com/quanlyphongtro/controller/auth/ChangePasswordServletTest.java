package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.PasswordUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
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
class ChangePasswordServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private UserDAO mockUserDAO;
    @Mock
    private RequestDispatcher requestDispatcher;

    private ProfileServlet servlet;
    private MockedStatic<PasswordUtil> mockedPasswordUtil;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ProfileServlet();
        servlet.init();
        
        Field field = ProfileServlet.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(servlet, mockUserDAO);

        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(request.getContextPath()).thenReturn("/hostel");
        lenient().when(request.getRequestDispatcher(org.mockito.ArgumentMatchers.anyString())).thenReturn(requestDispatcher);
        
        mockedPasswordUtil = mockStatic(PasswordUtil.class);
    }

    @AfterEach
    void tearDown() {
        mockedPasswordUtil.close();
    }

    private void setupMockUser() {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(1);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        
        User user = new User();
        user.setId(1);
        user.setPasswordHash("hashed_old_password");
        when(mockUserDAO.findById(1)).thenReturn(Optional.of(user));
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [KHI người dùng gửi form Đổi mật khẩu kèm theo mật khẩu cũ chính xác và mật khẩu mới hợp lệ, HỆ THỐNG PHẢI cập nhật]
    void testDoPost_ChangePassword_Success() throws Exception {
        setupMockUser();
        when(request.getParameter("action")).thenReturn("change_password");
        when(request.getParameter("currentPassword")).thenReturn("OldPass123!");
        when(request.getParameter("newPassword")).thenReturn("NewPass123!@#");
        when(request.getParameter("confirmPassword")).thenReturn("NewPass123!@#");

        mockedPasswordUtil.when(() -> PasswordUtil.verify("OldPass123!", "hashed_old_password")).thenReturn(true);
        mockedPasswordUtil.when(() -> PasswordUtil.hash("NewPass123!@#")).thenReturn("hashed_new_password");

        servlet.doPost(request, response);

        verify(mockUserDAO).updatePassword(1, "hashed_new_password");
        verify(request).getSession(); 
        verify(response).sendRedirect("/hostel/profile");
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [KHI người dùng nhập sai mật khẩu cũ, HỆ THỐNG PHẢI từ chối cập nhật VÀ hiển thị lỗi]
    void testDoPost_IncorrectOldPassword() throws Exception {
        setupMockUser();
        when(request.getParameter("action")).thenReturn("change_password");
        when(request.getParameter("currentPassword")).thenReturn("WrongOldPass123!");
        when(request.getParameter("newPassword")).thenReturn("NewPass123!@#");
        when(request.getParameter("confirmPassword")).thenReturn("NewPass123!@#");

        mockedPasswordUtil.when(() -> PasswordUtil.verify("WrongOldPass123!", "hashed_old_password")).thenReturn(false);

        servlet.doPost(request, response);

        verify(mockUserDAO, never()).updatePassword(anyInt(), anyString());
        verify(response).sendRedirect("/hostel/profile");
    }

    @Test
    // # EARS [KHI người dùng nhập mật khẩu mới không đáp ứng đủ 7 quy tắc, HỆ THỐNG PHẢI hiển thị cảnh báo]
    void testDoPost_PolicyViolation() throws Exception {
        setupMockUser();
        when(request.getParameter("action")).thenReturn("change_password");
        when(request.getParameter("currentPassword")).thenReturn("OldPass123!");
        when(request.getParameter("newPassword")).thenReturn("short"); 
        when(request.getParameter("confirmPassword")).thenReturn("short");

        servlet.doPost(request, response);

        verify(mockUserDAO, never()).updatePassword(anyInt(), anyString());
        verify(response).sendRedirect("/hostel/profile");
    }

    @Test
    // # EARS [KHI Session không tồn tại hoặc đã hết hạn, điều hướng ngay về trang Đăng nhập]
    void testAuth_NoSession_RedirectsToLogin() throws Exception {
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendRedirect("/hostel/login");
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [Mật khẩu rỗng hoặc toàn khoảng trắng KHI nhập chuỗi rỗng HỆ THỐNG PHẢI trim và bắt lỗi Validation]
    void testDoPost_EmptyOrWhitespacePasswords() throws Exception {
        setupMockUser();
        when(request.getParameter("action")).thenReturn("change_password");
        when(request.getParameter("currentPassword")).thenReturn("OldPass123!");
        when(request.getParameter("newPassword")).thenReturn("    ");
        when(request.getParameter("confirmPassword")).thenReturn("    ");

        servlet.doPost(request, response);

        verify(mockUserDAO, never()).updatePassword(anyInt(), anyString());
        verify(response).sendRedirect("/hostel/profile");
    }

    @Test
    // # EARS [Mật khẩu cực dài: KHI nhập mật khẩu dài 1000 ký tự HỆ THỐNG PHẢI bắt lỗi]
    void testDoPost_ExtremelyLongPassword() throws Exception {
        setupMockUser();
        when(request.getParameter("action")).thenReturn("change_password");
        when(request.getParameter("currentPassword")).thenReturn("OldPass123!");
        String longPass = "A".repeat(1000) + "123!@#";
        when(request.getParameter("newPassword")).thenReturn(longPass);
        when(request.getParameter("confirmPassword")).thenReturn(longPass);

        servlet.doPost(request, response);
        verify(response).sendRedirect("/hostel/profile");
    }

    // Phase 5: Concurrent Scenarios

    @Test
    // # EARS [Tranh chấp đồng thời đổi mật khẩu: KHI 1 tài khoản bắn 10 request đồng thời]
    void testConcurrency_ChangePassword() throws Exception {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        
        mockedPasswordUtil.when(() -> PasswordUtil.verify(anyString(), anyString())).thenReturn(true);
        mockedPasswordUtil.when(() -> PasswordUtil.hash(anyString())).thenReturn("new_hash");

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getSession(false)).thenReturn(sess);
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(1);
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    
                    when(req.getParameter("action")).thenReturn("change_password");
                    when(req.getParameter("currentPassword")).thenReturn("OldPass123!");
                    when(req.getParameter("newPassword")).thenReturn("NewPass123!@#");
                    when(req.getParameter("confirmPassword")).thenReturn("NewPass123!@#");

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
        verify(mockUserDAO, times(numThreads)).updatePassword(1, "new_hash");
    }
}
