package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.util.DatabaseUtil;
import com.quanlyphongtro.util.VNPayConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Vector;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TenantPaymentReturnServletTest {

    private TenantPaymentReturnServlet servlet;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private Connection mockConnection;
    @Mock
    private PreparedStatement mockPreparedStatement;
    @Mock
    private ResultSet mockResultSet;

    private MockedStatic<VNPayConfig> mockedVNPayConfig;
    private MockedStatic<DatabaseUtil> mockedDatabaseUtil;

    @BeforeEach
    void setUp() throws Exception {
        servlet = new TenantPaymentReturnServlet();
        
        mockedVNPayConfig = Mockito.mockStatic(VNPayConfig.class);
        mockedVNPayConfig.when(VNPayConfig::getSecretKey).thenReturn("SECRET");
        
        mockedDatabaseUtil = Mockito.mockStatic(DatabaseUtil.class);
        mockedDatabaseUtil.when(DatabaseUtil::getConnection).thenReturn(mockConnection);
    }

    @AfterEach
    void tearDown() {
        if (mockedVNPayConfig != null) mockedVNPayConfig.close();
        if (mockedDatabaseUtil != null) mockedDatabaseUtil.close();
    }

    private void setupValidHash() {
        mockedVNPayConfig.when(() -> VNPayConfig.hmacSHA512(anyString(), anyString())).thenReturn("VALID_HASH");
        when(request.getParameter("vnp_SecureHash")).thenReturn("VALID_HASH");
    }

    private void setupParameters(String responseCode, String txnRef) {
        Vector<String> paramNames = new Vector<>();
        paramNames.add("vnp_ResponseCode");
        paramNames.add("vnp_TxnRef");
        paramNames.add("vnp_Amount");
        
        Enumeration<String> enumeration = paramNames.elements();
        when(request.getParameterNames()).thenReturn(enumeration);
        when(request.getParameter("vnp_ResponseCode")).thenReturn(responseCode);
        when(request.getParameter("vnp_TxnRef")).thenReturn(txnRef);
        when(request.getParameter("vnp_Amount")).thenReturn("50000000"); // 500,000 VND * 100
    }

    // T005 Test doGet VNPAY Return Success
    @Test
    void testDoGet_PaymentReturn_Success() throws Exception {
        setupValidHash();
        setupParameters("00", "INV100T123456");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(session);

        // Mock JDBC
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockPreparedStatement);
        when(mockPreparedStatement.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("room_id")).thenReturn(5);

        servlet.doGet(request, response);

        verify(mockConnection).commit();
        verify(response).sendRedirect("/tenant/invoices/100");
    }

    // T008 Test doGet VNPAY Return with Invalid Signature
    @Test
    void testDoGet_PaymentReturn_InvalidSignature() throws Exception {
        mockedVNPayConfig.when(() -> VNPayConfig.hmacSHA512(anyString(), anyString())).thenReturn("DIFFERENT_HASH");
        when(request.getParameter("vnp_SecureHash")).thenReturn("INVALID_HASH");
        
        Vector<String> paramNames = new Vector<>();
        paramNames.add("vnp_Amount");
        when(request.getParameterNames()).thenReturn(paramNames.elements());
        when(request.getParameter("vnp_Amount")).thenReturn("50000000");
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(session);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("errorMessage"), eq("Chữ ký bảo mật không hợp lệ!"));
        verify(response).sendRedirect("/tenant/invoices");
    }

    // T010 Test doGet VNPAY Return with Failed Transaction Code (!= 00)
    @Test
    void testDoGet_PaymentReturn_FailedTransaction() throws Exception {
        setupValidHash();
        setupParameters("24", "INV100T123456"); // User cancelled
        when(request.getContextPath()).thenReturn("");
        when(request.getSession(false)).thenReturn(session);

        servlet.doGet(request, response);

        verify(session).setAttribute(eq("errorMessage"), eq("Giao dịch không thành công hoặc bị hủy."));
        verify(response).sendRedirect("/tenant/invoices");
    }
}
