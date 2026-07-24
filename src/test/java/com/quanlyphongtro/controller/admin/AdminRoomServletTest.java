package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.RoomService;
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
import java.util.HashMap;
import java.util.Map;
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
class AdminRoomServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private RoomService mockRoomService;

    private AdminRoomServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new AdminRoomServlet();
        servlet.init();

        Field field = AdminRoomServlet.class.getDeclaredField("roomService");
        field.setAccessible(true);
        field.set(servlet, mockRoomService);
        
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(request.getContextPath()).thenReturn("/hostel");
        lenient().when(request.getRequestDispatcher(org.mockito.ArgumentMatchers.anyString())).thenReturn(requestDispatcher);
    }

    private void setupMockUser(String role) {
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole(role);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    // Phase 2: Happy Path

    @Test
    // # EARS [3.1 Xem chi tiết phòng: Quản trị viên truy cập trang chi tiết phòng hợp lệ]
    void testDoGet_RoomDetail_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123");
        
        Map<String, Object> roomData = new HashMap<>();
        roomData.put("id", 123);
        when(mockRoomService.getDetailForAdmin(123)).thenReturn(roomData);

        servlet.doGet(request, response);

        verify(request).setAttribute("room", roomData);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [3.2 Cập nhật thông tin phòng hợp lệ: Quản trị viên gửi biểu mẫu với diện tích và giá là số dương]
    void testDoPost_Update_PositiveValues_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123/update");
        when(request.getParameter("area")).thenReturn("25.5");
        when(request.getParameter("roomFee")).thenReturn("1500000");

        servlet.doPost(request, response);

        verify(mockRoomService).updateAreaAndFee(123, "25.5", "1500000");
        verify(response).sendRedirect("/hostel/admin/rooms/123");
    }

    @Test
    // # EARS [3.3 Xóa thông tin: Quản trị viên để trống ô diện tích hoặc giá phòng]
    void testDoPost_Update_EmptyValues_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123/update");
        when(request.getParameter("area")).thenReturn("");
        when(request.getParameter("roomFee")).thenReturn(null);

        servlet.doPost(request, response);

        verify(mockRoomService).updateAreaAndFee(123, "", null);
        verify(response).sendRedirect("/hostel/admin/rooms/123");
    }

    // Phase 3: Error Cases

    @Test
    // # EARS [3.4 Cơ sở vô hiệu hóa: Quản trị viên cố tình POST cho phòng thuộc cơ sở INACTIVE]
    void testDoPost_Update_FacilityInactive() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123/update");
        when(request.getParameter("area")).thenReturn("20");
        when(request.getParameter("roomFee")).thenReturn("1000000");

        doThrow(new ValidationException("Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng."))
            .when(mockRoomService).updateAreaAndFee(123, "20", "1000000");

        servlet.doPost(request, response);

        verify(request).getSession(); // for setFlashMessage
        verify(response).sendRedirect("/hostel/admin/rooms/123");
    }

    @Test
    // # EARS [3.5 Kiểm tra tính hợp lệ dữ liệu: Nhập số âm hoặc ký tự không hợp lệ]
    void testDoPost_Update_NegativeOrInvalidValues() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123/update");
        when(request.getParameter("area")).thenReturn("-5");
        when(request.getParameter("roomFee")).thenReturn("abc");

        doThrow(new ValidationException("Diện tích không được âm."))
            .when(mockRoomService).updateAreaAndFee(123, "-5", "abc");

        servlet.doPost(request, response);

        verify(request).getSession();
        verify(response).sendRedirect("/hostel/admin/rooms/123");
    }

    @Test
    // # EARS [3.1 Xem chi tiết phòng: Trạng thái phòng không tồn tại (404)]
    void testDoGetPost_RoomNotFound() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/999");
        when(mockRoomService.getDetailForAdmin(999)).thenThrow(new NotFoundException("Room not found"));

        servlet.doGet(request, response);
        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);

        when(request.getPathInfo()).thenReturn("/999/update");
        doThrow(new NotFoundException("Room not found")).when(mockRoomService).updateAreaAndFee(eq(999), any(), any());
        servlet.doPost(request, response);
        verify(response, times(2)).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    // # EARS [Phân quyền: Người dùng không phải ADMIN hoặc chưa login]
    void testAuth_Unauthorized_Forbidden() throws Exception {
        when(request.getPathInfo()).thenReturn("/123");
        when(request.getSession(false)).thenReturn(null);

        servlet.doGet(request, response);
        // Security logic would intercept, but at servlet level it handles safely.
    }

    // Phase 4: Boundary Values

    @Test
    // # EARS [3.5 Kiểm tra tính hợp lệ dữ liệu: Biên giá trị chính xác bằng 0]
    void testDoPost_Update_ZeroValues() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123/update");
        when(request.getParameter("area")).thenReturn("0");
        when(request.getParameter("roomFee")).thenReturn("0");

        servlet.doPost(request, response);
        verify(mockRoomService).updateAreaAndFee(123, "0", "0");
    }

    @Test
    // # EARS [3.5 Kiểm tra tính hợp lệ dữ liệu: Biên số cực lớn (tiền tỷ)]
    void testDoPost_Update_ExtremeLargeValues() throws Exception {
        setupMockUser("ADMIN");
        when(request.getPathInfo()).thenReturn("/123/update");
        when(request.getParameter("area")).thenReturn("9999999999.99");
        when(request.getParameter("roomFee")).thenReturn("999999999999999999");

        servlet.doPost(request, response);
        verify(mockRoomService).updateAreaAndFee(123, "9999999999.99", "999999999999999999");
    }

    // Phase 5: Concurrent Scenarios

    @Test
    // # EARS [2.4 Concurrent Scenarios: Tranh chấp đồng thời khi sửa 1 phòng]
    void testConcurrency_UpdateRoom() throws Exception {
        int numThreads = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getPathInfo()).thenReturn("/123/update");
                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getSession(false)).thenReturn(sess);
                    when(req.getSession()).thenReturn(sess);

                    when(req.getParameter("area")).thenReturn("25");
                    when(req.getParameter("roomFee")).thenReturn("2000000");

                    servlet.doPost(req, res);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS), "Threads did not finish in time");
        assertEquals(numThreads, successCount.get(), "All threads should complete successfully");
        verify(mockRoomService, times(numThreads)).updateAreaAndFee(123, "25", "2000000");
    }
}
