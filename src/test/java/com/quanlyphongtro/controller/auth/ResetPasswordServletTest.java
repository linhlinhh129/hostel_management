package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.util.ResetTokenManager;
import com.quanlyphongtro.util.SessionRegistry;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ResetPasswordServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private UserDAO mockUserDAO;

    private ResetPasswordServlet servlet;
    private MockedStatic<ResetTokenManager> mockedTokenManager;
    private MockedStatic<PasswordUtil> mockedPasswordUtil;
    private MockedStatic<SessionRegistry> mockedSessionRegistry;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ResetPasswordServlet();
        servlet.init();

        Field field = ResetPasswordServlet.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(servlet, mockUserDAO);

        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        mockedTokenManager = mockStatic(ResetTokenManager.class);
        mockedPasswordUtil = mockStatic(PasswordUtil.class);
        mockedSessionRegistry = mockStatic(SessionRegistry.class);
    }

    @AfterEach
    void tearDown() {
        mockedTokenManager.close();
        mockedPasswordUtil.close();
        mockedSessionRegistry.close();
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [KHI người dùng nhấp vào link khôi phục hợp lệ, HỆ THỐNG PHẢI hiển thị form]
    void testDoGet_ResetForm_ValidToken() throws Exception {
        when(request.getParameter("token")).thenReturn("valid-token");
        mockedTokenManager.when(() -> ResetTokenManager.verifyToken("valid-token")).thenReturn(1);

        servlet.doGet(request, response);

        verify(request).setAttribute("resetToken", "valid-token");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [KHI người dùng đặt lại mật khẩu mới thành công, HỆ THỐNG PHẢI vô hiệu hóa token đó VÀ thu hồi toàn bộ các phiên đăng nhập (session)]
    void testDoPost_ResetPassword_Success() throws Exception {
        when(request.getParameter("token")).thenReturn("valid-token");
        when(request.getParameter("newPassword")).thenReturn("NewPass123!@#");
        when(request.getParameter("confirmPassword")).thenReturn("NewPass123!@#");

        mockedTokenManager.when(() -> ResetTokenManager.verifyToken("valid-token")).thenReturn(1);
        mockedPasswordUtil.when(() -> PasswordUtil.hash("NewPass123!@#")).thenReturn("hashed_new_pass");

        servlet.doPost(request, response);

        verify(mockUserDAO).updatePassword(1, "hashed_new_pass");
        mockedTokenManager.verify(() -> ResetTokenManager.invalidateToken("valid-token"));
        mockedSessionRegistry.verify(() -> SessionRegistry.invalidateAllSessions(1));
        verify(response).sendRedirect("/hostel/login?success=reset");
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [KHI người dùng gửi yêu cầu đặt lại mật khẩu mới với token hết hạn hoặc sai, HỆ THỐNG PHẢI từ chối và báo lỗi]
    void testDoPost_InvalidOrExpiredToken() throws Exception {
        when(request.getParameter("token")).thenReturn("expired-token");
        when(request.getParameter("newPassword")).thenReturn("NewPass123!@#");
        when(request.getParameter("confirmPassword")).thenReturn("NewPass123!@#");

        mockedTokenManager.when(() -> ResetTokenManager.verifyToken("expired-token")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Đường dẫn khôi phục không hợp lệ hoặc đã hết hạn.");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [KHI người dùng nhập mật khẩu không khớp HỆ THỐNG PHẢI Validation lỗi]
    void testDoPost_PasswordMismatch() throws Exception {
        when(request.getParameter("token")).thenReturn("valid-token");
        when(request.getParameter("newPassword")).thenReturn("NewPass123!@#");
        when(request.getParameter("confirmPassword")).thenReturn("MismatchPass123!");

        mockedTokenManager.when(() -> ResetTokenManager.verifyToken("valid-token")).thenReturn(1);

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Xác nhận mật khẩu không khớp.");
        verify(requestDispatcher).forward(request, response);
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [Biên thời gian Token: KHI GET hoặc POST với Token quá 15 phút]
    void testTokenBoundary_Exactly15Mins() throws Exception {
        when(request.getParameter("token")).thenReturn("boundary-token");
        mockedTokenManager.when(() -> ResetTokenManager.verifyToken("boundary-token")).thenReturn(null);

        servlet.doGet(request, response);

        verify(request).setAttribute("errorMessage", "Đường dẫn khôi phục không hợp lệ hoặc đã hết hạn.");
    }

    // Phase 5: Concurrent Scenarios

    @Test
    // # EARS [Xóa Session đồng thời: Đảm bảo an toàn luồng khi SessionRegistry thực thi hành động Invalidate 100 sessions cùng lúc]
    void testConcurrency_SessionRevocation() throws Exception {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        mockedTokenManager.when(() -> ResetTokenManager.verifyToken(anyString())).thenReturn(1);
        mockedPasswordUtil.when(() -> PasswordUtil.hash(anyString())).thenReturn("hashed");

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);

                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getParameter("token")).thenReturn("valid-token-" + Math.random());
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
        mockedSessionRegistry.verify(() -> SessionRegistry.invalidateAllSessions(1), times(numThreads));
    }
}
