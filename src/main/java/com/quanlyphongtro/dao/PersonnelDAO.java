package com.quanlyphongtro.dao;
import com.quanlyphongtro.model.Facility;

import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PersonnelDAO extends BaseDAO {

    private User mapRow(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setRole(rs.getString("role"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setStatus(rs.getString("status"));
        u.setAvatarUrl(rs.getString("avatar_url"));
        u.setForceChangePass(rs.getBoolean("force_change_pass"));
        u.setIdentityNumber(rs.getString("identity_number"));
        u.setDob(toLocalDate(rs, "dob"));
        u.setGender(rs.getString("gender"));
        u.setPermanentAddress(rs.getString("permanent_address"));
        u.setCreatedAt(toLocalDateTime(rs, "created_at"));
        u.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        u.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        return u;
    }

    private static final String BASE_WHERE =
        "FROM dbo.users WHERE deleted_at IS NULL AND role IN ('MANAGER', 'OPERATOR')";

    public List<User> findAll(String keyword, String role, String status, int page, int pageSize) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * " + BASE_WHERE);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (full_name LIKE ? OR username LIKE ? OR email LIKE ? OR phone LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw); params.add(kw);
        }
        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            params.add(role.trim());
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }
        sql.append(" ORDER BY created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add((page - 1) * pageSize);
        params.add(pageSize);

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.findAll failed", e);
        }
        return list;
    }

    public int count(String keyword, String role, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) " + BASE_WHERE);
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (full_name LIKE ? OR username LIKE ? OR email LIKE ? OR phone LIKE ?)");
            String kw = "%" + keyword.trim() + "%";
            params.add(kw); params.add(kw); params.add(kw); params.add(kw);
        }
        if (role != null && !role.isBlank()) {
            sql.append(" AND role = ?");
            params.add(role.trim());
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND status = ?");
            params.add(status.trim());
        }

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.count failed", e);
        }
        return 0;
    }

    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM dbo.users WHERE user_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.findById failed for id={}", id, e);
        }
        return Optional.empty();
    }

    public List<String> findFacilityNamesForUser(int userId) {
        List<String> names = new ArrayList<>();
        // Kiểm tra cả manager_id lẫn operator_id
        String sql = "SELECT f.name FROM dbo.facilities f " +
                     "WHERE (f.manager_id = ? OR f.operator_id = ?) AND f.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) names.add(rs.getString("name"));
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.findFacilityNamesForUser failed for userId={}", userId, e);
        }
        return names;
    }

    /** Lấy facility_id mà MANAGER đang được gán. Trả về null nếu chưa gán. */
    public Integer findFacilityIdForUser(int userId) {
        // Tìm theo manager_id trước, rồi operator_id
        String sql = "SELECT facility_id, " +
                     "  CASE WHEN manager_id = ? THEN 'MANAGER' ELSE 'OPERATOR' END AS assigned_role " +
                     "FROM dbo.facilities " +
                     "WHERE (manager_id = ? OR operator_id = ?) AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("facility_id");
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.findFacilityIdForUser failed for userId={}", userId, e);
        }
        return null;
    }

    /**
     * Lấy danh sách cơ sở ACTIVE chưa có MANAGER.
     * Nếu excludeUserId != null: cũng hiển thị cơ sở mà chính user này đang là MANAGER.
     */
    public List<Facility> findFacilitiesForManager(Integer excludeUserId) {
        String sql = "SELECT f.*, mgr.full_name AS manager_name, opr.full_name AS operator_name " +
                     "FROM dbo.facilities f " +
                     "LEFT JOIN dbo.users mgr ON mgr.user_id = f.manager_id " +
                     "LEFT JOIN dbo.users opr ON opr.user_id = f.operator_id " +
                     "WHERE f.deleted_at IS NULL AND f.status = 'ACTIVE' " +
                     "AND (f.manager_id IS NULL" +
                     (excludeUserId != null ? " OR f.manager_id = ?" : "") +
                     ") ORDER BY f.name";
        List<Facility> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (excludeUserId != null) ps.setInt(1, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Facility f = new Facility();
                    f.setId(rs.getInt("facility_id"));
                    f.setCode(rs.getString("code"));
                    f.setName(rs.getString("name"));
                    f.setStatus(rs.getString("status"));
                    f.setManagerId(getInteger(rs, "manager_id"));
                    f.setOperatorId(getInteger(rs, "operator_id"));
                    list.add(f);
                }
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.findFacilitiesForManager failed", e);
        }
        return list;
    }

    /**
     * Lấy danh sách cơ sở ACTIVE chưa có OPERATOR.
     * Nếu excludeUserId != null: cũng hiển thị cơ sở mà chính user này đang là OPERATOR.
     */
    public List<Facility> findFacilitiesForOperator(Integer excludeUserId) {
        String sql = "SELECT f.*, mgr.full_name AS manager_name, opr.full_name AS operator_name " +
                     "FROM dbo.facilities f " +
                     "LEFT JOIN dbo.users mgr ON mgr.user_id = f.manager_id " +
                     "LEFT JOIN dbo.users opr ON opr.user_id = f.operator_id " +
                     "WHERE f.deleted_at IS NULL AND f.status = 'ACTIVE' " +
                     "AND (f.operator_id IS NULL" +
                     (excludeUserId != null ? " OR f.operator_id = ?" : "") +
                     ") ORDER BY f.name";
        List<Facility> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (excludeUserId != null) ps.setInt(1, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Facility f = new Facility();
                    f.setId(rs.getInt("facility_id"));
                    f.setCode(rs.getString("code"));
                    f.setName(rs.getString("name"));
                    f.setStatus(rs.getString("status"));
                    f.setManagerId(getInteger(rs, "manager_id"));
                    f.setOperatorId(getInteger(rs, "operator_id"));
                    list.add(f);
                }
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.findFacilitiesForOperator failed", e);
        }
        return list;
    }

    public int insert(User u) {
        String sql = "INSERT INTO dbo.users " +
            "(username, password_hash, role, full_name, email, phone, status, " +
            " force_change_pass, identity_number, dob, gender, permanent_address, " +
            " created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getRole());
            ps.setString(4, u.getFullName());
            ps.setString(5, u.getEmail());
            ps.setString(6, u.getPhone());
            ps.setString(7, u.getStatus());
            ps.setBoolean(8, u.isForceChangePass());
            ps.setString(9, u.getIdentityNumber());
            if (u.getDob() != null) ps.setDate(10, Date.valueOf(u.getDob()));
            else ps.setNull(10, Types.DATE);
            ps.setString(11, u.getGender());
            ps.setString(12, u.getPermanentAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.insert failed", e);
        }
        return -1;
    }

    public void update(User u) {
        String sql = "UPDATE dbo.users SET " +
            "full_name = ?, email = ?, phone = ?, role = ?, " +
            "identity_number = ?, dob = ?, gender = ?, permanent_address = ?, " +
            "updated_at = GETDATE() " +
            "WHERE user_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getRole());
            ps.setString(5, u.getIdentityNumber());
            if (u.getDob() != null) ps.setDate(6, Date.valueOf(u.getDob()));
            else ps.setNull(6, Types.DATE);
            ps.setString(7, u.getGender());
            ps.setString(8, u.getPermanentAddress());
            ps.setInt(9, u.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("PersonnelDAO.update failed for id={}", u.getId(), e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE dbo.users SET status = ?, updated_at = GETDATE() WHERE user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("PersonnelDAO.updateStatus failed for id={}", id, e);
        }
    }

    public boolean existsByEmail(String email, Integer excludeId) {
        String sql = excludeId != null
            ? "SELECT COUNT(*) FROM dbo.users WHERE email = ? AND user_id <> ? AND deleted_at IS NULL"
            : "SELECT COUNT(*) FROM dbo.users WHERE email = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.existsByEmail failed", e);
        }
        return false;
    }

    public boolean existsByPhone(String phone, Integer excludeId) {
        String sql = excludeId != null
            ? "SELECT COUNT(*) FROM dbo.users WHERE phone = ? AND user_id <> ? AND deleted_at IS NULL"
            : "SELECT COUNT(*) FROM dbo.users WHERE phone = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.existsByPhone failed", e);
        }
        return false;
    }

    public boolean existsByIdentityNumber(String idNum, Integer excludeId) {
        String sql = excludeId != null
            ? "SELECT COUNT(*) FROM dbo.users WHERE identity_number = ? AND user_id <> ? AND deleted_at IS NULL"
            : "SELECT COUNT(*) FROM dbo.users WHERE identity_number = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, idNum);
            if (excludeId != null) ps.setInt(2, excludeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.existsByIdentityNumber failed", e);
        }
        return false;
    }

    /** Gán MANAGER vào cơ sở (cập nhật cột manager_id). */
    public void assignFacility(int userId, int facilityId) {
        String sql = "UPDATE dbo.facilities SET manager_id = ?, updated_at = GETDATE() " +
                     "WHERE facility_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, facilityId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("PersonnelDAO.assignFacility failed userId={} facilityId={}", userId, facilityId, e);
        }
    }

    /** Bỏ gán MANAGER khỏi tất cả cơ sở đang được gán. */
    public void unassignFacility(int userId) {
        String sql = "UPDATE dbo.facilities SET manager_id = NULL, updated_at = GETDATE() " +
                     "WHERE manager_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("PersonnelDAO.unassignFacility failed userId={}", userId, e);
        }
    }

    /** Gán OPERATOR vào cơ sở (cập nhật cột operator_id). */
    public void assignOperatorFacility(int userId, int facilityId) {
        String sql = "UPDATE dbo.facilities SET operator_id = ?, updated_at = GETDATE() " +
                     "WHERE facility_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, facilityId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("PersonnelDAO.assignOperatorFacility failed userId={} facilityId={}", userId, facilityId, e);
        }
    }

    /** Bỏ gán OPERATOR khỏi tất cả cơ sở đang được gán. */
    public void unassignOperatorFacility(int userId) {
        String sql = "UPDATE dbo.facilities SET operator_id = NULL, updated_at = GETDATE() " +
                     "WHERE operator_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("PersonnelDAO.unassignOperatorFacility failed userId={}", userId, e);
        }
    }

    /** Đếm MANAGER đang active cho cơ sở, loại trừ excludeUserId (để check khi update). */
    public int countActiveManagerForFacility(int facilityId, Integer excludeUserId) {
        String sql = excludeUserId != null
            ? "SELECT COUNT(*) FROM dbo.users u " +
              "JOIN dbo.facilities f ON f.manager_id = u.user_id " +
              "WHERE f.facility_id = ? AND u.user_id <> ? AND u.status = 'ACTIVE' " +
              "AND f.deleted_at IS NULL AND u.deleted_at IS NULL"
            : "SELECT COUNT(*) FROM dbo.users u " +
              "JOIN dbo.facilities f ON f.manager_id = u.user_id " +
              "WHERE f.facility_id = ? AND u.status = 'ACTIVE' " +
              "AND f.deleted_at IS NULL AND u.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            if (excludeUserId != null) ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.countActiveManagerForFacility failed", e);
        }
        return 0;
    }

    /** Đếm OPERATOR đang active cho cơ sở, loại trừ excludeUserId. */
    public int countActiveOperatorForFacility(int facilityId, Integer excludeUserId) {
        String sql = excludeUserId != null
            ? "SELECT COUNT(*) FROM dbo.users u " +
              "JOIN dbo.facilities f ON f.operator_id = u.user_id " +
              "WHERE f.facility_id = ? AND u.user_id <> ? AND u.status = 'ACTIVE' " +
              "AND f.deleted_at IS NULL AND u.deleted_at IS NULL"
            : "SELECT COUNT(*) FROM dbo.users u " +
              "JOIN dbo.facilities f ON f.operator_id = u.user_id " +
              "WHERE f.facility_id = ? AND u.status = 'ACTIVE' " +
              "AND f.deleted_at IS NULL AND u.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facilityId);
            if (excludeUserId != null) ps.setInt(2, excludeUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.countActiveOperatorForFacility failed", e);
        }
        return 0;
    }

    public int countByRole(String role) {
        String sql = "SELECT COUNT(*) FROM dbo.users WHERE role = ? AND deleted_at IS NULL AND status <> 'INACTIVE'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, role);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("PersonnelDAO.countByRole failed for role={}", role, e);
        }
        return 0;
    }



    public int countAll() {
        String sql = "SELECT COUNT(*) FROM dbo.users WHERE role IN ('MANAGER','OPERATOR') " +
                     "AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            logger.error("PersonnelDAO.countAll failed", e);
        }
        return 0;
    }
}
