package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.DatabaseUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerContractsServlet", urlPatterns = {
        "/manager/contracts",
        "/manager/contracts/*"
})
public class ManagerContractsServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            handleList(req, resp);
        } else {
            String idStr = pathInfo.substring(1);
            try {
                int roomId = Integer.parseInt(idStr);
                handleDetail(roomId, req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo != null) {
            String[] parts = pathInfo.split("/");
            if (parts.length == 3) {
                try {
                    int roomId = Integer.parseInt(parts[1]);
                    String action = parts[2];
                    if ("terminate".equals(action)) {
                        handleTerminate(roomId, req, resp);
                        return;
                    } else if ("upload".equals(action)) {
                        handleUpload(roomId, req, resp);
                        return;
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
            }
        }

        // Check if updating deposit
        String servletPath = req.getServletPath();
        if ("/manager/contracts/deposit".equals(servletPath) || (pathInfo != null && pathInfo.contains("deposit"))) {
            handleUpdateDeposit(req, resp);
            return;
        }

        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String filterStatus = req.getParameter("status");
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

        List<Map<String, Object>> allContracts = new ArrayList<>();

        String sql = "SELECT r.room_id, r.code AS room_code, r.room_fee, r.deposit_amount, r.contract_start_date, r.contract_end_date, r.status AS room_status, f.name AS facility_name, u.full_name AS tenant_name, u.user_id AS tenant_id " +
                "FROM dbo.rooms r " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "JOIN dbo.users u ON r.tenant_id = u.user_id " +
                "WHERE f.manager_id = ? AND r.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUser.getId());
            try (ResultSet rs = ps.executeQuery()) {
                LocalDate today = LocalDate.now();
                while (rs.next()) {
                    Map<String, Object> c = new HashMap<>();
                    int roomId = rs.getInt("room_id");
                    c.put("id", roomId);
                    c.put("roomCode", rs.getString("room_code"));
                    c.put("facilityName", rs.getString("facility_name"));
                    c.put("tenantName", rs.getString("tenant_name"));
                    double rentPrice = rs.getDouble("room_fee");
                    c.put("rentPrice", rentPrice);
                    double depositAmount = rs.getDouble("deposit_amount");
                    c.put("depositAmount", depositAmount);

                    Date moveInDate = rs.getDate("contract_start_date");
                    Date expiryDate = rs.getDate("contract_end_date");
                    c.put("moveInDate", moveInDate != null ? moveInDate.toString() : "—");
                    c.put("expiryDate", expiryDate != null ? expiryDate.toString() : "—");

                    // Map deposit status
                    String depStatus = "UNPAID";
                    if (depositAmount >= rentPrice) {
                        depStatus = "PAID";
                    } else if (depositAmount > 0) {
                        depStatus = "PARTIAL";
                    }
                    c.put("depositStatus", depStatus);

                    // Determine status
                    String status = "ACTIVE";
                    if (expiryDate != null) {
                        LocalDate exp = expiryDate.toLocalDate();
                        if (exp.isBefore(today)) {
                            status = "OVERDUE";
                        } else if (exp.isBefore(today.plusDays(30))) {
                            status = "EXPIRING_SOON";
                        }
                    }
                    c.put("status", status);

                    // Filter in Java
                    if (filterStatus == null || filterStatus.trim().isEmpty() || filterStatus.trim().equals(status)) {
                        allContracts.add(c);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query contracts", e);
        }

        // Manual pagination
        int totalCount = allContracts.size();
        int totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;
        if (page > totalPages) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalCount);

        List<Map<String, Object>> paginated = new ArrayList<>();
        if (start < totalCount) {
            paginated = allContracts.subList(start, end);
        }

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", paginated);
        pageObj.put("total", totalCount);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("filterStatus", filterStatus);

        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/list.jsp").forward(req, resp);
    }

    private void handleDetail(int roomId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> contract = null;

        String sql = "SELECT r.*, f.code AS facility_code, f.name AS facility_name, f.manager_id, u.full_name AS tenant_name, u.username AS tenant_code, m.full_name AS manager_name " +
                "FROM dbo.rooms r " +
                "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                "JOIN dbo.users u ON r.tenant_id = u.user_id " +
                "LEFT JOIN dbo.users m ON f.manager_id = m.user_id " +
                "WHERE r.room_id = ? AND r.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int managerId = rs.getInt("manager_id");
                    if (managerId != currentUser.getId()) {
                        resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không quản lý cơ sở của hợp đồng này.");
                        return;
                    }

                    contract = new HashMap<>();
                    contract.put("id", rs.getInt("room_id"));
                    contract.put("roomCode", rs.getString("code"));
                    contract.put("facilityName", rs.getString("facility_name"));
                    contract.put("tenantName", rs.getString("tenant_name"));
                    double rentPrice = rs.getDouble("room_fee");
                    contract.put("rentPrice", rentPrice);
                    double depositAmount = rs.getDouble("deposit_amount");
                    contract.put("depositAmount", depositAmount);

                    Date moveInDate = rs.getDate("contract_start_date");
                    Date expiryDate = rs.getDate("contract_end_date");
                    contract.put("moveInDate", moveInDate != null ? moveInDate.toString() : "—");
                    contract.put("expiryDate", expiryDate != null ? expiryDate.toString() : "—");
                    contract.put("billingCycle", 1); // default cycle

                    Timestamp cAt = rs.getTimestamp("created_at");
                    contract.put("createdAt", cAt != null ? cAt.toLocalDateTime().toLocalDate().toString() : "—");
                    contract.put("createdByName", rs.getString("manager_name") != null ? rs.getString("manager_name") : "Ban Quản lý");

                    // Map deposit status
                    String depStatus = "UNPAID";
                    if (depositAmount >= rentPrice) {
                        depStatus = "PAID";
                    } else if (depositAmount > 0) {
                        depStatus = "PARTIAL";
                    }
                    contract.put("depositStatus", depStatus);

                    // Determine status
                    String status = "ACTIVE";
                    if (expiryDate != null) {
                        LocalDate exp = expiryDate.toLocalDate();
                        LocalDate today = java.time.LocalDate.now();
                        if (exp.isBefore(today)) {
                            status = "OVERDUE";
                        } else if (exp.isBefore(today.plusDays(30))) {
                            status = "EXPIRING_SOON";
                        }
                    }
                    contract.put("status", status);

                    // documentPath placeholder
                    contract.put("documentPath", null);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to query contract details", e);
        }

        if (contract == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("contract", contract);
        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/detail.jsp").forward(req, resp);
    }

    private void handleUpdateDeposit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String contractIdStr = req.getParameter("contractId");
        String depositStatus = req.getParameter("depositStatus");

        if (contractIdStr == null || depositStatus == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int roomId = Integer.parseInt(contractIdStr);
        double targetDeposit = 0.0;

        try (Connection conn = DatabaseUtil.getConnection()) {
            // Get room fee
            double roomFee = 0.0;
            String feeSql = "SELECT room_fee FROM dbo.rooms WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(feeSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        roomFee = rs.getDouble("room_fee");
                    }
                }
            }

            if ("PAID".equals(depositStatus)) {
                targetDeposit = roomFee;
            } else if ("PARTIAL".equals(depositStatus)) {
                targetDeposit = roomFee / 2.0;
            }

            // Update deposit
            String updateSql = "UPDATE dbo.rooms SET deposit_amount = ?, updated_at = GETDATE() WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateSql)) {
                ps.setDouble(1, targetDeposit);
                ps.setInt(2, roomId);
                ps.executeUpdate();
                setFlashMessage(req, "success", "Cập nhật trạng thái cọc thành công!");
            }
        } catch (Exception e) {
            logger.error("Failed to update deposit for room={}", roomId, e);
            setFlashMessage(req, "danger", "Lỗi cập nhật cọc: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/manager/contracts/" + roomId);
    }

    private void handleTerminate(int roomId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false);

            // Get tenantId from room
            int tenantId = 0;
            String tenantSql = "SELECT tenant_id FROM dbo.rooms WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(tenantSql)) {
                ps.setInt(1, roomId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tenantId = rs.getInt("tenant_id");
                    }
                }
            }

            // Release room
            String releaseSql = "UPDATE dbo.rooms SET tenant_id = NULL, status = 'AVAILABLE', contract_start_date = NULL, contract_end_date = NULL, updated_at = GETDATE() WHERE room_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(releaseSql)) {
                ps.setInt(1, roomId);
                ps.executeUpdate();
            }

            // Deactivate user if exists
            if (tenantId > 0) {
                String deactivateSql = "UPDATE dbo.users SET status = 'INACTIVE', updated_at = GETDATE() WHERE user_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deactivateSql)) {
                    ps.setInt(1, tenantId);
                    ps.executeUpdate();
                }
            }

            conn.commit();
            setFlashMessage(req, "success", "Thanh lý hợp đồng thành công!");
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ignored) {}
            }
            logger.error("Failed to terminate contract for room={}", roomId, e);
            setFlashMessage(req, "danger", "Lỗi thanh lý hợp đồng: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception ignored) {}
            }
        }

        resp.sendRedirect(req.getContextPath() + "/manager/contracts");
    }

    private void handleUpload(int roomId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Since we are mocking the document upload path, we redirect with success message.
        setFlashMessage(req, "success", "Tải tài liệu hợp đồng lên thành công (mock)!");
        resp.sendRedirect(req.getContextPath() + "/manager/contracts/" + roomId);
    }
}
