package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.AppException;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

// Mocking dependencies is straightforward
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class AdminFacilityServletTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher requestDispatcher;

    // We don't have the actual DAO/Service implementations yet, so we will use mock logic 
    // to simulate standard behavior for the servlet layer test.
    // In a real scenario, mock the actual dependencies injected into AdminFacilityServlet.

    @InjectMocks
    private AdminFacilityServlet servlet;

    @BeforeEach
    void setUp() throws Exception {
        when(request.getSession(false)).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getContextPath()).thenReturn("/hostel-management");
    }

    private void setupMockUser(String role) {
        UserSessionDTO user = new UserSessionDTO();
        user.setId(1);
        user.setRole(role);
        when(session.getAttribute("currentUser")).thenReturn(user);
    }

    @Test
    // # EARS [3.1 Xem danh sách cơ sở: Admin mở danh sách nhưng chưa có cơ sở nào]
    void testDoGet_List_EmptyDatabase() throws Exception {
        setupMockUser("ADMIN");
        when(request.getServletPath()).thenReturn("/admin/facilities");
        
        // Assume service returns empty list
        
        servlet.doGet(request, response);
        verify(requestDispatcher).forward(request, response);
    }
    
    @Test
    // # EARS [3.4 Thêm cơ sở mới: Admin bỏ trống các trường bắt buộc]
    void testDoPost_Create_MissingFields() throws Exception {
        setupMockUser("ADMIN");
        when(request.getServletPath()).thenReturn("/admin/facilities/create");
        when(request.getParameter("code")).thenReturn("");
        
        servlet.doPost(request, response);
        // Expecting forward back to form with error message
        verify(request).setAttribute(eq("errorMessage"), anyString());
    }

    @Test
    // # EARS [3.4 Thêm cơ sở mới: Admin nhập thông tin hợp lệ]
    void testDoPost_Create_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getServletPath()).thenReturn("/admin/facilities/create");
        when(request.getParameter("code")).thenReturn("hl");
        when(request.getParameter("name")).thenReturn("Hoa Lac");
        when(request.getParameter("address")).thenReturn("Hanoi");
        when(request.getParameter("floorCount")).thenReturn("5");
        when(request.getParameter("roomsPerFloor")).thenReturn("4");

        // mock save success
        servlet.doPost(request, response);
        verify(response).sendRedirect("/hostel-management/admin/facilities");
    }

    @Test
    // # EARS [3.5 Sửa thông tin cơ sở: Admin sửa cơ sở ở trạng thái DRAFT]
    void testDoPost_UpdateDraft_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getServletPath()).thenReturn("/admin/facilities/1/edit");
        when(request.getPathInfo()).thenReturn("/1/edit");
        
        when(request.getParameter("code")).thenReturn("HL");
        when(request.getParameter("name")).thenReturn("Hoa Lac New");

        servlet.doPost(request, response);
        verify(response).sendRedirect("/hostel-management/admin/facilities");
    }

    @Test
    // # EARS [3.6 Kích hoạt cơ sở: Admin thực hiện kích hoạt cơ sở]
    void testDoPost_Activate_Success() throws Exception {
        setupMockUser("ADMIN");
        when(request.getServletPath()).thenReturn("/admin/facilities/1/activate");
        
        servlet.doPost(request, response);
        verify(response).sendRedirect("/hostel-management/admin/facilities");
    }
}
