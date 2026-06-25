package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.MeterReadingDAO;
import com.quanlyphongtro.dto.MeterStatusDTO;

import java.time.LocalDate;
import java.util.List;

public class MeterReadingService {
    private MeterReadingDAO meterReadingDAO;

    public MeterReadingService() {
        this.meterReadingDAO = new MeterReadingDAO();
    }

    public List<MeterStatusDTO> getMeterStatusForCurrentMonth(String facility, String roomCode, Integer operatorId) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        
        return meterReadingDAO.getMeterStatusList(currentMonth, currentYear, facility, roomCode, operatorId);
    }

    public List<MeterStatusDTO> getMeterStatusList(int month, int year, String facility, String roomCode, Integer operatorId) {
        return meterReadingDAO.getMeterStatusList(month, year, facility, roomCode, operatorId);
    }

    public boolean insertMeterReading(int roomId, int electric, int water, String electricImg, String waterImg, int createdBy) {
        return meterReadingDAO.insertMeterReading(roomId, electric, water, electricImg, waterImg, createdBy);
    }

    public Integer checkCurrentMonthReadingExists(int roomId, int month, int year) {
        return meterReadingDAO.checkCurrentMonthReadingExists(roomId, month, year);
    }

    public boolean updateMeterReading(int meterId, int electric, int water, String electricImg, String waterImg) {
        return meterReadingDAO.updateMeterReading(meterId, electric, water, electricImg, waterImg);
    }

    public MeterStatusDTO getPreviousReadingByRoomCode(String roomCode) {
        return meterReadingDAO.getPreviousReadingByRoomCode(roomCode);
    }

    public MeterStatusDTO getReadingBeforeCurrentMonth(String roomCode, int currentMonth, int currentYear) {
        return meterReadingDAO.getReadingBeforeCurrentMonth(roomCode, currentMonth, currentYear);
    }
}
