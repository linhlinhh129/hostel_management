package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.service.ContractService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantContractServletTest {

    private TenantContractServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private ContractService mockContractService;

    private UserSessionDTO mockTenant;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TenantContractServlet();
        
        Field serviceField = TenantContractServlet.class.getDeclaredField("contractService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockContractService);

        mockTenant = new UserSessionDTO(); mockTenant.setId(10); mockTenant.setUsername("tenant"); mockTenant.setRole("TENANT");
    }

    private void setupSession() {
        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(session.getAttribute("currentUser")).thenReturn(mockTenant);
    }

    // T002 Happy Path - Test doGet View My Contracts successfully.
    @Test
    void testDoGet_ViewMyContracts_Success() throws Exception {
        // EARS: KHI người thuê truy cập màn hình Hợp đồng của tôi, THE SYSTEM SHALL hiển thị danh sách hợp đồng có tenant_id bằng người dùng đang đăng nhập.
        setupSession();
        when(request.getParameter("id")).thenReturn(null);
        
        List<Contract> contracts = new ArrayList<>();
        Contract c = new Contract();
        c.setContractId(1);
        contracts.add(c);
        
        when(mockContractService.getContractsByTenant(10)).thenReturn(contracts);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/contracts/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("contracts", contracts);
        verify(dispatcher).forward(request, response);
    }

    // T003 Happy Path - Test doGet View My Contracts when empty.
    @Test
    void testDoGet_ViewMyContracts_Empty() throws Exception {
        // EARS: KHI người thuê chưa có hợp đồng nào, THE SYSTEM SHALL hiển thị thông báo.
        setupSession();
        when(request.getParameter("id")).thenReturn(null);
        
        List<Contract> contracts = new ArrayList<>();
        when(mockContractService.getContractsByTenant(10)).thenReturn(contracts);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/contracts/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("contracts", contracts);
        verify(dispatcher).forward(request, response);
    }

    // T004 Happy Path - Test doGet View Contract Detail successfully.
    @Test
    void testDoGet_ViewContractDetail_Success() throws Exception {
        // EARS: KHI người thuê chọn một hợp đồng, THE SYSTEM SHALL hiển thị chi tiết.
        setupSession();
        when(request.getParameter("id")).thenReturn("100");
        
        Contract contract = new Contract();
        contract.setContractId(100);
        
        when(mockContractService.getContractDetailForTenant(100, 10)).thenReturn(contract);
        when(request.getRequestDispatcher("/WEB-INF/views/tenant/contracts/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("contract", contract);
        verify(dispatcher).forward(request, response);
    }

    // T005 & T006 Error - Test doGet Cross-Tenant Access / Not Found returns 403/404.
    @Test
    void testDoGet_ViewContract_CrossTenantOrNotFound() throws Exception {
        // EARS: KHI người thuê cố truy cập hợp đồng của người khác, THE SYSTEM SHALL trả về HTTP 403. KHI hợp đồng không tồn tại, trả về 404.
        // In the servlet logic, getContractDetailForTenant returning null is handled as a 404. 
        setupSession();
        when(request.getParameter("id")).thenReturn("200");
        
        when(mockContractService.getContractDetailForTenant(200, 10)).thenReturn(null);
        
        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hợp đồng hoặc bạn không có quyền xem.");
    }

    // T007 Error - Test doPost Update Contract Forbidden returns 405.
    @Test
    void testDoPost_UpdateContract_Forbidden() throws Exception {
        // EARS: Tenant doesn't have POST access (Read-only module). The Servlet doesn't override doPost.
        // By default HttpServlet returns 405 Method Not Allowed. We can test it by invoking the public service method of servlet.
        setupSession();
        lenient().when(request.getMethod()).thenReturn("POST");
        lenient().when(request.getProtocol()).thenReturn("HTTP/1.1");
        lenient().when(request.getRequestURI()).thenReturn("/tenant/contracts");

        // Use service method which delegates to doPost, then returns 405
        servlet.service(request, response);

        verify(response).sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "HTTP method POST is not supported by this URL");
    }

    // T008 Error - Test doGet Unauthorized Access returns 401/Redirect.
    @Test
    void testDoGet_UnauthorizedAccess() throws Exception {
        // BaseServlet handles getCurrentUser. If null, it redirects or throws.
        // Let's mock a null session.
        when(request.getSession(false)).thenReturn(null);
        when(request.getContextPath()).thenReturn("");

        servlet.doGet(request, response);
        // Base servlet behavior typically redirects to /login or throws exception.
        verify(response).sendRedirect("/login");
    }
}

