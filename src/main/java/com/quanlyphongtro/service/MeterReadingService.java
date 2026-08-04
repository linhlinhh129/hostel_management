package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.MeterReadingDAO;
import com.quanlyphongtro.dto.MeterStatusDTO;

import java.time.LocalDate;
import java.util.List;

public class MeterReadingService {
    private MeterReadingDAO meterReadingDAO;

    // Khởi tạo DAO truy xuất chỉ số điện nước
    public MeterReadingService() {
        this.meterReadingDAO = new MeterReadingDAO();
    }

    // Lấy danh sách trạng thái chốt số điện nước tháng hiện tại
    public List<MeterStatusDTO> getMeterStatusForCurrentMonth(String facility, String roomCode, Integer operatorId) {
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();
        int currentYear = currentDate.getYear();
        
        return meterReadingDAO.getMeterStatusList(currentMonth, currentYear, facility, roomCode, operatorId);
    }

    // Lấy danh sách chỉ số điện nước của tháng/năm chỉ định
    public List<MeterStatusDTO> getMeterStatusList(int month, int year, String facility, String roomCode, Integer operatorId) {
        return meterReadingDAO.getMeterStatusList(month, year, facility, roomCode, operatorId);
    }

    // Thêm mới bản ghi chỉ số điện nước và tự động tính lượng tiêu thụ
    public boolean insertMeterReading(MeterStatusDTO dto, int createdBy) {
        calculateUsage(dto);
        return meterReadingDAO.insertMeterReading(dto, createdBy);
    }

    // Kiểm tra bản ghi chỉ số tháng hiện tại đã tồn tại chưa để quyết định insert hay update
    public Integer checkCurrentMonthReadingExists(int roomId, int month, int year) {
        return meterReadingDAO.checkCurrentMonthReadingExists(roomId, month, year);
    }

    // Cập nhật chỉ số điện nước và tính lại lượng tiêu thụ
    public boolean updateMeterReading(MeterStatusDTO dto) {
        calculateUsage(dto);
        return meterReadingDAO.updateMeterReading(dto);
    }

    // Tính toán lượng điện nước tiêu thụ (xử lý nghiệp vụ chạm đỉnh quay vòng rollover)
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

    // Lấy chỉ số kỳ trước gần nhất dựa trên mã phòng
    public MeterStatusDTO getPreviousReadingByRoomCode(String roomCode) {
        return meterReadingDAO.getPreviousReadingByRoomCode(roomCode);
    }

    // Lấy thông tin chỉ số phục vụ việc chỉnh sửa
    public MeterStatusDTO getReadingForEdit(int meterId) {
        return meterReadingDAO.getReadingForEdit(meterId);
    }

    // Lấy chỉ số phòng của tháng liền kề trước tháng chỉ định
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
