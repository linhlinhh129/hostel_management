package com.quanlyphongtro.controller.operator;

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

@WebServlet("/operator/meter-readings")
public class ListElectricServlet extends HttpServlet {
    private MeterReadingService meterReadingService;

    @Override
    public void init() throws ServletException {
        this.meterReadingService = new MeterReadingService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String facility = request.getParameter("facility");
        String roomCode = request.getParameter("roomCode");

        List<MeterStatusDTO> meterList = meterReadingService.getMeterStatusForCurrentMonth(facility, roomCode);
        
        LocalDate now = LocalDate.now();
        request.setAttribute("currentMonth", now.getMonthValue());
        request.setAttribute("currentYear", now.getYear());
        request.setAttribute("meterList", meterList);
        request.setAttribute("activeMenu", "meter-readings");
        
        request.setAttribute("selectedFacility", facility);
        request.setAttribute("searchRoomCode", roomCode);
        
        request.getRequestDispatcher("/WEB-INF/views/operator/meter_readings/list.jsp").forward(request, response);
    }
}
