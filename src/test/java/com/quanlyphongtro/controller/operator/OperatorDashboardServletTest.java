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

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OperatorDashboardServletTest {

    private OperatorDashboardServlet servlet;

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

        servlet = new OperatorDashboardServlet();
        servlet.init();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_ShouldReturnDashboardData() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992); // Operator ID in Fixtures

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getRequestDispatcher("/WEB-INF/views/operator/dashboard.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("billingPeriodLabel"), anyString());
        verify(request).setAttribute(eq("totalRooms"), anyInt());
        verify(request).setAttribute(eq("pendingTickets"), anyInt());
        verify(request).setAttribute(eq("upcomingAppointments"), anyList());
        verify(dispatcher).forward(request, response);
    }
}
