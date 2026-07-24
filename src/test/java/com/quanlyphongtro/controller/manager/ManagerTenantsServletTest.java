package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.TenantService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ManagerTenantsServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;

    @Mock
    private TenantService mockTenantService;
    @Mock
    private AuditLogDAO mockAuditLogDAO;

    private ManagerTenantsServlet servlet;
    private UserSessionDTO currentUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = spy(new ManagerTenantsServlet());
        servlet.init();

        Field f1 = ManagerTenantsServlet.class.getDeclaredField("tenantService");
        f1.setAccessible(true);
        f1.set(servlet, mockTenantService);

        Field f2 = ManagerTenantsServlet.class.getDeclaredField("auditLogDAO");
        f2.setAccessible(true);
        f2.set(servlet, mockAuditLogDAO);

        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(request.getContextPath()).thenReturn("/hostel");
        lenient().when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        currentUser = new UserSessionDTO();
        currentUser.setId(10);
        currentUser.setRole("MANAGER");
        lenient().when(session.getAttribute("currentUser")).thenReturn(currentUser);
    }

    // Phase 2: Happy Path
    @Test
    // # EARS [KHI Manager submits valid dependent info for an ACTIVE tenant THE SYSTEM SHALL insert a new record]
    void testDoPost_AddDependent_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/1/dependents/add");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van Con");
        when(request.getParameter("relationship")).thenReturn("Son");
        when(request.getParameter("identityNumber")).thenReturn("123456789012");
        
        when(mockTenantService.addDependent(eq(1), eq(10), eq("Nguyen Van Con"), eq("Son"), any(), any(), any(), eq("123456789012"))).thenReturn(true);

        servlet.doPost(request, response);

        verify(session).setAttribute("flashType", "success");
        verify(response).sendRedirect(contains("/manager/tenants/1"));
    }

    @Test
    // # EARS [KHI Manager clicks on a dependent THE SYSTEM SHALL display dependent details]
    // And identity is masked by Service logic
    void testDoGet_ViewDependent_MaskIdentity() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/dependents");
        when(request.getPathInfo()).thenReturn("/100");
        
        Map<String, Object> dep = new HashMap<>();
        dep.put("identityNumber", "123***012"); // masked by service
        when(mockTenantService.getDependentDetail(100, 10)).thenReturn(dep);

        servlet.doGet(request, response);

        verify(request).setAttribute("dependent", dep);
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    // # EARS [KHI Manager submits deletion request for a dependent THE SYSTEM SHALL set deleted_at = GETDATE()]
    void testDoPost_SoftDelete_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/dependents");
        when(request.getPathInfo()).thenReturn("/100/remove");
        
        Map<String, Object> dep = new HashMap<>();
        dep.put("tenantId", 1);
        when(mockTenantService.getDependentDetail(100, 10)).thenReturn(dep);
        when(mockTenantService.removeDependent(100, 10)).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockTenantService).removeDependent(100, 10);
        verify(session).setAttribute("flashType", "success");
        verify(response).sendRedirect(contains("/manager/tenants/1"));
    }

    // Phase 3: Error Cases
    @Test
    // # EARS [KHI Manager cố thêm người phụ thuộc vào một hợp đồng INACTIVE, HỆ THỐNG PHẢI từ chối]
    void testDoPost_AddDependent_TenantInactive() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/1/dependents/add");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van Con");
        when(request.getParameter("relationship")).thenReturn("Son");
        
        when(mockTenantService.addDependent(eq(1), eq(10), eq("Nguyen Van Con"), eq("Son"), any(), any(), any(), any()))
                .thenThrow(new IllegalStateException("Người thuê chính phải ở trạng thái ACTIVE"));

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("Người thuê chính phải ở trạng thái ACTIVE"));
    }

    @Test
    // # EARS [KHI Manager submits dependent info missing fullName or relationship THE SYSTEM SHALL reject]
    void testDoPost_AddDependent_MissingFullName() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/1/dependents/add");
        when(request.getParameter("fullName")).thenReturn("");
        when(request.getParameter("relationship")).thenReturn("Son");

        servlet.doPost(request, response);

        verify(mockTenantService, never()).addDependent(anyInt(), anyInt(), any(), any(), any(), any(), any(), any());
        verify(session).setAttribute("flashType", "danger");
        verify(session).setAttribute(eq("flashMessage"), contains("Họ tên và Quan hệ là bắt buộc"));
    }

    @Test
    // # EARS [KHI Manager cố thao tác ngoài cơ sở được phân quyền, HỆ THỐNG PHẢI trả về lỗi 403 Forbidden]
    void testDoPost_EditDependent_IDOR_CrossFacility() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/dependents");
        when(request.getPathInfo()).thenReturn("/100/edit");
        when(request.getParameter("fullName")).thenReturn("Con");
        when(request.getParameter("relationship")).thenReturn("Son");
        
        // Return null dependent detail => means not found or IDOR
        when(mockTenantService.getDependentDetail(100, 10)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    // Phase 4: Boundary Values
    @Test
    // # EARS [KHI Số CMND/CCCD sai định dạng THE SYSTEM SHALL gán error message và redirect]
    void testDoPost_Validation_IdentityLength() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/1/dependents/add");
        when(request.getParameter("fullName")).thenReturn("Con");
        when(request.getParameter("relationship")).thenReturn("Son");
        when(request.getParameter("identityNumber")).thenReturn("12345678901"); // 11 digits

        servlet.doPost(request, response);

        verify(mockTenantService, never()).addDependent(anyInt(), anyInt(), any(), any(), any(), any(), any(), any());
        verify(session).setAttribute(eq("flashMessage"), contains("không hợp lệ (phải gồm 9 hoặc 12 chữ số)"));
    }

    @Test
    // # EARS [KHI Số điện thoại sai định dạng THE SYSTEM SHALL gán error message]
    void testDoPost_Validation_PhoneLength() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/1/dependents/add");
        when(request.getParameter("fullName")).thenReturn("Con");
        when(request.getParameter("relationship")).thenReturn("Son");
        when(request.getParameter("phone")).thenReturn("091234567"); // 9 digits

        servlet.doPost(request, response);

        verify(mockTenantService, never()).addDependent(anyInt(), anyInt(), any(), any(), any(), any(), any(), any());
        verify(session).setAttribute(eq("flashMessage"), contains("chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số"));
    }

    // Phase 5: Concurrent Scenarios
    @Test
    // # EARS [KHI xảy ra va chạm xóa mềm kép, HỆ THỐNG PHẢI bắt an toàn DataIntegrityException hoặc hiển thị thông báo đã bị xóa]
    void testConcurrency_SoftDelete_DoubleStrike() throws Exception {
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        Map<String, Object> dep = new HashMap<>();
        dep.put("tenantId", 1);
        when(mockTenantService.getDependentDetail(100, 10)).thenReturn(dep);

        doAnswer(inv -> {
            if (successCount.get() == 1) {
                throw new IllegalStateException("Người phụ thuộc đã bị xóa trước đó");
            }
            successCount.incrementAndGet();
            return true;
        }).when(mockTenantService).removeDependent(100, 10);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getServletPath()).thenReturn("/manager/dependents");
                    when(req.getPathInfo()).thenReturn("/100/remove");
                    when(req.getSession(false)).thenReturn(sess);
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(10);
                    u.setRole("MANAGER");
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    
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

    // T002 Happy Path - Test doGet View Tenants List successfully
    @Test
    void testDoGet_ViewTenantsList_Success() throws Exception {
        // EARS: WHEN Manager filters or searches tenants THE SYSTEM SHALL return matching tenants within manager's assigned facilities.
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn(null);
        when(request.getParameter("page")).thenReturn("1");

        when(mockTenantService.countTenants(eq(10), isNull(), isNull())).thenReturn(1);
        when(mockTenantService.getTenants(eq(10), isNull(), isNull(), eq(1), eq(10))).thenReturn(new java.util.ArrayList<>());
        
        servlet.doGet(request, response);

        verify(request).setAttribute(eq("page"), any());
        verify(requestDispatcher).forward(request, response);
    }

    // T003 Happy Path - Test doGet View Tenant Detail successfully
    @Test
    void testDoGet_ViewTenantDetail_Success() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100");

        Map<String, Object> tenantDetail = new HashMap<>();
        when(mockTenantService.getTenantDetail(100, 10)).thenReturn(tenantDetail);
        
        servlet.doGet(request, response);

        verify(request).setAttribute("tenant", tenantDetail);
        verify(requestDispatcher).forward(request, response);
    }

    // T004 Happy Path - Test doPost Update Tenant Profile successfully
    @Test
    void testDoPost_UpdateProfile_Success() throws Exception {
        // EARS: WHEN Manager submits valid profile updates for a tenant THE SYSTEM SHALL update user record in dbo.users.
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100/edit");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van A");
        when(request.getParameter("phone")).thenReturn("0987654321");
        when(request.getParameter("email")).thenReturn("a@test.com");
        when(request.getParameter("identityNumber")).thenReturn("123456789012");
        when(request.getParameter("permanentAddress")).thenReturn("Hanoi");
        when(request.getParameter("gender")).thenReturn("M");
        when(request.getParameter("dob")).thenReturn("1990-01-01");

        when(mockTenantService.editTenant(eq(100), eq(10), eq("Nguyen Van A"), eq("0987654321"), eq("a@test.com"), eq("123456789012"), eq("Hanoi"), eq("M"), any(LocalDate.class)))
            .thenReturn(true);

        servlet.doPost(request, response);

        verify(mockTenantService).editTenant(eq(100), eq(10), eq("Nguyen Van A"), eq("0987654321"), eq("a@test.com"), eq("123456789012"), eq("Hanoi"), eq("M"), any(LocalDate.class));
        verify(response).sendRedirect(contains("/manager/tenants/100"));
    }

    // T005 Happy Path - Test doPost Lock Tenant successfully
    @Test
    void testDoPost_LockTenant_Success() throws Exception {
        // EARS: WHEN Manager locks active tenant account THE SYSTEM SHALL set status to LOCKED.
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100/lock");

        when(mockTenantService.lockTenantAccount(100)).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockTenantService).lockTenantAccount(100);
        verify(session).setAttribute(eq("flashType"), eq("success"));
        verify(response).sendRedirect(contains("/manager/tenants"));
    }

    // T006 Happy Path - Test doPost Unlock Tenant successfully
    @Test
    void testDoPost_UnlockTenant_Success() throws Exception {
        // EARS: WHEN Manager unlocks locked tenant account THE SYSTEM SHALL set status to ACTIVE AND clear login attempts.
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100/unlock");

        when(mockTenantService.unlockTenantAccount(eq(100), any())).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockTenantService).unlockTenantAccount(eq(100), any());
        verify(session).setAttribute(eq("flashType"), eq("success"));
        verify(response).sendRedirect(contains("/manager/tenants"));
    }

    // T007 Happy Path - Test doPost End Rental successfully (free room)
    @Test
    void testDoPost_EndRental_Success() throws Exception {
        // EARS: WHEN Manager ends rental for an active tenant THE SYSTEM SHALL set status to INACTIVE AND set room tenant_id = NULL.
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100/end-rental");

        when(mockTenantService.endRental(100)).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockTenantService).endRental(100);
        verify(response).sendRedirect(contains("/manager/tenants/100"));
    }

    // T008 Error - Test doGet Create Tenant Directly Redirects to Contracts
    @Test
    void testDoGet_CreateTenantDirectly_Redirect() throws Exception {
        // EARS: WHEN Manager attempts to open create page directly THE SYSTEM SHALL redirect to /manager/contracts.
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/create");

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("flashType"), eq("error"));
        verify(response).sendRedirect(contains("/manager/contracts"));
    }

    // T009 Error - Test doPost Action Cross-Facility returns 403 Forbidden
    @Test
    void testDoPost_ActionCrossFacility_Forbidden() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/200/edit");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van B");
        when(request.getParameter("phone")).thenReturn("0987654322");
        when(request.getParameter("email")).thenReturn("b@test.com");
        when(request.getParameter("identityNumber")).thenReturn("123456789013");

        when(mockTenantService.editTenant(eq(200), eq(10), anyString(), anyString(), anyString(), anyString(), isNull(), isNull(), isNull()))
            .thenThrow(new java.nio.file.AccessDeniedException("Forbidden"));

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Forbidden");
    }

    // T010 Error - Test doPost Update Profile with Duplicate Email/CCCD fails
    @Test
    void testDoPost_UpdateDuplicateEmail_Fails() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100/edit");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van A");
        when(request.getParameter("phone")).thenReturn("0987654321");
        when(request.getParameter("email")).thenReturn("duplicate@test.com");
        when(request.getParameter("identityNumber")).thenReturn("123456789012");

        when(mockTenantService.editTenant(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), isNull(), isNull(), isNull()))
            .thenThrow(new IllegalArgumentException("Email đã tồn tại"));

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashType"), eq("danger"));
        verify(session).setAttribute(eq("flashMessage"), eq("Email đã tồn tại"));
        verify(response).sendRedirect(contains("/manager/tenants/100"));
    }

    // T011 Error - Test doPost Update Profile with Invalid Format fails
    @Test
    void testDoPost_UpdateInvalidFormat_Fails() throws Exception {
        when(request.getServletPath()).thenReturn("/manager/tenants");
        when(request.getPathInfo()).thenReturn("/100/edit");
        when(request.getParameter("fullName")).thenReturn("Nguyen Van A");
        when(request.getParameter("phone")).thenReturn("123"); // Invalid length
        when(request.getParameter("email")).thenReturn("a@test.com");
        when(request.getParameter("identityNumber")).thenReturn("123456789012");

        servlet.doPost(request, response);

        verify(mockTenantService, never()).editTenant(anyInt(), anyInt(), anyString(), anyString(), anyString(), anyString(), any(), any(), any());
        verify(session).setAttribute(eq("flashType"), eq("danger"));
        verify(session).setAttribute(eq("flashMessage"), eq("Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số)."));
        verify(response).sendRedirect(contains("/manager/tenants/100"));
    }

    // T014 Concurrent - Test End Rental and Lock Race Condition
    @Test
    void testConcurrency_EndRentalAndLock_RaceCondition() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        doAnswer(invocation -> {
            Thread.sleep(100);
            return true;
        }).when(mockTenantService).endRental(300);

        doReturn(true).when(mockTenantService).lockTenantAccount(300);

        // Thread 1 - End Rental
        executorService.submit(() -> {
            try {
                HttpServletRequest req = mock(HttpServletRequest.class);
                HttpServletResponse res = mock(HttpServletResponse.class);
                HttpSession sess = mock(HttpSession.class);
                when(req.getSession(false)).thenReturn(sess);
                when(req.getServletPath()).thenReturn("/manager/tenants");
                when(req.getPathInfo()).thenReturn("/300/end-rental");
                when(req.getContextPath()).thenReturn("/hostel");
                when(sess.getAttribute("currentUser")).thenReturn(currentUser);
                
                latch.await();
                servlet.doPost(req, res);
            } catch (Exception e) {
            } finally { doneLatch.countDown(); }
        });

        // Thread 2 - Lock
        executorService.submit(() -> {
            try {
                HttpServletRequest req = mock(HttpServletRequest.class);
                HttpServletResponse res = mock(HttpServletResponse.class);
                HttpSession sess = mock(HttpSession.class);
                when(req.getSession(false)).thenReturn(sess);
                when(req.getServletPath()).thenReturn("/manager/tenants");
                when(req.getPathInfo()).thenReturn("/300/lock");
                when(req.getContextPath()).thenReturn("/hostel");
                when(sess.getAttribute("currentUser")).thenReturn(currentUser);
                
                latch.await();
                servlet.doPost(req, res);
            } catch (Exception e) {
            } finally { doneLatch.countDown(); }
        });

        latch.countDown();
        doneLatch.await();
        executorService.shutdown();

        verify(mockTenantService).endRental(300);
        verify(mockTenantService).lockTenantAccount(300);
    }
}
