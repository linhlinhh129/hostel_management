package com.quanlyphongtro.dao;
import java.time.LocalDate;

import com.quanlyphongtro.model.Dependent;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class DependentDAO extends BaseDAO {

    private Dependent mapRow(ResultSet rs) throws SQLException {
        Dependent d = new Dependent();
        d.setId(rs.getInt("dependent_id"));
        d.setTenantId(rs.getInt("tenant_id"));
        d.setFullName(rs.getString("full_name"));
        d.setDob(toLocalDate(rs, "dob"));
        d.setGender(rs.getString("gender"));
        d.setRelationship(rs.getString("relationship"));
        d.setPhone(rs.getString("phone"));
        d.setIdentityNumber(rs.getString("identity_number"));
        d.setPermanentAddress(rs.getString("permanent_address"));
        d.setCreatedAt(toLocalDateTime(rs, "created_at"));
        d.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        d.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        return d;
    }

    public List<Dependent> findByTenantId(int tenantId) {
        List<Dependent> list = new ArrayList<>();
        String sql = "SELECT * FROM dbo.dependents WHERE tenant_id = ? AND deleted_at IS NULL ORDER BY created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByTenantId failed for tenantId={}", tenantId, e);
        }
        return list;
    }

    public Optional<Dependent> findByIdAndTenantId(int id, int tenantId) {
        String sql = "SELECT * FROM dbo.dependents WHERE dependent_id = ? AND tenant_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByIdAndTenantId failed for id={}, tenantId={}", id, tenantId, e);
        }
        return Optional.empty();
    }

    public boolean insert(Dependent d) {
        String sql = "INSERT INTO dbo.dependents (tenant_id, full_name, dob, gender, relationship, phone, identity_number, permanent_address) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, d.getTenantId());
            ps.setString(2, d.getFullName());
            ps.setDate(3, d.getDob() != null ? Date.valueOf(d.getDob()) : null);
            ps.setString(4, d.getGender());
            ps.setString(5, d.getRelationship());
            ps.setString(6, d.getPhone());
            ps.setString(7, d.getIdentityNumber());
            ps.setString(8, d.getPermanentAddress());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("insert failed for Dependent", e);
            return false;
        }
    }

    public boolean update(Dependent d) {
        String sql = "UPDATE dbo.dependents SET full_name=?, dob=?, gender=?, relationship=?, phone=?, identity_number=?, permanent_address=?, updated_at=GETDATE() " +
                     "WHERE dependent_id = ? AND tenant_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getFullName());
            ps.setDate(2, d.getDob() != null ? Date.valueOf(d.getDob()) : null);
            ps.setString(3, d.getGender());
            ps.setString(4, d.getRelationship());
            ps.setString(5, d.getPhone());
            ps.setString(6, d.getIdentityNumber());
            ps.setString(7, d.getPermanentAddress());
            ps.setInt(8, d.getId());
            ps.setInt(9, d.getTenantId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("update failed for Dependent id={}", d.getId(), e);
            return false;
        }
    }

    public boolean softDelete(int id, int tenantId) {
        String sql = "UPDATE dbo.dependents SET deleted_at=GETDATE() WHERE dependent_id = ? AND tenant_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, tenantId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("softDelete failed for Dependent id={}", id, e);
            return false;
        }
    }

    public List<Map<String, Object>> getTenantDependents(int tenantId) {
        List<Map<String, Object>> dependents = new ArrayList<>();
        String depSql = "SELECT * FROM dbo.dependents WHERE tenant_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(depSql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dep = new HashMap<>();
                    dep.put("id", rs.getInt("dependent_id"));
                    dep.put("fullName", rs.getString("full_name"));
                    dep.put("relationship", rs.getString("relationship"));
                    dep.put("phone", rs.getString("phone"));
                    dep.put("gender", rs.getString("gender"));
                    Date dDob = rs.getDate("dob");
                    dep.put("dob", dDob != null ? dDob.toString() : null);
                    dependents.add(dep);
                }
            }
        } catch (Exception e) {
            logger.error("getTenantDependents failed", e);
        }
        return dependents;
    }

    public Map<String, Object> getDependentDetail(int dependentId, int managerId) {
        Map<String, Object> dependent = null;
        String query = 
            "SELECT d.*, u.full_name AS tenant_name, u.user_id AS tenant_id, u.username AS tenant_code, u.status AS tenant_status " +
            "FROM dbo.dependents d " +
            "JOIN dbo.users u ON d.tenant_id = u.user_id " +
            "WHERE d.dependent_id = ? AND d.deleted_at IS NULL AND u.deleted_at IS NULL " +
            "AND ( " +
            "    EXISTS ( " +
            "        SELECT 1 FROM dbo.rooms r " +
            "        JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "        WHERE r.tenant_id = u.user_id AND f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL " +
            "    ) " +
            "    OR EXISTS ( " +
            "        SELECT 1 FROM dbo.contracts c " +
            "        JOIN dbo.rooms cr ON c.room_id = cr.room_id " +
            "        JOIN dbo.facilities f ON cr.facility_id = f.facility_id " +
            "        WHERE c.tenant_id = u.user_id AND f.manager_id = ? AND c.deleted_at IS NULL AND cr.deleted_at IS NULL AND f.deleted_at IS NULL " +
            "    ) " +
            ")";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, dependentId);
            ps.setInt(2, managerId);
            ps.setInt(3, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    dependent = new HashMap<>();
                    dependent.put("id", rs.getInt("dependent_id"));
                    dependent.put("fullName", rs.getString("full_name"));
                    dependent.put("relationship", rs.getString("relationship"));
                    dependent.put("phone", rs.getString("phone"));
                    dependent.put("gender", rs.getString("gender"));
                    Date dob = rs.getDate("dob");
                    dependent.put("dob", dob != null ? dob.toString() : null);
                    dependent.put("tenantId", rs.getInt("tenant_id"));
                    dependent.put("tenantName", rs.getString("tenant_name"));
                    dependent.put("tenantCode", rs.getString("tenant_code"));
                    dependent.put("tenantStatus", rs.getString("tenant_status"));
                    dependent.put("identityNumber", rs.getString("identity_number"));
                    dependent.put("permanentAddress", rs.getString("permanent_address"));
                }
            }
        } catch (Exception e) {
            logger.error("getDependentDetail failed", e);
        }
        return dependent;
    }

    public int verifyDependentAndGetTenantId(int dependentId, int managerId, Map<String, String> statusOut) {
        String verifySql = 
            "SELECT d.tenant_id, u.status AS tenant_status FROM dbo.dependents d " +
            "JOIN dbo.users u ON d.tenant_id = u.user_id " +
            "WHERE d.dependent_id = ? AND d.deleted_at IS NULL " +
            "AND ( " +
            "    EXISTS ( " +
            "        SELECT 1 FROM dbo.rooms r " +
            "        JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "        WHERE r.tenant_id = u.user_id AND f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL " +
            "    ) " +
            "    OR EXISTS ( " +
            "        SELECT 1 FROM dbo.contracts c " +
            "        JOIN dbo.rooms cr ON c.room_id = cr.room_id " +
            "        JOIN dbo.facilities f ON cr.facility_id = f.facility_id " +
            "        WHERE c.tenant_id = u.user_id AND f.manager_id = ? AND c.deleted_at IS NULL AND cr.deleted_at IS NULL AND f.deleted_at IS NULL " +
            "    ) " +
            ")";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setInt(1, dependentId);
            ps.setInt(2, managerId);
            ps.setInt(3, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    statusOut.put("status", rs.getString("tenant_status"));
                    return rs.getInt("tenant_id");
                }
            }
        } catch (Exception e) {
            logger.error("verifyDependentAndGetTenantId failed", e);
        }
        return 0;
    }

    public boolean addDependent(int tenantId, String fullName, String relationship, String phone, String gender, LocalDate dob, String identityNumber) {
        String sql = "INSERT INTO dbo.dependents (tenant_id, full_name, relationship, phone, gender, dob, identity_number, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setString(2, fullName);
            ps.setString(3, relationship);
            ps.setString(4, phone);
            ps.setString(5, gender);
            ps.setDate(6, dob != null ? Date.valueOf(dob) : null);
            ps.setString(7, identityNumber);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("addDependent failed", e);
            return false;
        }
    }

    public boolean updateDependent(int dependentId, String fullName, String relationship, String phone, String gender, LocalDate dob, String identityNumber) {
        String updateSql = "UPDATE dbo.dependents SET full_name = ?, relationship = ?, phone = ?, gender = ?, dob = ?, identity_number = ?, updated_at = GETDATE() WHERE dependent_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, fullName);
            ps.setString(2, relationship);
            ps.setString(3, phone);
            ps.setString(4, gender);
            ps.setDate(5, dob != null ? Date.valueOf(dob) : null);
            ps.setString(6, identityNumber);
            ps.setInt(7, dependentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("updateDependent failed", e);
            return false;
        }
    }

    public boolean deleteDependent(int dependentId) {
        String sql = "UPDATE dbo.dependents SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE dependent_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dependentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("deleteDependent failed", e);
            return false;
        }
    }
}
