package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDate;

public class ContractDAO extends BaseDAO {

    private Contract mapRow(ResultSet rs) throws Exception {
        Contract contract = new Contract();
        contract.setContractId(rs.getInt("contract_id"));
        contract.setCode(rs.getString("code"));
        contract.setRoomId(rs.getInt("room_id"));
        contract.setTenantId(getInteger(rs, "tenant_id"));
        contract.setTenantFullName(rs.getString("tenant_full_name"));
        contract.setTenantDob(toLocalDate(rs, "tenant_dob"));
        contract.setTenantPermanentAddress(rs.getString("tenant_permanent_address"));
        contract.setTenantIdentityNumber(rs.getString("tenant_identity_number"));
        contract.setTenantIdentityIssueDate(toLocalDate(rs, "tenant_identity_issue_date"));
        contract.setTenantIdentityIssuePlace(rs.getString("tenant_identity_issue_place"));
        contract.setTenantPhone(rs.getString("tenant_phone"));
        contract.setAmountInWords(rs.getString("amount_in_words"));
        contract.setSignedDate(toLocalDate(rs, "signed_date"));
        contract.setStartDate(toLocalDate(rs, "start_date"));
        contract.setEndDate(toLocalDate(rs, "end_date"));
        contract.setStatus(rs.getString("status"));
        contract.setCreatedBy(getInteger(rs, "created_by"));
        contract.setCreatedAt(toLocalDateTime(rs, "created_at"));
        contract.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        contract.setDeletedAt(toLocalDateTime(rs, "deleted_at"));
        return contract;
    }

    public List<Contract> findAllByManagerId(int managerId, String searchName) {
        StringBuilder sql = new StringBuilder("SELECT c.* FROM dbo.contracts c " +
                     "JOIN dbo.rooms r ON c.room_id = r.room_id " +
                     "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                     "WHERE f.manager_id = ? AND c.deleted_at IS NULL ");
        
        if (searchName != null && !searchName.trim().isEmpty()) {
            sql.append(" AND c.tenant_full_name LIKE ? ");
        }
        sql.append(" ORDER BY c.created_at DESC");

        List<Contract> contracts = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, managerId);
            if (searchName != null && !searchName.trim().isEmpty()) {
                ps.setString(2, "%" + searchName.trim() + "%");
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findAllByManagerId failed for managerId={}", managerId, e);
        }
        return contracts;
    }

    public List<com.quanlyphongtro.model.Room> getAvailableRooms(int managerId) {
        String sql = "SELECT r.* FROM dbo.rooms r " +
                     "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                     "WHERE f.manager_id = ? AND r.tenant_id IS NULL AND r.deleted_at IS NULL";
        List<com.quanlyphongtro.model.Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.quanlyphongtro.model.Room room = new com.quanlyphongtro.model.Room();
                    room.setId(rs.getInt("room_id"));
                    room.setCode(rs.getString("code"));
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            logger.error("getAvailableRooms failed for managerId={}", managerId, e);
        }
        return rooms;
    }

    public Optional<Contract> findByIdAndManagerId(int contractId, int managerId) {
        String sql = "SELECT c.*, " +
                     "r.code as r_code, r.room_fee as r_fee, " +
                     "f.address as f_address, f.electricity_price as f_elec, f.internet_fee as f_net, f.service_fee as f_svc, " +
                     "m.full_name as m_name, m.dob as m_dob, m.identity_number as m_id_num, m.phone as m_phone " +
                     "FROM dbo.contracts c " +
                     "JOIN dbo.rooms r ON c.room_id = r.room_id " +
                     "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                     "JOIN dbo.users m ON f.manager_id = m.user_id " +
                     "WHERE c.contract_id = ? AND f.manager_id = ? AND c.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = mapRow(rs);
                    
                    com.quanlyphongtro.model.Room room = new com.quanlyphongtro.model.Room();
                    room.setCode(rs.getString("r_code"));
                    room.setRoomFee(rs.getBigDecimal("r_fee"));
                    contract.setRoom(room);
                    
                    com.quanlyphongtro.model.Facility facility = new com.quanlyphongtro.model.Facility();
                    facility.setAddress(rs.getString("f_address"));
                    facility.setElectricityPrice(rs.getBigDecimal("f_elec"));
                    facility.setInternetFee(rs.getBigDecimal("f_net"));
                    facility.setServiceFee(rs.getBigDecimal("f_svc"));
                    contract.setFacility(facility);
                    
                    com.quanlyphongtro.model.User managerObj = new com.quanlyphongtro.model.User();
                    managerObj.setFullName(rs.getString("m_name"));
                    managerObj.setDob(toLocalDate(rs, "m_dob"));
                    managerObj.setIdentityNumber(rs.getString("m_id_num"));
                    managerObj.setPhone(rs.getString("m_phone"));
                    contract.setManager(managerObj);
                    
                    return Optional.of(contract);
                }
            }
        } catch (Exception e) {
            logger.error("findByIdAndManagerId failed for contractId={}, managerId={}", contractId, managerId, e);
        }
        return Optional.empty();
    }

    public Optional<Contract> findActiveContractByRoomId(int roomId) {
        String sql = "SELECT * FROM dbo.contracts WHERE room_id = ? AND status = 'ACTIVE' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findActiveContractByRoomId failed for roomId={}", roomId, e);
        }
        return Optional.empty();
    }

    public int create(Contract contract) {
        // Tự động sửa lỗi Database NOT NULL cho tenant_id để cứu sinh viên
        try (Connection conn = DatabaseUtil.getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE dbo.contracts ALTER COLUMN tenant_id INT NULL;");
        } catch (Exception ignored) {
            // Lỗi do không có quyền hoặc đã alter rồi thì bỏ qua
        }

        String sql = "INSERT INTO dbo.contracts (code, room_id, tenant_id, tenant_full_name, tenant_dob, " +
                     "tenant_permanent_address, tenant_identity_number, tenant_identity_issue_date, " +
                     "tenant_identity_issue_place, tenant_phone, amount_in_words, signed_date, start_date, " +
                     "end_date, status, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, contract.getCode());
            ps.setInt(2, contract.getRoomId());
            if (contract.getTenantId() != null) {
                ps.setInt(3, contract.getTenantId());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setString(4, contract.getTenantFullName());
            if (contract.getTenantDob() != null) {
                ps.setDate(5, java.sql.Date.valueOf(contract.getTenantDob()));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, contract.getTenantPermanentAddress());
            ps.setString(7, contract.getTenantIdentityNumber());
            if (contract.getTenantIdentityIssueDate() != null) {
                ps.setDate(8, java.sql.Date.valueOf(contract.getTenantIdentityIssueDate()));
            } else {
                ps.setNull(8, java.sql.Types.DATE);
            }
            ps.setString(9, contract.getTenantIdentityIssuePlace());
            ps.setString(10, contract.getTenantPhone());
            ps.setString(11, contract.getAmountInWords());
            if (contract.getSignedDate() != null) {
                ps.setDate(12, java.sql.Date.valueOf(contract.getSignedDate()));
            } else {
                ps.setNull(12, java.sql.Types.DATE);
            }
            if (contract.getStartDate() != null) {
                ps.setDate(13, java.sql.Date.valueOf(contract.getStartDate()));
            } else {
                ps.setNull(13, java.sql.Types.DATE);
            }
            if (contract.getEndDate() != null) {
                ps.setDate(14, java.sql.Date.valueOf(contract.getEndDate()));
            } else {
                ps.setNull(14, java.sql.Types.DATE);
            }
            ps.setString(15, contract.getStatus());
            if (contract.getCreatedBy() != null) {
                ps.setInt(16, contract.getCreatedBy());
            } else {
                ps.setNull(16, java.sql.Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("create failed", e);
        }
        return -1;
    }

    public List<Contract> findAllByTenantId(int tenantId) {
        StringBuilder sql = new StringBuilder("SELECT c.* FROM dbo.contracts c " +
                     "WHERE c.tenant_id = ? AND c.deleted_at IS NULL " +
                     "ORDER BY c.created_at DESC");
        List<Contract> contracts = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    contracts.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findAllByTenantId failed for tenantId={}", tenantId, e);
        }
        return contracts;
    }

    public Optional<Contract> findByIdAndTenantId(int contractId, int tenantId) {
        String sql = "SELECT c.*, " +
                     "r.code as r_code, r.room_fee as r_fee, r.deposit_amount as r_deposit, " +
                     "f.address as f_address, f.electricity_price as f_elec, f.water_price as f_water, f.internet_fee as f_net, f.service_fee as f_svc, " +
                     "m.full_name as m_name, m.dob as m_dob, m.identity_number as m_id_num, m.phone as m_phone " +
                     "FROM dbo.contracts c " +
                     "JOIN dbo.rooms r ON c.room_id = r.room_id " +
                     "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                     "LEFT JOIN dbo.users m ON f.manager_id = m.user_id " +
                     "WHERE c.contract_id = ? AND c.tenant_id = ? AND c.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = mapRow(rs);
                    
                    com.quanlyphongtro.model.Room room = new com.quanlyphongtro.model.Room();
                    room.setCode(rs.getString("r_code"));
                    room.setRoomFee(rs.getBigDecimal("r_fee"));
                    room.setDepositAmount(rs.getBigDecimal("r_deposit"));
                    contract.setRoom(room);
                    
                    com.quanlyphongtro.model.Facility facility = new com.quanlyphongtro.model.Facility();
                    facility.setAddress(rs.getString("f_address"));
                    facility.setElectricityPrice(rs.getBigDecimal("f_elec"));
                    facility.setWaterPrice(rs.getBigDecimal("f_water"));
                    facility.setInternetFee(rs.getBigDecimal("f_net"));
                    facility.setServiceFee(rs.getBigDecimal("f_svc"));
                    contract.setFacility(facility);
                    
                    com.quanlyphongtro.model.User managerObj = new com.quanlyphongtro.model.User();
                    managerObj.setFullName(rs.getString("m_name"));
                    managerObj.setDob(toLocalDate(rs, "m_dob"));
                    managerObj.setIdentityNumber(rs.getString("m_id_num"));
                    managerObj.setPhone(rs.getString("m_phone"));
                    contract.setManager(managerObj);
                    
                    return Optional.of(contract);
                }
            }
        } catch (Exception e) {
            logger.error("findByIdAndTenantId failed for contractId={}, tenantId={}", contractId, tenantId, e);
        }
        return Optional.empty();
    }

    public Map<String, Object> getContractForAddTenant(int contractId, int managerId) {
        Map<String, Object> prefilledContract = null;
        String sql = "SELECT c.*, r.code AS room_code FROM dbo.contracts c " +
                "JOIN dbo.rooms r ON c.room_id = r.room_id " +
                "WHERE c.contract_id = ? AND c.created_by = ? AND c.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    prefilledContract = new HashMap<>();
                    prefilledContract.put("contractId", rs.getInt("contract_id"));
                    prefilledContract.put("roomId", rs.getInt("room_id"));
                    prefilledContract.put("roomCode", rs.getString("room_code"));
                    prefilledContract.put("tenantFullName", rs.getString("tenant_full_name"));
                    prefilledContract.put("tenantPhone", rs.getString("tenant_phone"));
                    prefilledContract.put("tenantIdentityNumber", rs.getString("tenant_identity_number"));
                    prefilledContract.put("tenantPermanentAddress", rs.getString("tenant_permanent_address"));
                    prefilledContract.put("tenantId", getInteger(rs, "tenant_id"));
                    java.sql.Date dob = rs.getDate("tenant_dob");
                    prefilledContract.put("tenantDob", dob != null ? dob.toString() : "");
                    java.sql.Date sDate = rs.getDate("start_date");
                    prefilledContract.put("startDate", sDate != null ? sDate.toString() : "");
                }
            }
        } catch (Exception e) {
            logger.error("getContractForAddTenant failed", e);
        }
        return prefilledContract;
    }

    public Integer getUserIdByUsername(String username) {
        String sql = "SELECT user_id FROM dbo.users WHERE username = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        } catch (Exception e) {
            logger.error("getUserIdByUsername failed", e);
        }
        return null;
    }

    public Map<String, Object> getUserRoleAndIdentityByUsername(String username) {
        Map<String, Object> user = null;
        String sql = "SELECT user_id, role, full_name, identity_number FROM dbo.users WHERE username = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    user = new HashMap<>();
                    user.put("id", rs.getInt("user_id"));
                    user.put("role", rs.getString("role"));
                    user.put("fullName", rs.getString("full_name"));
                    user.put("identityNumber", rs.getString("identity_number"));
                }
            }
        } catch (Exception e) {
            logger.error("getUserRoleAndIdentityByUsername failed", e);
        }
        return user;
    }

    public int countActiveChecksForUser(int userId) {
        String activeCheckSql = "SELECT COUNT(*) FROM (" +
                "SELECT contract_id FROM dbo.contracts WHERE tenant_id = ? AND status = 'ACTIVE' AND deleted_at IS NULL " +
                "UNION ALL " +
                "SELECT room_id FROM dbo.rooms WHERE tenant_id = ? AND deleted_at IS NULL" +
                ") active_checks";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(activeCheckSql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.error("countActiveChecksForUser failed", e);
        }
        return 0;
    }

    public boolean addTenantTransaction(boolean userExists, int userId, String passwordHash, String fullName, String phone, String identityNumber, LocalDate dob, String gender, String permanentAddress, String email, int roomId, LocalDate startDate, int contractId) {
        String updUserSql = "UPDATE dbo.users SET status = 'ACTIVE', password_hash = ?, full_name = ?, phone = ?, " +
                "identity_number = ?, dob = ?, gender = ?, permanent_address = ?, force_change_pass = 1, updated_at = GETDATE() " +
                "WHERE user_id = ?";
        String insUserSql = "INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, identity_number, dob, gender, permanent_address, force_change_pass, created_at, updated_at) " +
                "VALUES (?, ?, 'TENANT', ?, ?, ?, 'ACTIVE', ?, ?, ?, ?, 1, GETDATE(), GETDATE())";
        String updRoomSql = "UPDATE dbo.rooms SET tenant_id = ?, status = 'OCCUPIED', contract_start_date = ?, contract_end_date = ?, updated_at = GETDATE() WHERE room_id = ?";
        String updContractSql = "UPDATE dbo.contracts SET tenant_id = ?, updated_at = GETDATE() WHERE contract_id = ?";

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            int finalUserId = userId;
            if (userExists) {
                try (PreparedStatement ps = conn.prepareStatement(updUserSql)) {
                    ps.setString(1, passwordHash);
                    ps.setString(2, fullName.trim());
                    ps.setString(3, phone.trim());
                    ps.setString(4, identityNumber.trim());
                    ps.setDate(5, dob != null ? java.sql.Date.valueOf(dob) : null);
                    ps.setString(6, gender);
                    ps.setString(7, permanentAddress);
                    ps.setInt(8, userId);
                    ps.executeUpdate();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(insUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, email.trim());
                    ps.setString(2, passwordHash);
                    ps.setString(3, fullName.trim());
                    ps.setString(4, email.trim());
                    ps.setString(5, phone.trim());
                    ps.setString(6, identityNumber.trim());
                    ps.setDate(7, dob != null ? java.sql.Date.valueOf(dob) : null);
                    ps.setString(8, gender);
                    ps.setString(9, permanentAddress);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            finalUserId = rs.getInt(1);
                        }
                    }
                }
            }

            // Update room
            try (PreparedStatement ps = conn.prepareStatement(updRoomSql)) {
                ps.setInt(1, finalUserId);
                ps.setDate(2, java.sql.Date.valueOf(startDate));
                ps.setDate(3, java.sql.Date.valueOf(startDate.plusYears(1))); // Default 1 year contract
                ps.setInt(4, roomId);
                ps.executeUpdate();
            }

            // Update contract
            try (PreparedStatement ps = conn.prepareStatement(updContractSql)) {
                ps.setInt(1, finalUserId);
                ps.setInt(2, contractId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("addTenantTransaction failed", e);
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (Exception ignored) {}
            }
        }
    }

    public Map<String, String> verifyContractForDelete(int contractId, int managerId) {
        String checkSql = "SELECT status, code FROM dbo.contracts WHERE contract_id = ? AND created_by = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, contractId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, String> res = new HashMap<>();
                    res.put("status", rs.getString("status"));
                    res.put("code", rs.getString("code"));
                    return res;
                }
            }
        } catch (Exception e) {
            logger.error("verifyContractForDelete failed", e);
        }
        return null;
    }

    public boolean softDeleteContract(int contractId) {
        String deleteSql = "UPDATE dbo.contracts SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE contract_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(deleteSql)) {
            ps.setInt(1, contractId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("softDeleteContract failed", e);
            return false;
        }
    }
}
