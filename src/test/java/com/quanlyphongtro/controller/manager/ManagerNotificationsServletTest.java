package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.NotificationService;
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerNotificationsServletTest {

    private ManagerNotificationsServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private NotificationService mockNotificationService;
    @Mock
    private AuditLogDAO mockAuditLogDAO;

    private UserSessionDTO mockManager;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManagerNotificationsServlet();
        
        Field notificationServiceField = ManagerNotificationsServlet.class.getDeclaredField("notificationService");
        notificationServiceField.setAccessible(true);
        notificationServiceField.set(servlet, mockNotificationService);
        
        Field auditLogDAOField = ManagerNotificationsServlet.class.getDeclaredField("auditLogDAO");
        auditLogDAOField.setAccessible(true);
        auditLogDAOField.set(servlet, mockAuditLogDAO);

        mockManager = new UserSessionDTO(); mockManager.setId(10); mockManager.setUsername("manager"); mockManager.setRole("MANAGER");
    }

    private void setupSession() {
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(session.getAttribute("currentUser")).thenReturn(mockManager);
        lenient().when(request.getRequestDispatcher(org.mockito.ArgumentMatchers.anyString())).thenReturn(dispatcher);
    }

    // T002 Happy Path - Test doPost send general notification successfully
    @Test
    void testDoPost_SendGeneralNotification_Success() throws Exception {
        // EARS: WHEN Manager submit thông báo với tiêu đề, nội dung và phạm vi là cơ sở hoặc phòng được phân công THE SYSTEM SHALL chèn bản ghi mới vào bảng dbo.notifications với trạng thái SENT.
        setupSession();
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("title")).thenReturn("Test Title");
        when(request.getParameter("content")).thenReturn("Test Content");
        when(request.getParameter("recipientType")).thenReturn("ROOM");
        when(request.getParameter("recipientId")).thenReturn("5");

        when(mockNotificationService.sendNotification(eq("Test Title"), eq("Test Content"), eq("ROOM"), eq(5), any(), eq(10)))
                .thenReturn(true);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockNotificationService).sendNotification("Test Title", "Test Content", "ROOM", 5, null, 10);
        verify(response).sendRedirect("/manager/notifications?tab=general&type=sent");
    }

    // T003 Happy Path - Test doPost send debt reminder successfully
    @Test
    void testDoPost_SendDebtReminder_Success() throws Exception {
        // EARS: WHEN Manager sends debt reminder for an overdue invoice THE SYSTEM SHALL generate a notification with code prefix NTF-DEBT- targeting the room of that invoice.
        setupSession();
        when(request.getPathInfo()).thenReturn("/send-debt-reminder");
        when(request.getParameter("invoiceId")).thenReturn("100");
        when(request.getParameter("title")).thenReturn("Nhắc nợ");
        when(request.getParameter("content")).thenReturn("Đóng tiền");

        when(mockNotificationService.sendDebtReminder(eq(100), eq("Nhắc nợ"), eq("Đóng tiền"), eq(10)))
                .thenReturn(true);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockNotificationService).sendDebtReminder(100, "Nhắc nợ", "Đóng tiền", 10);
        verify(response).sendRedirect("/manager/notifications?tab=payment-reminder");
    }

    // T004 Happy Path - Test doPost report utility issue successfully
    @Test
    void testDoPost_ReportUtilityIssue_Success() throws Exception {
        // EARS: WHEN Manager reports incorrect meter readings and sends operator request THE SYSTEM SHALL update meter reading status to REPORTED AND insert a new request under UTILITY category with status PENDING assigned to Operator.
        setupSession();
        when(request.getPathInfo()).thenReturn("/send-operator");
        when(request.getParameter("invoiceId")).thenReturn("200");
        when(request.getParameter("operatorId")).thenReturn("15");
        when(request.getParameter("title")).thenReturn("Sai chỉ số");
        when(request.getParameter("content")).thenReturn("Kiểm tra lại");

        when(mockNotificationService.sendOperatorRequest(eq(200), eq(15), eq("Sai chỉ số"), eq("Kiểm tra lại"), eq(10)))
                .thenReturn(true);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockNotificationService).sendOperatorRequest(200, 15, "Sai chỉ số", "Kiểm tra lại", 10);
        verify(response).sendRedirect("/manager/notifications?tab=incorrect-utility");
    }

    // T005 Error - Test doPost rejects sending global notification (ALL) with 403 Forbidden
    @Test
    void testDoPost_SendGlobalNotification_Forbidden() throws Exception {
        // EARS: WHEN Manager tries to send a global notification to the entire system THE SYSTEM SHALL reject and return HTTP 403 Forbidden.
        setupSession();
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("title")).thenReturn("Toàn bộ");
        when(request.getParameter("content")).thenReturn("Thông báo toàn hệ thống");
        when(request.getParameter("recipientType")).thenReturn("ALL");

        when(mockNotificationService.sendNotification(eq("Toàn bộ"), eq("Thông báo toàn hệ thống"), eq("ALL"), isNull(), any(), eq(10)))
                .thenThrow(new java.nio.file.AccessDeniedException("Không có quyền gửi thông báo toàn hệ thống."));
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errorMessage"), eq("Không có quyền gửi thông báo toàn hệ thống."));
        verify(dispatcher).forward(request, response);
    }

    // T006 Error - Test doPost rejects cross-facility notification with 403 Forbidden
    @Test
    void testDoPost_SendCrossFacility_Forbidden() throws Exception {
        // EARS: WHEN thao tác ngoài cơ sở quản lý THE SYSTEM SHALL trả về lỗi 403 Forbidden.
        setupSession();
        when(request.getPathInfo()).thenReturn("/send-operator");
        when(request.getParameter("invoiceId")).thenReturn("200");
        when(request.getParameter("operatorId")).thenReturn("15");
        when(request.getParameter("title")).thenReturn("Sai chỉ số");
        when(request.getParameter("content")).thenReturn("Kiểm tra lại");

        when(mockNotificationService.sendOperatorRequest(eq(200), eq(15), eq("Sai chỉ số"), eq("Kiểm tra lại"), eq(10)))
                .thenThrow(new java.nio.file.AccessDeniedException("Cơ sở không thuộc quyền quản lý."));

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN, "Cơ sở không thuộc quyền quản lý.");
    }

    // T007 Error - Test doPost fails gracefully on missing title/content (Validation)
    @Test
    void testDoPost_MissingTitleOrContent_Fails() throws Exception {
        // EARS: WHEN Tiêu đề hoặc nội dung bỏ trống THE SYSTEM SHALL Gán error message vào Session và redirect về lại trang điền form
        setupSession();
        when(request.getPathInfo()).thenReturn("/create");
        when(request.getParameter("title")).thenReturn("");
        when(request.getParameter("content")).thenReturn("Nội dung");
        when(request.getParameter("recipientType")).thenReturn("ROOM");

        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(mockNotificationService, never()).sendNotification(any(), any(), any(), any(), any(), anyInt());
        verify(request).setAttribute(eq("errorMessage"), eq("Vui lòng điền đầy đủ các trường bắt buộc."));
        verify(dispatcher).forward(request, response);
    }

    // T008 Error - Test doPost utility issue report correctly rolls back on DB failure
    @Test
    void testDoPost_ReportUtilityIssue_RollbackOnFailure() throws Exception {
        // EARS: WHEN Lỗi ghi nhận giao dịch Database THE SYSTEM SHALL Thực hiện rollback giao dịch, gán error message và redirect
        setupSession();
        when(request.getPathInfo()).thenReturn("/send-operator");
        when(request.getParameter("invoiceId")).thenReturn("200");
        when(request.getParameter("operatorId")).thenReturn("15");
        when(request.getParameter("title")).thenReturn("Sai chỉ số");
        when(request.getParameter("content")).thenReturn("Kiểm tra lại");

        when(mockNotificationService.sendOperatorRequest(eq(200), eq(15), eq("Sai chỉ số"), eq("Kiểm tra lại"), eq(10)))
                .thenThrow(new RuntimeException("DB Exception during transaction"));

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    // T009 Boundary - Test notification creation with maximum allowed length
    @Test
    void testDoPost_Notification_MaxLength() throws Exception {
        setupSession();
        when(request.getPathInfo()).thenReturn("/create");
        String maxTitle = "A".repeat(255);
        when(request.getParameter("title")).thenReturn(maxTitle);
        when(request.getParameter("content")).thenReturn("Content");
        when(request.getParameter("recipientType")).thenReturn("ROOM");
        when(request.getParameter("recipientId")).thenReturn("5");

        when(mockNotificationService.sendNotification(eq(maxTitle), eq("Content"), eq("ROOM"), eq(5), any(), eq(10)))
                .thenReturn(true);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockNotificationService).sendNotification(maxTitle, "Content", "ROOM", 5, null, 10);
    }

    // T010 Boundary - Test debt reminder allowed when exactly 1 day overdue
    @Test
    void testDoPost_DebtReminder_ExactlyOverdueByOneDay() throws Exception {
        setupSession();
        when(request.getPathInfo()).thenReturn("/send-debt-reminder");
        when(request.getParameter("invoiceId")).thenReturn("100");
        when(request.getParameter("title")).thenReturn("Nhắc nợ 1 ngày");
        when(request.getParameter("content")).thenReturn("Đóng tiền");

        when(mockNotificationService.sendDebtReminder(eq(100), eq("Nhắc nợ 1 ngày"), eq("Đóng tiền"), eq(10)))
                .thenReturn(true);
        lenient().when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockNotificationService).sendDebtReminder(100, "Nhắc nợ 1 ngày", "Đóng tiền", 10);
    }

    // T011 Concurrent - Test double-strike utility issue reporting
    @Test
    void testConcurrency_DoubleStrike_ReportUtilityIssue() throws Exception {
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        doAnswer(invocation -> {
            int currentSuccess = successCount.incrementAndGet();
            if (currentSuccess > 1) {
                throw new IllegalStateException("Đã được báo cáo trước đó");
            }
            return true;
        }).when(mockNotificationService).reportIncorrectInvoice(eq(500), eq(10));

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getSession(false)).thenReturn(sess);
                    when(sess.getAttribute("currentUser")).thenReturn(mockManager);
                    when(req.getPathInfo()).thenReturn("/");
                    when(req.getParameter("action")).thenReturn("report-incorrect");
                    when(req.getParameter("invoiceId")).thenReturn("500");
                    when(req.getContextPath()).thenReturn("");
                    
                    Map<String, Object> verifyMap = new HashMap<>();
                    verifyMap.put("code", "INV-123");
                    lenient().when(mockNotificationService.getInvoiceDetailsForSendOperator(eq(500), eq(10))).thenReturn(verifyMap);

                    latch.await();
                    servlet.doGet(req, res);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        latch.countDown();
        doneLatch.await();
        executorService.shutdown();

        // 1 success, 1 throws IllegalStateException which redirects with flash message "danger"
        verify(mockNotificationService, times(2)).reportIncorrectInvoice(500, 10);
    }
}

