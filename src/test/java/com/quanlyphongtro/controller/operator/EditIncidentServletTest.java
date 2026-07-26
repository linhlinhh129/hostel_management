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
public class EditIncidentServletTest {

    private EditIncidentServlet servlet;

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
        // Prepare DB Fixtures
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
        SQLFixtureHelper.executeSqlScript("fixtures/insert_operator_test_data.sql");

        servlet = new EditIncidentServlet();
        servlet.init();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_MissingId_ShouldRedirect() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn(null);
        when(request.getParameter("source")).thenReturn(null);
        when(request.getContextPath()).thenReturn("/app");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/operator/incidents/my-reports");
    }

    @Test
    public void Test_doGet_InvalidId_ShouldRedirect() throws ServletException, IOException {
        when(request.getParameter("id")).thenReturn("999999"); // Fake ID
        when(request.getParameter("source")).thenReturn("requests");
        when(request.getContextPath()).thenReturn("/app");

        servlet.doGet(request, response);

        verify(response).sendRedirect("/app/operator/requests?error=invalid_status");
    }
}
