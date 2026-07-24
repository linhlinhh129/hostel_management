package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.service.RequestService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class DetailRequestServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    
    @Mock
    private RequestService mockRequestService;
    
    @Mock
    private AuditLogDAO mockAuditLogDAO;

    @Mock
    private Part mockPart;
    
    @Mock
    private ServletContext servletContext;

    private DetailRequestServlet servlet; 
    private UserSessionDTO currentUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = spy(new DetailRequestServlet()); 
        servlet.init();

        Field f = DetailRequestServlet.class.getDeclaredField("requestService");
        f.setAccessible(true);
        f.set(servlet, mockRequestService);
        
        Field f2 = DetailRequestServlet.class.getDeclaredField("auditLogDAO");
        f2.setAccessible(true);
        f2.set(servlet, mockAuditLogDAO);

        doReturn(servletContext).when(servlet).getServletContext();
        when(servletContext.getRealPath(anyString())).thenReturn("D:/dummy/path");

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        currentUser = new UserSessionDTO();
        currentUser.setId(10);
        currentUser.setRole("OPERATOR");
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
    }

    // Phase 2: Happy Path
    @Test
    // # EARS [KHI Operator gửi POST request action=complete chứa 1 file ảnh hợp lệ và notes đầy đủ, DAO phải được gọi update với rejection_reason và attachment_urls2]
    void testDoPost_CompleteRequest_OneImage() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("notes")).thenReturn("Đã sửa");

        when(mockPart.getName()).thenReturn("after_images");
        when(mockPart.getSize()).thenReturn(1024L);
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"after_images\"; filename=\"img1.jpg\"");
        when(mockPart.getContentType()).thenReturn("image/jpeg");
        
        List<Part> parts = new ArrayList<>();
        parts.add(mockPart);
        when(request.getParts()).thenReturn(parts);

        when(mockRequestService.completeRequest(eq(101), eq("Đã sửa"), anyString())).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockRequestService).completeRequest(eq(101), eq("Đã sửa"), anyString()); 
        verify(response).sendRedirect(anyString());
    }

    @Test
    // # EARS [KHI form được gửi lên mà thuộc tính file upload rỗng, HỆ THỐNG BẮT BUỘC chặn lại, trả về lỗi Validation]
    void testDoPost_MissingImages() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("notes")).thenReturn("Done");

        when(request.getParts()).thenReturn(new ArrayList<>()); // Empty parts

        servlet.doPost(request, response);

        verify(mockRequestService, never()).completeRequest(anyInt(), anyString(), anyString());
        verify(request).setAttribute(eq("error"), anyString());
    }

    @Test
    // # EARS [KHI để trống notes, HỆ THỐNG PHẢI bắt Validation Error]
    void testDoPost_MissingNotes() throws Exception {
        when(request.getParameter("action")).thenReturn("complete");
        when(request.getParameter("id")).thenReturn("101");
        when(request.getParameter("notes")).thenReturn(""); // Empty

        servlet.doPost(request, response);

        verify(mockRequestService, never()).completeRequest(anyInt(), anyString(), anyString());
        verify(request).setAttribute(eq("error"), anyString());
    }

    // Phase 5: Concurrent Scenarios
    @Test
    // # EARS [KHI 2 Operator cùng lúc ấn nút submit Hoàn thành. Mock DAO văng lỗi ở thread thứ 2. Xử lý an toàn]
    void testConcurrency_DoubleComplete() throws Exception {
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        when(mockRequestService.completeRequest(eq(101), anyString(), anyString())).thenAnswer(inv -> {
            if (successCount.get() == 1) {
                return false; // Second one fails
            }
            successCount.incrementAndGet();
            return true;
        });

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getSession(false)).thenReturn(sess);
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(10);
                    u.setRole("OPERATOR");
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    when(req.getRequestDispatcher(anyString())).thenReturn(mock(RequestDispatcher.class));
                    
                    when(req.getParameter("action")).thenReturn("complete");
                    when(req.getParameter("id")).thenReturn("101");
                    when(req.getParameter("notes")).thenReturn("Done");
                    when(req.getParameter("no_image_checkbox")).thenReturn("on"); // easy bypass file upload in concurrency test

                    servlet.doPost(req, res);
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, successCount.get()); 
    }
}
