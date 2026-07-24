package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.CommunityPostService;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantMyPostsServletTest {

    private TenantMyPostsServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private CommunityPostService mockPostService;

    private UserSessionDTO mockTenant;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TenantMyPostsServlet();

        Field serviceField = TenantMyPostsServlet.class.getDeclaredField("postService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockPostService);

        mockTenant = new UserSessionDTO(); mockTenant.setId(10); mockTenant.setUsername("tenant"); mockTenant.setRole("TENANT");
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockTenant);
    }

    // T002 Test View My Posts list
    @Test
    void testDoGet_ViewMyPosts_Success() throws Exception {
        setupSession();

        List<NewsFeedDTO> posts = new ArrayList<>();
        when(mockPostService.getMyPosts(10)).thenReturn(posts);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/my-posts.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("posts", posts);
        verify(dispatcher).forward(request, response);
    }

    // T009 Test Unauthorized Access
    @Test
    void testDoGet_UnauthorizedAccess() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/login");
    }
}

