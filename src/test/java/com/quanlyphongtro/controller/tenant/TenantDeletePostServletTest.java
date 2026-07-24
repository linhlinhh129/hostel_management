package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.CommunityPostService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantDeletePostServletTest {

    private TenantDeletePostServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private CommunityPostService mockPostService;

    private UserSessionDTO mockTenant;
    private StringWriter stringWriter;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TenantDeletePostServlet();

        Field serviceField = TenantDeletePostServlet.class.getDeclaredField("postService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockPostService);

        mockTenant = new UserSessionDTO(); mockTenant.setId(10); mockTenant.setUsername("tenant"); mockTenant.setRole("TENANT");
        stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockTenant);
    }

    // T005 Test Delete Post successfully (PENDING)
    @Test
    void testDoPost_DeletePost_Success() throws Exception {
        setupSession();
        when(request.getParameter("postId")).thenReturn("100");
        when(mockPostService.deletePost(100, 10)).thenReturn(true);

        servlet.doPost(request, response);

        assertTrue(stringWriter.toString().contains("\"success\":true"));
    }

    // T007 & T008 Test Delete Cross-Tenant Post / Approved Post returns 403
    @Test
    void testDoPost_DeletePost_Forbidden() throws Exception {
        setupSession();
        when(request.getParameter("postId")).thenReturn("200");
        when(mockPostService.deletePost(200, 10)).thenReturn(false); // Service handles checking ownership/status

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(stringWriter.toString().contains("Bạn không có quyền xóa bài viết này hoặc bài viết đã được duyệt"));
    }
}

