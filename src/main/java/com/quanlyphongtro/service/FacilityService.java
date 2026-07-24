package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;

import java.util.List;

public interface FacilityService {

    // ── List / read ───────────────────────────────────────────────────────

    PageDTO<Facility> list(String keyword, String status, int page, int pageSize);

    Facility getById(int id) throws NotFoundException;

    /** Chỉ dùng khi cơ sở đã ACTIVE hoặc INACTIVE (đã sinh phòng). */
    List<Room> getRooms(int facilityId);

    // ── Create / update ───────────────────────────────────────────────────

    /**
     * Tạo cơ sở mới ở trạng thái DRAFT.
     *
     * @param electricityPrice có thể null/blank → 0
     * @param waterPrice       có thể null/blank → 0
     * @param internetFee      có thể null/blank → 0
     * @param serviceFee       có thể null/blank → 0
     */
    void create(String code, String name, String address,
                String floorCountStr, String roomsPerFloorStr,
                String electricityPrice, String waterPrice,
                String internetFee, String serviceFee)
            throws ValidationException;

    /**
     * Cập nhật cơ sở.
     * Khi ACTIVE: chỉ cho sửa name, address, và service prices.
     * Khi DRAFT: cho sửa tất cả bao gồm code, floorCount, roomsPerFloor.
     */
    void update(int id,
                String code, String name, String address,
                String floorCountStr, String roomsPerFloorStr,
                String electricityPrice, String waterPrice,
                String internetFee, String serviceFee)
            throws NotFoundException, ValidationException;

    // ── Status transitions ────────────────────────────────────────────────

    /** Kích hoạt DRAFT → ACTIVE, sinh phòng tự động trong 1 transaction. */
    void activate(int id) throws NotFoundException, ValidationException;

    /**
     * Vô hiệu hóa ACTIVE → INACTIVE.
     * Chặn nếu còn phòng đang thuê.
     */
    void deactivate(int id) throws NotFoundException, ValidationException;

    // ── Room inline update ────────────────────────────────────────────────

    /** Cập nhật diện tích phòng từ trang detail cơ sở. */
    void updateRoomArea(int facilityId, int roomId, String areaStr)
            throws NotFoundException, ValidationException;

    // ── Lookup helpers (dùng cho RoleFilter, PersonnelService) ───────────

    /** Trả về Facility mà manager đang phụ trách, hoặc null. */
    Facility findByManagerId(int managerId);

    /** Trả về Facility mà operator đang phụ trách, hoặc null. */
    Facility findByOperatorId(int operatorId);
}
