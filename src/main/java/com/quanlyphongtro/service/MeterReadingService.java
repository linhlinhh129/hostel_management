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

    public boolean insertMeterReading(MeterStatusDTO dto, int createdBy) {
        calculateUsage(dto);
        return meterReadingDAO.insertMeterReading(dto, createdBy);
    }

    public Integer checkCurrentMonthReadingExists(int roomId, int month, int year) {
        return meterReadingDAO.checkCurrentMonthReadingExists(roomId, month, year);
    }

    public boolean updateMeterReading(MeterStatusDTO dto) {
        calculateUsage(dto);
        return meterReadingDAO.updateMeterReading(dto);
    }

    private void calculateUsage(MeterStatusDTO dto) {
        int prevElec = dto.getPreviousElectricReading() != null ? dto.getPreviousElectricReading() : 0;
        int currElec = dto.getCurrentElectricReading() != null ? dto.getCurrentElectricReading() : 0;
        String eStatus = dto.getElectricStatus();
        if ("ROLLOVER".equals(eStatus)) {
            int maxLimit = 100000;
            dto.setElectricMaxLimit(maxLimit);
            dto.setElectricUsage((maxLimit - prevElec) + currElec);
        } else {
            dto.setElectricUsage(currElec - prevElec);
        }

        int prevWater = dto.getPreviousWaterReading() != null ? dto.getPreviousWaterReading() : 0;
        int currWater = dto.getCurrentWaterReading() != null ? dto.getCurrentWaterReading() : 0;
        String wStatus = dto.getWaterStatus();
        if ("ROLLOVER".equals(wStatus)) {
            int maxLimit = 100000;
            dto.setWaterMaxLimit(maxLimit);
            dto.setWaterUsage((maxLimit - prevWater) + currWater);
        } else {
            dto.setWaterUsage(currWater - prevWater);
        }
    }

    public MeterStatusDTO getPreviousReadingByRoomCode(String roomCode) {
        return meterReadingDAO.getPreviousReadingByRoomCode(roomCode);
    }

    public MeterStatusDTO getReadingForEdit(int meterId) {
        return meterReadingDAO.getReadingForEdit(meterId);
    }

    public MeterStatusDTO getReadingBeforeCurrentMonth(String roomCode, int currentMonth, int currentYear) {
        return meterReadingDAO.getReadingBeforeCurrentMonth(roomCode, currentMonth, currentYear);
    }

    /**
     * Kiểm tra hóa đơn của phòng trong tháng/năm đã được thanh toán chưa.
     * Nếu true → không cho phép cập nhật lại chỉ số điện nước.
     */
    public boolean isInvoicePaidForMonth(int roomId, int month, int year) {
        return meterReadingDAO.isInvoicePaidForMonth(roomId, month, year);
    }
}
