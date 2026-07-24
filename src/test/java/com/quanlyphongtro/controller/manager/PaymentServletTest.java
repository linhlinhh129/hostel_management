package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dto.PaymentListItemDTO;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServletTest {

    private PaymentServlet servlet;

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
        servlet = new PaymentServlet();
        
        Field paymentServiceField = PaymentServlet.class.getDeclaredField("paymentService");
        paymentServiceField.setAccessible(true);
        paymentServiceField.set(servlet, mockPaymentService);

        mockManager = new UserSessionDTO(); mockManager.setId(10); mockManager.setUsername("manager"); mockManager.setRole("MANAGER");
    }

    private void setupSession() {
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(mockManager);
    }

    // T002 Happy Path - Test doGet View List successfully
    @Test
    void testDoGet_ViewPaymentList_Success() throws Exception {
        // EARS: WHEN Management Board opens payment transaction list THE SYSTEM SHALL display all payment transactions.
        setupSession();
        lenient().when(request.getParameter(org.mockito.ArgumentMatchers.anyString())).thenReturn(null);
        lenient().when(request.getParameter("page")).thenReturn("1");
        
        List<PaymentListItemDTO> mockList = new ArrayList<>();
        mockList.add(new PaymentListItemDTO());
        when(mockPaymentService.findPayments(eq(10), isNull(), isNull(), isNull(), isNull(), isNull(), isNull(), eq(0), eq(10)))
                .thenReturn(mockList);
        when(mockPaymentService.countPayments(eq(10), isNull(), isNull(), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(1);
                
        when(request.getRequestDispatcher("/WEB-INF/views/manager/payments/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(mockPaymentService).findPayments(10, null, null, null, null, null, null, 0, 10);
        verify(request).setAttribute("payments", mockList);
        verify(request).setAttribute("currentPage", 1);
        verify(request).setAttribute("totalPages", 1);
        verify(dispatcher).forward(request, response);
    }
}

