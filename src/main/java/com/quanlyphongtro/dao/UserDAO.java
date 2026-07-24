package com.quanlyphongtro.dao;

import com.quanlyphongtro.constant.StatusConstant;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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

    public int countTenants(int managerId, String keyword, String status) {
        int totalCount = 0;
        StringBuilder whereClause = new StringBuilder(
            " WHERE u.role = 'TENANT' AND u.deleted_at IS NULL" +
            " AND (r.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)" +
            "      OR EXISTS (SELECT 1 FROM dbo.contracts c INNER JOIN dbo.rooms cr ON c.room_id = cr.room_id AND cr.deleted_at IS NULL WHERE c.tenant_id = u.user_id AND c.deleted_at IS NULL AND cr.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)))");
        List<Object> params = new ArrayList<>();
        params.add(managerId);
        params.add(managerId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeParam = "%" + keyword.trim() + "%";
            whereClause.append(" AND (u.full_name LIKE ? OR u.phone LIKE ? OR u.email LIKE ? OR r.code LIKE ?)");
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }

        if (status != null && !status.trim().isEmpty()) {
            whereClause.append(" AND u.status = ?");
            params.add(status.trim());
        }

        String countSql = "SELECT COUNT(1) FROM dbo.users u" +
            " LEFT JOIN dbo.rooms r ON r.tenant_id = u.user_id AND r.deleted_at IS NULL" +
            whereClause.toString();

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(countSql)) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("countTenants failed", e);
        }
        return totalCount;
    }

    public List<Map<String, Object>> getTenants(int managerId, String keyword, String status, int offset, int limit) {
        List<Map<String, Object>> tenants = new ArrayList<>();
        StringBuilder whereClause = new StringBuilder(
            " WHERE u.role = 'TENANT' AND u.deleted_at IS NULL" +
            " AND (r.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)" +
            "      OR EXISTS (SELECT 1 FROM dbo.contracts c INNER JOIN dbo.rooms cr ON c.room_id = cr.room_id AND cr.deleted_at IS NULL WHERE c.tenant_id = u.user_id AND c.deleted_at IS NULL AND cr.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)))");
        List<Object> params = new ArrayList<>();
        params.add(managerId);
        params.add(managerId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeParam = "%" + keyword.trim() + "%";
            whereClause.append(" AND (u.full_name LIKE ? OR u.phone LIKE ? OR u.email LIKE ? OR r.code LIKE ?)");
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }

        if (status != null && !status.trim().isEmpty()) {
            whereClause.append(" AND u.status = ?");
            params.add(status.trim());
        }

        String selectSql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.status," +
            " r.room_id, r.code AS room_code, r.contract_start_date" +
            " FROM dbo.users u" +
            " LEFT JOIN dbo.rooms r ON r.tenant_id = u.user_id AND r.deleted_at IS NULL" +
            whereClause.toString() +
            " ORDER BY u.user_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(selectSql)) {
            int i = 0;
            for (; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ps.setInt(i + 1, offset);
            ps.setInt(i + 2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> tenant = new HashMap<>();
                    tenant.put("id", rs.getInt("user_id"));
                    tenant.put("tenantCode", rs.getString("username"));
                    tenant.put("fullName", rs.getString("full_name"));
                    tenant.put("phone", rs.getString("phone"));
                    tenant.put("email", rs.getString("email"));
                    tenant.put("roomId", rs.getInt("room_id"));
                    tenant.put("roomCode", rs.getString("room_code"));
                    java.sql.Date sDate = rs.getDate("contract_start_date");
                    tenant.put("contractStartDate", sDate != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(sDate) : null);
                    tenant.put("status", rs.getString("status"));
                    tenants.add(tenant);
                }
            }
        } catch (Exception e) {
            logger.error("getTenants failed", e);
        }
        return tenants;
    }

    public Map<String, Object> getTenantDetail(int tenantId) {
        Map<String, Object> tenant = null;
        String tenantSql = "SELECT u.*, r.room_id, r.code AS room_code, r.contract_start_date, " +
                "(SELECT TOP 1 contract_id FROM dbo.contracts WHERE tenant_id = u.user_id AND deleted_at IS NULL ORDER BY CASE WHEN status = 'ACTIVE' THEN 0 ELSE 1 END, created_at DESC) AS contract_id " +
                "FROM dbo.users u " +
                "LEFT JOIN dbo.rooms r ON u.user_id = r.tenant_id " +
                "WHERE u.user_id = ? AND u.role = 'TENANT' AND u.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(tenantSql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tenant = new HashMap<>();
                    tenant.put("id", rs.getInt("user_id"));
                    tenant.put("tenantCode", rs.getString("username"));
                    tenant.put("fullName", rs.getString("full_name"));
                    java.sql.Date dob = rs.getDate("dob");
                    tenant.put("dob", dob != null ? dob.toString() : null);
                    tenant.put("gender", rs.getString("gender"));
                    tenant.put("phone", rs.getString("phone"));
                    tenant.put("email", rs.getString("email"));
                    tenant.put("identityNumber", rs.getString("identity_number"));
                    tenant.put("permanentAddress", rs.getString("permanent_address"));
                    tenant.put("status", rs.getString("status"));
                    tenant.put("roomId", rs.getInt("room_id"));
                    tenant.put("roomCode", rs.getString("room_code"));
                    java.sql.Date sDate = rs.getDate("contract_start_date");
                    tenant.put("contractStartDate", sDate != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(sDate) : null);
                    int contractId = rs.getInt("contract_id");
                    tenant.put("contractId", rs.wasNull() ? null : contractId);
                }
            }
        } catch (Exception e) {
            logger.error("getTenantDetail failed", e);
        }
        return tenant;
    }

    public boolean verifyTenantEditPermission(int tenantId, int managerId) {
        String verifySql = 
            "SELECT 1 FROM dbo.users u " +
            "LEFT JOIN dbo.rooms r ON u.user_id = r.tenant_id " +
            "LEFT JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "WHERE u.user_id = ? AND u.role = 'TENANT' AND u.deleted_at IS NULL AND (f.manager_id = ? OR r.room_id IS NULL)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setInt(1, tenantId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("verifyTenantEditPermission failed", e);
            return false;
        }
    }

    public boolean isDuplicateEmail(String email, int tenantId) {
        String duplicateSql = "SELECT user_id FROM dbo.users WHERE (username = ? OR email = ?) AND user_id != ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(duplicateSql)) {
            ps.setString(1, email);
            ps.setString(2, email);
            ps.setInt(3, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("isDuplicateEmail failed", e);
            return false;
        }
    }

    public boolean updateTenantInfo(int tenantId, String username, String email, String fullName, String phone, String identityNumber, java.time.LocalDate dob, String gender, String permanentAddress) {
        String updateSql = 
            "UPDATE dbo.users SET username = ?, email = ?, full_name = ?, phone = ?, identity_number = ?, " +
            "dob = ?, gender = ?, permanent_address = ?, updated_at = GETDATE() " +
            "WHERE user_id = ? AND role = 'TENANT' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, fullName);
            ps.setString(4, phone);
            ps.setString(5, identityNumber);
            ps.setDate(6, dob != null ? java.sql.Date.valueOf(dob) : null);
            ps.setString(7, gender);
            ps.setString(8, permanentAddress);
            ps.setInt(9, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("updateTenantInfo failed", e);
            return false;
        }
    }

    public boolean softDeleteTenant(int tenantId) {
        String sql = "UPDATE dbo.users SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE user_id = ? AND role = 'TENANT'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("softDeleteTenant failed", e);
            return false;
        }
    }

    public boolean endRentalTransaction(int tenantId) {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // 1. Release room
            String sqlRoom = "UPDATE dbo.rooms SET tenant_id = NULL, status = 'AVAILABLE', contract_start_date = NULL, contract_end_date = NULL, updated_at = GETDATE() WHERE tenant_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlRoom)) {
                ps.setInt(1, tenantId);
                ps.executeUpdate();
            }

            // 2. Set tenant's status to INACTIVE
            String sqlUser = "UPDATE dbo.users SET status = 'INACTIVE', updated_at = GETDATE() WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUser)) {
                ps.setInt(1, tenantId);
                ps.executeUpdate();
            }

            // 3. Set contract's status to INACTIVE
            String sqlContract = "UPDATE dbo.contracts SET status = 'INACTIVE', updated_at = GETDATE() WHERE tenant_id = ? AND status = 'ACTIVE'";
            try (PreparedStatement ps = conn.prepareStatement(sqlContract)) {
                ps.setInt(1, tenantId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("endRentalTransaction failed for tenant={}", tenantId, e);
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
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
