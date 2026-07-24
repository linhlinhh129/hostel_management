package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.MeterReadingService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UpdateMeterReadingServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletContext servletContext;
    
    @Mock
    private MeterReadingService mockMeterReadingService;
    @Mock
    private AuditLogDAO mockAuditLogDAO;
    
    @Mock
    private Part electricPart;
    @Mock
    private Part waterPart;

    private UpdateMeterReadingServlet servlet;
    private UserSessionDTO currentUser;

    @BeforeEach
    void setUp() throws Exception {
        servlet = spy(new UpdateMeterReadingServlet());
        servlet.init();

        Field f1 = UpdateMeterReadingServlet.class.getDeclaredField("meterReadingService");
        f1.setAccessible(true);
        f1.set(servlet, mockMeterReadingService);

        Field f2 = UpdateMeterReadingServlet.class.getDeclaredField("auditLogDAO");
        f2.setAccessible(true);
        f2.set(servlet, mockAuditLogDAO);

        doReturn(servletContext).when(servlet).getServletContext();
        when(servletContext.getRealPath(anyString())).thenReturn("D:/dummy/path");

        when(request.getSession(false)).thenReturn(session);
        when(request.getContextPath()).thenReturn("/hostel");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);

        currentUser = new UserSessionDTO();
        currentUser.setId(10);
        currentUser.setRole("OPERATOR");
        when(session.getAttribute("currentUser")).thenReturn(currentUser);
        
        when(electricPart.getHeader("content-disposition")).thenReturn("form-data; name=\"electricMeterImage\"; filename=\"electric.jpg\"");
        when(waterPart.getHeader("content-disposition")).thenReturn("form-data; name=\"waterMeterImage\"; filename=\"water.jpg\"");
        when(electricPart.getSize()).thenReturn(1024L);
        when(waterPart.getSize()).thenReturn(1024L);
    }

    // Phase 2: Happy Path
    @Test
    // # EARS [KHI người dùng nhập thông tin hợp lệ, hệ thống chưa có dữ liệu tháng này, HỆ THỐNG PHẢI Insert và báo thành công]
    void testDoPost_InsertSuccess() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("150");
        when(request.getParameter("newWater")).thenReturn("50");
        when(request.getPart("electricMeterImage")).thenReturn(electricPart);
        when(request.getPart("waterMeterImage")).thenReturn(waterPart);

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setRoomId(1);
        prev.setPreviousElectricReading(100);
        prev.setPreviousWaterReading(30);
        
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);
        when(mockMeterReadingService.checkCurrentMonthReadingExists(eq(1), anyInt(), anyInt())).thenReturn(null); // Not exists -> Insert
        when(mockMeterReadingService.insertMeterReading(eq(1), eq(150), eq(50), anyString(), anyString(), eq(10))).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockMeterReadingService).insertMeterReading(eq(1), eq(150), eq(50), anyString(), anyString(), eq(10));
        verify(session).setAttribute("flashType", "success");
        verify(response).sendRedirect(anyString());
    }

    @Test
    // # EARS [KHI người dùng nhập hợp lệ nhưng tháng này đã có dữ liệu, HỆ THỐNG PHẢI Update bản ghi cũ]
    void testDoPost_UpdateSuccess() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("150");
        when(request.getParameter("newWater")).thenReturn("50");
        when(request.getPart("electricMeterImage")).thenReturn(electricPart);
        when(request.getPart("waterMeterImage")).thenReturn(waterPart);

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setRoomId(1);
        prev.setPreviousElectricReading(100);
        prev.setPreviousWaterReading(30);
        
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);
        when(mockMeterReadingService.checkCurrentMonthReadingExists(eq(1), anyInt(), anyInt())).thenReturn(999); // Exists ID=999
        when(mockMeterReadingService.updateMeterReading(eq(999), eq(150), eq(50), anyString(), anyString())).thenReturn(true);

        servlet.doPost(request, response);

        verify(mockMeterReadingService).updateMeterReading(eq(999), eq(150), eq(50), anyString(), anyString());
        verify(session).setAttribute("flashType", "success");
    }

    // Phase 3: Error Cases
    @Test
    // # EARS [KHI người dùng không nhập phòng, HỆ THỐNG PHẢI redirect báo lỗi]
    void testDoPost_MissingRoomCode() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("");
        
        servlet.doPost(request, response);
        
        verify(session).setAttribute("flashMessage", "Vui lòng nhập Mã phòng.");
        verify(mockMeterReadingService, never()).insertMeterReading(anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt());
    }

    @Test
    // # EARS [KHI người dùng nhập mã phòng không tồn tại, HỆ THỐNG PHẢI trả về ROOM_NOT_FOUND]
    void testDoPost_RoomNotFound() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P999");
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(anyString(), anyInt(), anyInt())).thenReturn(null);
        when(mockMeterReadingService.getPreviousReadingByRoomCode("P999")).thenReturn(null);

        servlet.doPost(request, response);

        verify(session).setAttribute("flashMessage", "Mã phòng không tồn tại hoặc phòng không ở trạng thái đang thuê.");
        verify(mockMeterReadingService, never()).insertMeterReading(anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt());
    }

    @Test
    // # EARS [KHI chỉ số điện mới nhỏ hơn chỉ số điện kỳ trước, HỆ THỐNG PHẢI trả lỗi ELECTRIC_READING_INVALID]
    void testDoPost_ElectricReadingInvalid() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("90"); // Less than prev(100)
        when(request.getParameter("newWater")).thenReturn("50");

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setPreviousElectricReading(100);
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("Chỉ số điện không hợp lệ"));
    }

    @Test
    // # EARS [KHI chỉ số nước mới nhỏ hơn chỉ số nước kỳ trước, HỆ THỐNG PHẢI trả lỗi WATER_READING_INVALID]
    void testDoPost_WaterReadingInvalid() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("150");
        when(request.getParameter("newWater")).thenReturn("10"); // Less than prev(30)

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setPreviousElectricReading(100);
        prev.setPreviousWaterReading(30);
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("Chỉ số nước không hợp lệ"));
    }

    @Test
    // # EARS [KHI người dùng không tải lên ảnh công tơ điện, HỆ THỐNG PHẢI trả lỗi ELECTRIC_METER_IMAGE_REQUIRED]
    void testDoPost_MissingElectricImage() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("150");
        when(request.getParameter("newWater")).thenReturn("50");
        
        when(request.getPart("electricMeterImage")).thenReturn(null); // Missing
        when(request.getPart("waterMeterImage")).thenReturn(waterPart);

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setPreviousElectricReading(100);
        prev.setPreviousWaterReading(30);
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("ảnh minh chứng công tơ điện"));
    }

    @Test
    // # EARS [KHI tham số đầu vào không phải là số, HỆ THỐNG catch NumberFormatException an toàn]
    void testDoPost_NumberFormatException() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("abc"); // Invalid format

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setPreviousElectricReading(100);
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("Dữ liệu nhập vào không hợp lệ"));
    }

    // Phase 4: Boundary Values
    @Test
    // # EARS [KHI chỉ số điện nước mới bằng y hệt kỳ trước (Biên hợp lệ), HỆ THỐNG PHẢI cho phép]
    void testDoPost_SameReadings() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("100"); // Exact boundary
        when(request.getParameter("newWater")).thenReturn("30");   // Exact boundary
        when(request.getPart("electricMeterImage")).thenReturn(electricPart);
        when(request.getPart("waterMeterImage")).thenReturn(waterPart);

        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setRoomId(1);
        prev.setPreviousElectricReading(100);
        prev.setPreviousWaterReading(30);
        
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);
        when(mockMeterReadingService.insertMeterReading(anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt())).thenReturn(true);

        servlet.doPost(request, response);

        verify(session).setAttribute("flashType", "success");
    }

    @Test
    // # EARS [KHI đọc chỉ số lần đầu tiên, giá trị cũ mặc định là 0, pass vòng Validation]
    void testDoPost_FirstTimeReading() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getParameter("newElectric")).thenReturn("10"); 
        when(request.getParameter("newWater")).thenReturn("5");   
        when(request.getPart("electricMeterImage")).thenReturn(electricPart);
        when(request.getPart("waterMeterImage")).thenReturn(waterPart);

        when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(null);
        
        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setRoomId(1); // Room exists
        when(mockMeterReadingService.getPreviousReadingByRoomCode("P101")).thenReturn(prev);
        
        when(mockMeterReadingService.insertMeterReading(anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt())).thenReturn(true);

        servlet.doPost(request, response);

        // prev was effectively 0, so 10 and 5 passed validation
        verify(session).setAttribute("flashType", "success");
    }

    @Test
    // # EARS [KHI người dùng tải file quá lớn vượt mức 5MB của Tomcat]
    void testDoPost_FileSizeExceeded() throws Exception {
        when(request.getParameter("roomCode")).thenReturn("P101");
        when(request.getPart("electricMeterImage")).thenThrow(new IllegalStateException("File size exceeded"));
        
        MeterStatusDTO prev = new MeterStatusDTO();
        prev.setPreviousElectricReading(100);
        when(mockMeterReadingService.getReadingBeforeCurrentMonth(anyString(), anyInt(), anyInt())).thenReturn(prev);

        servlet.doPost(request, response);

        verify(session).setAttribute(eq("flashMessage"), contains("Đã xảy ra lỗi hệ thống"));
    }

    // Phase 5: Concurrent Scenarios
    @Test
    // # EARS [KHI 2 Thread cùng nộp chỉ số. Thread 2 bị văng Exception (Mô phỏng DB Constraint)]
    void testConcurrency_DoubleSubmit() throws Exception {
        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        AtomicInteger successCount = new AtomicInteger(0);

        when(mockMeterReadingService.insertMeterReading(anyInt(), anyInt(), anyInt(), anyString(), anyString(), anyInt())).thenAnswer(inv -> {
            if (successCount.get() == 1) {
                throw new RuntimeException("DataIntegrityViolation - Duplicate Key");
            }
            successCount.incrementAndGet();
            return true;
        });

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    HttpServletRequest req = mock(HttpServletRequest.class);
                    HttpServletResponse res = mock(HttpServletResponse.class);
                    HttpSession sess = mock(HttpSession.class);

                    when(req.getContextPath()).thenReturn("/hostel");
                    when(req.getSession(false)).thenReturn(sess);
                    
                    UserSessionDTO u = new UserSessionDTO();
                    u.setId(10);
                    u.setRole("OPERATOR");
                    when(sess.getAttribute("currentUser")).thenReturn(u);
                    when(req.getParameter("roomCode")).thenReturn("P101");
                    when(req.getParameter("newElectric")).thenReturn("150");
                    when(req.getParameter("newWater")).thenReturn("50");
                    when(req.getPart("electricMeterImage")).thenReturn(electricPart);
                    when(req.getPart("waterMeterImage")).thenReturn(waterPart);

                    MeterStatusDTO prev = new MeterStatusDTO();
                    prev.setRoomId(1);
                    prev.setPreviousElectricReading(100);
                    prev.setPreviousWaterReading(30);
                    when(mockMeterReadingService.getReadingBeforeCurrentMonth(eq("P101"), anyInt(), anyInt())).thenReturn(prev);
                    when(mockMeterReadingService.checkCurrentMonthReadingExists(eq(1), anyInt(), anyInt())).thenReturn(null);

                    servlet.doPost(req, res);
                } catch (Exception e) {
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(5, TimeUnit.SECONDS));
        assertEquals(1, successCount.get()); // Only 1 should succeed
    }
}
