package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.service.DependentService;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantDependentServletTest {

    private TenantDependentServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private DependentService mockDependentService;

    private UserSessionDTO mockTenant;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TenantDependentServlet();
        
        Field serviceField = TenantDependentServlet.class.getDeclaredField("dependentService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockDependentService);

        mockTenant = new UserSessionDTO(); mockTenant.setId(10); mockTenant.setUsername("tenant"); mockTenant.setRole("TENANT");
    }

    private void setupSession() {
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(session.getAttribute("currentUser")).thenReturn(mockTenant);
    }

    // T002 Happy Path - Test doGet View My Dependents successfully.
    @Test
    void testDoGet_ViewMyDependents_Success() throws Exception {
        // EARS: Lấy danh sách từ CSDL điều kiện tenant_id = [current_user] VÀ deleted_at IS NULL.
        setupSession();
        when(request.getPathInfo()).thenReturn("/");
        
        List<Dependent> dependents = new ArrayList<>();
        Dependent d = new Dependent();
        d.setId(1);
        dependents.add(d);
        
        when(mockDependentService.getDependentsByTenantId(10)).thenReturn(dependents);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/dependents/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("dependents", dependents);
        verify(dispatcher).forward(request, response);
    }

    // T003 Happy Path - Test doGet View My Dependents empty.
    @Test
    void testDoGet_ViewMyDependents_Empty() throws Exception {
        // EARS: WHERE Tenant chưa có người phụ thuộc THE SYSTEM SHALL Hiển thị: "Hiện chưa có người phụ thuộc nào được đăng ký."
        setupSession();
        when(request.getPathInfo()).thenReturn(null);
        
        List<Dependent> dependents = new ArrayList<>();
        when(mockDependentService.getDependentsByTenantId(10)).thenReturn(dependents);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/dependents/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("dependents", dependents);
        verify(dispatcher).forward(request, response);
    }

    // T004 & T005 Happy Path - Test doGet View Dependent Detail successfully & Mask CCCD.
    @Test
    void testDoGet_ViewDependentDetail_Success_MaskCCCD() throws Exception {
        // EARS: THE SYSTEM SHALL Mask thông tin CCCD/CMND trước khi hiển thị (vd: 0790******123).
        setupSession();
        when(request.getPathInfo()).thenReturn("/100");
        
        Dependent dependent = new Dependent();
        dependent.setId(100);
        dependent.setIdentityNumber("0790******123"); // Masked by service
        
        when(mockDependentService.getDependentById(100, 10)).thenReturn(Optional.of(dependent));
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/dependents/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("dependent", dependent);
        verify(dispatcher).forward(request, response);
    }

    // T006 & T007 Error - Test doGet Cross-Tenant Access / Not Found / Soft Deleted returns 404/403.
    @Test
    void testDoGet_ViewDependent_NotFoundOrIDOR() throws Exception {
        // EARS: WHERE Tenant truy cập người phụ thuộc không thuộc quyền quản lý THE SYSTEM SHALL Từ chối truy cập. 
        // WHEN dependentId không tồn tại hoặc đã bị Soft Delete THE SYSTEM SHALL Trả HTTP 404 Not Found.
        setupSession();
        when(request.getPathInfo()).thenReturn("/200");
        
        when(mockDependentService.getDependentById(200, 10)).thenReturn(Optional.empty());
        
        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    // T008 Error - Test doPost Modify Dependent Forbidden returns 405.
    @Test
    void testDoPost_ModifyDependent_Forbidden() throws Exception {
        // EARS: Tenant Read-only module. Default POST returns 405.
        setupSession();
        lenient().when(request.getMethod()).thenReturn("POST");
        lenient().when(request.getProtocol()).thenReturn("HTTP/1.1");
        lenient().when(request.getRequestURI()).thenReturn("/tenant/dependents");

        servlet.service(request, response);

        verify(response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method POST is not supported by this URL");
    }

    // T009 Error - Test doGet Unauthorized Access returns 401/Redirect.
    @Test
    void testDoGet_UnauthorizedAccess() throws Exception {
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);
        verify(response).sendRedirect("/login");
    }
}

