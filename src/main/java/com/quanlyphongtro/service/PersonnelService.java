package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.User;

import java.util.List;

public interface PersonnelService {

    // ── List / read ───────────────────────────────────────────────────────

    PageDTO<User> list(String keyword, String role, String status, int page, int pageSize);

    User getById(int id) throws NotFoundException;

    Integer findFacilityIdForUser(int userId);

    List<Facility> findFacilitiesForManager(Integer excludeUserId);

    List<Facility> findFacilitiesForOperator(Integer excludeUserId);

    // ── Create / update ───────────────────────────────────────────────────

    /**
     * Tạo nhân sự mới (MANAGER hoặc OPERATOR).
     * Sinh mật khẩu tạm, gửi email async, gán facility nếu được cung cấp.
     */
    void create(String fullName, String email, String phone, String role,
                String identityNumber, String dobStr, String gender,
                String permanentAddress, String facilityIdStr,
                int createdByUserId, String loginLink)
            throws ValidationException;

    /**
     * Cập nhật thông tin nhân sự.
     */
    void update(int id, String fullName, String email, String phone, String role,
                String identityNumber, String dobStr, String gender,
                String permanentAddress, String facilityIdStr)
            throws NotFoundException, ValidationException;

    // ── Status / delete ───────────────────────────────────────────────────

    /**
     * Toggle ACTIVE ↔ INACTIVE.
     * Không cho phép tự thay đổi status của chính mình.
     */
    void toggleStatus(int id, int currentUserId) throws NotFoundException, ValidationException;

    /**
     * Xóa mềm. Chỉ cho phép khi status = INACTIVE và không tự xóa chính mình.
     */
    void softDelete(int id, int currentUserId) throws NotFoundException, ValidationException;
}
