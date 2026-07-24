package com.quanlyphongtro.controller.operator;
import jakarta.servlet.http.HttpSession;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.model.Facility;
import java.util.ArrayList;

import com.quanlyphongtro.dto.MeterStatusDTO;
import com.quanlyphongtro.service.MeterReadingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/operator/meter-readings/history")
public class MeterReadingHistoryServlet extends HttpServlet {
    private MeterReadingService meterReadingService;

    @Override
    public void init() throws ServletException {
        this.meterReadingService = new MeterReadingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");

        String facility = request.getParameter("facility");
        String roomCode = request.getParameter("roomCode");

        FacilityDAO facilityDAO = new FacilityDAO();
        List<Facility> allFacilities = facilityDAO.findActiveList();
        List<Facility> myFacilities = new ArrayList<>();
        for (Facility f : allFacilities) {
            if (f.getOperatorId() != null && f.getOperatorId().equals(currentUser.getId())) {
                myFacilities.add(f);
            }
        }
        request.setAttribute("facilities", myFacilities);

        LocalDate now = LocalDate.now();
        int filterMonth = now.getMonthValue();
        int filterYear = now.getYear();
        
        String monthParam = request.getParameter("month");
        String yearParam = request.getParameter("year");
        
        if (monthParam != null && !monthParam.trim().isEmpty()) {
            try {
                filterMonth = Integer.parseInt(monthParam);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
        
        if (yearParam != null && !yearParam.trim().isEmpty()) {
            try {
                filterYear = Integer.parseInt(yearParam);
            } catch (NumberFormatException e) {
                // Ignore
            }
        }

        List<MeterStatusDTO> meterList = meterReadingService.getMeterStatusList(filterMonth, filterYear, facility, roomCode, currentUser.getId());
        
        request.setAttribute("currentMonth", now.getMonthValue());
        request.setAttribute("currentYear", now.getYear());
        request.setAttribute("selectedMonth", filterMonth);
        request.setAttribute("selectedYear", filterYear);
        request.setAttribute("meterList", meterList);
        request.setAttribute("activeMenu", "meter-readings-history");
        
        request.setAttribute("selectedFacility", facility);
        request.setAttribute("searchRoomCode", roomCode);
        
        request.getRequestDispatcher("/WEB-INF/views/operator/meter_readings/history.jsp").forward(request, response);
    }
}
