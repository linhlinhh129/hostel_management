package com.quanlyphongtro.service.impl;
import com.quanlyphongtro.exception.ForbiddenException;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.dao.RoomDAO;
import java.util.List;

import com.quanlyphongtro.constant.RoleConstant;
import com.quanlyphongtro.constant.StatusConstant;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.UserService;
import com.quanlyphongtro.util.LoginAttemptTracker;
import com.quanlyphongtro.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UserDAO userDAO;

    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }

    public UserServiceImpl(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    // Bước 5.1: Xử lý logic Đăng nhập chính thức (từ tầng Controller gọi xuống)
    public Optional<UserSessionDTO> login(String username, String password) {
        // Validation bổ sung ở tầng Service
        if (username == null || password == null || username.isBlank() || password.length() < 7) {
            logger.warn("LOGIN FAIL [{}]: input validation failed — username blank or password length < 7 (len={})",
                    username, password == null ? "null" : password.length());
            return Optional.empty();
        }

        String normalizedUsername = username.trim();

        // Bước 5.2: Kiểm tra Brute-force lần 2 (để an toàn nếu có bypass Controller)
        if (LoginAttemptTracker.isLocked(normalizedUsername)) {
            logger.warn("LOGIN FAIL [{}]: account temporarily locked", normalizedUsername);
            return Optional.empty();
        }

        // Bước 5.3: Truy vấn DB. Đầu tiên tìm bằng Username
        Optional<User> userOpt = userDAO.findByUsername(normalizedUsername);
        if (userOpt.isEmpty()) {
            // Fallback: Nếu không tìm thấy, thử tìm bằng Email 
            // (Thường dùng cho Tenant và Nhân sự mới khi chưa cấp username chính thức)
            userOpt = userDAO.findByEmail(normalizedUsername);
        }
        if (userOpt.isEmpty()) {
            logger.warn("LOGIN FAIL [{}]: username not found in DB", normalizedUsername);
            return Optional.empty();
        }

        User user = userOpt.get();
        logger.info("LOGIN [{}]: found user id={}, status={}, deleted={}, hash_prefix={}",
                normalizedUsername, user.getId(), user.getStatus(), user.isDeleted(),
                user.getPasswordHash() == null ? "null" : user.getPasswordHash().substring(0, Math.min(20, user.getPasswordHash().length())));

        // Bước 5.4: Kiểm tra trạng thái tài khoản
        if (user.isDeleted()) {
            logger.warn("LOGIN FAIL [{}]: account is soft-deleted", normalizedUsername);
            return Optional.empty();
        }

        // Bị Admin khóa thủ công
        if (user.isLocked()) {
            throw new ForbiddenException("LOCKED");
        }

        // Trạng thái không ACTIVE (ví dụ đang pending)
        if (!user.isActive()) {
            logger.warn("LOGIN FAIL [{}]: account status is '{}' (not ACTIVE)", normalizedUsername, user.getStatus());
            return Optional.empty();
        }

        // Bước 5.5: So sánh mật khẩu bằng hàm Hash (Bcrypt/PBKDF2...)
        boolean passwordMatch = PasswordUtil.verify(password, user.getPasswordHash());
        logger.info("LOGIN [{}]: password verify result = {}", normalizedUsername, passwordMatch);

        // Nếu mật khẩu sai
        if (!passwordMatch) {
            // Tăng biến đếm số lần sai
            int attempts = LoginAttemptTracker.recordFailure(normalizedUsername);
            if (attempts >= RoleConstant.MAX_LOGIN_ATTEMPTS) {
                // Nếu sai quá MAX_LOGIN_ATTEMPTS, khóa tài khoản vĩnh viễn trong DB
                userDAO.updateStatus(user.getId(), StatusConstant.LOCKED);
                logger.warn("LOGIN FAIL [{}]: account locked after {} failed attempts", normalizedUsername, attempts);
            } else {
                logger.warn("LOGIN FAIL [{}]: wrong password (attempt {}/{})", normalizedUsername, attempts, RoleConstant.MAX_LOGIN_ATTEMPTS);
            }
            return Optional.empty();
        }

        // Bước 5.6: Nếu Mật khẩu đúng -> Xóa bộ đếm nhập sai
        LoginAttemptTracker.reset(normalizedUsername);
        logger.info("LOGIN SUCCESS [{}]: role={}", normalizedUsername, user.getRole());

        // Khởi tạo và trả về đối tượng Session DTO để Controller lưu cookie
        return Optional.of(buildSessionDTO(user));
    }

    @Override
    public Optional<UserSessionDTO> getSessionById(int userId) {
        Optional<User> userOpt = userDAO.findById(userId);
        if (userOpt.isEmpty() || userOpt.get().isDeleted() || !userOpt.get().isActive()) {
            return Optional.empty();
        }
        return Optional.of(buildSessionDTO(userOpt.get()));
    }

    private UserSessionDTO buildSessionDTO(User user) {
        UserSessionDTO dto = new UserSessionDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setFullName(user.getFullName());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setInitials(UserSessionDTO.extractInitials(user.getFullName()));
        dto.setFirstLogin(user.isForceChangePass());

        // Set facilityCode/roomCode vào session để dùng trong sidebar và filter
        String role = user.getRole();
        if ("MANAGER".equals(role) || "OPERATOR".equals(role)) {
            // Lấy facilityCode từ facilities.manager_id hoặc operator_id
            try {
                FacilityDAO facilityDAO =
                        new FacilityDAO();
                Optional<Facility> facilityOpt = "OPERATOR".equals(role) 
                        ? facilityDAO.findByOperatorId(user.getId()) 
                        : facilityDAO.findByManagerId(user.getId());
                
                facilityOpt.ifPresent(f -> {
                    dto.setFacilityCode(f.getCode());
                    // Lưu facilityId vào session qua facilityCode (code là unique key hiển thị)
                    // facilityId thực sẽ được resolve lại khi cần từ DAO
                });
            } catch (Exception ex) {
                logger.warn("Could not resolve facilityCode for userId={}", user.getId(), ex);
            }
        } else if ("TENANT".equals(role)) {
            // Lấy roomCode từ rooms.tenant_id
            try {
                RoomDAO roomDAO =
                        new RoomDAO();
                roomDAO.findByTenantId(user.getId())
                        .ifPresent(r -> dto.setRoomCode(r.getCode()));
            } catch (Exception ex) {
                logger.warn("Could not resolve roomCode for userId={}", user.getId(), ex);
            }
        }

        return dto;
    }

    @Override
    public List<User> getStaffUsers() {
        return userDAO.getStaffUsers();
    }

    @Override
    public List<User> getStaffUsersByTenantId(int tenantId) {
        return userDAO.getStaffUsersByTenantId(tenantId);
    }
}
