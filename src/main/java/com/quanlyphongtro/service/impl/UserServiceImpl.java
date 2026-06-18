package com.quanlyphongtro.service.impl;

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
    public Optional<UserSessionDTO> login(String username, String password) {
        if (username == null || password == null || username.isBlank() || password.length() < 7) {
            return Optional.empty();
        }

        String normalizedUsername = username.trim();

        if (LoginAttemptTracker.isLocked(normalizedUsername)) {
            logger.warn("Login blocked — account temporarily locked: {}", normalizedUsername);
            return Optional.empty();
        }

        Optional<User> userOpt = userDAO.findByUsername(normalizedUsername);
        if (userOpt.isEmpty()) {
            return Optional.empty();
        }

        User user = userOpt.get();

        if (user.isDeleted()) {
            return Optional.empty();
        }

        if (user.isLocked()) {
            return Optional.empty();
        }

        if (!user.isActive()) {
            return Optional.empty();
        }

        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            int attempts = LoginAttemptTracker.recordFailure(normalizedUsername);
            if (attempts >= RoleConstant.MAX_LOGIN_ATTEMPTS) {
                userDAO.updateStatus(user.getId(), StatusConstant.LOCKED);
                logger.warn("Account locked after {} failed attempts: {}", attempts, normalizedUsername);
            }
            return Optional.empty();
        }

        LoginAttemptTracker.reset(normalizedUsername);

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
        // force_change_pass → firstLogin flag trong session
        dto.setFirstLogin(user.isForceChangePass());
        return dto;
    }
}
