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

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IncidentReportServletTest {

    private IncidentReportServlet servlet;

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

        servlet = new IncidentReportServlet();
        servlet.init();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_ShouldReturnCreateIncidentPage() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992); // Operator

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getRequestDispatcher("/WEB-INF/views/operator/incidents/create.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("facilities"), anyList());
        verify(request).setAttribute(eq("facilityRoomsMap"), anyMap());
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doPost_MissingFields_ShouldReturnError() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        
        when(request.getParameter("facility")).thenReturn("");
        when(request.getParameter("locationType")).thenReturn(null);
        when(request.getParameter("locationDetail")).thenReturn(null);
        when(request.getParameter("category")).thenReturn(null);
        when(request.getParameter("priority")).thenReturn(null);
        when(request.getParameter("incidentName")).thenReturn("Test Incident");
        when(request.getParameter("content")).thenReturn(null);
        
        when(request.getRequestDispatcher("/WEB-INF/views/operator/incidents/create.jsp")).thenReturn(dispatcher);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("error"), eq("Vui lòng nhập đầy đủ các trường bắt buộc."));
        verify(dispatcher).forward(request, response);
    }
}
