package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.UserSessionDTO;

import java.util.List;
import java.util.Optional;
import com.quanlyphongtro.model.User;

public interface UserService {
    /**
     * Authenticate user with username and password.
     * Returns UserSessionDTO on success, empty on failure.
     */
    Optional<UserSessionDTO> login(String username, String password);

    /**
     * Get user session by user ID.
     */
    Optional<UserSessionDTO> getSessionById(int userId);

    List<User> getStaffUsers();

    List<User> getStaffUsersByTenantId(int tenantId);
}
