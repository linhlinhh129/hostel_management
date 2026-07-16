package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.ContractDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.ContractService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.util.EmailService;

public class ContractServiceImpl implements ContractService {

    private final ContractDAO contractDAO = new ContractDAO();
    private final RoomDAO roomDAO = new RoomDAO();

    @Override
    public List<Contract> getContractsByManager(int managerId, String searchName) {
        List<Contract> contracts = contractDAO.findAllByManagerId(managerId, searchName);
        // Map room details if necessary
        for (Contract c : contracts) {
            Optional<Room> r = roomDAO.findById(c.getRoomId());
            r.ifPresent(c::setRoom);
        }
        return contracts;
    }

    @Override
    public Contract getContractDetail(int contractId, int managerId) {
        Optional<Contract> opt = contractDAO.findByIdAndManagerId(contractId, managerId);
        if (opt.isPresent()) {
            Contract c = opt.get();
            Optional<Room> r = roomDAO.findById(c.getRoomId());
            r.ifPresent(c::setRoom);
            return c;
        }
        return null;
    }

    @Override
    public void createContract(Contract contract, int managerId) throws Exception {
        // Validate room
        Optional<Room> roomOpt = roomDAO.findById(contract.getRoomId());
        if (roomOpt.isEmpty()) {
            throw new Exception("Phòng không tồn tại.");
        }
        Room room = roomOpt.get();

        // Validate manager has access to this room
        Optional<Facility> facilityOpt = roomDAO.findFacilityByRoomId(room.getId() > 0 ? room.getId() : contract.getRoomId());
        // Since RoomDAO.findById might set roomId or id, just use contract.getRoomId() directly
        facilityOpt = roomDAO.findFacilityByRoomId(contract.getRoomId());
        if (facilityOpt.isEmpty() || facilityOpt.get().getManagerId() == null || facilityOpt.get().getManagerId() != managerId) {
            throw new Exception("Bạn không có quyền tạo hợp đồng cho phòng này.");
        }

        // Check active contract
        Optional<Contract> activeOpt = contractDAO.findActiveContractByRoomId(contract.getRoomId());
        if (activeOpt.isPresent()) {
            throw new Exception("Phòng này đang có hợp đồng ACTIVE.");
        }

        // Generate Code: HD-RoomCode-YearMonthDay-Sequence
        String code = "HD-" + room.getCode() + "-" + LocalDate.now().toString().replace("-", "") + "-" + System.currentTimeMillis() % 10000;
        contract.setCode(code);
        contract.setStatus("ACTIVE");
        contract.setCreatedBy(managerId);

        // Required fields check
        if (contract.getTenantFullName() == null || contract.getTenantFullName().trim().isEmpty()) {
            throw new Exception("Tên người thuê không được để trống.");
        }
        if (contract.getTenantIdentityNumber() == null || contract.getTenantIdentityNumber().trim().isEmpty()) {
            throw new Exception("CCCD không được để trống.");
        }
        if (contract.getSignedDate() == null || contract.getStartDate() == null || contract.getEndDate() == null) {
            throw new Exception("Ngày tháng ký/bắt đầu/kết thúc không được để trống.");
        }

        int id = contractDAO.create(contract);
        if (id <= 0) {
            throw new Exception("Lỗi hệ thống, không thể tạo hợp đồng trong cơ sở dữ liệu.");
        }
        contract.setContractId(id);

        // Cập nhật trạng thái phòng sang OCCUPIED
        room.setStatus("OCCUPIED");
        room.setContractStartDate(contract.getStartDate());
        room.setContractEndDate(contract.getEndDate());
        if (contract.getTenantId() != null) {
            room.setTenantId(contract.getTenantId());
        }
        roomDAO.update(room);
    }

    @Override
    public List<Contract> getContractsByTenant(int tenantId) {
        List<Contract> contracts = contractDAO.findAllByTenantId(tenantId);
        for (Contract c : contracts) {
            Optional<Room> r = roomDAO.findById(c.getRoomId());
            r.ifPresent(c::setRoom);
        }
        return contracts;
    }

    @Override
    public Contract getContractDetailForTenant(int contractId, int tenantId) {
        Optional<Contract> opt = contractDAO.findByIdAndTenantId(contractId, tenantId);
        if (opt.isPresent()) {
            Contract c = opt.get();
            Optional<Room> r = roomDAO.findById(c.getRoomId());
            r.ifPresent(c::setRoom);
            return c;
        }
        return null;
    }

    @Override
    public List<Room> getAvailableRooms(int managerId) {
        return contractDAO.getAvailableRooms(managerId);
    }

    @Override
    public Map<String, Object> getContractForAddTenant(int contractId, int managerId) throws Exception {
        Map<String, Object> contract = contractDAO.getContractForAddTenant(contractId, managerId);
        if (contract == null) {
            return null;
        }
        Integer tenantId = (Integer) contract.get("tenantId");
        if (tenantId != null && tenantId > 0) {
            throw new IllegalStateException("Hợp đồng này đã có tài khoản người thuê liên kết.");
        }
        return contract;
    }

    @Override
    public Map<String, Object> addTenantFromContract(int contractId, int roomId, String fullName, String phone, String email, String identityNumber, String permanentAddress, String gender, String dobStr, String contractStartDateStr, boolean confirmReactivate, int managerId, String loginLink) throws Exception {
        Map<String, Object> result = new HashMap<>();
        // Validate inputs
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Họ tên không được để trống.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống.");
        }
        if (!email.trim().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (!com.quanlyphongtro.util.ValidationUtil.isValidVnPhone(phone.trim())) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
        }
        if (identityNumber == null || identityNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số CMND/CCCD không được để trống.");
        }
        if (!com.quanlyphongtro.util.ValidationUtil.isValidVnIdentity(identityNumber.trim())) {
            throw new IllegalArgumentException("Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).");
        }

        String username = email.trim();
        PersonnelDAO personnelDAO = new PersonnelDAO();
        Integer existingUserId = contractDAO.getUserIdByUsername(username);

        if (personnelDAO.existsByPhone(phone.trim(), existingUserId)) {
            throw new IllegalArgumentException("Số điện thoại '" + phone.trim() + "' đã được sử dụng bởi tài khoản khác.");
        }
        if (personnelDAO.existsByIdentityNumber(identityNumber.trim(), existingUserId)) {
            throw new IllegalArgumentException("Số CMND/CCCD '" + identityNumber.trim() + "' đã được sử dụng bởi tài khoản khác.");
        }

        String plainPassword = PasswordUtil.generateTempPassword();
        String passwordHash = PasswordUtil.hash(plainPassword);

        LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr) : null;
        LocalDate startDate = (contractStartDateStr != null && !contractStartDateStr.isEmpty())
                ? LocalDate.parse(contractStartDateStr)
                : LocalDate.now();

        Map<String, Object> existingUser = contractDAO.getUserRoleAndIdentityByUsername(username);
        boolean userExists = false;
        int userId = 0;

        if (existingUser != null) {
            userId = (Integer) existingUser.get("id");
            String existingRole = (String) existingUser.get("role");
            if (!"TENANT".equals(existingRole)) {
                throw new IllegalArgumentException("Email/Tên đăng nhập đã tồn tại trong hệ thống với vai trò khác.");
            }

            // Check active checks
            int activeCount = contractDAO.countActiveChecksForUser(userId);
            if (activeCount > 0) {
                throw new IllegalArgumentException("Email/Tên đăng nhập đã tồn tại trong hệ thống và đang hoạt động ở phòng/cơ sở khác.");
            }

            if (!confirmReactivate) {
                result.put("status", "REACTIVATE_CONFIRM");
                result.put("fullName", existingUser.get("fullName"));
                result.put("identityNumber", existingUser.get("identityNumber"));
                return result;
            }
            userExists = true;
        }

        boolean success = contractDAO.addTenantTransaction(userExists, userId, passwordHash, fullName, phone, identityNumber, dob, gender, permanentAddress, email, roomId, startDate, contractId);
        if (!success) {
            throw new Exception("Lỗi cập nhật cơ sở dữ liệu khi tạo tài khoản người thuê.");
        }

        // Send email asynchronously
        EmailService.sendTempPassword(email.trim(), fullName.trim(), username, plainPassword, loginLink);

        result.put("status", "SUCCESS");
        result.put("userExists", userExists);
        Integer finalId = contractDAO.getUserIdByUsername(username);
        result.put("userId", finalId != null ? finalId : 0);
        return result;
    }

    @Override
    public Map<String, String> verifyContractForDelete(int contractId, int managerId) {
        return contractDAO.verifyContractForDelete(contractId, managerId);
    }

    @Override
    public boolean softDeleteContract(int contractId) {
        return contractDAO.softDeleteContract(contractId);
    }
}
