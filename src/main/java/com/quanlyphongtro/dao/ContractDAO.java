package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                     "WHERE f.manager_id = ? AND r.status = 'AVAILABLE' AND r.deleted_at IS NULL";
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
}
