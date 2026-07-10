package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.dto.RoomDetailDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;

import java.nio.file.AccessDeniedException;
import java.util.List;
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
     * @throws NotFoundException   nếu roomId không tồn tại
     * @throws ValidationException nếu giá trị không hợp lệ hoặc cơ sở INACTIVE
     */
    void updateAreaAndFee(int roomId, String areaStr, String feeStr)
            throws NotFoundException, ValidationException;

    /**
     * Trả về danh sách cơ sở mà manager phụ trách.
     */
    List<Map<String, Object>> getFacilitiesByManager(Integer managerId);

    /**
     * Trả về facility_id đầu tiên (theo code ASC) mà manager phụ trách.
     * Trả về null nếu manager chưa được gán cơ sở nào.
     */
    Integer getDefaultFacilityId(Integer managerId);

    /**
     * Kiểm tra manager có quyền quản lý facilityId đó không.
     * Trả về thông tin cơ sở nếu hợp lệ, null nếu không có quyền.
     */
    Map<String, Object> verifyFacilityManager(int facilityId, Integer managerId);

    /**
     * Trả về danh sách phòng có phân trang theo facilityId.
     */
    PageResult<RoomDTO> getFacilityRoomsPage(int facilityId, String status, int page, int pageSize);

    /**
     * Lấy chi tiết phòng cho Manager.
     * Kiểm tra quyền: manager phải phụ trách cơ sở chứa phòng đó.
     *
     * @throws AccessDeniedException nếu manager không có quyền xem phòng này
     */
    RoomDetailDTO getRoomDetail(int roomId, Integer managerId) throws AccessDeniedException;
}
