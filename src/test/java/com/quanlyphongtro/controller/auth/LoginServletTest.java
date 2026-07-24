package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.constant.ErrorMessageConstant;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ForbiddenException;
import com.quanlyphongtro.service.UserService;
import com.quanlyphongtro.util.LoginAttemptTracker;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private UserService mockUserService;

    private LoginServlet servlet;
    private MockedStatic<LoginAttemptTracker> mockedTracker;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new LoginServlet();
        servlet.init();

        Field field = LoginServlet.class.getDeclaredField("userService");
        field.setAccessible(true);
        field.set(servlet, mockUserService);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/hostel");
        
        mockedTracker = mockStatic(LoginAttemptTracker.class);
        mockedTracker.when(() -> LoginAttemptTracker.isLocked(anyString())).thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        mockedTracker.close();
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [Đăng nhập bình thường: KHI người dùng Submit POST thông tin đúng VÀ force_change_pass = 0, HỆ THỐNG PHẢI khởi tạo Session VÀ redirect về Dashboard]
    void testDoPost_LoginNormal_Success() throws Exception {
        when(request.getParameter("username")).thenReturn("admin_user");
        when(request.getParameter("password")).thenReturn("Password123!");

        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole("ADMIN");
        user.setFirstLogin(false); // force_change_pass = 0

        when(mockUserService.login("admin_user", "Password123!")).thenReturn(Optional.of(user));
        when(request.getSession(true)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).setAttribute("currentUser", user);
        verify(response).sendRedirect("/hostel/admin/dashboard");
    }

    @Test
    // # EARS [Đăng nhập lần đầu: KHI người dùng Submit POST thông tin đúng NHƯNG force_change_pass = 1, HỆ THỐNG PHẢI redirect về trang force-change-password]
    void testDoPost_LoginFirstTime_ForceChangePassword() throws Exception {
        when(request.getParameter("username")).thenReturn("new_user");
        when(request.getParameter("password")).thenReturn("TempPass123!");

        UserSessionDTO user = new UserSessionDTO();
        user.setId(2);
        user.setRole("TENANT");
        user.setFirstLogin(true); // force_change_pass = 1

        when(mockUserService.login("new_user", "TempPass123!")).thenReturn(Optional.of(user));
        when(request.getSession(true)).thenReturn(session);

        servlet.doPost(request, response);

        verify(session).setAttribute("currentUser", user);
        verify(response).sendRedirect("/hostel/first-login");
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [Sai thông tin: KHI người dùng nhập sai tên hoặc mật khẩu, HỆ THỐNG PHẢI trả về thông báo lỗi và KHÔNG khóa tài khoản (nếu < 5 lần)]
    void testDoPost_InvalidCredentials() throws Exception {
        when(request.getParameter("username")).thenReturn("user123");
        when(request.getParameter("password")).thenReturn("WrongPass123!");

        when(mockUserService.login("user123", "WrongPass123!")).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", ErrorMessageConstant.INVALID_CREDENTIALS);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [Đăng nhập vào tài khoản đã khóa: KHI nhập ĐÚNG thông tin nhưng tài khoản đang LOCKED, HỆ THỐNG PHẢI từ chối]
    void testDoPost_AccountLocked_Rejected() throws Exception {
        when(request.getParameter("username")).thenReturn("locked_user");
        when(request.getParameter("password")).thenReturn("CorrectPass123!");

        mockedTracker.when(() -> LoginAttemptTracker.isLocked("locked_user")).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockUserService, never()).login(anyString(), anyString());
        verify(request).setAttribute("errorMessage", ErrorMessageConstant.ACCOUNT_LOCKED);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [Khóa tài khoản (Brute-force): KHI người dùng nhập sai mật khẩu vượt quá 5 lần, HỆ THỐNG PHẢI gọi lệnh cập nhật trạng thái LOCKED]
    void testDoPost_BruteForce_LockAccount() throws Exception {
        when(request.getParameter("username")).thenReturn("hacked_user");
        when(request.getParameter("password")).thenReturn("WrongPass123!");

        // Simulate 5th fail throws ForbiddenException
        when(mockUserService.login("hacked_user", "WrongPass123!")).thenThrow(new ForbiddenException("LOCKED"));

        servlet.doPost(request, response);

        verify(request).setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin.");
        verify(requestDispatcher).forward(request, response);
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [Biên giới hạn 5 lần sai: Mô phỏng logic lần 4 vs 5 qua Exception]
    void testBruteForce_Exactly5thAttempt() throws Exception {
        when(request.getParameter("username")).thenReturn("user");
        when(request.getParameter("password")).thenReturn("WrongPass123!");
        
        // 4th attempt
        when(mockUserService.login("user", "WrongPass123!")).thenReturn(Optional.empty());
        servlet.doPost(request, response);
        verify(request, times(1)).setAttribute("errorMessage", ErrorMessageConstant.INVALID_CREDENTIALS);

        // 5th attempt
        when(mockUserService.login("user", "WrongPass123!")).thenThrow(new ForbiddenException("LOCKED"));
        servlet.doPost(request, response);
        verify(request, times(1)).setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin.");
    }

    @Test
    // # EARS [Input rỗng: KHI Submit rỗng/khoảng trắng, HỆ THỐNG PHẢI từ chối bằng Validation sớm]
    void testEmptyInputs() throws Exception {
        when(request.getParameter("username")).thenReturn("   ");
        when(request.getParameter("password")).thenReturn("");

        servlet.doPost(request, response);

        verify(mockUserService, never()).login(anyString(), anyString());
        verify(request).setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không hợp lệ.");
        verify(requestDispatcher).forward(request, response);
    }

    // Phase 5: Concurrent Scenarios

    @Test
    // # EARS [Race Condition khi đếm số lần sai: Bắn 50 requests sai mật khẩu đồng thời]
    void testConcurrency_BruteForceLocking() throws Exception {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        when(mockUserService.login(anyString(), anyString())).thenReturn(Optional.empty());

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);

                    when(req.getParameter("username")).thenReturn("target_user");
                    when(req.getParameter("password")).thenReturn("WrongPass123!");
                    when(req.getRequestDispatcher(anyString())).thenReturn(mock(RequestDispatcher.class));

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
        verify(mockUserService, times(numThreads)).login(anyString(), anyString());
    }
}
