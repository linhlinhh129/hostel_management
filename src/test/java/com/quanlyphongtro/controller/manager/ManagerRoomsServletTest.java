package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.RoomDetailDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.RoomService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerRoomsServletTest {

    private ManagerRoomsServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private RoomService mockRoomService;

    private UserSessionDTO mockManager;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManagerRoomsServlet();
        
        Field roomServiceField = ManagerRoomsServlet.class.getDeclaredField("roomService");
        roomServiceField.setAccessible(true);
        roomServiceField.set(servlet, mockRoomService);

        mockManager = new UserSessionDTO(); mockManager.setId(10); mockManager.setUsername("manager"); mockManager.setRole("MANAGER");
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockManager);
    }

    // T002 Happy Path - Test doGet View Assigned Facilities successfully
    @Test
    void testDoGet_ViewAssignedFacilities_Success() throws Exception {
        // EARS: WHEN Ban Quản Lý truy cập màn hình Quản lý vận hành cơ sở THE SYSTEM SHALL hiển thị danh sách cơ sở mà Ban Quản Lý được phân công.
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/rooms");
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("showGrid")).thenReturn("true");

        List<Map<String, Object>> facilities = new ArrayList<>();
        Map<String, Object> fac = new HashMap<>();
        fac.put("id", 1);
        facilities.add(fac);
        
        when(mockRoomService.getFacilitiesByManager(10)).thenReturn(facilities);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("facilities", facilities);
        verify(dispatcher).forward(request, response);
    }

    // T003 Happy Path - Test doGet View Facilities when empty (unassigned)
    @Test
    void testDoGet_ViewFacilities_Empty() throws Exception {
        // EARS: WHEN Ban Quản Lý chưa được phân công cơ sở nào THE SYSTEM SHALL hiển thị thông báo.
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/rooms");
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("showGrid")).thenReturn("true");

        List<Map<String, Object>> facilities = new ArrayList<>();
        when(mockRoomService.getFacilitiesByManager(10)).thenReturn(facilities);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("facilities", facilities);
        verify(dispatcher).forward(request, response);
    }

    // T004 Happy Path - Test doGet View Rooms in Facility successfully
    @Test
    void testDoGet_ViewRoomsInFacility_Success() throws Exception {
        // EARS: WHEN Ban Quản Lý chọn một cơ sở được phân công THE SYSTEM SHALL hiển thị danh sách phòng thuộc cơ sở đó.
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/facilities/1/rooms");

        Map<String, Object> facility = new HashMap<>();
        when(mockRoomService.verifyFacilityManager(1, 10)).thenReturn(facility);

        PageResult<RoomDTO> pageResult = new PageResult<>(new ArrayList<>(), 0, 1, 10);
        when(mockRoomService.getFacilityRoomsPage(eq(1), isNull(), eq(1), eq(10))).thenReturn(pageResult);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("page", pageResult);
        verify(dispatcher).forward(request, response);
    }

    // T005 Happy Path - Test doGet View Room Detail successfully
    @Test
    void testDoGet_ViewRoomDetail_Success() throws Exception {
        // EARS: WHEN Ban Quản Lý chọn một phòng thuộc cơ sở được phân công THE SYSTEM SHALL hiển thị thông tin chi tiết của phòng.
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/rooms/100");
        when(request.getPathInfo()).thenReturn("/100");

        RoomDetailDTO roomDetail = new RoomDetailDTO();
        when(mockRoomService.getRoomDetail(100, 10)).thenReturn(roomDetail);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/rooms/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("room", roomDetail);
        verify(dispatcher).forward(request, response);
    }

    // T006 Error - Test doGet View Rooms Cross-Facility returns 403
    @Test
    void testDoGet_ViewRooms_CrossFacility_Forbidden() throws Exception {
        // EARS: WHEN cơ sở không thuộc phạm vi được phân công THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi FACILITY_ACCESS_DENIED (403).
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/facilities/2/rooms");

        when(mockRoomService.verifyFacilityManager(2, 10)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không quản lý cơ sở này.");
    }

    // T007 Error - Test doGet View Room Detail Cross-Facility returns 403
    @Test
    void testDoGet_ViewRoomDetail_CrossFacility_Forbidden() throws Exception {
        // EARS: WHEN phòng thuộc cơ sở ngoài phạm vi được phân công THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi FACILITY_ACCESS_DENIED (403).
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/rooms/200");
        when(request.getPathInfo()).thenReturn("/200");

        when(mockRoomService.getRoomDetail(200, 10)).thenThrow(new AccessDeniedException("FACILITY_ACCESS_DENIED"));

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "FACILITY_ACCESS_DENIED");
    }

    // T009 Error - Test doGet Room Not Found returns 404
    @Test
    void testDoGet_RoomNotFound() throws Exception {
        // EARS: WHEN phòng không tồn tại THE SYSTEM SHALL từ chối yêu cầu và trả về lỗi ROOM_NOT_FOUND (404).
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/rooms/999");
        when(request.getPathInfo()).thenReturn("/999");

        when(mockRoomService.getRoomDetail(999, 10)).thenReturn(null);

        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // T010 Error - Test Unauthorized Access (TENANT) returns 401/403
    @Test
    void testDoGet_UnauthorizedAccess() throws Exception {
        // EARS: WHILE người dùng không có vai trò MANAGER THE SYSTEM SHALL từ chối truy cập.
        when(request.getRequestURI()).thenReturn("/manager/rooms");
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("showGrid")).thenReturn("true");
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/login");
    }

    // T011 Boundary - Test doGet Pagination Bounds for Rooms List
    @Test
    void testDoGet_PaginationBounds() throws Exception {
        setupSession();
        when(request.getRequestURI()).thenReturn("/manager/facilities/1/rooms");
        when(request.getParameter("page")).thenReturn("-5"); // Negative page

        Map<String, Object> facility = new HashMap<>();
        when(mockRoomService.verifyFacilityManager(1, 10)).thenReturn(facility);

        PageResult<RoomDTO> pageResult = new PageResult<>(new ArrayList<>(), 0, 1, 10);
        when(mockRoomService.getFacilityRoomsPage(eq(1), isNull(), eq(1), eq(10))).thenReturn(pageResult); // Should fallback to page 1
        when(request.getRequestDispatcher("/WEB-INF/views/manager/rooms/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(mockRoomService).getFacilityRoomsPage(1, null, 1, 10);
    }

}

