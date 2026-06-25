package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.OperatorDashboardDAO;
import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.service.MeterReadingService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet(name = "OperatorDashboardServlet", urlPatterns = "/operator/dashboard")
public class OperatorDashboardServlet extends BaseServlet {

    private OperatorDashboardDAO dashboardDAO;
    private MeterReadingService meterReadingService;

    @Override
    public void init() throws ServletException {
        this.dashboardDAO = new OperatorDashboardDAO();
        this.meterReadingService = new MeterReadingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Get operatorId from session
        int operatorId = 1;
        if (req.getSession(false) != null && req.getSession(false).getAttribute("currentUser") != null) {
            com.quanlyphongtro.dto.UserSessionDTO currentUser = (com.quanlyphongtro.dto.UserSessionDTO) req.getSession(false).getAttribute("currentUser");
            operatorId = currentUser.getId();
        }

        com.quanlyphongtro.dao.FacilityDAO facilityDAO = new com.quanlyphongtro.dao.FacilityDAO();
        java.util.List<com.quanlyphongtro.model.Facility> allFacilities = facilityDAO.findActiveList();
        java.util.List<String> myFacilityNames = new java.util.ArrayList<>();
        for (com.quanlyphongtro.model.Facility f : allFacilities) {
            if (f.getOperatorId() != null && f.getOperatorId().equals(operatorId)) {
                myFacilityNames.add(f.getName());
            }
        }
        
        String facilityNames = myFacilityNames.isEmpty() ? "Chưa phân công" : String.join(", ", myFacilityNames);

        String period = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("MM/yyyy", new Locale("vi")));
        req.setAttribute("billingPeriodLabel", period);
        req.setAttribute("facilityName", facilityNames);

        // 1. Lấy dữ liệu Điện nước (Meters)
        List<MeterStatusDTO> allMeterStatus = meterReadingService.getMeterStatusForCurrentMonth(null, null, operatorId);
        int totalRooms = allMeterStatus.size();
        
        List<MeterStatusDTO> pendingMeters = allMeterStatus.stream()
            .filter(m -> "CHUA_CAP_NHAT".equals(m.getStatus()))
            .collect(Collectors.toList());
            
        int pendingMeterRooms = pendingMeters.size();
        int updatedMeterRooms = totalRooms - pendingMeterRooms;
        int meterUpdateProgress = totalRooms == 0 ? 0 : (updatedMeterRooms * 100) / totalRooms;
        
        // Cắt lấy tối đa 5 phòng chưa cập nhật
        List<MeterStatusDTO> topPendingMeters = pendingMeters.stream()
            .limit(5)
            .collect(Collectors.toList());

        req.setAttribute("totalRooms", totalRooms);
        req.setAttribute("updatedMeterRooms", updatedMeterRooms);
        req.setAttribute("pendingMeterRooms", pendingMeterRooms);
        req.setAttribute("meterUpdateProgress", meterUpdateProgress);
        req.setAttribute("pendingMeterRoomList", topPendingMeters);



        // 2. Lấy dữ liệu Yêu cầu (Tickets)
        Map<String, Integer> ticketStats = dashboardDAO.getTicketStats(operatorId);
        int ticketCountNew = ticketStats.getOrDefault("PENDING", 0);
        int ticketCountInProgress = ticketStats.getOrDefault("IN_PROGRESS", 0);
        int ticketCountDone = ticketStats.getOrDefault("COMPLETED", 0);
        int totalPendingTickets = ticketCountNew + ticketCountInProgress;

        req.setAttribute("pendingTickets", totalPendingTickets);
        req.setAttribute("ticketCountNew", ticketCountNew);
        req.setAttribute("ticketCountInProgress", ticketCountInProgress);
        req.setAttribute("ticketCountDone", ticketCountDone);

        // 3. Lấy Lịch hẹn hôm nay
        List<com.quanlyphongtro.model.Request> todaysAppointments = dashboardDAO.getTodaysAppointments(operatorId);
        req.setAttribute("todaysAppointments", todaysAppointments);

        req.getRequestDispatcher("/WEB-INF/views/operator/dashboard.jsp").forward(req, resp);
    }
}
