package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.CommunityPostService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantCreatePostServletTest {

    private TenantCreatePostServlet servlet;

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
        servlet = spy(new TenantCreatePostServlet()); // using spy to mock getServletContext if needed, but not strictly required if we mock the parts

        Field serviceField = TenantCreatePostServlet.class.getDeclaredField("postService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockPostService);

        mockTenant = new UserSessionDTO(); mockTenant.setId(10); mockTenant.setUsername("tenant"); mockTenant.setRole("TENANT");
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockTenant);
    }

    // T004 Test Create Post successfully
    @Test
    void testDoPost_CreatePost_Success() throws Exception {
        // Test requires mocking ServletContext for getRealPath
        // Since we can't easily mock getServletContext().getRealPath() without more setup, 
        // we test the scenario without images to avoid hitting the file system logic.
        setupSession();
        when(request.getParameter("title")).thenReturn("Title");
        when(request.getParameter("content")).thenReturn("Content");
        
        // Mock getParts to return empty (no image)
        // Wait, TenantCreatePostServlet calls getServletContext().getRealPath("") regardless of parts!
        // So we must mock getServletContext()
        jakarta.servlet.ServletContext mockServletContext = mock(jakarta.servlet.ServletContext.class);
        doReturn(mockServletContext).when(servlet).getServletContext();
        when(mockServletContext.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));
        
        Collection<Part> parts = new ArrayList<>();
        when(request.getParts()).thenReturn(parts);
        when(request.getContextPath()).thenReturn("");
        when(mockPostService.createPost(eq(10), eq("Title"), eq("Content"), isNull())).thenReturn(new com.quanlyphongtro.model.CommunityPost());

        servlet.doPost(request, response);

        verify(mockPostService).createPost(eq(10), eq("Title"), eq("Content"), isNull());
        verify(session).setAttribute(eq("successMessage"), anyString()); // setFlashMessage
        verify(response).sendRedirect("/tenant/my-posts");
    }

    // T006 Test Create Post with Invalid Data returns Error
    @Test
    void testDoPost_CreatePost_InvalidImageType() throws Exception {
        setupSession();
        when(request.getParameter("title")).thenReturn("Title");
        when(request.getParameter("content")).thenReturn("Content");
        
        jakarta.servlet.ServletContext mockServletContext = mock(jakarta.servlet.ServletContext.class);
        doReturn(mockServletContext).when(servlet).getServletContext();
        when(mockServletContext.getRealPath("")).thenReturn(System.getProperty("java.io.tmpdir"));
        
        Collection<Part> parts = new ArrayList<>();
        Part mockPart = mock(Part.class);
        when(mockPart.getName()).thenReturn("images");
        when(mockPart.getSize()).thenReturn(100L);
        when(mockPart.getContentType()).thenReturn("application/pdf"); // Invalid Type
        parts.add(mockPart);
        
        when(request.getParts()).thenReturn(parts);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("Chỉ chấp nhận ảnh JPG, PNG hoặc WEBP"));
        verify(dispatcher).forward(request, response);
    }
}

