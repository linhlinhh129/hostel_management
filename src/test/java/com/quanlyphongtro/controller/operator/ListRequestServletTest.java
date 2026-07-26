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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListRequestServletTest {

    private ListRequestServlet servlet;

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
        // Clear old data and insert test fixtures
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
        SQLFixtureHelper.executeSqlScript("fixtures/insert_operator_test_data.sql");
    }

    @AfterAll
    public static void tearDownClass() {
        // Clean up fixtures after tests
        SQLFixtureHelper.executeSqlScript("fixtures/delete_operator_test_data.sql");
    }

    @BeforeEach
    public void setUp() throws ServletException {
        servlet = new ListRequestServlet();
        servlet.init();
    }

    @Test
    public void Test_doGet_NoFilter_ShouldReturnFirstPageAndSetAttributes() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992); // Assigned staff id in fixtures

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("status")).thenReturn(null);
        when(request.getParameter("category")).thenReturn(null);
        when(request.getParameter("page")).thenReturn(null);
        when(request.getRequestDispatcher("/WEB-INF/views/operator/requests/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        ArgumentCaptor<List<Request>> requestListCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("requestList"), requestListCaptor.capture());
        
        List<Request> requestList = requestListCaptor.getValue();
        // From fixtures, there should be 1 request assigned to 9992
        assertThat(requestList).isNotEmpty();
        assertThat(requestList.get(0).getCode()).isEqualTo("REQ_TEST_01");

        verify(request).setAttribute(eq("currentPage"), eq(1));
        verify(request).setAttribute(eq("totalRecords"), eq(1));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doGet_WithStatusAndCategoryFilter_ShouldReturnFilteredRequests() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        
        when(request.getParameter("status")).thenReturn("PENDING");
        when(request.getParameter("category")).thenReturn("Incident");
        when(request.getParameter("page")).thenReturn("1");
        when(request.getRequestDispatcher("/WEB-INF/views/operator/requests/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        ArgumentCaptor<List<Request>> requestListCaptor = ArgumentCaptor.forClass(List.class);
        verify(request).setAttribute(eq("requestList"), requestListCaptor.capture());
        
        List<Request> requestList = requestListCaptor.getValue();
        assertThat(requestList).isNotEmpty();
        assertThat(requestList.get(0).getCategory()).isEqualTo("Incident");
        assertThat(requestList.get(0).getStatus()).isEqualTo("PENDING");

        verify(request).setAttribute(eq("paramStatus"), eq("PENDING"));
        verify(request).setAttribute(eq("paramCategory"), eq("Incident"));
        verify(dispatcher).forward(request, response);
    }

    @Test
    public void Test_doGet_InvalidPageParam_ShouldDefaultToPage1() throws ServletException, IOException {
        UserSessionDTO currentUser = new UserSessionDTO();
        currentUser.setId(9992);

        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        when(request.getParameter("status")).thenReturn(null);
        when(request.getParameter("category")).thenReturn(null);
        when(request.getParameter("page")).thenReturn("invalid_page_number");
        when(request.getRequestDispatcher("/WEB-INF/views/operator/requests/list.jsp")).thenReturn(dispatcher);

        servlet.doGet(request, response);

        verify(request).setAttribute(eq("currentPage"), eq(1));
        verify(dispatcher).forward(request, response);
    }
}
