package com.quanlyphongtro.controller.manager;
import java.nio.file.AccessDeniedException;
import com.quanlyphongtro.util.ValidationUtil;
import java.util.Optional;
import com.quanlyphongtro.model.Room;
import java.time.format.DateTimeFormatter;
import com.quanlyphongtro.util.LoginAttemptTracker;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.util.AuditLogHelper;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.TenantServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
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
    private final TenantService tenantService = new TenantServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        String pathInfo = req.getPathInfo();

        if (servletPath.startsWith("/manager/dependents")) {
            if (pathInfo == null || "/".equals(pathInfo)) {
                resp.sendRedirect(req.getContextPath() + "/manager/tenants");
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
            setFlashMessage(req, "error", "Vui lòng tạo tài khoản người thuê thông qua chi tiết hợp đồng.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
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
                setFlashMessage(req, "error", "Vui lòng tạo tài khoản người thuê thông qua chi tiết hợp đồng.");
                resp.sendRedirect(req.getContextPath() + "/manager/contracts");
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

        int totalCount = tenantService.countTenants(currentUser.getId(), keyword, status);
        List<Map<String, Object>> tenants = tenantService.getTenants(currentUser.getId(), keyword, status, page, pageSize);

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

    private void handleDetail(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> tenant = null;
        List<Map<String, Object>> dependents = null;

        try {
            tenant = tenantService.getTenantDetail(tenantId, currentUser.getId());
            if (tenant != null) {
                dependents = tenantService.getTenantDependents(tenantId);
            }
        } catch (AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Failed to query tenant detail", e);
        }

        if (tenant == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
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
        String identityNumber = req.getParameter("identityNumber");

        if (fullName == null || relationship == null || fullName.trim().isEmpty() || relationship.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Họ tên và Quan hệ là bắt buộc.");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
            return;
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!ValidationUtil.isValidVnPhone(phone)) {
                setFlashMessage(req, "danger", "Số điện thoại người phụ thuộc không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                return;
            }
        }
        if (identityNumber != null && !identityNumber.trim().isEmpty()) {
            if (!ValidationUtil.isValidVnIdentity(identityNumber)) {
                setFlashMessage(req, "danger", "Số CMND/CCCD người phụ thuộc không hợp lệ (phải gồm 9 hoặc 12 chữ số).");
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                return;
            }
        }

        LocalDate dob = null;
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr.trim());
                if (dob.isAfter(LocalDate.now())) {
                    setFlashMessage(req, "danger", "Ngày sinh của người phụ thuộc không thể ở tương lai.");
                    resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                    return;
                }
            } catch (Exception e) {
                setFlashMessage(req, "danger", "Ngày sinh không đúng định dạng (yyyy-MM-dd).");
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                return;
            }
        }

        try {
            boolean success = tenantService.addDependent(tenantId, currentUser.getId(), fullName, relationship, phone, gender, dob, identityNumber);
            if (success) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Add Dependent", fullName.trim(), currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after add dependent", ex);
                }
                setFlashMessage(req, "success", "Thêm người phụ thuộc thành công!");
            } else {
                setFlashMessage(req, "danger", "Không thể thêm người phụ thuộc.");
            }
        } catch (AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
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

        int tenantId = 0;
        try {
            Map<String, Object> dep = tenantService.getDependentDetail(dependentId, currentUser.getId());
            if (dep != null) {
                tenantId = (Integer) dep.get("tenantId");
            }
        } catch (Exception e) {
            logger.error("Failed to fetch dependent detail for removal verification", e);
        }

        if (tenantId == 0) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            boolean success = tenantService.removeDependent(dependentId, currentUser.getId());
            if (success) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Remove Dependent", "Dependent ID " + dependentId, currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after remove dependent", ex);
                }
                setFlashMessage(req, "success", "Xóa người phụ thuộc thành công!");
            } else {
                setFlashMessage(req, "danger", "Lỗi khi xóa người phụ thuộc.");
            }
        } catch (AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (IllegalStateException e) {
            setFlashMessage(req, "danger", e.getMessage());
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

        String endDateStr = req.getParameter("endDate");
        if (endDateStr != null && !endDateStr.trim().isEmpty()) {
            try {
                LocalDate endDate = LocalDate.parse(endDateStr.trim());
                Optional<Room> roomOpt = tenantService.getTenantRoom(tenantId);
                if (roomOpt.isPresent() && roomOpt.get().getContractStartDate() != null) {
                    LocalDate startDate = roomOpt.get().getContractStartDate();
                    if (endDate.isBefore(startDate)) {
                        setFlashMessage(req, "danger", "Ngày kết thúc thuê không thể trước ngày bắt đầu hợp đồng (" + startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + ").");
                        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                        return;
                    }
                }
            } catch (Exception e) {
                setFlashMessage(req, "danger", "Ngày kết thúc thuê không đúng định dạng (yyyy-MM-dd).");
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                return;
            }
        }

        boolean success = tenantService.endRental(tenantId);
        if (success) {
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "ACTIVE", "INACTIVE (End Rental)", currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after end rental", ex);
            }
            setFlashMessage(req, "success", "Kết thúc hợp đồng thuê và giải phóng phòng thành công!");
        } else {
            setFlashMessage(req, "danger", "Lỗi kết thúc thuê.");
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
    }

    private void handleDependentDetail(int dependentId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> dependent = null;
        try {
            dependent = tenantService.getDependentDetail(dependentId, currentUser.getId());
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
        String identityNumber = req.getParameter("identityNumber");

        if (fullName == null || relationship == null || fullName.trim().isEmpty() || relationship.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Họ tên và Quan hệ là bắt buộc.");
            if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
            } else {
                resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
            }
            return;
        }

        if (phone != null && !phone.trim().isEmpty()) {
            if (!ValidationUtil.isValidVnPhone(phone)) {
                setFlashMessage(req, "danger", "Số điện thoại người phụ thuộc không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
                if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
                }
                return;
            }
        }
        if (identityNumber != null && !identityNumber.trim().isEmpty()) {
            if (!ValidationUtil.isValidVnIdentity(identityNumber)) {
                setFlashMessage(req, "danger", "Số CMND/CCCD người phụ thuộc không hợp lệ (phải gồm 9 hoặc 12 chữ số).");
                if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
                }
                return;
            }
        }

        LocalDate dob = null;
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr.trim());
                if (dob.isAfter(LocalDate.now())) {
                    setFlashMessage(req, "danger", "Ngày sinh của người phụ thuộc không thể ở tương lai.");
                    if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                        resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
                    } else {
                        resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
                    }
                    return;
                }
            } catch (Exception e) {
                setFlashMessage(req, "danger", "Ngày sinh không đúng định dạng (yyyy-MM-dd).");
                if (tenantIdStr != null && !tenantIdStr.isEmpty()) {
                    resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantIdStr);
                } else {
                    resp.sendRedirect(req.getContextPath() + "/manager/dependents/" + dependentId);
                }
                return;
            }
        }

        int tenantId = 0;
        try {
            Map<String, Object> dep = tenantService.getDependentDetail(dependentId, currentUser.getId());
            if (dep != null) {
                tenantId = (Integer) dep.get("tenantId");
            }
        } catch (Exception e) {
            logger.error("Failed to verify dependent ownership", e);
        }

        if (tenantId == 0) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            boolean success = tenantService.editDependent(dependentId, currentUser.getId(), fullName, relationship, phone, gender, dob, identityNumber);
            if (success) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Update Dependent", fullName.trim(), currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after edit dependent", ex);
                }
                setFlashMessage(req, "success", "Cập nhật người phụ thuộc thành công!");
            } else {
                setFlashMessage(req, "danger", "Lỗi khi cập nhật người phụ thuộc.");
            }
        } catch (AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (IllegalStateException e) {
            setFlashMessage(req, "danger", e.getMessage());
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

        if (!ValidationUtil.isValidVnPhone(phone)) {
            setFlashMessage(req, "danger", "Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
            return;
        }
        if (!ValidationUtil.isValidVnIdentity(identityNumber)) {
            setFlashMessage(req, "danger", "Số CMND/CCCD không hợp lệ (phải gồm 9 hoặc 12 chữ số).");
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
            return;
        }

        LocalDate dob = null;
        if (dobStr != null && !dobStr.trim().isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr.trim());
            } catch (Exception e) {
                setFlashMessage(req, "danger", "Ngày sinh không đúng định dạng (yyyy-MM-dd).");
                resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
                return;
            }
        }

        try {
            boolean success = tenantService.editTenant(tenantId, currentUser.getId(), fullName, phone, email, identityNumber, permanentAddress, gender, dob);
            if (success) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UPDATE", "Update Info", fullName.trim(), currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after edit tenant", ex);
                }
                setFlashMessage(req, "success", "Cập nhật thông tin người thuê thành công!");
            } else {
                setFlashMessage(req, "danger", "Lỗi khi cập nhật thông tin người thuê.");
            }
        } catch (AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (IllegalArgumentException e) {
            setFlashMessage(req, "danger", e.getMessage());
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

        boolean success = tenantService.softDeleteTenant(tenantId);
        if (success) {
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "DELETE", null, "Soft Delete", currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after soft delete", ex);
            }
            setFlashMessage(req, "success", "Xóa người thuê thành công!");
        } else {
            setFlashMessage(req, "danger", "Không tìm thấy người thuê hoặc không có quyền xóa.");
        }

        resp.sendRedirect(req.getContextPath() + "/manager/tenants");
    }

    private void handleLockAccount(int tenantId, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        boolean success = tenantService.lockTenantAccount(tenantId);
        if (success) {
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "LOCK_EMPLOYEE", "ACTIVE", "LOCKED", currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after lock", ex);
            }
            setFlashMessage(req, "success", "Đã khóa tài khoản người thuê thành công!");
        } else {
            setFlashMessage(req, "danger", "Không thể khóa tài khoản người thuê.");
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

        boolean success = tenantService.unlockTenantAccount(tenantId, username -> {
            LoginAttemptTracker.reset(username);
        });

        if (success) {
            try {
                AuditLogHelper.log(auditLogDAO, req, "users", tenantId, "UNLOCK_EMPLOYEE", "LOCKED", "ACTIVE", currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after unlock", ex);
            }
            setFlashMessage(req, "success", "Đã mở khóa tài khoản người thuê thành công!");
        } else {
            setFlashMessage(req, "danger", "Không thể mở khóa tài khoản người thuê.");
        }

        String referer = req.getHeader("Referer");
        if (referer != null && referer.contains("/manager/tenants/")) {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants/" + tenantId);
        } else {
            resp.sendRedirect(req.getContextPath() + "/manager/tenants");
        }
    }
}
