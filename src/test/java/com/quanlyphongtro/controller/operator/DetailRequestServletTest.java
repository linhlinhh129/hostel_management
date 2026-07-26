package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.SQLFixtureHelper;
import com.quanlyphongtro.util.TestDBInitializer;
import com.quanlyphongtro.model.Request;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DetailRequestServletTest {

    private DetailRequestServlet servlet;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeAll
    public static void setUpClass() {
        TestDBInitializer.initJNDI();
    }

    @BeforeEach
    public void setUp() throws ServletException {
        // Reset DB before EACH test to ensure independent tests
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
        SQLFixtureHelper.executeSqlScript("fixtures/insert_operator_test_data.sql");
        
        servlet = new DetailRequestServlet();
        servlet.init();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_ValidId_ShouldReturnRequestDetail() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("9991");
        when(request.getRequestDispatcher("/WEB-INF/views/operator/requests/detail.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        ArgumentCaptor<Request> reqCaptor = ArgumentCaptor.forClass(Request.class);
        verify(request).setAttribute(eq("reqDetail"), reqCaptor.capture());
        
        Request reqDetail = reqCaptor.getValue();
        assertThat(reqDetail).isNotNull();
        assertThat(reqDetail.getId()).isEqualTo(9991);
        assertThat(reqDetail.getTitle()).isEqualTo("Broken light");
        
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doGet_InvalidId_ShouldReturn404() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("999999");
        when(request.getRequestDispatcher("/WEB-INF/views/error/404.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("error"), eq("Yêu cầu không tồn tại."));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doPost_AcceptAction_ShouldAssignToOperatorAndRedirect() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.getId(); // Return 0 by default, let's set it
        currentUser.setId(9992); // Operator ID

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("id")).thenReturn("9991");
        when(request.getParameter("action")).thenReturn("accept");
        
        // This is a redirect, so we check sendRedirect
        servlet.doPost(request, response);

        verify(session).setAttribute(eq("successMessage"), eq("Đã tiếp nhận yêu cầu thành công!"));
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void Test_doPost_RejectAction_MissingReason_ShouldReturnError() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("id")).thenReturn("9991");
        when(request.getParameter("action")).thenReturn("reject");
        when(request.getParameter("rejectReason")).thenReturn(""); // Missing reason
        
        // Because it calls doGet internally, it will need dispatcher for detail.jsp
        when(request.getRequestDispatcher("/WEB-INF/views/operator/requests/detail.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), eq("Lý do từ chối không được để trống."));
        verify(dispatcher).forward(request, response);
    }
}
