package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.RequestService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ManagerTicketsServletTest {

    private ManagerTicketsServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private RequestService mockRequestService;

    private UserSessionDTO mockManager;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new ManagerTicketsServlet();
        
        Field serviceField = ManagerTicketsServlet.class.getDeclaredField("requestService");
        serviceField.setAccessible(true);
        serviceField.set(servlet, mockRequestService);

        mockManager = new UserSessionDTO(); mockManager.setId(10); mockManager.setUsername("manager"); mockManager.setRole("MANAGER");
    }

    private void setupSession() {
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);
        lenient().when(session.getAttribute("currentUser")).thenReturn(mockManager);
    }

    // T002 Happy Path - Test doGet View Tickets List successfully
    @Test
    void testDoGet_ViewTickets_Success() throws Exception {
        // EARS: WHEN truy cập danh sách yêu cầu THE SYSTEM SHALL hiển thị danh sách các yêu cầu theo phân trang.
        setupSession();
        when(request.getPathInfo()).thenReturn("/");
        lenient().when(request.getParameter(org.mockito.ArgumentMatchers.anyString())).thenReturn(null);
        lenient().when(request.getParameter("page")).thenReturn("1");
        
        when(mockRequestService.countManagerTickets(eq(10), eq("TENANT"), isNull(), isNull())).thenReturn(1);
        when(mockRequestService.getManagerTickets(eq(10), eq("TENANT"), isNull(), isNull(), eq(1), eq(10)))
            .thenReturn(new ArrayList<>());
            
        when(request.getRequestDispatcher("/WEB-INF/views/manager/tickets/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("page"), any());
        verify(dispatcher).forward(request, response);
    }

    // T003 Happy Path - Test doGet View Ticket Detail and Timeline successfully
    @Test
    void testDoGet_ViewTicketDetail_Success() throws Exception {
        // EARS: WHEN xem chi tiết THE SYSTEM SHALL hiển thị thông tin và Timeline.
        setupSession();
        when(request.getPathInfo()).thenReturn("/50");
        
        Map<String, Object> ticket = new HashMap<>();
        when(mockRequestService.getManagerTicketDetail(50, 10)).thenReturn(ticket);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/tickets/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute("ticket", ticket);
        verify(dispatcher).forward(request, response);
    }

    // T004 Happy Path - Test doPost Receive Ticket successfully
    @Test
    void testDoPost_ReceiveTicket_Success() throws Exception {
        // EARS: WHEN Manager receives a ticket in PENDING or NEW status THE SYSTEM SHALL update ticket status to RECEIVED.
        when(request.getPathInfo()).thenReturn("/50/receive");
        when(request.getContextPath()).thenReturn("");
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);

        when(mockRequestService.receiveTicket(50)).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockRequestService).receiveTicket(50);
        verify(response).sendRedirect("/manager/tickets/50");
        verify(session).setAttribute("success", "Tiếp nhận yêu cầu thành công!");
    }

    // T005 Happy Path - Test doPost Schedule Ticket successfully
    @Test
    void testDoPost_ScheduleTicket_Success() throws Exception {
        // EARS: WHEN Manager schedules an appointment date THE SYSTEM SHALL update status to IN_PROGRESS.
        when(request.getPathInfo()).thenReturn("/50/schedule");
        when(request.getParameter("appointmentDate")).thenReturn("2026-07-25T10:00");
        when(request.getContextPath()).thenReturn("");
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);

        when(mockRequestService.scheduleTicket(eq(50), any(java.time.LocalDateTime.class))).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockRequestService).scheduleTicket(eq(50), any(java.time.LocalDateTime.class));
        verify(session).setAttribute("success", "Bắt đầu xử lý yêu cầu thành công!");
    }

    // T007 Happy Path - Test doPost Reject Ticket successfully
    @Test
    void testDoPost_RejectTicket_Success() throws Exception {
        // EARS: WHEN Manager rejects a ticket with reason THE SYSTEM SHALL update status to REJECTED.
        when(request.getPathInfo()).thenReturn("/50/reject");
        when(request.getParameter("reason")).thenReturn("Không hợp lệ");
        when(request.getContextPath()).thenReturn("");
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);

        when(mockRequestService.rejectTicket(50, "Không hợp lệ")).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockRequestService).rejectTicket(50, "Không hợp lệ");
        verify(session).setAttribute("success", "Từ chối yêu cầu thành công!");
    }

    // T009 Error - Test doPost Complete ticket without notes fails gracefully
    @Test
    void testDoPost_CompleteWithoutNotes_Fails() throws Exception {
        // EARS: WHEN hoàn thành nhưng bỏ trống notes THE SYSTEM SHALL báo lỗi không được phép.
        when(request.getPathInfo()).thenReturn("/50/complete");
        when(request.getParameter("notes")).thenReturn("");
        when(request.getContextPath()).thenReturn("");
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);

        servlet.doPost(request, response);

        verify(mockRequestService, never()).completeTicket(anyInt(), anyString(), any());
        verify(session).setAttribute("danger", "Ghi chú hoàn thành không được để trống.");
        verify(response).sendRedirect("/manager/tickets/50");
    }

    // T010 Error - Test doPost Reject ticket without reason fails gracefully
    @Test
    void testDoPost_RejectWithoutReason_Fails() throws Exception {
        // EARS: WHEN từ chối nhưng bỏ trống reason THE SYSTEM SHALL báo lỗi không được phép.
        when(request.getPathInfo()).thenReturn("/50/reject");
        when(request.getParameter("reason")).thenReturn("");
        when(request.getContextPath()).thenReturn("");
        lenient().when(request.getSession(org.mockito.ArgumentMatchers.anyBoolean())).thenReturn(session);

        servlet.doPost(request, response);

        verify(mockRequestService, never()).rejectTicket(anyInt(), anyString());
        verify(session).setAttribute("danger", "Vui lòng nhập lý do từ chối.");
        verify(response).sendRedirect("/manager/tickets/50");
    }

    // T014 Concurrent - Test Receive vs Reject race condition handling
    @Test
    void testConcurrency_ReceiveVsReject_RaceCondition() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        doAnswer(invocation -> {
            Thread.sleep(100);
            return true;
        }).when(mockRequestService).receiveTicket(50);

        doThrow(new IllegalStateException("Yêu cầu đã được tiếp nhận.")).when(mockRequestService).rejectTicket(eq(50), anyString());

        // Thread 1 - Receive
        executorService.submit(() -> {
            try {
                HttpServletRequest req = mock(HttpServletRequest.class);
                HttpServletResponse res = mock(HttpServletResponse.class);
                HttpSession sess = mock(HttpSession.class);
                when(req.getSession()).thenReturn(sess);
                when(req.getPathInfo()).thenReturn("/50/receive");
                when(req.getContextPath()).thenReturn("");
                
                latch.await();
                servlet.doPost(req, res);
            } catch (Exception e) {
            } finally { doneLatch.countDown(); }
        });

        // Thread 2 - Reject
        executorService.submit(() -> {
            try {
                HttpServletRequest req = mock(HttpServletRequest.class);
                HttpServletResponse res = mock(HttpServletResponse.class);
                HttpSession sess = mock(HttpSession.class);
                when(req.getSession()).thenReturn(sess);
                when(req.getPathInfo()).thenReturn("/50/reject");
                when(req.getParameter("reason")).thenReturn("Lý do");
                when(req.getContextPath()).thenReturn("");
                
                latch.await();
                servlet.doPost(req, res);
            } catch (Exception e) {
            } finally { doneLatch.countDown(); }
        });

        latch.countDown();
        doneLatch.await();
        executorService.shutdown();

        verify(mockRequestService).receiveTicket(50);
        verify(mockRequestService).rejectTicket(50, "Lý do");
    }
}

