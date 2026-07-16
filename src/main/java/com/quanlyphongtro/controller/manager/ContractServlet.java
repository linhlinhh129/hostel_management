package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.service.ContractService;
import com.quanlyphongtro.service.impl.ContractServiceImpl;
import com.quanlyphongtro.util.AuditLogHelper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
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
        req.setAttribute("availableRooms", contractService.getAvailableRooms(managerId));
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

                try {
                    AuditLogHelper.log(auditLogDAO, req,
                        "contracts",
                        contract.getContractId(),
                        "CREATE",
                        null,
                        contract.getCode(),
                        user.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after contract create id={}", contract.getContractId(), ex);
                }

                resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contract.getContractId());
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                req.setAttribute("availableRooms", contractService.getAvailableRooms(user.getId()));
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

        try {
            int contractId = Integer.parseInt(contractIdStr.trim());
            Map<String, Object> prefilledContract = contractService.getContractForAddTenant(contractId, managerId);
            if (prefilledContract == null) {
                setFlashMessage(req, "error", "Không tìm thấy hợp đồng hợp lệ để tạo tài khoản người thuê.");
                resp.sendRedirect(req.getContextPath() + "/manager/contracts");
                return;
            }
            req.setAttribute("prefilledContract", prefilledContract);
            req.getRequestDispatcher("/WEB-INF/views/manager/contracts/add_tenant.jsp").forward(req, resp);
        } catch (IllegalStateException e) {
            setFlashMessage(req, "error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contractIdStr.trim());
        } catch (Exception e) {
            logger.error("Failed to load prefilled contract details for contractId={}", contractIdStr, e);
            setFlashMessage(req, "error", "Không tìm thấy hợp đồng hợp lệ để tạo tài khoản người thuê.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
        }
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

        if (roomIdStr == null || roomIdStr.isEmpty() || contractIdStr == null || contractIdStr.isEmpty()) {
            req.setAttribute("errorMessage", "Vui lòng nhập đầy đủ các trường bắt buộc.");
            showAddTenantForm(req, resp, currentUser.getId());
            return;
        }

        int roomId = Integer.parseInt(roomIdStr);
        int contractId = Integer.parseInt(contractIdStr);
        boolean confirmReactivate = "true".equals(req.getParameter("confirmReactivate"));

        try {
            String loginLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath() + "/login";
            Map<String, Object> result = contractService.addTenantFromContract(
                contractId, roomId, fullName, phone, email, identityNumber, permanentAddress, gender, dobStr, contractStartDateStr, confirmReactivate, currentUser.getId(), loginLink
            );

            if ("REACTIVATE_CONFIRM".equals(result.get("status"))) {
                req.setAttribute("showReactivateConfirmation", true);
                req.setAttribute("existingUserFullName", result.get("fullName"));
                req.setAttribute("existingUserIdentity", result.get("identityNumber"));
                showAddTenantForm(req, resp, currentUser.getId());
                return;
            }

            boolean userExists = (Boolean) result.get("userExists");
            int newUserId = (Integer) result.get("userId");

            try {
                AuditLogHelper.log(auditLogDAO, req, "users", newUserId, userExists ? "REACTIVATE" : "CREATE", null, email.trim(), currentUser.getId());
            } catch (Exception ex) {
                logger.warn("AuditLog failed after tenant create", ex);
            }

            if (userExists) {
                setFlashMessage(req, "success", "Kích hoạt lại tài khoản người thuê cũ thành công! Đã gửi mật khẩu tạm thời mới vào email " + email.trim() + ".");
            } else {
                setFlashMessage(req, "success", "Tạo tài khoản người thuê thành công! Đã gửi thông tin tài khoản và mật khẩu tạm thời vào email " + email.trim() + ".");
            }
            resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contractId);

        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            showAddTenantForm(req, resp, currentUser.getId());
        } catch (Exception e) {
            logger.error("Failed to create tenant from contract", e);
            req.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
            showAddTenantForm(req, resp, currentUser.getId());
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
            Map<String, String> verification = contractService.verifyContractForDelete(contractId, currentUser.getId());

            if (verification == null) {
                setFlashMessage(req, "error", "Hợp đồng không tồn tại hoặc bạn không có quyền xóa.");
                resp.sendRedirect(req.getContextPath() + "/manager/contracts");
                return;
            }

            String status = verification.get("status");
            String code = verification.get("code");

            if (!"INACTIVE".equals(status)) {
                setFlashMessage(req, "error", "Chỉ được xóa hợp đồng khi trạng thái là INACTIVE.");
                resp.sendRedirect(req.getContextPath() + "/manager/contracts/detail?id=" + contractId);
                return;
            }

            boolean success = contractService.softDeleteContract(contractId);
            if (success) {
                try {
                    AuditLogHelper.log(auditLogDAO, req, "contracts", contractId, "DELETE", null,
                            "Soft Delete contract: " + code, currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after contract delete", ex);
                }
                setFlashMessage(req, "success", "Xóa hợp đồng " + code + " thành công!");
            } else {
                setFlashMessage(req, "error", "Xóa hợp đồng thất bại.");
            }
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");

        } catch (NumberFormatException e) {
            setFlashMessage(req, "error", "ID hợp đồng không hợp lệ.");
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
        } catch (Exception e) {
            logger.error("Failed to delete contract", e);
            setFlashMessage(req, "error", "Lỗi: " + e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/manager/contracts");
        }
    }
}
