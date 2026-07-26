package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.SQLFixtureHelper;
import com.quanlyphongtro.util.TestDBInitializer;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationListServletTest {

    private NotificationListServlet servlet;

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
    public void setUp() {
        // Prepare DB Fixtures
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
        SQLFixtureHelper.executeSqlScript("fixtures/insert_operator_test_data.sql");

        servlet = new NotificationListServlet();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_List_ShouldReturnNotifications() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992); // Operator in fixtures
        currentUser.setRole("OPERATOR");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getPathInfo()).thenReturn("/");
        when(request.getParameter("page")).thenReturn("1");
        
        when(request.getRequestDispatcher("/WEB-INF/views/operator/notifications.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("currentPage"), eq(1));
        verify(request).setAttribute(eq("pageTitle"), eq("Thông báo hệ thống"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doGet_Detail_InvalidId_ShouldReturn404() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);
        currentUser.setRole("OPERATOR");

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getPathInfo()).thenReturn("/999999"); // Fake ID
        
        servlet.doGet(request, response);

        verify(response).sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
