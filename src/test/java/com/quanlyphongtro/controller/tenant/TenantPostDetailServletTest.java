package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.CommunityPostService;
import com.quanlyphongtro.service.impl.CommunityPostServiceImpl;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantPostDetailServletTest {

    private TenantPostDetailServlet servlet;

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
        servlet = new TenantPostDetailServlet();

        Field serviceField = TenantPostDetailServlet.class.getDeclaredField("postService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockPostService);

        mockTenant = new UserSessionDTO(); mockTenant.setId(10); mockTenant.setUsername("tenant"); mockTenant.setRole("TENANT");
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockTenant);
    }

    // T003 Test View Post Detail successfully
    @Test
    void testDoGet_ViewPostDetail_Success() throws Exception {
        setupSession();
        when(request.getParameter("id")).thenReturn("100");

        NewsFeedDTO post = new NewsFeedDTO();
        post.setPostId(100);
        post.setAuthorId(10);
        when(mockPostService.getPostDetail(100, 10)).thenReturn(post);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/post-detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("post", post);
        verify(dispatcher).forward(request, response);
    }

    @Test
    void testDoGet_ViewPostDetail_NotFound() throws Exception {
        setupSession();
        when(request.getParameter("id")).thenReturn("200");

        when(mockPostService.getPostDetail(200, 10)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(eq(HttpServletResponse.SC_NOT_FOUND), anyString());
    }
}

