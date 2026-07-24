package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.User;
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
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProfileServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private UserDAO mockUserDAO;
    @Mock
    private ServletContext servletContext;
    @Mock
    private Part mockPart;

    private ProfileServlet servlet;
    private UserSessionDTO currentUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ProfileServlet() {
            @Override
            public ServletContext getServletContext() {
                return servletContext;
            }
        };
        servlet.init();

        Field field = ProfileServlet.class.getDeclaredField("userDAO");
        field.setAccessible(true);
        field.set(servlet, mockUserDAO);

        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(request.getContextPath()).thenReturn("/hostel");
        lenient().when(request.getRequestDispatcher(org.mockito.ArgumentMatchers.anyString())).thenReturn(requestDispatcher);
        when(servletContext.getRealPath("")).thenReturn("D:/mockpath");

        currentUser = new UserSessionDTO();
        currentUser.setId(1);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
    }

    private void mockDBUser() {
        User user = new User();
        user.setId(1);
        user.setFullName("Old Name");
        when(mockUserDAO.findById(1)).thenReturn(Optional.of(user));
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [Xem Hồ sơ theo Vai trò: KHI User GET profile, HỆ THỐNG PHẢI gọi Mock trả về thêm thông tin profile]
    void testDoGet_ViewProfile_Success() throws Exception {
        mockDBUser();
        servlet.doGet(request, response);

        verify(mockUserDAO).findById(1);
        verify(request).setAttribute(eq("userProfile"), any(User.class));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [Cập nhật Thông tin cơ bản: KHI POST Form với thông tin đúng format, HỆ THỐNG PHẢI cập nhật DB]
    void testDoPost_UpdateProfile_Success() throws Exception {
        mockDBUser();
        when(request.getParameter("action")).thenReturn("update_profile");
        when(request.getParameter("fullName")).thenReturn("New Name");
        when(request.getParameter("phone")).thenReturn("0912345678");
        when(request.getParameter("identityNumber")).thenReturn("001099123456");
        when(request.getParameter("dob")).thenReturn("2000-01-01");

        servlet.doPost(request, response);

        verify(mockUserDAO).updateProfile(any(User.class));
        verify(response).sendRedirect("/hostel/profile");
        assertEquals("New Name", currentUser.getFullName());
    }

    @Test
    // # EARS [Upload Avatar: KHI đính kèm file ảnh hợp lệ, HỆ THỐNG PHẢI lưu file và cập nhật avatar_url]
    void testDoPost_UploadAvatar_Success() throws Exception {
        mockDBUser();
        when(request.getParameter("action")).thenReturn("update_profile");
        
        when(request.getPart("avatar")).thenReturn(mockPart);
        when(mockPart.getSize()).thenReturn(1024L);
        when(mockPart.getHeader("content-disposition")).thenReturn("form-data; name=\"avatar\"; filename=\"avatar.png\"");

        servlet.doPost(request, response);

        verify(mockPart).write(anyString());
        verify(mockUserDAO).updateProfile(argThat(u -> u.getAvatarUrl().contains("avatar.png")));
        verify(response).sendRedirect("/hostel/profile");
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [ID Spoofing: KHI một User cố tình truyền tham số ngầm userId=2, HỆ THỐNG PHẢI CHỈ lấy ID từ HttpSession]
    void testIDOR_SpoofingAttempt() throws Exception {
        mockDBUser();
        when(request.getParameter("action")).thenReturn("update_profile");
        when(request.getParameter("userId")).thenReturn("999"); // ID Spoofing Attempt
        when(request.getParameter("fullName")).thenReturn("Hacked");

        servlet.doPost(request, response);

        verify(mockUserDAO).updateProfile(argThat(u -> u.getId() == 1 && u.getFullName().equals("Hacked")));
    }

    @Test
    // # EARS [Lỗi Format Input: KHI truyền SDT sai format, HỆ THỐNG PHẢI từ chối ngay lập tức]
    void testUpdateProfile_InvalidFormats() throws Exception {
        mockDBUser();
        when(request.getParameter("action")).thenReturn("update_profile");
        when(request.getParameter("phone")).thenReturn("123");

        servlet.doPost(request, response);

        verify(mockUserDAO, never()).updateProfile(any(User.class));
        verify(response).sendRedirect("/hostel/profile"); 
    }

    @Test
    // # EARS [Trùng lặp Dữ liệu: KHI cập nhật Email/SDT đã tồn tại của người khác, HỆ THỐNG PHẢI bắt lỗi]
    void testUpdateProfile_DuplicateConstraints() throws Exception {
        mockDBUser();
        when(request.getParameter("action")).thenReturn("update_profile");
        when(request.getParameter("phone")).thenReturn("0912345678");

        doThrow(new RuntimeException("Duplicate entry '0912345678' for key 'phone'"))
            .when(mockUserDAO).updateProfile(any(User.class));

        servlet.doPost(request, response);

        verify(request).getSession(false); 
        verify(response).sendRedirect("/hostel/profile");
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [Dữ liệu Optional rỗng: KHI truyền dob rỗng, HỆ THỐNG PHẢI lưu null thay vì văng lỗi]
    void testUpdateProfile_OptionalFieldsNull() throws Exception {
        mockDBUser();
        when(request.getParameter("action")).thenReturn("update_profile");
        when(request.getParameter("dob")).thenReturn("");

        servlet.doPost(request, response);

        verify(mockUserDAO).updateProfile(argThat(u -> u.getDob() == null));
    }

    // Phase 5: Concurrent Scenarios

    @Test
    // # EARS [Xung đột Unique Constraints: Hai request đồng thời update SĐT trùng nhau]
    void testConcurrency_UniqueUpdate() throws Exception {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        doAnswer(inv -> {
            Thread.sleep(10);
            return null;
        }).when(mockUserDAO).updateProfile(any(User.class));

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
                    
                    User dbUser = new User();
                    dbUser.setId(1);
                    when(mockUserDAO.findById(1)).thenReturn(Optional.of(dbUser));
                    
                    when(req.getParameter("action")).thenReturn("update_profile");
                    when(req.getParameter("fullName")).thenReturn("Thread Name");

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
        verify(mockUserDAO, times(numThreads)).updateProfile(any(User.class));
    }
}
