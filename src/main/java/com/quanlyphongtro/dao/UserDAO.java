package com.quanlyphongtro.dao;

import com.quanlyphongtro.constant.StatusConstant;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAO extends BaseDAO {

    /**
     * Map ResultSet sang User — chỉ dùng các cột thực tế có trong schema.sql.
     * Schema columns: user_id, username, password_hash, role, full_name, email, phone,
     *   status, avatar_url, force_change_pass, identity_number, dob, gender,
     *   permanent_address, created_at, updated_at, deleted_at
     */
    private User mapRow(ResultSet rs) throws Exception {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setRole(rs.getString("role"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setStatus(rs.getString("status"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setForceChangePass(rs.getBoolean("force_change_pass"));
        user.setIdentityNumber(rs.getString("identity_number"));
        user.setDob(toLocalDate(rs, "dob"));
        user.setGender(rs.getString("gender"));
        user.setPermanentAddress(rs.getString("permanent_address"));
        user.setCreatedAt(toLocalDateTime(rs, "created_at"));
        user.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        user.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        return user;
    }

    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM dbo.users WHERE username = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByUsername failed for username={}", username, e);
        }
        return Optional.empty();
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM dbo.users WHERE user_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findById failed for id={}", id, e);
        }
        return Optional.empty();
    }

    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM dbo.users WHERE email = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByEmail failed for email={}", email, e);
        }
        return Optional.empty();
    }

    /**
     * Cập nhật trạng thái tài khoản (ACTIVE / LOCKED / INACTIVE).
     */
    public void updateStatus(int userId, String status) {
        String sql = "UPDATE dbo.users SET status = ?, updated_at = GETDATE() WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("updateStatus failed for userId={}", userId, e);
        }
    }

    /**
     * Xóa cờ force_change_pass sau khi người dùng đổi mật khẩu lần đầu thành công.
     */
    public void clearForceChangePass(int userId) {
        String sql = "UPDATE dbo.users SET force_change_pass = 0, updated_at = GETDATE() WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("clearForceChangePass failed for userId={}", userId, e);
        }
    }

    /**
     * Cập nhật password hash sau khi người dùng đổi mật khẩu.
     */
    public void updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE dbo.users SET password_hash = ?, force_change_pass = 0, updated_at = GETDATE() WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("updatePassword failed for userId={}", userId, e);
        }
    }

    /**
     * Cập nhật thông tin hồ sơ người dùng.
     */
    public void updateProfile(User user) {
        String sql = "UPDATE dbo.users SET full_name = ?, phone = ?, avatar_url = ?, identity_number = ?, dob = ?, gender = ?, permanent_address = ?, updated_at = GETDATE() WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            ps.setString(3, user.getAvatarUrl());
            ps.setString(4, user.getIdentityNumber());
            if (user.getDob() != null) {
                ps.setDate(5, java.sql.Date.valueOf(user.getDob()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, user.getGender());
            ps.setString(7, user.getPermanentAddress());
            ps.setInt(8, user.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("updateProfile failed for userId={}", user.getId(), e);
        }
    }

    public List<User> getStaffUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM dbo.users WHERE status = 'ACTIVE' AND deleted_at IS NULL AND role IN ('MANAGER', 'OPERATOR')";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("getStaffUsers failed", e);
        }
        return users;
    }

    public List<User> getStaffUsersByTenantId(int tenantId) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.* FROM dbo.users u " +
                     "JOIN dbo.facilities f ON (u.user_id = f.manager_id OR u.user_id = f.operator_id) " +
                     "JOIN dbo.rooms r ON r.facility_id = f.facility_id " +
                     "WHERE r.tenant_id = ? AND u.status = 'ACTIVE' AND u.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("getStaffUsersByTenantId failed for tenantId={}", tenantId, e);
        }
        return users;
    }
}
