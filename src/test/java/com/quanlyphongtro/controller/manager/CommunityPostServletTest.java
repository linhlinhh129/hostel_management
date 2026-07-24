package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dto.CommunityPostCreateDTO;
import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.CommunityPostService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommunityPostServletTest {

    private CommunityPostServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private CommunityPostService mockCommunityPostService;
    @Mock
    private ServletContext servletContext;
    @Mock
    private Part mockPart;

    private UserSessionDTO mockManager;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new CommunityPostServlet();
        
        Field serviceField = CommunityPostServlet.class.getDeclaredField("communityPostService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockCommunityPostService);

        mockManager = new UserSessionDTO(); mockManager.setId(10); mockManager.setUsername("manager"); mockManager.setRole("MANAGER");
    }

    private void setupSession() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockManager);
    }

    // T002 Happy Path - Test doGet View Post List successfully
    @Test
    void testDoGet_ViewPostList_Success() throws Exception {
        // EARS: WHEN Ban quản lý truy cập trang Danh sách bài viết THE SYSTEM SHALL hiển thị danh sách tất cả bài viết.
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles");
        when(request.getPathInfo()).thenReturn(null);

        List<CommunityPostDTO> posts = new ArrayList<>();
        CommunityPostDTO dto = new CommunityPostDTO();
        dto.setId(1);
        posts.add(dto);
        when(mockCommunityPostService.getPostsForManager(eq(10), eq(0), eq(100))).thenReturn(posts);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/list-pending.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("posts", posts);
        verify(dispatcher).forward(request, response);
    }

    // T003 Happy Path - Test doGet View Post Detail successfully
    @Test
    void testDoGet_ViewPostDetail_Success() throws Exception {
        // EARS: WHEN Ban quản lý chọn xem chi tiết THE SYSTEM SHALL hiển thị chi tiết bài viết.
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles/detail");
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("id")).thenReturn("5");

        CommunityPostDTO post = new CommunityPostDTO();
        when(mockCommunityPostService.getPostById(5, 10)).thenReturn(post);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("post", post);
        verify(dispatcher).forward(request, response);
    }

    // T004 Happy Path - Test doPost Create Post successfully
    @Test
    void testDoPost_CreatePost_Success() throws Exception {
        // EARS: WHEN Ban quản lý nhập đầy đủ thông tin THE SYSTEM SHALL tạo bài viết mới với trạng thái PENDING.
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles");
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("title")).thenReturn("Bài viết mới");
        when(request.getParameter("content")).thenReturn("Nội dung");
        when(request.getPart("image")).thenReturn(mockPart);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath("/uploads")).thenReturn("/real/path");
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockCommunityPostService).createPost(any(CommunityPostCreateDTO.class), eq(mockPart), eq(10), eq("/real/path"), eq("MANAGER"));
        verify(response).sendRedirect("/manager/articles?success=create");
    }

    // T005 Happy Path - Test doPost Approve Post successfully (AJAX)
    @Test
    void testDoPost_ApprovePost_Success() throws Exception {
        // EARS: WHEN Ban quản lý chọn Duyệt bài viết PENDING THE SYSTEM SHALL gọi API qua AJAX POST và cập nhật APPROVED.
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles");
        when(request.getPathInfo()).thenReturn("/approve");
        when(request.getParameter("postId")).thenReturn("15");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPost(request, response);

        verify(mockCommunityPostService).approvePost(15, 10);
        assertTrue(sw.toString().contains("\"success\":true"));
    }

    // T006 Happy Path - Test doPost Delete Post successfully (Soft Delete)
    @Test
    void testDoPost_DeletePost_Success() throws Exception {
        // EARS: WHEN Ban quản lý chọn Xóa THE SYSTEM SHALL gọi API và đánh dấu bài viết đã xóa.
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles");
        when(request.getPathInfo()).thenReturn("/delete");
        when(request.getParameter("postId")).thenReturn("15");

        StringWriter sw = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(sw));

        servlet.doPost(request, response);

        verify(mockCommunityPostService).deletePost(15);
        assertTrue(sw.toString().contains("\"success\":true"));
    }

    // T007 Error - Test doPost create post fails gracefully on empty title/content
    @Test
    void testDoPost_CreatePost_EmptyTitleOrContent() throws Exception {
        // EARS: WHEN tiêu đề hoặc nội dung để trống THE SYSTEM SHALL từ chối tạo bài viết.
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles");
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("title")).thenReturn("");
        when(request.getParameter("content")).thenReturn("");
        when(request.getPart("image")).thenReturn(null);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath("/uploads")).thenReturn("/real/path");
        
        doThrow(new ValidationException("Tiêu đề không được để trống")).when(mockCommunityPostService)
            .createPost(any(CommunityPostCreateDTO.class), isNull(), eq(10), eq("/real/path"), eq("MANAGER"));

        when(request.getRequestDispatcher("/WEB-INF/views/manager/postManagement/create.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Tiêu đề không được để trống");
        verify(dispatcher).forward(request, response);
    }

    // T008 Error - Test doGet view detail redirects on Post NotFound
    @Test
    void testDoGet_ViewPostDetail_NotFound() throws Exception {
        // EARS: GET /manager/articles/detail lỗi ID / Không tìm thấy -> Chuyển hướng về trang danh sách kèm ?error=notfound
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles/detail");
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("id")).thenReturn("999");
        when(request.getContextPath()).thenReturn("");

        when(mockCommunityPostService.getPostById(999, 10)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendRedirect("/manager/articles?error=notfound");
    }

    // T010 Boundary - Test doPost title creation with maximum 250 characters
    @Test
    void testDoPost_TitleMaxLength() throws Exception {
        setupSession();
        when(request.getServletPath()).thenReturn("/manager/articles");
        when(request.getPathInfo()).thenReturn("/create");
        String maxTitle = "A".repeat(250);
        when(request.getParameter("title")).thenReturn(maxTitle);
        when(request.getParameter("content")).thenReturn("Nội dung");
        when(request.getPart("image")).thenReturn(mockPart);
        when(request.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath("/uploads")).thenReturn("/real/path");
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockCommunityPostService).createPost(argThat((com.quanlyphongtro.dto.CommunityPostCreateDTO dto) -> dto.getTitle().equals(maxTitle)), eq(mockPart), eq(10), eq("/real/path"), eq("MANAGER"));
    }

    // T012 Concurrent - Test Double-Approve race condition handling
    @Test
    void testConcurrency_DoubleApprove_RaceCondition() throws Exception {
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        AtomicInteger approveCount = new AtomicInteger(0);

        doAnswer(invocation -> {
            int current = approveCount.incrementAndGet();
            if (current > 1) {
                throw new IllegalStateException("Bài viết đã được duyệt.");
            }
            return null;
        }).when(mockCommunityPostService).approvePost(15, 10);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);
                    when(req.getSession()).thenReturn(sess);
                    when(sess.getAttribute("currentUser")).thenReturn(mockManager);
                    
                    when(req.getServletPath()).thenReturn("/manager/articles");
                    when(req.getPathInfo()).thenReturn("/approve");
                    when(req.getParameter("postId")).thenReturn("15");
                    
                    StringWriter sw = new StringWriter();
                    when(res.getWriter()).thenReturn(new PrintWriter(sw));

                    latch.await();
                    servlet.doPost(req, res);
                } catch (Exception e) {
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown();
        doneLatch.await();
        executorService.shutdown();

        // One call succeeds, one throws exception -> 500 status sent to one response
        verify(mockCommunityPostService, times(2)).approvePost(15, 10);
    }
}

