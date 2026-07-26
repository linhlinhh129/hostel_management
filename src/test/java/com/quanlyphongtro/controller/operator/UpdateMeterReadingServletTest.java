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
public class UpdateMeterReadingServletTest {

    private UpdateMeterReadingServlet servlet;

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

        servlet = new UpdateMeterReadingServlet();
        servlet.init();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_ShouldReturnUpdatePage() throws ServletException, IOException {
        when(request.getParameter("roomCode")).thenReturn("R_TEST_101");
        when(request.getRequestDispatcher("/WEB-INF/views/operator/meter_readings/update.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("roomCode"), eq("R_TEST_101"));
        verify(request).setAttribute(eq("activeMenu"), eq("meter-readings-update"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doPost_MissingRoomCode_ShouldRedirectWithError() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("roomCode")).thenReturn("");
        when(request.getContextPath()).thenReturn("/app");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), eq("Vui lòng nhập Mã phòng."));
        verify(session).setAttribute(eq("flashType"), eq("error"));
        verify(response).sendRedirect("/app/operator/meter-readings");
    }

    @Test
    public void Test_doPost_NewElectricLessThanOld_ShouldRedirectWithError() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);
        
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("roomCode")).thenReturn("R_TEST_101");
        // In fixture, previous electric reading is 100
        when(request.getParameter("newElectric")).thenReturn("90"); // Less than 100
        when(request.getParameter("newWater")).thenReturn("20");
        when(request.getContextPath()).thenReturn("/app");

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("không được nhỏ hơn số cũ"));
        verify(session).setAttribute(eq("flashType"), eq("error"));
        verify(response).sendRedirect("/app/operator/meter-readings");
    }
}
