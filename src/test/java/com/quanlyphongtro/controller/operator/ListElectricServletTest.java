package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.SQLFixtureHelper;
import com.quanlyphongtro.util.TestDBInitializer;
import com.quanlyphongtro.dto.MeterStatusDTO;
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
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListElectricServletTest {

    private ListElectricServlet servlet;

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

        servlet = new ListElectricServlet();
        servlet.init();
    }

    @AfterEach
    public void tearDown() {
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @Test
    public void Test_doGet_ShouldReturnMeterListAndSetAttributes() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992); // Operator in fixtures

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("facility")).thenReturn(null);
        when(request.getParameter("roomCode")).thenReturn(null);
        
        when(request.getRequestDispatcher("/WEB-INF/views/operator/meter_readings/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        ArgumentCaptor<List<MeterStatusDTO>> meterListCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("meterList"), meterListCaptor.capture());
        
        List<MeterStatusDTO> meterList = meterListCaptor.getValue();
        assertThat(meterList).isNotNull();
        // Fixture has room 'R_TEST_101' which belongs to facility managed by 9992
        // Based on logic, we should see it
        assertThat(meterList).isNotEmpty();
        assertThat(meterList.get(0).getRoomCode()).isEqualTo("R_TEST_101");

        LocalDate now = LocalDate.now();
        verify(request).setAttribute(eq("currentMonth"), eq(now.getMonthValue()));
        verify(request).setAttribute(eq("currentYear"), eq(now.getYear()));
        verify(dispatcher).forward(request, response);
    }
}
