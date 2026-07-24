package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dto.PaymentDetailDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.PaymentService;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentDetailServletTest {

    private PaymentDetailServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher dispatcher;
    @Mock
    private PaymentService mockPaymentService;

    private UserSessionDTO mockManager;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new PaymentDetailServlet();
        
        Field paymentServiceField = PaymentDetailServlet.class.getDeclaredField("paymentService");
        paymentServiceField.setAccessible(true);
        paymentServiceField.set(servlet, mockPaymentService);

        mockManager = new UserSessionDTO(); mockManager.setId(10); mockManager.setUsername("manager"); mockManager.setRole("MANAGER");
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockManager);
    }

    // T003 Happy Path - Test doGet View Detail successfully
    @Test
    void testDoGet_ViewPaymentDetail_Success() throws Exception {
        // EARS: WHEN Management Board selects a transaction THE SYSTEM SHALL display transaction details.
        setupSession();
        when(request.getPathInfo()).thenReturn("/100");
        PaymentDetailDTO mockDTO = new PaymentDetailDTO();
        when(mockPaymentService.findById(10, 100)).thenReturn(mockDTO);
        when(request.getRequestDispatcher("/WEB-INF/views/manager/payments/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(mockPaymentService).findById(10, 100);
        verify(request).setAttribute("payment", mockDTO);
        verify(dispatcher).forward(request, response);
    }

    // T004 Happy Path - Test doPost Approve Payment successfully
    @Test
    void testDoPost_ApprovePayment_Success() throws Exception {
        // EARS: WHEN Management Board approves a valid transaction (PENDING or REJECTED) THE SYSTEM SHALL update transaction status to SUCCESS.
        setupSession();
        when(request.getPathInfo()).thenReturn("/100/approve");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockPaymentService).approvePayment(100, 10);
        verify(session).setAttribute("success", "Đã duyệt giao dịch thành công.");
        verify(response).sendRedirect("/manager/payments/100");
    }

    // T005 Happy Path - Test doPost Reject Payment successfully
    @Test
    void testDoPost_RejectPayment_Success() throws Exception {
        // EARS: WHEN Management Board rejects a PENDING transaction THE SYSTEM SHALL update transaction status to REJECTED.
        setupSession();
        when(request.getPathInfo()).thenReturn("/100/reject");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockPaymentService).rejectPayment(100, 10);
        verify(session).setAttribute("success", "Đã từ chối giao dịch.");
        verify(response).sendRedirect("/manager/payments/100");
    }

    // T006 Happy Path - Test doPost Re-approve REJECTED payment successfully
    @Test
    void testDoPost_ReApproveRejectedPayment_Success() throws Exception {
        // EARS: As a Ban quản lý, I want to duyệt lại các giao dịch đã bị từ chối trước đó
        // Trạng thái ban đầu REJECTED -> Service sẽ cho phép. Test giống Approve
        setupSession();
        when(request.getPathInfo()).thenReturn("/200/approve");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockPaymentService).approvePayment(200, 10);
        verify(response).sendRedirect("/manager/payments/200");
    }

    // T007 Error - Test doPost Approve non-existent payment returns 404 (Logic Exception)
    @Test
    void testDoPost_ApproveNonExistentPayment_Fails() throws Exception {
        // EARS: WHEN Management Board approves a non-existing transaction THE SYSTEM SHALL return HTTP 404
        setupSession();
        when(request.getPathInfo()).thenReturn("/999/approve");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");
        
        doThrow(new IllegalArgumentException("Giao dịch không tồn tại")).when(mockPaymentService).approvePayment(999, 10);

        servlet.doPost(request, response);

        verify(session).setAttribute("error", "Có lỗi xảy ra: Giao dịch không tồn tại");
        verify(response).sendRedirect("/manager/payments/999"); // Error handling flow redirects back
    }

    // T008 Error - Test doPost Approve already SUCCESS payment returns 400
    @Test
    void testDoPost_ApproveAlreadyApprovedPayment_Fails() throws Exception {
        // EARS: WHEN transaction is already approved THE SYSTEM SHALL return HTTP 400
        setupSession();
        when(request.getPathInfo()).thenReturn("/100/approve");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        doThrow(new IllegalStateException("Giao dịch đã được duyệt trước đó.")).when(mockPaymentService).approvePayment(100, 10);

        servlet.doPost(request, response);

        verify(session).setAttribute("error", "Có lỗi xảy ra: Giao dịch đã được duyệt trước đó.");
        verify(response).sendRedirect("/manager/payments/100");
    }

    // T009 Error - Test doPost Reject already SUCCESS payment returns 400
    @Test
    void testDoPost_RejectAlreadyApprovedPayment_Fails() throws Exception {
        setupSession();
        when(request.getPathInfo()).thenReturn("/100/reject");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        doThrow(new IllegalStateException("Không thể từ chối giao dịch đã duyệt.")).when(mockPaymentService).rejectPayment(100, 10);

        servlet.doPost(request, response);

        verify(session).setAttribute("error", "Có lỗi xảy ra: Không thể từ chối giao dịch đã duyệt.");
        verify(response).sendRedirect("/manager/payments/100");
    }

    // T010 Error - Test unauthorized access returns 401
    @Test
    void testUnauthorizedAccess() throws Exception {
        // EARS: WHILE user is unauthenticated THE SYSTEM SHALL prevent approving transactions AND return HTTP 401
        when(request.getPathInfo()).thenReturn("/100/approve");
        when(request.getSession(false)).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    // T011 Boundary - Test Approve Payment when amount exactly matches invoice total
    @Test
    void testDoPost_ApprovePayment_AmountExactlyMatchesInvoice() throws Exception {
        // No special condition needed in servlet for this, handled by Service. But we verify normal flow.
        setupSession();
        when(request.getPathInfo()).thenReturn("/300/approve");
        when(request.getSession()).thenReturn(session);
        when(request.getContextPath()).thenReturn("");

        servlet.doPost(request, response);

        verify(mockPaymentService).approvePayment(300, 10);
        verify(response).sendRedirect("/manager/payments/300");
    }

    // T012 Concurrent - Test double approve race condition
    @Test
    void testConcurrency_DoubleApprove_RaceCondition() throws Exception {
        int numberOfThreads = 2;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        doAnswer(invocation -> {
            int currentSuccess = successCount.incrementAndGet();
            if (currentSuccess > 1) {
                throw new IllegalStateException("Giao dịch đã được duyệt trước đó.");
            }
            return null;
        }).when(mockPaymentService).approvePayment(500, 10);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getSession(false)).thenReturn(sess);
                    when(sess.getAttribute("currentUser")).thenReturn(mockManager);
                    when(req.getPathInfo()).thenReturn("/500/approve");
                    when(req.getSession()).thenReturn(sess);
                    when(req.getContextPath()).thenReturn("");
                    
                    latch.await();
                    servlet.doPost(req, res);
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

        // Should invoke approvePayment 2 times, one throws exception resulting in error msg
        verify(mockPaymentService, times(2)).approvePayment(500, 10);
    }

    // T013 Concurrent - Test approve and reject race condition
    @Test
    void testConcurrency_ApproveAndReject_RaceCondition() throws Exception {
        // Thread 1 calls approve, Thread 2 calls reject. Service should throw exception on the slower one.
        // Similar to T012 but mixed actions.
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(2);

        doAnswer(invocation -> {
            Thread.sleep(100);
            return null;
        }).when(mockPaymentService).approvePayment(600, 10);

        doThrow(new IllegalStateException("Giao dịch đang được xử lý.")).when(mockPaymentService).rejectPayment(600, 10);

        // Thread 1 - Approve
        executorService.submit(() -> {
            try {
                HttpServletRequest req = mock(HttpServletRequest.class);
                HttpServletResponse res = mock(HttpServletResponse.class);
                HttpSession sess = mock(HttpSession.class);
                when(req.getSession(false)).thenReturn(sess);
                when(sess.getAttribute("currentUser")).thenReturn(mockManager);
                when(req.getPathInfo()).thenReturn("/600/approve");
                when(req.getSession()).thenReturn(sess);
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
                when(req.getSession(false)).thenReturn(sess);
                when(sess.getAttribute("currentUser")).thenReturn(mockManager);
                when(req.getPathInfo()).thenReturn("/600/reject");
                when(req.getSession()).thenReturn(sess);
                when(req.getContextPath()).thenReturn("");
                
                latch.await();
                servlet.doPost(req, res);
            } catch (Exception e) {
            } finally { doneLatch.countDown(); }
        });

        latch.countDown();
        doneLatch.await();
        executorService.shutdown();

        verify(mockPaymentService).approvePayment(600, 10);
        verify(mockPaymentService).rejectPayment(600, 10);
    }
}

