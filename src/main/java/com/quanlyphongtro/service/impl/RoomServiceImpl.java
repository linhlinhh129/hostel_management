package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.RoomService;

import java.math.BigDecimal;
import java.util.Map;

public class RoomServiceImpl implements RoomService {

    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    public Map<String, Object> getDetailForAdmin(int roomId) throws NotFoundException {
        return roomDAO.findDetailForAdmin(roomId).orElseThrow(NotFoundException::new);
    }

    @Override
    public void updateAreaAndFee(int roomId, String areaStr, String feeStr)
            throws NotFoundException, ValidationException {

        // Kiểm tra phòng tồn tại và lấy facilityStatus
        Map<String, Object> room = roomDAO.findDetailForAdmin(roomId)
                .orElseThrow(NotFoundException::new);

        if ("INACTIVE".equals(room.get("facilityStatus")))
            throw new ValidationException("Cơ sở đã bị vô hiệu hóa. Không thể chỉnh sửa thông tin phòng.");

        BigDecimal area = parsePositiveDecimal(areaStr, "Diện tích");
        BigDecimal fee  = parsePositiveDecimal(feeStr,  "Giá phòng");

        boolean updated = roomDAO.updateAreaAndFee(roomId, area, fee);
        if (!updated) throw new NotFoundException();
    }

    // ── Private helpers ───────────────────────────────────────────────────

    /**
     * Trả về null nếu chuỗi trống (xóa giá trị).
     * Ném ValidationException nếu không phải số hoặc âm.
     */
    private BigDecimal parsePositiveDecimal(String raw, String fieldLabel) throws ValidationException {
        if (raw == null || raw.isBlank()) return null;
        try {
            BigDecimal val = new BigDecimal(raw.replace(",", ".").trim());
            if (val.compareTo(BigDecimal.ZERO) < 0)
                throw new ValidationException(fieldLabel + " không được âm.");
            return val;
        } catch (NumberFormatException e) {
            throw new ValidationException(fieldLabel + " không hợp lệ.");
        }
    }
}
