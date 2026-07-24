package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Notification;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class NotificationListServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    
    @Mock
    private NotificationDAO mockNotificationDAO;

    private NotificationListServlet servlet;
    private UserSessionDTO currentUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new NotificationListServlet();
        servlet.init();

        Field f = NotificationListServlet.class.getDeclaredField("notificationDAO");
        f.setAccessible(true);
        f.set(servlet, mockNotificationDAO);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        currentUser = new UserSessionDTO();
        currentUser.setId(10);
        currentUser.setRole("OPERATOR");
        currentUser.setFacilityCode("FAC-01"); // Assigned facility
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
    }

    // Phase 2: Happy Path
    @Test
    // # EARS [KHI Mock DAO trả về một thông báo có target_type = 'ALL', HỆ THỐNG PHẢI hiển thị thành công]
    void testDoGet_LoadNotifications_SystemWide() throws Exception {
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("page")).thenReturn("1");
        
        List<Notification> mockList = new ArrayList<>();
        Notification n = new Notification();
        n.setTargetType("ALL");
        mockList.add(n);

        when(mockNotificationDAO.findNotificationsForOperator(0, 1, 10)).thenReturn(mockList);
        when(mockNotificationDAO.countNotificationsForOperator(0)).thenReturn(1);

        servlet.doGet(request, response);

        verify(mockNotificationDAO).findNotificationsForOperator(0, 1, 10);
        verify(request).setAttribute("notifications", mockList);
        verify(requestDispatcher).forward(request, response);
    }

    // Phase 3: Error Cases
    @Test
    // # EARS [KHI User có Role là TENANT truy cập, HỆ THỐNG PHẢI ném 403 Forbidden]
    void testDoGet_UnauthorizedAccess() throws Exception {
        UserSessionDTO tenant = new UserSessionDTO();
        tenant.setRole("TENANT");
        when(session.getAttribute("currentUser")).thenReturn(tenant);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    // Phase 4: Boundary Values
    @Test
    // # EARS [KHI Operator thuộc 1 khu trọ hoàn toàn mới chưa có thông báo nào, HỆ THỐNG PHẢI gắn list rỗng an toàn]
    void testDoGet_NoNotifications() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("page")).thenReturn("1");
        when(mockNotificationDAO.findNotificationsForOperator(0, 1, 10)).thenReturn(new ArrayList<>());
        when(mockNotificationDAO.countNotificationsForOperator(0)).thenReturn(0);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("notifications"), argThat(list -> ((List<?>)list).isEmpty()));
        verify(request).setAttribute("totalPages", 1);
    }

    @Test
    // # EARS [KHI truyền tham số ?page=-1, HỆ THỐNG PHẢI xử lý an toàn fallback về 1]
    void testDoGet_PaginationOutOfBounds() throws Exception {
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("page")).thenReturn("-1");
        when(mockNotificationDAO.findNotificationsForOperator(0, 1, 10)).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(mockNotificationDAO).findNotificationsForOperator(0, 1, 10);
    }

    // Phase 5: Concurrent Scenarios
    @Test
    // # EARS [KHI Operator đang chuyển trang, đồng thời Admin thu hồi thông báo. Đảm bảo Servlet không có trạng thái chia sẻ]
    void testConcurrency_SnapshotRead() throws Exception {
        int numThreads = 5;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        when(mockNotificationDAO.findNotificationsForOperator(anyInt(), anyInt(), anyInt())).thenReturn(new ArrayList<>());

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
                    u.setFacilityCode("FAC-01");
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    when(req.getRequestDispatcher(anyString())).thenReturn(mock(RequestDispatcher.class));
                    
                    servlet.doGet(req, res);
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        verify(mockNotificationDAO, times(numThreads)).findNotificationsForOperator(anyInt(), anyInt(), anyInt());
    }
}
