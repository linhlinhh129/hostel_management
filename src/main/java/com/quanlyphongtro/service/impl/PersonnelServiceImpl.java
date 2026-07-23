package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.PersonnelService;
import com.quanlyphongtro.util.EmailService;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class PersonnelServiceImpl implements PersonnelService {

    private static final Logger logger = LoggerFactory.getLogger(PersonnelServiceImpl.class);

    private final PersonnelDAO personnelDAO = new PersonnelDAO();
    private final FacilityDAO  facilityDAO  = new FacilityDAO();

    // ── List / read ───────────────────────────────────────────────────────

    @Override
    public PageDTO<User> list(String keyword, String role, String status, int page, int pageSize) {
        int total = personnelDAO.count(keyword, role, status);
        List<User> items = personnelDAO.findAll(keyword, role, status, page, pageSize);
        for (User u : items) {
            u.setFacilityNames(personnelDAO.findFacilityNamesForUser(u.getId()));
        }
        return new PageDTO<>(items, page, pageSize, total);
    }

    @Override
    public User getById(int id) throws NotFoundException {
        User user = personnelDAO.findById(id).orElseThrow(NotFoundException::new);
        user.setFacilityNames(personnelDAO.findFacilityNamesForUser(id));
        return user;
    }

    @Override
    public Integer findFacilityIdForUser(int userId) {
        return personnelDAO.findFacilityIdForUser(userId);
    }

    @Override
    public List<Facility> findFacilitiesForManager(Integer excludeUserId) {
        return personnelDAO.findFacilitiesForManager(excludeUserId);
    }

    @Override
    public List<Facility> findFacilitiesForOperator(Integer excludeUserId) {
        return personnelDAO.findFacilitiesForOperator(excludeUserId);
    }

    // ── Create ────────────────────────────────────────────────────────────

    @Override
    public void create(String fullName, String email, String phone, String role,
                       String identityNumber, String dobStr, String gender,
                       String permanentAddress, String facilityIdStr,
                       int createdByUserId, String loginLink) throws ValidationException {

        fullName       = trim(fullName);
        email          = trim(email);
        phone          = trim(phone);
        role           = trim(role);
        identityNumber = trim(identityNumber);
        gender         = trim(gender);
        permanentAddress = trim(permanentAddress);

        // ── Basic validation ──────────────────────────────────────────────
        if (fullName.isEmpty())  throw new ValidationException("Họ tên không được để trống.");
        if (email.isEmpty())     throw new ValidationException("Email không được để trống.");
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$"))
            throw new ValidationException("Email không đúng định dạng.");
        if (phone.isEmpty())     throw new ValidationException("Số điện thoại không được để trống.");
        if (!ValidationUtil.isValidVnPhone(phone))
            throw new ValidationException("Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
        if (role.isEmpty())      throw new ValidationException("Vai trò không được để trống.");
        if ("ADMIN".equals(role))
            throw new ValidationException("Không thể tạo nhân sự với vai trò ADMIN.");
        if (!role.equals("MANAGER") && !role.equals("OPERATOR"))
            throw new ValidationException("Vai trò không hợp lệ.");
        if (identityNumber.isEmpty())
            throw new ValidationException("Số CMND/CCCD không được để trống.");
        if (!ValidationUtil.isValidVnIdentity(identityNumber))
            throw new ValidationException("Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).");

        // ── Uniqueness ────────────────────────────────────────────────────
        if (personnelDAO.existsByEmail(email, null))
            throw new ValidationException("Email '" + email + "' đã được sử dụng.");
        if (personnelDAO.existsByPhone(phone, null))
            throw new ValidationException("Số điện thoại '" + phone + "' đã được sử dụng.");
        if (personnelDAO.existsByIdentityNumber(identityNumber, null))
            throw new ValidationException("Số CMND/CCCD '" + identityNumber + "' đã được sử dụng.");

        // ── Facility ──────────────────────────────────────────────────────
        Integer facilityId = parseFacilityId(facilityIdStr);
        if (("MANAGER".equals(role) || "OPERATOR".equals(role)) && facilityId == null) {
            throw new ValidationException("Nhân sự phải được gán một cơ sở quản lý.");
        }
        if (facilityId != null) {
            validateFacilityAssignment(facilityId, role, null);
        }

        // ── Build and persist user ────────────────────────────────────────
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setIdentityNumber(identityNumber);
        user.setGender(gender.isEmpty() ? null : gender);
        user.setPermanentAddress(permanentAddress.isEmpty() ? null : permanentAddress);
        user.setDob(parseDob(dobStr));
        user.setStatus("ACTIVE");
        user.setForceChangePass(true);
        user.setUsername(email);

        String tempPassword = PasswordUtil.generateTempPassword();
        user.setPasswordHash(PasswordUtil.hash(tempPassword));

        int newId = personnelDAO.insert(user);
        if (newId < 0) throw new ValidationException("Tạo nhân sự thất bại. Vui lòng thử lại.");

        // ── Assign facility ───────────────────────────────────────────────
        if (facilityId != null) {
            if ("MANAGER".equals(role)) {
                personnelDAO.assignFacility(newId, facilityId);
            } else {
                personnelDAO.assignOperatorFacility(newId, facilityId);
            }
        }

        // ── Send email async (non-blocking) ───────────────────────────────
        final String finalEmail    = email;
        final String finalFullName = fullName;
        final String finalPwd      = tempPassword;
        try {
            EmailService.sendTempPassword(finalEmail, finalFullName, finalEmail, finalPwd, loginLink);
        } catch (Exception ex) {
            logger.warn("Lỗi gửi email", newId, ex);
        }
    }

    // ── Update ────────────────────────────────────────────────────────────

    @Override
    public void update(int id, String fullName, String email, String phone, String role,
                       String identityNumber, String dobStr, String gender,
                       String permanentAddress, String facilityIdStr)
            throws NotFoundException, ValidationException {

        User existing = personnelDAO.findById(id).orElseThrow(NotFoundException::new);

        fullName         = trim(fullName);
        email            = trim(email);
        phone            = trim(phone);
        role             = trim(role);
        identityNumber   = trim(identityNumber);
        gender           = trim(gender);
        permanentAddress = trim(permanentAddress);

        // ── Basic validation ──────────────────────────────────────────────
        if (fullName.isEmpty())  throw new ValidationException("Họ tên không được để trống.");
        if (email.isEmpty())     throw new ValidationException("Email không được để trống.");
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$"))
            throw new ValidationException("Email không đúng định dạng.");
        if (phone.isEmpty())     throw new ValidationException("Số điện thoại không được để trống.");
        if (!ValidationUtil.isValidVnPhone(phone))
            throw new ValidationException("Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
        if (role.isEmpty() || "ADMIN".equals(role))
            throw new ValidationException("Vai trò không hợp lệ.");
        if (!identityNumber.isEmpty() && !ValidationUtil.isValidVnIdentity(identityNumber))
            throw new ValidationException("Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).");

        // ── Date of birth ─────────────────────────────────────────────────
        LocalDate dob = null;
        if (!dobStr.trim().isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr.trim());
                if (dob.isAfter(LocalDate.now()))
                    throw new ValidationException("Ngày sinh không được lớn hơn ngày hiện tại.");
            } catch (ValidationException e) {
                throw e;
            } catch (Exception ignored) {}
        }

        // ── Uniqueness ────────────────────────────────────────────────────
        if (personnelDAO.existsByEmail(email, id))
            throw new ValidationException("Email đã được sử dụng bởi tài khoản khác.");
        if (personnelDAO.existsByPhone(phone, id))
            throw new ValidationException("Số điện thoại đã được sử dụng bởi tài khoản khác.");
        if (!identityNumber.isEmpty() && personnelDAO.existsByIdentityNumber(identityNumber, id))
            throw new ValidationException("Số CMND/CCCD đã được sử dụng bởi tài khoản khác.");

        // ── Facility ──────────────────────────────────────────────────────
        Integer newFacilityId = parseFacilityId(facilityIdStr);
        if (("MANAGER".equals(role) || "OPERATOR".equals(role)) && newFacilityId == null)
            throw new ValidationException("Nhân sự phải được gán một cơ sở quản lý.");
        if (newFacilityId != null) {
            validateFacilityAssignment(newFacilityId, role, id);
        }

        // ── Persist ───────────────────────────────────────────────────────
        existing.setFullName(fullName);
        existing.setEmail(email);
        existing.setPhone(phone);
        existing.setRole(role);
        existing.setIdentityNumber(identityNumber.isEmpty() ? existing.getIdentityNumber() : identityNumber);
        existing.setGender(gender.isEmpty() ? existing.getGender() : gender);
        existing.setPermanentAddress(permanentAddress.isEmpty() ? existing.getPermanentAddress() : permanentAddress);
        if (dob != null) existing.setDob(dob);

        personnelDAO.update(existing);

        // ── Re-assign facility ────────────────────────────────────────────
        if ("MANAGER".equals(role)) {
            personnelDAO.unassignFacility(id);
            personnelDAO.unassignOperatorFacility(id);
        } else if ("OPERATOR".equals(role)) {
            personnelDAO.unassignOperatorFacility(id);
            personnelDAO.unassignFacility(id);
        }
        if (newFacilityId != null) {
            if ("MANAGER".equals(role)) personnelDAO.assignFacility(id, newFacilityId);
            else personnelDAO.assignOperatorFacility(id, newFacilityId);
        }
    }

    // ── Status / delete ───────────────────────────────────────────────────

    @Override
    public void toggleStatus(int id, int currentUserId) throws NotFoundException, ValidationException {
        if (currentUserId == id)
            throw new ValidationException("Không thể thay đổi trạng thái tài khoản của chính mình.");

        User user = personnelDAO.findById(id).orElseThrow(NotFoundException::new);
        String newStatus = "ACTIVE".equals(user.getStatus()) ? "INACTIVE" : "ACTIVE";
        personnelDAO.updateStatus(id, newStatus);
    }


    // ── Private helpers ───────────────────────────────────────────────────

    private String trim(String s) {
        return s == null ? "" : s.trim();
    }

    private LocalDate parseDob(String dobStr) {
        if (dobStr == null || dobStr.trim().isEmpty()) return null;
        try { return LocalDate.parse(dobStr.trim()); } catch (Exception e) { return null; }
    }

    private Integer parseFacilityId(String s) throws ValidationException {
        if (s == null || s.trim().isEmpty()) return null;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { throw new ValidationException("Cơ sở không hợp lệ."); }
    }

    private void validateFacilityAssignment(int facilityId, String role, Integer excludeUserId)
            throws ValidationException {
        Facility fac = facilityDAO.findById(facilityId)
                .orElseThrow(() -> new ValidationException("Cơ sở không tồn tại."));
        if (!"ACTIVE".equals(fac.getStatus()))
            throw new ValidationException("Chỉ được gán cơ sở đang ở trạng thái ACTIVE.");

        if ("MANAGER".equals(role)) {
            if (personnelDAO.countActiveManagerForFacility(facilityId, excludeUserId) > 0)
                throw new ValidationException("Cơ sở đã có Ban Quản Lý. Mỗi cơ sở chỉ được 1 Ban Quản Lý.");
        } else if ("OPERATOR".equals(role)) {
            if (personnelDAO.countActiveOperatorForFacility(facilityId, excludeUserId) > 0)
                throw new ValidationException("Cơ sở đã có Nhân viên vận hành. Mỗi cơ sở chỉ được 1 Nhân viên vận hành.");
        }
    }
}
