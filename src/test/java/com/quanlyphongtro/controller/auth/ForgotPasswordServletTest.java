package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.EmailService;
import com.quanlyphongtro.util.ResetTokenManager;
import com.quanlyphongtro.util.RateLimitManager;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ForgotPasswordServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private UserDAO mockUserDAO;

    private ForgotPasswordServlet servlet;
    private MockedStatic<EmailService> mockedEmailService;
    private MockedStatic<ResetTokenManager> mockedTokenManager;
    private MockedStatic<RateLimitManager> mockedRateLimit;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ForgotPasswordServlet();
        servlet.init();

        Field field = ForgotPasswordServlet.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(servlet, mockUserDAO);

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        
        mockedEmailService = mockStatic(EmailService.class);
        mockedTokenManager = mockStatic(ResetTokenManager.class);
        mockedRateLimit = mockStatic(RateLimitManager.class);
        
        mockedRateLimit.when(() -> RateLimitManager.isAllowed(anyString())).thenReturn(true);
    }

    @AfterEach
    void tearDown() {
        mockedEmailService.close();
        mockedTokenManager.close();
        mockedRateLimit.close();
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [KHI người dùng gửi form yêu cầu khôi phục mật khẩu, HỆ THỐNG PHẢI luôn hiển thị thông báo chung chung VÀ chỉ gửi Email nếu tài khoản hợp lệ]
    void testDoPost_SendLink_Success() throws Exception {
        when(request.getParameter("email")).thenReturn("valid@test.com");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getContextPath()).thenReturn("/hostel");

        User user = new User();
        user.setId(1);
        when(mockUserDAO.findByEmail("valid@test.com")).thenReturn(Optional.of(user));
        mockedTokenManager.when(() -> ResetTokenManager.generateToken(1)).thenReturn("fake-token-123");

        servlet.doPost(request, response);

        mockedEmailService.verify(() -> EmailService.sendResetLink("valid@test.com", "http://localhost:8080/hostel/reset-password?token=fake-token-123"));
        verify(request).setAttribute("emailSent", true);
        verify(requestDispatcher).forward(request, response);
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [Chống Dò quét (Anti-User Enumeration): KHI POST một email không tồn tại HỆ THỐNG PHẢI âm thầm bỏ qua NHƯNG VẪN hiển thị thông báo]
    void testAntiEnumeration_InvalidEmail() throws Exception {
        when(request.getParameter("email")).thenReturn("invalid@test.com");
        when(mockUserDAO.findByEmail("invalid@test.com")).thenReturn(Optional.empty());

        servlet.doPost(request, response);

        mockedEmailService.verifyNoInteractions();
        mockedTokenManager.verifyNoInteractions();
        verify(request).setAttribute("emailSent", true); 
        verify(requestDispatcher).forward(request, response);
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [Email rỗng: KHI nhập email rỗng hệ thống PHẢI chặn ở vòng Validation]
    void testEmptyEmail() throws Exception {
        when(request.getParameter("email")).thenReturn("");

        servlet.doPost(request, response);

        mockedEmailService.verifyNoInteractions();
        verify(request).setAttribute("errorMessage", "Vui lòng nhập địa chỉ email hợp lệ.");
        verify(requestDispatcher).forward(request, response);
    }
}
