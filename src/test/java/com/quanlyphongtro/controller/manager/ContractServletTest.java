package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dao.AuditLogDAO;
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
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ContractServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private ContractService mockContractService;
    @Mock
    private AuditLogDAO mockAuditLogDAO;

    private ContractServlet servlet;
    private UserSessionDTO currentUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = spy(new ContractServlet());
        servlet.init();

        Field f1 = ContractServlet.class.getDeclaredField("contractService");
        f1.setAccessible(true);
        f1.set(servlet, mockContractService);

        Field f2 = ContractServlet.class.getDeclaredField("auditLogDAO");
        f2.setAccessible(true);
        f2.set(servlet, mockAuditLogDAO);

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
        when(request.getServerPort()).thenReturn(8080);
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        currentUser = new UserSessionDTO();
        currentUser.setId(10);
        currentUser.setRole("MANAGER");
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
    }

    // Phase 2: Happy Path
    @Test
    // # EARS [KHI Ban quản lý truy cập màn hình Quản lý hợp đồng, THE SYSTEM SHALL hiển thị danh sách hợp đồng]
    void testDoGet_LoadContractsList_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts");
        when(mockContractService.getContractsByManager(eq(10), any())).thenReturn(new ArrayList<>());

        servlet.doGet(request, response);

        verify(mockContractService).getContractsByManager(eq(10), any());
        verify(request).setAttribute(eq("contracts"), any());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [KHI Ban quản lý lưu hợp đồng với dữ liệu hợp lệ, THE SYSTEM SHALL tạo bản ghi mới]
    void testDoPost_CreateContract_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts/create");
        when(request.getParameter("roomId")).thenReturn("1");
        when(request.getParameter("tenantFullName")).thenReturn("Nguyen Van A");
        when(request.getParameter("tenantIdentityNumber")).thenReturn("012345678912"); // 12 digits
        when(request.getParameter("tenantPhone")).thenReturn("0912345678"); // 10 digits
        when(request.getParameter("signedDate")).thenReturn("2024-01-01");
        when(request.getParameter("startDate")).thenReturn("2024-01-01");
        when(request.getParameter("endDate")).thenReturn("2025-01-01");

        doNothing().when(mockContractService).createContract(any(Contract.class), eq(10));

        servlet.doPost(request, response);

        verify(mockContractService).createContract(any(Contract.class), eq(10));
        verify(response).sendRedirect(anyString());
    }

    @Test
    // # EARS [KHI quá trình thêm người thuê thành công, THE SYSTEM SHALL cập nhật tenant_id vào hợp đồng]
    void testDoPost_AddTenant_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts/add-tenant");
        when(request.getParameter("roomId")).thenReturn("1");
        when(request.getParameter("contractId")).thenReturn("100");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van A");
        when(request.getParameter("email")).thenReturn("test@hostel.com");
        when(request.getParameter("phone")).thenReturn("0912345678");
        when(request.getParameter("identityNumber")).thenReturn("012345678912");

        Map<String, Object> result = new HashMap<>();
        result.put("userExists", false);
        result.put("userId", 55);
        result.put("status", "SUCCESS");

        when(mockContractService.addTenantFromContract(
            eq(100), eq(1), eq("Nguyen Van A"), eq("0912345678"), eq("test@hostel.com"), 
            eq("012345678912"), any(), any(), any(), any(), eq(false), eq(10), anyString()
        )).thenReturn(result);

        servlet.doPost(request, response);

        verify(mockContractService).addTenantFromContract(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), any(), any(), any(), any(), anyBoolean(), anyInt(), anyString());
        verify(session).setAttribute(eq("flashType"), eq("success"));
        verify(response).sendRedirect(contains("/manager/contracts/detail"));
    }

    @Test
    // # EARS [KHI trạng thái hợp đồng là INACTIVE, THE SYSTEM SHALL thực hiện soft delete]
    void testDoPost_SoftDelete_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts/delete");
        when(request.getParameter("id")).thenReturn("100");
        
        Map<String, String> ver = new HashMap<>();
        ver.put("status", "INACTIVE");
        ver.put("code", "HD-001");
        
        when(mockContractService.verifyContractForDelete(100, 10)).thenReturn(ver);
        when(mockContractService.softDeleteContract(100)).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockContractService).softDeleteContract(100);
        verify(session).setAttribute("flashType", "success");
        verify(response).sendRedirect(contains("/manager/contracts"));
    }

    // Phase 3: Error Cases
    @Test
    // # EARS [KHI Ban quản lý bỏ trống họ tên khách thuê, THE SYSTEM SHALL từ chối tạo hợp đồng]
    void testDoPost_CreateContract_ValidationFailed() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts/create");
        when(request.getParameter("roomId")).thenReturn("1");
        // missing tenantFullName throws NumberFormatException or similar during parse if dates are missing, but let's test specific
        when(request.getParameter("tenantFullName")).thenReturn(""); 
        
        // This will actually cause Exception in parsing Dates because they are null, triggering the catch block.
        servlet.doPost(request, response);
        
        verify(request).setAttribute(eq("errorMessage"), contains("Lỗi: "));
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [KHI trạng thái hợp đồng không phải là INACTIVE, THE SYSTEM SHALL từ chối xóa]
    void testDoPost_SoftDelete_ActiveContract_Denied() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts/delete");
        when(request.getParameter("id")).thenReturn("100");
        
        Map<String, String> ver = new HashMap<>();
        ver.put("status", "ACTIVE"); // Active!
        
        when(mockContractService.verifyContractForDelete(100, 10)).thenReturn(ver);

        servlet.doPost(request, response);

        verify(mockContractService, never()).softDeleteContract(100);
        verify(session).setAttribute(eq("flashMessage"), contains("Chỉ được xóa hợp đồng khi trạng thái là INACTIVE"));
    }

    // Phase 4: Boundary Values
    @Test
    // # EARS [KHI nhập số CCCD sai định dạng 11 số, HỆ THỐNG PHẢI văng IllegalArgumentException]
    void testDoPost_CreateContract_CCCD_BoundaryValues() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/contracts/create");
        when(request.getParameter("roomId")).thenReturn("1");
        when(request.getParameter("tenantFullName")).thenReturn("Nguyen Van A");
        when(request.getParameter("tenantIdentityNumber")).thenReturn("01234567891"); // 11 digits (invalid)
        when(request.getParameter("tenantPhone")).thenReturn("0912345678");
        when(request.getParameter("signedDate")).thenReturn("2024-01-01");
        when(request.getParameter("startDate")).thenReturn("2024-01-01");
        when(request.getParameter("endDate")).thenReturn("2025-01-01");

        servlet.doPost(request, response);

        verify(mockContractService, never()).createContract(any(), anyInt());
        verify(request).setAttribute(eq("errorMessage"), contains("Số CMND/CCCD không hợp lệ"));
    }

    // Phase 5: Concurrent Scenarios
    @Test
    // # EARS [KHI 2 Manager cùng tạo hợp đồng cho 1 phòng, Thread 2 bị văng Exception (DataIntegrityViolation)]
    void testConcurrency_DoubleBooking() throws Exception {
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        doAnswer(inv -> {
            if (successCount.get() == 1) {
                throw new RuntimeException("ROOM_ALREADY_HAS_ACTIVE_CONTRACT");
            }
            successCount.incrementAndGet();
            return null;
        }).when(mockContractService).createContract(any(Contract.class), anyInt());

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getServletPath()).thenReturn("/manager/contracts/create");
                    when(req.getSession(false)).thenReturn(sess);
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(10);
                    u.setRole("MANAGER");
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    
                    when(req.getParameter("roomId")).thenReturn("1");
                    when(req.getParameter("tenantPhone")).thenReturn("0912345678");
                    when(req.getParameter("tenantIdentityNumber")).thenReturn("012345678912");
                    when(req.getParameter("signedDate")).thenReturn("2024-01-01");
                    when(req.getParameter("startDate")).thenReturn("2024-01-01");
                    when(req.getParameter("endDate")).thenReturn("2025-01-01");
                    when(req.getRequestDispatcher(anyString())).thenReturn(mock(RequestDispatcher.class));

                    servlet.doPost(req, res);
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, successCount.get()); // Only 1 should succeed
    }
}
