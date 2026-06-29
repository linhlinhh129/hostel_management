package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.dao.ContractDAO;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.service.ContractService;
import com.quanlyphongtro.service.impl.ContractServiceImpl;
import com.quanlyphongtro.util.DatabaseUtil;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.util.AuditLogHelper;
import com.quanlyphongtro.util.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(urlPatterns = {
        "/manager/contracts",
        "/manager/contracts/create",
        "/manager/contracts/detail",
        "/manager/contracts/add-tenant",
        "/manager/contracts/delete"
})
public class ContractServlet extends BaseServlet {

    private final ContractService contractService = new ContractServiceImpl();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final PersonnelDAO personnelDAO = new PersonnelDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        try {
            UserSessionDTO user = getCurrentUser(req);
            if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }

            if ("/manager/contracts/create".equals(path)) {
                showCreateForm(req, resp, user.getId());
            } else if ("/manager/contracts/detail".equals(path)) {
                showDetail(req, resp, user.getId());
            } else if ("/manager/contracts/add-tenant".equals(path)) {
                showAddTenantForm(req, resp, user.getId());
            } else {
                showList(req, resp, user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp, int managerId)
            throws ServletException, IOException {
        String searchName = req.getParameter("searchName");
        List<Contract> contracts = contractService.getContractsByManager(managerId, searchName);
        req.setAttribute("contracts", contracts);
        req.setAttribute("searchName", searchName);
        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/list.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp, int managerId)
            throws ServletException, IOException {
        ContractDAO contractDAO = new ContractDAO();
        req.setAttribute("availableRooms", contractDAO.getAvailableRooms(managerId));
        String roomIdParam = req.getParameter("roomId");
        if (roomIdParam != null && !roomIdParam.trim().isEmpty()) {
            req.setAttribute("preselectedRoomId", roomIdParam.trim());
        }
        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/create.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int managerId)
            throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Contract contract = contractService.getContractDetail(id, managerId);
            if (contract == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hợp đồng");
                return;
            }
            req.setAttribute("contract", contract);
            req.getRequestDispatcher("/WEB-INF/views/manager/contracts/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hợp đồng không hợp lệ");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/manager/contracts/create".equals(path)) {
            UserSessionDTO user = getCurrentUser(req);
            try {
                if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }

                Contract contract = new Contract();
                contract.setRoomId(Integer.parseInt(req.getParameter("roomId")));

                String tenantIdStr = req.getParameter("tenantId");
                if (tenantIdStr != null && !tenantIdStr.trim().isEmpty()) {
                    contract.setTenantId(Integer.parseInt(tenantIdStr));
                }

                contract.setTenantFullName(req.getParameter("tenantFullName"));
                String dobStr = req.getParameter("tenantDob");
                if (dobStr != null && !dobStr.trim().isEmpty()) {
                    contract.setTenantDob(LocalDate.parse(dobStr));
                }
                contract.setTenantPermanentAddress(req.getParameter("tenantPermanentAddress"));
                contract.setTenantIdentityNumber(req.getParameter("tenantIdentityNumber"));
                String issueDateStr = req.getParameter("tenantIdentityIssueDate");
                if (issueDateStr != null && !issueDateStr.trim().isEmpty()) {
                    contract.setTenantIdentityIssueDate(LocalDate.parse(issueDateStr));
                }
                contract.setTenantIdentityIssuePlace(req.getParameter("tenantIdentityIssuePlace"));
                contract.setTenantPhone(req.getParameter("tenantPhone"));
                contract.setAmountInWords(req.getParameter("amountInWords"));
                contract.setSignedDate(LocalDate.parse(req.getParameter("signedDate")));
                contract.setStartDate(LocalDate.parse(req.getParameter("startDate")));
                contract.setEndDate(LocalDate.parse(req.getParameter("endDate")));

                if (!com.quanlyphongtro.util.ValidationUtil.isValidVnPhone(contract.getTenantPhone())) {
                    throw new IllegalArgumentException(
                            "Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
                }
                if (!com.quanlyphongtro.util.ValidationUtil.isValidVnIdentity(contract.getTenantIdentityNumber())) {
                    throw new IllegalArgumentException("Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).");
                }

                contractService.createContract(contract, user.getId());

                // Redirect to the detail page of the newly created contract
                resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contract.getContractId());
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                ContractDAO contractDAO = new ContractDAO();
                req.setAttribute("availableRooms", contractDAO.getAvailableRooms(user.getId()));
                req.getRequestDispatcher("/WEB-INF/views/manager/contracts/create.jsp").forward(req, resp);
            }
        } else if ("/manager/contracts/add-tenant".equals(path)) {
            try {
                UserSessionDTO user = getCurrentUser(req);
                if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
                handleAddTenantSubmit(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
            }
        } else if ("/manager/contracts/delete".equals(path)) {
            try {
                UserSessionDTO user = getCurrentUser(req);
                if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }
                handleDeleteContract(req, resp);
            } catch (Exception e) {
                e.printStackTrace();
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void showAddTenantForm(HttpServletRequest req, HttpServletResponse resp, int managerId)
            throws ServletException, IOException {
        String contractIdStr = req.getParameter("contractId");
        if (contractIdStr == null || contractIdStr.trim().isEmpty()) {
            setFlashMessage(req, "error", "Yêu cầu cung cấp hợp đồng để tạo tài khoản người thuê.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
            return;
        }

        Map<String, Object> prefilledContract = null;
        try {
            int contractId = Integer.parseInt(contractIdStr.trim());
            String sql = "SELECT c.*, r.code AS room_code FROM dbo.contracts c " +
                    "JOIN dbo.rooms r ON c.room_id = r.room_id " +
                    "WHERE c.contract_id = ? AND c.created_by = ? AND c.deleted_at IS NULL";
            try (Connection conn = DatabaseUtil.getConnection();
                    PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, contractId);
                ps.setInt(2, managerId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int tenantId = rs.getInt("tenant_id");
                        if (!rs.wasNull() && tenantId > 0) {
                            setFlashMessage(req, "error", "Hợp đồng này đã có tài khoản người thuê liên kết.");
                            resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contractId);
                            return;
                        }
                        prefilledContract = new HashMap<>();
                        prefilledContract.put("contractId", rs.getInt("contract_id"));
                        prefilledContract.put("roomId", rs.getInt("room_id"));
                        prefilledContract.put("roomCode", rs.getString("room_code"));
                        prefilledContract.put("tenantFullName", rs.getString("tenant_full_name"));
                        prefilledContract.put("tenantPhone", rs.getString("tenant_phone"));
                        prefilledContract.put("tenantIdentityNumber", rs.getString("tenant_identity_number"));
                        prefilledContract.put("tenantPermanentAddress", rs.getString("tenant_permanent_address"));
                        Date dob = rs.getDate("tenant_dob");
                        prefilledContract.put("tenantDob", dob != null ? dob.toString() : "");
                        Date sDate = rs.getDate("start_date");
                        prefilledContract.put("startDate", sDate != null ? sDate.toString() : "");
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load prefilled contract details for contractId={}", contractIdStr, e);
        }

        if (prefilledContract == null) {
            setFlashMessage(req, "error", "Không tìm thấy hợp đồng hợp lệ để tạo tài khoản người thuê.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
            return;
        }

        req.setAttribute("prefilledContract", prefilledContract);
        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/add_tenant.jsp").forward(req, resp);
    }

    private void handleAddTenantSubmit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullName = req.getParameter("fullName");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String identityNumber = req.getParameter("identityNumber");
        String permanentAddress = req.getParameter("permanentAddress");
        String gender = req.getParameter("gender");
        String dobStr = req.getParameter("dob");
        String roomIdStr = req.getParameter("roomId");
        String contractStartDateStr = req.getParameter("contractStartDate");
        String contractIdStr = req.getParameter("contractId");

        Map<String, Object> dto = new HashMap<>();
        dto.put("fullName", fullName);
        dto.put("phone", phone);
        dto.put("email", email);
        dto.put("identityNumber", identityNumber);
        dto.put("permanentAddress", permanentAddress);
        dto.put("gender", gender);
        dto.put("dob", dobStr);
        req.setAttribute("dto", dto);

        // ── Validation: kiểm tra rỗng từng trường (đồng bộ với admin/personnel)
        if (fullName == null || fullName.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Họ tên không được để trống.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (email == null || email.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Email không được để trống.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (!email.trim().matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")) {
            req.setAttribute("errorMessage", "Email không đúng định dạng.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (phone == null || phone.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Số điện thoại không được để trống.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (!com.quanlyphongtro.util.ValidationUtil.isValidVnPhone(phone.trim())) {
            req.setAttribute("errorMessage",
                    "Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (identityNumber == null || identityNumber.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Số CMND/CCCD không được để trống.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (!com.quanlyphongtro.util.ValidationUtil.isValidVnIdentity(identityNumber.trim())) {
            req.setAttribute("errorMessage", "Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (roomIdStr == null || roomIdStr.isEmpty() || contractIdStr == null || contractIdStr.isEmpty()) {
            req.setAttribute("errorMessage", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }

        // ── Kiểm tra trùng lặp (phải loại trừ user đị reactivate để không chặn chính họ)
        String username = email.trim(); // Username is the email

        // Tìm user hiện có với email này (để lấy excludeId khi check uniqueness)
        Integer existingUserId = null;
        try (Connection connCheck = DatabaseUtil.getConnection();
             PreparedStatement psCheck = connCheck.prepareStatement(
                 "SELECT user_id FROM dbo.users WHERE username = ? AND deleted_at IS NULL")) {
            psCheck.setString(1, username);
            try (ResultSet rs = psCheck.executeQuery()) {
                if (rs.next()) existingUserId = rs.getInt("user_id");
            }
        } catch (Exception ignored) {}

        if (personnelDAO.existsByPhone(phone.trim(), existingUserId)) {
            req.setAttribute("errorMessage", "Số điện thoại '" + phone.trim() + "' đã được sử dụng bởi tài khoản khác.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }
        if (personnelDAO.existsByIdentityNumber(identityNumber.trim(), existingUserId)) {
            req.setAttribute("errorMessage", "Số CMND/CCCD '" + identityNumber.trim() + "' đã được sử dụng bởi tài khoản khác.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }

        String plainPassword = PasswordUtil.generateTempPassword();
        String passwordHash = PasswordUtil.hash(plainPassword);

        int roomId = Integer.parseInt(roomIdStr);
        LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr) : null;
        LocalDate startDate = (contractStartDateStr != null && !contractStartDateStr.isEmpty())
                ? LocalDate.parse(contractStartDateStr)
                : LocalDate.now();

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            String confirmReactivate = req.getParameter("confirmReactivate");

            int newUserId = 0;
            boolean userExists = false;
            String checkSql = "SELECT user_id, role, full_name, identity_number FROM dbo.users WHERE username = ? AND deleted_at IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int foundUserId = rs.getInt("user_id");
                        String existingRole = rs.getString("role");
                        
                        if (!"TENANT".equals(existingRole)) {
                            req.setAttribute("errorMessage", "Email/Tên đăng nhập đã tồn tại trong hệ thống với vai trò khác.");
                            conn.rollback();
                            showAddTenantForm(req, resp, currentUser.getId());
                            return;
                        }

                        String activeCheckSql = "SELECT COUNT(*) FROM (" +
                                "SELECT contract_id FROM dbo.contracts WHERE tenant_id = ? AND status = 'ACTIVE' AND deleted_at IS NULL " +
                                "UNION ALL " +
                                "SELECT room_id FROM dbo.rooms WHERE tenant_id = ? AND deleted_at IS NULL" +
                                ") active_checks";
                        try (PreparedStatement psCheck2 = conn.prepareStatement(activeCheckSql)) {
                            psCheck2.setInt(1, foundUserId);
                            psCheck2.setInt(2, foundUserId);
                            try (ResultSet rsCheck = psCheck2.executeQuery()) {
                                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                                    req.setAttribute("errorMessage", "Email/Tên đăng nhập đã tồn tại trong hệ thống và đang hoạt động ở phòng/cơ sở khác.");
                                    conn.rollback();
                                    showAddTenantForm(req, resp, currentUser.getId());
                                    return;
                                }
                            }
                        }

                        if (!"true".equals(confirmReactivate)) {
                            req.setAttribute("showReactivateConfirmation", true);
                            req.setAttribute("existingUserFullName", rs.getString("full_name"));
                            req.setAttribute("existingUserIdentity", rs.getString("identity_number"));
                            
                            conn.rollback();
                            showAddTenantForm(req, resp, currentUser.getId());
                            return;
                        }

                        newUserId = foundUserId;
                        userExists = true;
                    }
                }
            }

            if (userExists) {
                String updUserSql = "UPDATE dbo.users SET status = 'ACTIVE', password_hash = ?, full_name = ?, phone = ?, " +
                        "identity_number = ?, dob = ?, gender = ?, permanent_address = ?, force_change_pass = 1, updated_at = GETDATE() " +
                        "WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updUserSql)) {
                    ps.setString(1, passwordHash);
                    ps.setString(2, fullName.trim());
                    ps.setString(3, phone.trim());
                    ps.setString(4, identityNumber.trim());
                    ps.setDate(5, dob != null ? Date.valueOf(dob) : null);
                    ps.setString(6, gender);
                    ps.setString(7, permanentAddress);
                    ps.setInt(8, newUserId);
                    ps.executeUpdate();
                }
            } else {
                String insUserSql = "INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, identity_number, dob, gender, permanent_address, force_change_pass, created_at, updated_at) " +
                        "VALUES (?, ?, 'TENANT', ?, ?, ?, 'ACTIVE', ?, ?, ?, ?, 1, GETDATE(), GETDATE())";
                try (PreparedStatement ps = conn.prepareStatement(insUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, passwordHash);
                    ps.setString(3, fullName.trim());
                    ps.setString(4, email.trim());
                    ps.setString(5, phone.trim());
                    ps.setString(6, identityNumber.trim());
                    ps.setDate(7, dob != null ? Date.valueOf(dob) : null);
                    ps.setString(8, gender);
                    ps.setString(9, permanentAddress);
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            newUserId = rs.getInt(1);
                        }
                    }
                }
            }

            // Update room
            String updRoomSql = "UPDATE dbo.rooms SET tenant_id = ?, status = 'OCCUPIED', contract_start_date = ?, contract_end_date = ?, updated_at = GETDATE() WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updRoomSql)) {
                ps.setInt(1, newUserId);
                ps.setDate(2, Date.valueOf(startDate));
                ps.setDate(3, Date.valueOf(startDate.plusYears(1))); // Default 1 year contract
                ps.setInt(4, roomId);
                ps.executeUpdate();
            }

            // Update contract
            String updContractSql = "UPDATE dbo.contracts SET tenant_id = ?, updated_at = GETDATE() WHERE contract_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updContractSql)) {
                ps.setInt(1, newUserId);
                ps.setInt(2, Integer.parseInt(contractIdStr.trim()));
                ps.executeUpdate();
            }

            conn.commit();

            // Send email asynchronously
            EmailService.sendTempPassword(email.trim(), fullName.trim(), username, plainPassword);

            try {
                AuditLogHelper.log(auditLogDAO, req, "users", newUserId, userExists ? "REACTIVATE" : "CREATE", null, username, currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after tenant create", ex);
            }

            if (userExists) {
                setFlashMessage(req, "success", "Kích hoạt lại tài khoản người thuê cũ thành công! Đã gửi mật khẩu tạm thời mới vào email " + email.trim() + ".");
            } else {
                setFlashMessage(req, "success", "Tạo tài khoản người thuê thành công! Đã gửi thông tin tài khoản và mật khẩu tạm thời vào email " + email.trim() + ".");
            }
            resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contractIdStr.trim());

        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }
            logger.error("Failed to create tenant from contract", e);
            req.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            showAddTenantForm(req, resp, currentUser.getId());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ignored) {
                }
            }
        }
    }

    private void handleDeleteContract(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String idStr = req.getParameter("id");
        if (idStr == null || idStr.trim().isEmpty()) {
            setFlashMessage(req, "error", "Yêu cầu ID hợp đồng để xóa.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
            return;
        }

        try {
            int contractId = Integer.parseInt(idStr.trim());

            // Verify contract exists, is inactive, and created by this manager
            String checkSql = "SELECT status, code FROM dbo.contracts WHERE contract_id = ? AND created_by = ? AND deleted_at IS NULL";
            String status = null;
            String code = null;
            try (Connection conn = DatabaseUtil.getConnection();
                    PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setInt(1, contractId);
                ps.setInt(2, currentUser.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        status = rs.getString("status");
                        code = rs.getString("code");
                    }
                }
            }

            if (status == null) {
                setFlashMessage(req, "error", "Hợp đồng không tồn tại hoặc bạn không có quyền xóa.");
                resp.sendRedirect(req.getContextPath() + "/manager/contracts");
                return;
            }

            if (!"INACTIVE".equals(status)) {
                setFlashMessage(req, "error", "Chỉ được xóa hợp đồng khi trạng thái là INACTIVE.");
                resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contractId);
                return;
            }

            // Perform soft delete
            String deleteSql = "UPDATE dbo.contracts SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE contract_id = ?";
            try (Connection conn = DatabaseUtil.getConnection();
                    PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                ps.setInt(1, contractId);
                ps.executeUpdate();
            }

            try {
                AuditLogHelper.log(auditLogDAO, req, "contracts", contractId, "DELETE", null,
                        "Soft Delete contract: " + code, currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after contract delete", ex);
            }

            setFlashMessage(req, "success", "Xóa hợp đồng " + code + " thành công!");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");

        } catch (NumberFormatException e) {
            setFlashMessage(req, "error", "ID hợp đồng không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
        } catch (Exception e) {
            logger.error("Failed to delete contract", e);
            setFlashMessage(req, "error", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
        }
    }
}
