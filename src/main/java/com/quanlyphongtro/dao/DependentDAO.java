package com.quanlyphongtro.dao;

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
}
