package com.quanlyphongtro.service;

import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;

import java.util.Map;

public interface RoomService {

    /**
     * Lấy chi tiết phòng kèm thông tin cơ sở và người thuê cho Admin.
     *
     * @throws NotFoundException nếu roomId không tồn tại
     */
    Map<String, Object> getDetailForAdmin(int roomId) throws NotFoundException;

    /**
     * Cập nhật diện tích và giá phòng.
     * Không cho phép sửa khi cơ sở đang INACTIVE.
     *
     * @throws NotFoundException  nếu roomId không tồn tại
     * @throws ValidationException nếu giá trị không hợp lệ hoặc cơ sở INACTIVE
     */
    void updateAreaAndFee(int roomId, String areaStr, String feeStr)
            throws NotFoundException, ValidationException;
}
