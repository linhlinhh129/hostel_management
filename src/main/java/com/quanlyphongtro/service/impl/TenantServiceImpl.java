package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dao.DependentDAO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.TenantService;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TenantServiceImpl implements TenantService {

    private final RoomDAO roomDAO = new RoomDAO();
    private final UserDAO userDAO = new UserDAO();
    private final DependentDAO dependentDAO = new DependentDAO();

    @Override
    public Optional<Room> getTenantRoom(int tenantId) {
        return roomDAO.findByTenantId(tenantId);
    }

    @Override
    public Optional<Facility> getFacilityByRoomId(int roomId) {
        return roomDAO.findFacilityByRoomId(roomId);
    }

    @Override
    public Optional<User> getTenantProfile(int tenantId) {
        return userDAO.findById(tenantId);
    }

    @Override
    public int countTenants(int managerId, String keyword, String status) {
        return userDAO.countTenants(managerId, keyword, status);
    }

    @Override
    public List<Map<String, Object>> getTenants(int managerId, String keyword, String status, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return userDAO.getTenants(managerId, keyword, status, offset, pageSize);
    }

    @Override
    public Map<String, Object> getTenantDetail(int tenantId, int managerId) throws Exception {
        Map<String, Object> tenant = userDAO.getTenantDetail(tenantId);
        if (tenant == null) {
            return null;
        }

        // Verify manager has permission for this tenant
        boolean hasPermission = userDAO.verifyTenantEditPermission(tenantId, managerId);
        if (!hasPermission) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền xem thông tin người thuê này.");
        }
        return tenant;
    }

    @Override
    public List<Map<String, Object>> getTenantDependents(int tenantId) {
        return dependentDAO.getTenantDependents(tenantId);
    }

    @Override
    public boolean editTenant(int tenantId, int managerId, String fullName, String phone, String email, String identityNumber, String permanentAddress, String gender, java.time.LocalDate dob) throws Exception {
        // 1. Verify manager permissions for this tenant
        boolean hasPermission = userDAO.verifyTenantEditPermission(tenantId, managerId);
        if (!hasPermission) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền chỉnh sửa thông tin người thuê này.");
        }

        // 2. Check for duplicate username/email
        String username = email.trim();
        boolean duplicate = userDAO.isDuplicateEmail(username, tenantId);
        if (duplicate) {
            throw new IllegalArgumentException("Email/Tên đăng nhập đã tồn tại trong hệ thống.");
        }

        // 3. Update info
        return userDAO.updateTenantInfo(tenantId, username, email.trim(), fullName.trim(), phone.trim(), identityNumber.trim(), dob, gender, permanentAddress);
    }

    @Override
    public boolean softDeleteTenant(int tenantId) {
        return userDAO.softDeleteTenant(tenantId);
    }

    @Override
    public boolean lockTenantAccount(int tenantId) {
        userDAO.updateStatus(tenantId, "LOCKED");
        return true;
    }

    @Override
    public boolean unlockTenantAccount(int tenantId, MapStringConsumer usernameResetOut) {
        Optional<User> uOpt = userDAO.findById(tenantId);
        if (uOpt.isPresent()) {
            userDAO.updateStatus(tenantId, "ACTIVE");
            usernameResetOut.accept(uOpt.get().getUsername());
            return true;
        }
        return false;
    }

    @Override
    public boolean endRental(int tenantId) {
        return userDAO.endRentalTransaction(tenantId);
    }

    @Override
    public Map<String, Object> getDependentDetail(int dependentId, int managerId) throws Exception {
        Map<String, Object> dependent = dependentDAO.getDependentDetail(dependentId, managerId);
        if (dependent == null) {
            return null;
        }
        return dependent;
    }

    @Override
    public boolean removeDependent(int dependentId, int managerId) throws Exception {
        Map<String, String> statusOut = new HashMap<>();
        int tenantId = dependentDAO.verifyDependentAndGetTenantId(dependentId, managerId, statusOut);
        if (tenantId == 0) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền xóa người phụ thuộc này.");
        }

        String tenantStatus = statusOut.get("status");
        if (!"ACTIVE".equals(tenantStatus)) {
            throw new IllegalStateException("Hợp đồng thuê đã kết thúc. Không thể xóa người phụ thuộc.");
        }

        return dependentDAO.deleteDependent(dependentId);
    }

    @Override
    public boolean editDependent(int dependentId, int managerId, String fullName, String relationship, String phone, String gender, java.time.LocalDate dob, String identityNumber) throws Exception {
        Map<String, String> statusOut = new HashMap<>();
        int tenantId = dependentDAO.verifyDependentAndGetTenantId(dependentId, managerId, statusOut);
        if (tenantId == 0) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền chỉnh sửa người phụ thuộc này.");
        }

        String tenantStatus = statusOut.get("status");
        if (!"ACTIVE".equals(tenantStatus)) {
            throw new IllegalStateException("Hợp đồng thuê đã kết thúc. Không thể chỉnh sửa thông tin người phụ thuộc.");
        }

        return dependentDAO.updateDependent(dependentId, fullName.trim(), relationship.trim(), phone, gender, dob, identityNumber);
    }

    @Override
    public boolean addDependent(int tenantId, int managerId, String fullName, String relationship, String phone, String gender, java.time.LocalDate dob, String identityNumber) throws Exception {
        // Verify manager controls the tenant
        boolean hasPermission = userDAO.verifyTenantEditPermission(tenantId, managerId);
        if (!hasPermission) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền thêm người phụ thuộc cho người thuê này.");
        }

        return dependentDAO.addDependent(tenantId, fullName.trim(), relationship.trim(), phone, gender, dob, identityNumber);
    }
}
