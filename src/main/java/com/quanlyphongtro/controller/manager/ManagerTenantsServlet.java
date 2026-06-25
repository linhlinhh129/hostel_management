package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.DatabaseUtil;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.util.AuditLogHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerTenantsServlet", urlPatterns = {
        "/manager/tenants",
        "/manager/tenants/*",
        "/manager/dependents",
        "/manager/dependents/*"
})
public class ManagerTenantsServlet extends BaseServlet {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if (servletPath.startsWith("/manager/dependents")) {
            if (pathInfo == null || "/".equals(pathInfo)) {
                handleDependentList(req, resp);
            } else {
                String[] parts = pathInfo.split("/");
                if (parts.length == 2) {
                    try {
                        int dependentId = Integer.parseInt(parts[1]);
                        handleDependentDetail(dependentId, req, resp);
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            }
            return;
        }

        if (pathInfo == null || "/".equals(pathInfo)) {
            handleList(req, resp);
        } else if ("/create".equals(pathInfo)) {
            handleCreateForm(req, resp);
        } else {
            // detail: /manager/tenants/{id}
            String[] parts = pathInfo.split("/");
            if (parts.length == 2) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleDetail(tenantId, req, resp);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if (servletPath.startsWith("/manager/dependents")) {
            if (pathInfo != null) {
                String[] parts = pathInfo.split("/");
                if (parts.length == 3 && "remove".equals(parts[2])) {
                    try {
                        int dependentId = Integer.parseInt(parts[1]);
                        handleRemoveDependent(dependentId, req, resp);
                        return;
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                } else if (parts.length == 3 && "edit".equals(parts[2])) {
                    try {
                        int dependentId = Integer.parseInt(parts[1]);
                        handleEditDependentSubmit(dependentId, req, resp);
                        return;
                    } catch (NumberFormatException e) {
                        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                }
            }
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        if (pathInfo != null) {
            String[] parts = pathInfo.split("/");
            if ("/create".equals(pathInfo)) {
                handleCreateSubmit(req, resp);
                return;
            } else if (parts.length == 3 && "end-rental".equals(parts[2])) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleEndRental(tenantId, req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else if (parts.length == 3 && "edit".equals(parts[2])) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleEditTenantSubmit(tenantId, req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else if (parts.length == 3 && "delete".equals(parts[2])) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleSoftDelete(tenantId, req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else if (parts.length == 3 && "lock".equals(parts[2])) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleLockAccount(tenantId, req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else if (parts.length == 3 && "unlock".equals(parts[2])) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleUnlockAccount(tenantId, req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            } else if (parts.length == 4 && "dependents".equals(parts[2]) && "add".equals(parts[3])) {
                try {
                    int tenantId = Integer.parseInt(parts[1]);
                    handleAddDependent(tenantId, req, resp);
                    return;
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
        }
        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("keyword");
        String status = req.getParameter("status");
        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (Exception e) {
                page = 1;
            }
        }
        int pageSize = 10;

        List<Map<String, Object>> tenants = new ArrayList<>();
        int totalCount = 0;

        // Build filtering query — chỉ lấy tenant có phòng trong cơ sở manager phụ trách
        StringBuilder whereClause = new StringBuilder(
            " WHERE u.role = 'TENANT' AND u.deleted_at IS NULL" +
            " AND r.facility_id IN (SELECT facility_id FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL)");
        List<Object> params = new ArrayList<>();
        params.add(currentUser.getId());

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

        String countSql = "SELECT COUNT(DISTINCT u.user_id) FROM dbo.users u" +
            " INNER JOIN dbo.rooms r ON r.tenant_id = u.user_id AND r.deleted_at IS NULL" +
            whereClause.toString();
        String selectSql = "SELECT u.user_id, u.username, u.full_name, u.email, u.phone, u.status," +
            " r.room_id, r.code AS room_code, r.contract_start_date" +
            " FROM dbo.users u" +
            " INNER JOIN dbo.rooms r ON r.tenant_id = u.user_id AND r.deleted_at IS NULL" +
            whereClause.toString() +
            " ORDER BY u.user_id DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Count query
            try (PreparedStatement ps = conn.prepareStatement(countSql)) {
                for (int i = 0; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        totalCount = rs.getInt(1);
                    }
                }
            }

            // Items query
            try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
                int i = 0;
                for (; i < params.size(); i++) {
                    ps.setObject(i + 1, params.get(i));
                }
                ps.setInt(i + 1, (page - 1) * pageSize);
                ps.setInt(i + 2, pageSize);

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
                        Date sDate = rs.getDate("contract_start_date");
                        tenant.put("contractStartDate", sDate != null ? sDate.toString() : null);
                        tenant.put("status", rs.getString("status"));
                        tenants.add(tenant);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query tenants", e);
        }

        int totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", tenants);
        pageObj.put("total", totalCount);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedStatus", status);

        req.getRequestDispatcher("/WEB-INF/views/manager/tenants/list.jsp").forward(req, resp);
    }

    private void handleCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Map<String, Object>> assignedFacilities = new ArrayList<>();
        Map<Integer, List<Map<String, Object>>> roomsByFacility = new HashMap<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Query facilities managed by current manager
            String facSql = "SELECT facility_id, code, name FROM dbo.facilities WHERE manager_id = ? AND deleted_at IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(facSql)) {
                ps.setInt(1, currentUser.getId());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Map<String, Object> f = new HashMap<>();
                        int fId = rs.getInt("facility_id");
                        f.put("id", fId);
                        f.put("code", rs.getString("code"));
                        f.put("name", rs.getString("name"));
                        assignedFacilities.add(f);
                        roomsByFacility.put(fId, new ArrayList<>());
                    }
                }
            }

            // Query AVAILABLE rooms under these facilities
            String roomSql = "SELECT room_id, facility_id, code, area FROM dbo.rooms WHERE tenant_id IS NULL AND deleted_at IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(roomSql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int fId = rs.getInt("facility_id");
                    if (roomsByFacility.containsKey(fId)) {
                        Map<String, Object> r = new HashMap<>();
                        r.put("id", rs.getInt("room_id"));
                        String roomCode = rs.getString("code");
                        r.put("code", roomCode);
                        r.put("area", rs.getDouble("area"));

                        // Parse floor
                        String floorStr = "—";
                        if (roomCode != null && roomCode.length() >= 4) {
                            String last4 = roomCode.substring(roomCode.length() - 4);
                            if (last4.matches("\\d+")) {
                                floorStr = String.valueOf(Integer.parseInt(last4.substring(0, 2)));
                            }
                        }
                        r.put("floor", floorStr);

                        roomsByFacility.get(fId).add(r);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load facilities/rooms for create", e);
        }

        req.setAttribute("assignedFacilities", assignedFacilities);
        req.setAttribute("roomsByFacility", roomsByFacility);
        req.getRequestDispatcher("/WEB-INF/views/manager/tenants/create.jsp").forward(req, resp);
    }

    private void handleCreateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        if (fullName == null || phone == null || email == null || identityNumber == null || roomIdStr == null || roomIdStr.isEmpty()) {
            req.setAttribute("errorMessage", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            handleCreateForm(req, resp);
            return;
        }

        String username = email.trim(); // Username will be the email

        // Generate temporary password automatically using PasswordUtil
        String plainPassword = PasswordUtil.generateTempPassword();
        String passwordHash = PasswordUtil.hash(plainPassword);

        int roomId = Integer.parseInt(roomIdStr);
        LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr) : null;
        LocalDate startDate = (contractStartDateStr != null && !contractStartDateStr.isEmpty()) ? LocalDate.parse(contractStartDateStr) : LocalDate.now();

        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Check if username already exists
            String checkSql = "SELECT user_id FROM dbo.users WHERE username = ? AND deleted_at IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        req.setAttribute("errorMessage", "Email/Tên đăng nhập đã tồn tại trong hệ thống.");
                        conn.rollback();
                        handleCreateForm(req, resp);
                        return;
                    }
                }
            }

            // Insert user
            String insUserSql = "INSERT INTO dbo.users (username, password_hash, role, full_name, email, phone, status, identity_number, dob, gender, permanent_address, force_change_pass, created_at, updated_at) " +
                    "VALUES (?, ?, 'TENANT', ?, ?, ?, 'ACTIVE', ?, ?, ?, ?, 1, GETDATE(), GETDATE())";
            int newUserId = 0;
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

            // Update room
            String updRoomSql = "UPDATE dbo.rooms SET tenant_id = ?, status = 'OCCUPIED', contract_start_date = ?, contract_end_date = ?, updated_at = GETDATE() WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updRoomSql)) {
                ps.setInt(1, newUserId);
                ps.setDate(2, Date.valueOf(startDate));
                ps.setDate(3, Date.valueOf(startDate.plusYears(1))); // Default 1 year contract
                ps.setInt(4, roomId);
                ps.executeUpdate();
            }

            conn.commit();

            // Send email to tenant with credentials
            com.quanlyphongtro.util.EmailService.sendTempPassword(email.trim(), fullName.trim(), username, plainPassword);

            try {
                AuditLogHelper.log(auditLogDAO, req, "users", newUserId, "CREATE", null, username, currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after tenant create", ex);
            }

            setFlashMessage(req, "success", "Tạo người thuê và gán phòng thành công! Đã gửi tài khoản và mật khẩu tạm thời vào email " + email.trim() + ".");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants");

        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("Failed to create tenant", e);
            req.setAttribute("errorMessage", "Lỗi cơ sở dữ liệu: " + e.getMessage());
            handleCreateForm(req, resp);
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private void handleDetail(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> tenant = null;
        List<Map<String, Object>> dependents = new ArrayList<>();

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Query tenant details
            String tenantSql = "SELECT u.*, r.room_id, r.code AS room_code, r.contract_start_date FROM dbo.users u " +
                    "LEFT JOIN dbo.rooms r ON u.user_id = r.tenant_id WHERE u.user_id = ? AND u.role = 'TENANT' AND u.deleted_at IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(tenantSql)) {
                ps.setInt(1, tenantId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tenant = new HashMap<>();
                        tenant.put("id", rs.getInt("user_id"));
                        tenant.put("tenantCode", rs.getString("username"));
                        tenant.put("fullName", rs.getString("full_name"));
                        Date dob = rs.getDate("dob");
                        tenant.put("dob", dob != null ? dob.toString() : null);
                        tenant.put("gender", rs.getString("gender"));
                        tenant.put("phone", rs.getString("phone"));
                        tenant.put("email", rs.getString("email"));
                        tenant.put("identityNumber", rs.getString("identity_number"));
                        tenant.put("permanentAddress", rs.getString("permanent_address"));
                        tenant.put("status", rs.getString("status"));
                        tenant.put("roomId", rs.getInt("room_id"));
                        tenant.put("roomCode", rs.getString("room_code"));
                        Date sDate = rs.getDate("contract_start_date");
                        tenant.put("contractStartDate", sDate != null ? sDate.toString() : null);
                    }
                }
            }

            if (tenant == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            // Query dependents
            String depSql = "SELECT * FROM dbo.dependents WHERE tenant_id = ? AND deleted_at IS NULL";
            try (PreparedStatement ps = conn.prepareStatement(depSql)) {
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
            }
        } catch (Exception e) {
            logger.error("Failed to query tenant detail", e);
        }

        req.setAttribute("tenant", tenant);
        req.setAttribute("dependents", dependents);
        req.getRequestDispatcher("/WEB-INF/views/manager/tenants/detail.jsp").forward(req, resp);
    }

    private void handleAddDependent(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullName = req.getParameter("fullName");
        String relationship = req.getParameter("relationship");
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");
        String dobStr = req.getParameter("dob");

        if (fullName == null || relationship == null || fullName.trim().isEmpty() || relationship.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Họ tên và Quan hệ là bắt buộc.");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
            return;
        }

        LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? java.time.LocalDate.parse(dobStr) : null;

        String sql = "INSERT INTO dbo.dependents (tenant_id, full_name, relationship, phone, gender, dob, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            ps.setString(2, fullName.trim());
            ps.setString(3, relationship.trim());
            ps.setString(4, phone);
            ps.setString(5, gender);
            ps.setDate(6, dob != null ? Date.valueOf(dob) : null);
            ps.executeUpdate();
            
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Add Dependent", fullName.trim(), currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after add dependent", ex);
            }
            
            setFlashMessage(req, "success", "Thêm người phụ thuộc thành công!");
        } catch (Exception e) {
            logger.error("Failed to add dependent", e);
            setFlashMessage(req, "danger", "Lỗi thêm người phụ thuộc: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
    }

    private void handleRemoveDependent(int dependentId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        
        // Query tenantId first to redirect back
        int tenantId = 0;
        String findTenantSql = "SELECT tenant_id FROM dbo.dependents WHERE dependent_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(findTenantSql)) {
            ps.setInt(1, dependentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tenantId = rs.getInt("tenant_id");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to find tenant for dependent deletion", e);
        }

        if (tenantId == 0) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String sql = "UPDATE dbo.dependents SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE dependent_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dependentId);
            ps.executeUpdate();
            
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Remove Dependent", "Dependent ID " + dependentId, currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after remove dependent", ex);
            }
            
            setFlashMessage(req, "success", "Xóa người phụ thuộc thành công!");
        } catch (Exception e) {
            logger.error("Failed to remove dependent", e);
            setFlashMessage(req, "danger", "Lỗi xóa người phụ thuộc: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
    }

    private void handleEndRental(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

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

            conn.commit();
            
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "ACTIVE", "INACTIVE (End Rental)", currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after end rental", ex);
            }
            
            setFlashMessage(req, "success", "Kết thúc hợp đồng thuê và giải phóng phòng thành công!");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            logger.error("Failed to end rental for tenant={}", tenantId, e);
            setFlashMessage(req, "danger", "Lỗi kết thúc thuê: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException ignored) {}
            }
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
    }

    private void handleDependentList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("keyword");
        List<Map<String, Object>> dependents = new ArrayList<>();

        StringBuilder query = new StringBuilder(
            "SELECT d.*, u.full_name AS tenant_name, u.user_id AS tenant_id, u.username AS tenant_code " +
            "FROM dbo.dependents d " +
            "JOIN dbo.users u ON d.tenant_id = u.user_id " +
            "JOIN dbo.rooms r ON u.user_id = r.tenant_id " +
            "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "WHERE f.manager_id = ? AND d.deleted_at IS NULL AND u.deleted_at IS NULL"
        );
        List<Object> params = new ArrayList<>();
        params.add(currentUser.getId());

        if (keyword != null && !keyword.trim().isEmpty()) {
            String likeParam = "%" + keyword.trim() + "%";
            query.append(" AND (d.full_name LIKE ? OR d.relationship LIKE ? OR u.full_name LIKE ?)");
            params.add(likeParam);
            params.add(likeParam);
            params.add(likeParam);
        }
        query.append(" ORDER BY d.dependent_id DESC");

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dep = new HashMap<>();
                    dep.put("id", rs.getInt("dependent_id"));
                    dep.put("fullName", rs.getString("full_name"));
                    dep.put("relationship", rs.getString("relationship"));
                    dep.put("phone", rs.getString("phone"));
                    dep.put("gender", rs.getString("gender"));
                    Date dob = rs.getDate("dob");
                    dep.put("dob", dob != null ? dob.toString() : null);
                    dep.put("tenantId", rs.getInt("tenant_id"));
                    dep.put("tenantName", rs.getString("tenant_name"));
                    dep.put("tenantCode", rs.getString("tenant_code"));
                    dependents.add(dep);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query dependents list", e);
        }

        req.setAttribute("dependents", dependents);
        req.setAttribute("keyword", keyword);
        req.getRequestDispatcher("/WEB-INF/views/manager/dependents/list.jsp").forward(req, resp);
    }

    private void handleDependentDetail(int dependentId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> dependent = null;

        String query = 
            "SELECT d.*, u.full_name AS tenant_name, u.user_id AS tenant_id, u.username AS tenant_code " +
            "FROM dbo.dependents d " +
            "JOIN dbo.users u ON d.tenant_id = u.user_id " +
            "JOIN dbo.rooms r ON u.user_id = r.tenant_id " +
            "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "WHERE d.dependent_id = ? AND f.manager_id = ? AND d.deleted_at IS NULL AND u.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, dependentId);
            ps.setInt(2, currentUser.getId());
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
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query dependent detail", e);
        }

        if (dependent == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("dependent", dependent);
        req.getRequestDispatcher("/WEB-INF/views/manager/dependents/detail.jsp").forward(req, resp);
    }

    private void handleEditDependentSubmit(int dependentId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String fullName = req.getParameter("fullName");
        String relationship = req.getParameter("relationship");
        String phone = req.getParameter("phone");
        String gender = req.getParameter("gender");
        String dobStr = req.getParameter("dob");
        String tenantIdStr = req.getParameter("tenantId");

        if (fullName == null || relationship == null || fullName.trim().isEmpty() || relationship.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Họ tên và Quan hệ là bắt buộc.");
            if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
            } else {
                resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
            }
            return;
        }

        LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr) : null;

        // Verify that this dependent belongs to a tenant managed by this manager
        String verifySql = 
            "SELECT d.tenant_id FROM dbo.dependents d " +
            "JOIN dbo.rooms r ON d.tenant_id = r.tenant_id " +
            "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "WHERE d.dependent_id = ? AND f.manager_id = ? AND d.deleted_at IS NULL";

        int tenantId = 0;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setInt(1, dependentId);
            ps.setInt(2, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    tenantId = rs.getInt("tenant_id");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to verify dependent ownership", e);
        }

        if (tenantId == 0) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String updateSql = "UPDATE dbo.dependents SET full_name = ?, relationship = ?, phone = ?, gender = ?, dob = ?, updated_at = GETDATE() WHERE dependent_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, fullName.trim());
            ps.setString(2, relationship.trim());
            ps.setString(3, phone);
            ps.setString(4, gender);
            ps.setDate(5, dob != null ? Date.valueOf(dob) : null);
            ps.setInt(6, dependentId);
            ps.executeUpdate();
            
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Update Dependent", fullName.trim(), currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after edit dependent", ex);
            }
            
            setFlashMessage(req, "success", "Cập nhật người phụ thuộc thành công!");
        } catch (Exception e) {
            logger.error("Failed to update dependent", e);
            setFlashMessage(req, "danger", "Lỗi cập nhật người phụ thuộc: " + e.getMessage());
        }

        if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
        } else {
            resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
        }
    }

    private void handleEditTenantSubmit(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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

        if (fullName == null || phone == null || email == null || identityNumber == null ||
            fullName.trim().isEmpty() || phone.trim().isEmpty() || email.trim().isEmpty() || identityNumber.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
            return;
        }

        LocalDate dob = (dobStr != null && !dobStr.isEmpty()) ? LocalDate.parse(dobStr) : null;
        String username = email.trim();

        // 1. Verify manager permissions for this tenant
        String verifySql = 
            "SELECT 1 FROM dbo.users u " +
            "LEFT JOIN dbo.rooms r ON u.user_id = r.tenant_id " +
            "LEFT JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
            "WHERE u.user_id = ? AND u.role = 'TENANT' AND u.deleted_at IS NULL AND (f.manager_id = ? OR r.room_id IS NULL)";

        boolean hasPermission = false;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(verifySql)) {
            ps.setInt(1, tenantId);
            ps.setInt(2, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hasPermission = true;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to verify tenant edit permission", e);
        }

        if (!hasPermission) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // 2. Check for duplicate username/email
        String duplicateSql = "SELECT user_id FROM dbo.users WHERE (username = ? OR email = ?) AND user_id != ? AND deleted_at IS NULL";
        boolean duplicate = false;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(duplicateSql)) {
            ps.setString(1, username);
            ps.setString(2, email.trim());
            ps.setInt(3, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    duplicate = true;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to check duplicate email", e);
        }

        if (duplicate) {
            setFlashMessage(req, "danger", "Email/Tên đăng nhập đã tồn tại trong hệ thống.");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
            return;
        }

        // 3. Update database
        String updateSql = 
            "UPDATE dbo.users SET username = ?, email = ?, full_name = ?, phone = ?, identity_number = ?, " +
            "dob = ?, gender = ?, permanent_address = ?, updated_at = GETDATE() " +
            "WHERE user_id = ? AND role = 'TENANT' AND deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(updateSql)) {
            ps.setString(1, username);
            ps.setString(2, email.trim());
            ps.setString(3, fullName.trim());
            ps.setString(4, phone.trim());
            ps.setString(5, identityNumber.trim());
            ps.setDate(6, dob != null ? Date.valueOf(dob) : null);
            ps.setString(7, gender);
            ps.setString(8, permanentAddress);
            ps.setInt(9, tenantId);
            ps.executeUpdate();
            
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Update Info", fullName.trim(), currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after edit tenant", ex);
            }
            
            setFlashMessage(req, "success", "Cập nhật thông tin người thuê thành công!");
        } catch (Exception e) {
            logger.error("Failed to update tenant info", e);
            setFlashMessage(req, "danger", "Lỗi cập nhật người thuê: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
    }

    private void handleSoftDelete(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String sql = "UPDATE dbo.users SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE user_id = ? AND role = 'TENANT'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "DELETE", null, "Soft Delete", currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after soft delete", ex);
                }
                setFlashMessage(req, "success", "Xóa người thuê thành công!");
            } else {
                setFlashMessage(req, "danger", "Không tìm thấy người thuê hoặc không có quyền xóa.");
            }
        } catch (Exception e) {
            logger.error("Failed to soft delete tenant={}", tenantId, e);
            setFlashMessage(req, "danger", "Lỗi xóa người thuê: " + e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/manager/tenants");
    }

    private void handleLockAccount(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String sql = "UPDATE dbo.users SET status = 'LOCKED', updated_at = GETDATE() WHERE user_id = ? AND role = 'TENANT'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "LOCK_EMPLOYEE", "ACTIVE", "LOCKED", currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after lock", ex);
                }
                setFlashMessage(req, "success", "Đã khóa tài khoản người thuê thành công!");
            } else {
                setFlashMessage(req, "danger", "Không thể khóa tài khoản người thuê.");
            }
        } catch (Exception e) {
            logger.error("Failed to lock tenant account={}", tenantId, e);
            setFlashMessage(req, "danger", "Lỗi khóa tài khoản: " + e.getMessage());
        }

        String referer = req.getHeader("Referer");
        if (referer != null && referer.contains("/manager/tenants/")) {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants");
        }
    }

    private void handleUnlockAccount(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String queryUsername = "SELECT username FROM dbo.users WHERE user_id = ? AND role = 'TENANT'";
        String updateSql = "UPDATE dbo.users SET status = 'ACTIVE', updated_at = GETDATE() WHERE user_id = ? AND role = 'TENANT'";
        try (Connection conn = DatabaseUtil.getConnection()) {
            String username = null;
            try (PreparedStatement ps = conn.prepareStatement(queryUsername)) {
                ps.setInt(1, tenantId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        username = rs.getString("username");
                    }
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setInt(1, tenantId);
                int rows = ps.executeUpdate();
                if (rows > 0) {
                    if (username != null) {
                        com.quanlyphongtro.util.LoginAttemptTracker.reset(username);
                    }
                    try {
                        AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UNLOCK_EMPLOYEE", "LOCKED", "ACTIVE", currentUser.getId());
                    } catch (Exception ex) {
                        logger.warn("AuditLog failed after unlock", ex);
                    }
                    setFlashMessage(req, "success", "Đã mở khóa tài khoản người thuê thành công!");
                } else {
                    setFlashMessage(req, "danger", "Không thể mở khóa tài khoản người thuê.");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to unlock tenant account={}", tenantId, e);
            setFlashMessage(req, "danger", "Lỗi mở khóa tài khoản: " + e.getMessage());
        }

        String referer = req.getHeader("Referer");
        if (referer != null && referer.contains("/manager/tenants/")) {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants");
        }
    }
}
