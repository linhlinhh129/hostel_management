package com.quanlyphongtro.service.impl;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dao.DependentDAO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.util.ValidationUtil;

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

    /**
     * Đếm tổng số lượng người thuê thuộc quyền quản lý (phục vụ tính số trang)
     */
    @Override
    public int countTenants(int managerId, String keyword, String status) {
        return userDAO.countTenants(managerId, keyword, status);
    }

    /**
     * Lấy danh sách người thuê có phân trang
     */
    @Override
    public List<Map<String, Object>> getTenants(int managerId, String keyword, String status, int page, int pageSize) {
        // Tính toán offset số bản ghi cần bỏ qua
        int offset = (page - 1) * pageSize;
        return userDAO.getTenants(managerId, keyword, status, offset, pageSize);
    }

    /**
     * Xem thông tin chi tiết của 1 người thuê + Kiểm tra quyền của Manager
     */
    @Override
    public Map<String, Object> getTenantDetail(int tenantId, int managerId) throws Exception {
        // 1. Lấy thông tin người thuê từ DAO
        Map<String, Object> tenant = userDAO.getTenantDetail(tenantId);
        if (tenant == null) {
            return null;
        }

        // 2. Xác minh Manager có quyền quản lý người thuê này hay không
        boolean hasPermission = userDAO.verifyTenantEditPermission(tenantId, managerId);
        if (!hasPermission) {
            throw new AccessDeniedException("Bạn không có quyền xem thông tin người thuê này.");
        }
        return tenant;
    }

    /**
     * Lấy danh sách người phụ thuộc đi kèm với người thuê
     */
    @Override
    public List<Map<String, Object>> getTenantDependents(int tenantId) {
        return dependentDAO.getTenantDependents(tenantId);
    }

    /**
     * Chỉnh sửa thông tin người thuê + Kiểm tra các quy tắc nghiệp vụ
     */
    @Override
    public boolean editTenant(int tenantId, int managerId, String fullName, String phone, String email, String identityNumber, String permanentAddress, String gender, LocalDate dob) throws Exception {
        // 1. Kiểm tra quyền của Manager
        boolean hasPermission = userDAO.verifyTenantEditPermission(tenantId, managerId);
        if (!hasPermission) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa thông tin người thuê này.");
        }

        // 2. Kiểm tra quy tắc độ tuổi: Người thuê phải đủ 18 tuổi trở lên
        if (dob != null && !ValidationUtil.isAtLeast18YearsOld(dob)) {
            throw new IllegalArgumentException("Người thuê phải từ 18 tuổi trở lên.");
        }

        // 3. Kiểm tra trùng lặp Email/Tên đăng nhập
        String username = email.trim();
        boolean duplicate = userDAO.isDuplicateEmail(username, tenantId);
        if (duplicate) {
            throw new IllegalArgumentException("Email/Tên đăng nhập đã tồn tại trong hệ thống.");
        }

        // 4. Chuẩn hóa dữ liệu (.trim()) và gọi DAO cập nhật vào CSDL
        return userDAO.updateTenantInfo(tenantId, username, email.trim(), fullName.trim(), phone.trim(), identityNumber.trim(), dob, gender, permanentAddress);
    }

    /**
     * Xóa mềm tài khoản người thuê
     */
    @Override
    public boolean softDeleteTenant(int tenantId) {
        return userDAO.softDeleteTenant(tenantId);
    }

    /**
     * Khóa tài khoản người thuê (status = LOCKED)
     */
    @Override
    public boolean lockTenantAccount(int tenantId) {
        userDAO.updateStatus(tenantId, "LOCKED");
        return true;
    }

    /**
     * Mở khóa tài khoản người thuê (status = ACTIVE) + Reset số lần đập mật khẩu sai
     */
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

    /**
     * Kết thúc hợp đồng thuê (Chạy Transaction 3 bước giải phóng phòng + chốt hợp đồng)
     */
    @Override
    public boolean endRental(int tenantId) {
        return userDAO.endRentalTransaction(tenantId);
    }

    /**
     * Lấy thông tin chi tiết người phụ thuộc
     */
    @Override
    public Map<String, Object> getDependentDetail(int dependentId, int managerId) throws Exception {
        Map<String, Object> dependent = dependentDAO.getDependentDetail(dependentId, managerId);
        if (dependent == null) {
            return null;
        }
        return dependent;
    }

    /**
     * Xóa người phụ thuộc + Kiểm tra hợp đồng thuê phải đang ACTIVE
     */
    @Override
    public boolean removeDependent(int dependentId, int managerId) throws Exception {
        // 1. Xác minh quyền và lấy trạng thái hợp đồng người thuê chính
        Map<String, String> statusOut = new HashMap<>();
        int tenantId = dependentDAO.verifyDependentAndGetTenantId(dependentId, managerId, statusOut);
        if (tenantId == 0) {
            throw new AccessDeniedException("Bạn không có quyền xóa người phụ thuộc này.");
        }

        // 2. Không cho phép xóa người phụ thuộc khi hợp đồng đã kết thúc
        String tenantStatus = statusOut.get("status");
        if (!"ACTIVE".equals(tenantStatus)) {
            throw new IllegalStateException("Hợp đồng thuê đã kết thúc. Không thể xóa người phụ thuộc.");
        }

        // 3. Thực hiện xóa mềm
        return dependentDAO.deleteDependent(dependentId);
    }

    /**
     * Cập nhật thông tin người phụ thuộc + Kiểm tra trạng thái hợp đồng ACTIVE
     */
    @Override
    public boolean editDependent(int dependentId, int managerId, String fullName, String relationship, String phone, String gender, LocalDate dob, String identityNumber) throws Exception {
        Map<String, String> statusOut = new HashMap<>();
        int tenantId = dependentDAO.verifyDependentAndGetTenantId(dependentId, managerId, statusOut);
        if (tenantId == 0) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa người phụ thuộc này.");
        }

        String tenantStatus = statusOut.get("status");
        if (!"ACTIVE".equals(tenantStatus)) {
            throw new IllegalStateException("Hợp đồng thuê đã kết thúc. Không thể chỉnh sửa thông tin người phụ thuộc.");
        }

        return dependentDAO.updateDependent(dependentId, fullName.trim(), relationship.trim(), phone, gender, dob, identityNumber);
    }

    /**
     * Thêm mới người phụ thuộc cho người thuê chính
     */
    @Override
    public boolean addDependent(int tenantId, int managerId, String fullName, String relationship, String phone, String gender, LocalDate dob, String identityNumber) throws Exception {
        // 1. Xác minh Manager quản lý người thuê này
        boolean hasPermission = userDAO.verifyTenantEditPermission(tenantId, managerId);
        if (!hasPermission) {
            throw new AccessDeniedException("Bạn không có quyền thêm người phụ thuộc cho người thuê này.");
        }

        // 2. Chuẩn hóa và lưu vào CSDL
        return dependentDAO.addDependent(tenantId, fullName.trim(), relationship.trim(), phone, gender, dob, identityNumber);
    }
}
