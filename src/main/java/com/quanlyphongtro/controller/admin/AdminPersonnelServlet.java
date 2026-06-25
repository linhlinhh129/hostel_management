package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.AuditLogHelper;
import com.quanlyphongtro.util.EmailService;
import com.quanlyphongtro.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(name = "AdminPersonnelServlet",
        urlPatterns = {"/admin/personnel", "/admin/personnel/*"})
public class AdminPersonnelServlet extends BaseServlet {

    private final PersonnelDAO personnelDAO = new PersonnelDAO();
    private final FacilityDAO  facilityDAO  = new FacilityDAO();
    private final AuditLogDAO  auditLogDAO  = new AuditLogDAO();

    private static final int PAGE_SIZE = 20;
    private static final String BASE_PATH = "/admin/personnel";
    private static final String VIEW_BASE = "/WEB-INF/views/admin/personnel/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        try {
            if (path == null || path.isEmpty() || path.equals("/")) {
                showList(req, resp);
            } else if (path.equals("/create")) {
                showCreate(req, resp);
            } else if (path.matches("/\\d+")) {
                showDetail(req, resp, extractId(path));
            } else if (path.matches("/\\d+/edit")) {
                showEdit(req, resp, extractIdFromPrefix(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminPersonnelServlet doGet error", e);
            handleException(req, resp, e);
            req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = getSubPath(req);
        try {
            if (path.equals("/create")) {
                doCreate(req, resp);
            } else if (path.matches("/\\d+/edit")) {
                doUpdate(req, resp, extractIdFromPrefix(path));
            } else if (path.matches("/\\d+/status")) {
                doToggleStatus(req, resp, extractIdFromPrefix(path));
            } else if (path.matches("/\\d+/delete")) {
                doDelete(req, resp, extractIdFromPrefix(path));
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (ValidationException e) {
            req.setAttribute("errorMessage", e.getMessage());
            // Re-forward về đúng form (create vs edit)
            if (path.equals("/create")) {
                    // Giữ lại dữ liệu user đã nhập để không bị xóa trắng form
                    req.setAttribute("dto", buildDtoFromRequest(req));
                    req.setAttribute("managerFacilities",  personnelDAO.findFacilitiesForManager(null));
                    req.setAttribute("operatorFacilities", personnelDAO.findFacilitiesForOperator(null));
                    req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
            } else if (path.matches("/\\d+/edit")) {
                int editId = extractIdFromPrefix(path);
                try {
                    User user = personnelDAO.findById(editId).orElseThrow(NotFoundException::new);
                    Integer currentFacilityId = personnelDAO.findFacilityIdForUser(editId);
                    user.setFacilityNames(personnelDAO.findFacilityNamesForUser(editId));
                    req.setAttribute("user", user);
                    req.setAttribute("currentFacilityId", currentFacilityId);
                    req.setAttribute("managerFacilities",  personnelDAO.findFacilitiesForManager(
                            "MANAGER".equals(user.getRole()) ? editId : null));
                    req.setAttribute("operatorFacilities", personnelDAO.findFacilitiesForOperator(
                            "OPERATOR".equals(user.getRole()) ? editId : null));
                    req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
                } catch (Exception ex) {
                    resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (NotFoundException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            logger.error("AdminPersonnelServlet doPost error", e);
            setFlashMessage(req, "error", "Có lỗi xảy ra. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH);
        }
    }

    // ─── GET handlers ────────────────────────────────────────────────────────

    private void showList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String keyword        = nullToEmpty(req.getParameter("keyword"));
        String selectedRole   = nullToEmpty(req.getParameter("role"));
        String selectedStatus = nullToEmpty(req.getParameter("status"));
        int page = parseIntOrDefault(req.getParameter("page"), 1);

        int total = personnelDAO.count(keyword, selectedRole, selectedStatus);
        List<User> items = personnelDAO.findAll(keyword, selectedRole, selectedStatus, page, PAGE_SIZE);

        // Populate facilityNames for each user
        for (User u : items) {
            List<String> names = personnelDAO.findFacilityNamesForUser(u.getId());
            u.setFacilityNames(names);
        }

        req.setAttribute("page", new PageDTO<>(items, page, PAGE_SIZE, total));
        req.setAttribute("keyword", keyword);
        req.setAttribute("selectedRole", selectedRole);
        req.setAttribute("selectedStatus", selectedStatus);
        req.getRequestDispatcher(VIEW_BASE + "list.jsp").forward(req, resp);
    }

    private void showCreate(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        // Truyền 2 list riêng: cơ sở chưa có MANAGER và cơ sở chưa có OPERATOR
        req.setAttribute("managerFacilities",  personnelDAO.findFacilitiesForManager(null));
        req.setAttribute("operatorFacilities", personnelDAO.findFacilitiesForOperator(null));
        req.getRequestDispatcher(VIEW_BASE + "create.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        User user = personnelDAO.findById(id)
            .orElseThrow(NotFoundException::new);
        user.setFacilityNames(personnelDAO.findFacilityNamesForUser(id));
        req.setAttribute("user", user);
        req.getRequestDispatcher(VIEW_BASE + "detail.jsp").forward(req, resp);
    }

    private void showEdit(HttpServletRequest req, HttpServletResponse resp, int id)
            throws ServletException, IOException {
        User user = personnelDAO.findById(id)
            .orElseThrow(NotFoundException::new);
        Integer currentFacilityId = personnelDAO.findFacilityIdForUser(id);
        user.setFacilityNames(personnelDAO.findFacilityNamesForUser(id));

        req.setAttribute("user", user);
        req.setAttribute("currentFacilityId", currentFacilityId);

        // Truyền cả 2 list, mỗi list include cơ sở hiện tại của user để không bị mất khi render
        req.setAttribute("managerFacilities",  personnelDAO.findFacilitiesForManager(
                "MANAGER".equals(user.getRole()) ? id : null));
        req.setAttribute("operatorFacilities", personnelDAO.findFacilitiesForOperator(
                "OPERATOR".equals(user.getRole()) ? id : null));

        req.getRequestDispatcher(VIEW_BASE + "edit.jsp").forward(req, resp);
    }

    // ─── POST handlers ───────────────────────────────────────────────────────

    private void doCreate(HttpServletRequest req, HttpServletResponse resp)
            throws Exception {
        String fullName       = trim(req.getParameter("fullName"));
        String email          = trim(req.getParameter("email"));
        String phone          = trim(req.getParameter("phone"));
        String role           = trim(req.getParameter("role"));
        String identityNumber = trim(req.getParameter("identityNumber"));
        String dobStr         = trim(req.getParameter("dob"));
        String gender         = trim(req.getParameter("gender"));
        String permanentAddr  = trim(req.getParameter("permanentAddress"));
        String facilityIdStr  = trim(req.getParameter("facilityId"));

        // Validation
        if (fullName.isEmpty()) throw new ValidationException("Họ tên không được để trống.");
        if (email.isEmpty())    throw new ValidationException("Email không được để trống.");
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")) {
            throw new ValidationException("Email không đúng định dạng.");
        }
        if (phone.isEmpty())    throw new ValidationException("Số điện thoại không được để trống.");
        if (!phone.matches("\\d{10}")) {
            throw new ValidationException("Số điện thoại phải gồm 10 chữ số.");
        }
        if (role.isEmpty()) throw new ValidationException("Vai trò không được để trống.");
        if ("ADMIN".equals(role)) {
            throw new ValidationException("Không thể tạo nhân sự với vai trò ADMIN.");
        }
        if (!role.equals("MANAGER") && !role.equals("OPERATOR")) {
            throw new ValidationException("Vai trò không hợp lệ.");
        }
        if (identityNumber.isEmpty()) {
            throw new ValidationException("Số CMND/CCCD không được để trống.");
        }
        if (!identityNumber.matches("\\d{12}")) {
            throw new ValidationException("Số CMND/CCCD phải gồm 12 chữ số.");
        }

        // Uniqueness checks
        if (personnelDAO.existsByEmail(email, null)) {
            throw new ValidationException("Email '" + email + "' đã được sử dụng.");
        }
        if (personnelDAO.existsByPhone(phone, null)) {
            throw new ValidationException("Số điện thoại '" + phone + "' đã được sử dụng.");
        }
        if (personnelDAO.existsByIdentityNumber(identityNumber, null)) {
            throw new ValidationException("Số CMND/CCCD '" + identityNumber + "' đã được sử dụng.");
        }

        // Facility assignment
        Integer facilityId = null;
        if (!facilityIdStr.isEmpty()) {
            try { facilityId = Integer.parseInt(facilityIdStr); }
            catch (NumberFormatException e) { throw new ValidationException("Cơ sở không hợp lệ."); }
        }

        // Build user
        User user = new User();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setRole(role);
        user.setIdentityNumber(identityNumber);
        user.setGender(gender.isEmpty() ? null : gender);
        user.setPermanentAddress(permanentAddr.isEmpty() ? null : permanentAddr);
        if (!dobStr.isEmpty()) {
            try { user.setDob(LocalDate.parse(dobStr)); }
            catch (Exception e) { /* ignore invalid date */ }
        }
        user.setStatus("ACTIVE");
        user.setForceChangePass(true);

        // Username = email (đồng bộ với luồng tạo tenant)
        // Kiểm tra username chưa tồn tại (email đã check unique ở trên nên username cũng unique)
        user.setUsername(email);

        // Generate and hash temp password
        String tempPassword = PasswordUtil.generateTempPassword();
        user.setPasswordHash(PasswordUtil.hash(tempPassword));

        int newId = personnelDAO.insert(user);
        if (newId < 0) throw new Exception("Tạo nhân sự thất bại.");

        // Assign facility
        if (facilityId != null) {
            Facility fac = facilityDAO.findById(facilityId)
                .orElseThrow(() -> new ValidationException("Cơ sở không tồn tại."));
            if (!"ACTIVE".equals(fac.getStatus())) {
                throw new ValidationException("Cơ sở phải ở trạng thái ACTIVE để phân công.");
            }
            if ("MANAGER".equals(role)) {
                int existing = personnelDAO.countActiveManagerForFacility(facilityId, null);
                if (existing > 0) {
                    throw new ValidationException("Cơ sở đã có Ban Quản Lý. Mỗi cơ sở chỉ được 1 Ban Quản Lý.");
                }
                personnelDAO.assignFacility(newId, facilityId);
            } else if ("OPERATOR".equals(role)) {
                int existing = personnelDAO.countActiveOperatorForFacility(facilityId, null);
                if (existing > 0) {
                    throw new ValidationException("Cơ sở đã có Nhân viên vận hành. Mỗi cơ sở chỉ được 1 Nhân viên vận hành.");
                }
                personnelDAO.assignOperatorFacility(newId, facilityId);
            }
        }

        // Send email (async, non-blocking)
        try {
            EmailService.sendTempPassword(email, fullName, email, tempPassword);
        } catch (Exception ex) {
            logger.warn("Email send failed for new user id={}", newId, ex);
        }

        UserSessionDTO currentUser = getCurrentUser(req);
        try {
            AuditLogHelper.log(auditLogDAO, req, "users", newId,
                "CREATE_EMPLOYEE", null, email,
                currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after personnel create id={}", newId, ex);
        }

        setFlashMessage(req, "success", "Tạo nhân sự '" + fullName + "' thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + newId);
    }

    private void doUpdate(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        User existing = personnelDAO.findById(id)
            .orElseThrow(NotFoundException::new);

        String fullName       = trim(req.getParameter("fullName"));
        String email          = trim(req.getParameter("email"));
        String phone          = trim(req.getParameter("phone"));
        String role           = trim(req.getParameter("role"));
        String identityNumber = trim(req.getParameter("identityNumber"));
        String dobStr         = trim(req.getParameter("dob"));
        String gender         = trim(req.getParameter("gender"));
        String permanentAddr  = trim(req.getParameter("permanentAddress"));
        String facilityIdStr  = trim(req.getParameter("facilityId"));

        // ── Validation cơ bản ────────────────────────────────────────────
        if (fullName.isEmpty()) throw new ValidationException("Họ tên không được để trống.");
        if (email.isEmpty())    throw new ValidationException("Email không được để trống.");
        if (!email.matches("^[\\w.+-]+@[\\w-]+\\.[\\w.]{2,}$")) {
            throw new ValidationException("Email không đúng định dạng.");
        }
        if (phone.isEmpty() || !phone.matches("\\d{10}")) {
            throw new ValidationException("Số điện thoại phải gồm 10 chữ số.");
        }
        if (role.isEmpty() || "ADMIN".equals(role)) {
            throw new ValidationException("Vai trò không hợp lệ.");
        }
        if (!identityNumber.isEmpty() && !identityNumber.matches("\\d{12}")) {
            throw new ValidationException("Số CMND/CCCD phải gồm 12 chữ số.");
        }

        // ── Kiểm tra ngày sinh ───────────────────────────────────────────
        LocalDate dob = null;
        if (!dobStr.isEmpty()) {
            try {
                dob = LocalDate.parse(dobStr);
                if (dob.isAfter(LocalDate.now())) {
                    throw new ValidationException("Ngày sinh không được lớn hơn ngày hiện tại.");
                }
            } catch (ValidationException e) {
                throw e;
            } catch (Exception ignored) {}
        }

        // ── Kiểm tra unique ──────────────────────────────────────────────
        if (personnelDAO.existsByEmail(email, id)) {
            throw new ValidationException("Email đã được sử dụng bởi tài khoản khác.");
        }
        if (personnelDAO.existsByPhone(phone, id)) {
            throw new ValidationException("Số điện thoại đã được sử dụng bởi tài khoản khác.");
        }
        if (!identityNumber.isEmpty() && personnelDAO.existsByIdentityNumber(identityNumber, id)) {
            throw new ValidationException("Số CMND/CCCD đã được sử dụng bởi tài khoản khác.");
        }

        // ── Xử lý cơ sở ─────────────────────────────────────────────────
        Integer newFacilityId = null;
        if (!facilityIdStr.isEmpty()) {
            try { newFacilityId = Integer.parseInt(facilityIdStr); }
            catch (NumberFormatException e) { throw new ValidationException("Cơ sở không hợp lệ."); }
        }

        // MANAGER/OPERATOR phải có cơ sở
        if (("MANAGER".equals(role) || "OPERATOR".equals(role)) && newFacilityId == null) {
            throw new ValidationException("Nhân sự phải được gán một cơ sở quản lý.");
        }

        if (newFacilityId != null) {
            Facility fac = facilityDAO.findById(newFacilityId)
                .orElseThrow(() -> new ValidationException("Cơ sở không tồn tại."));
            if (!"ACTIVE".equals(fac.getStatus())) {
                throw new ValidationException("Chỉ được gán cơ sở đang ở trạng thái ACTIVE.");
            }
            // Kiểm tra conflict MANAGER: cơ sở đã có MANAGER khác không?
            if ("MANAGER".equals(role)) {
                int existingManagers = personnelDAO.countActiveManagerForFacility(newFacilityId, id);
                if (existingManagers > 0) {
                    throw new ValidationException("Cơ sở đã có Ban Quản Lý. Mỗi cơ sở chỉ được 1 Ban Quản Lý.");
                }
            }
            // Kiểm tra conflict OPERATOR: cơ sở đã có OPERATOR khác không?
            if ("OPERATOR".equals(role)) {
                int existingOperators = personnelDAO.countActiveOperatorForFacility(newFacilityId, id);
                if (existingOperators > 0) {
                    throw new ValidationException("Cơ sở đã có Nhân viên vận hành. Mỗi cơ sở chỉ được 1 Nhân viên vận hành.");
                }
            }
        }

        // ── Snapshot cũ — phải lấy TRƯỚC khi set bất cứ thứ gì lên existing ──
        String oldRole       = existing.getRole();
        String oldFullName   = existing.getFullName();
        Integer oldFacilityId = personnelDAO.findFacilityIdForUser(id);
        String oldFacilityName = oldFacilityId != null
            ? facilityDAO.findById(oldFacilityId).map(f -> f.getName()).orElse(String.valueOf(oldFacilityId))
            : "—";

        // ── Cập nhật thông tin user ──────────────────────────────────────
        existing.setFullName(fullName);
        existing.setEmail(email);
        existing.setPhone(phone);
        existing.setRole(role);
        existing.setIdentityNumber(identityNumber.isEmpty() ? existing.getIdentityNumber() : identityNumber);
        existing.setGender(gender.isEmpty() ? existing.getGender() : gender);
        existing.setPermanentAddress(permanentAddr.isEmpty() ? existing.getPermanentAddress() : permanentAddr);
        if (dob != null) existing.setDob(dob);

        personnelDAO.update(existing);

        // ── Cập nhật gán cơ sở ──────────────────────────────────────────
        // 1. Bỏ gán cơ sở cũ (theo đúng vai trò mới)
        if ("MANAGER".equals(role)) {
            personnelDAO.unassignFacility(id);          // xóa manager_id
            personnelDAO.unassignOperatorFacility(id);  // phòng hờ: dọn operator_id nếu trước đây là OPERATOR
        } else if ("OPERATOR".equals(role)) {
            personnelDAO.unassignOperatorFacility(id);  // xóa operator_id
            personnelDAO.unassignFacility(id);          // phòng hờ: dọn manager_id nếu trước đây là MANAGER
        }
        // 2. Gán cơ sở mới
        if (newFacilityId != null) {
            if ("MANAGER".equals(role)) {
                personnelDAO.assignFacility(id, newFacilityId);
            } else if ("OPERATOR".equals(role)) {
                personnelDAO.assignOperatorFacility(id, newFacilityId);
            }
        }

        // ── Audit log ────────────────────────────────────────────────────
        UserSessionDTO currentUser = getCurrentUser(req);
        try {
            // Lookup tên cơ sở mới
            String newFacilityName = newFacilityId != null
                ? facilityDAO.findById(newFacilityId).map(f -> f.getName()).orElse(String.valueOf(newFacilityId))
                : "—";

            // Snapshot trước (dùng biến đã capture trước khi update)
            String oldSnapshot = oldFullName
                + " | role=" + oldRole
                + " | facilityId=" + oldFacilityName;
            // Snapshot sau
            String newSnapshot = fullName
                + " | role=" + role
                + " | facilityId=" + newFacilityName;
            AuditLogHelper.log(auditLogDAO, req, "users", id,
                "UPDATE_EMPLOYEE", oldSnapshot, newSnapshot,
                currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after personnel update id={}", id, ex);
        }

        setFlashMessage(req, "success", "Cập nhật nhân sự '" + fullName + "' thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doToggleStatus(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser != null && currentUser.getId() != null && currentUser.getId() == id) {
            setFlashMessage(req, "error", "Không thể thay đổi trạng thái tài khoản của chính mình.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
            return;
        }

        User user = personnelDAO.findById(id)
            .orElseThrow(NotFoundException::new);

        String newStatus;
        String action;
        if ("ACTIVE".equals(user.getStatus())) {
            newStatus = "INACTIVE";
            action = "LOCK_EMPLOYEE";
        } else {
            newStatus = "ACTIVE";
            action = "UNLOCK_EMPLOYEE";
        }

        personnelDAO.updateStatus(id, newStatus);

        try {
            AuditLogHelper.log(auditLogDAO, req, "users", id,
                action, user.getStatus(), newStatus,
                currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after toggle status id={}", id, ex);
        }

        String msg = "ACTIVE".equals(newStatus)
            ? "Đã mở khóa tài khoản nhân sự."
            : "Đã khóa tài khoản nhân sự.";
        setFlashMessage(req, "success", msg);
        resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
    }

    private void doDelete(HttpServletRequest req, HttpServletResponse resp, int id)
            throws Exception {
        UserSessionDTO currentUser = getCurrentUser(req);

        // Không cho phép tự xóa chính mình
        if (currentUser != null && currentUser.getId() != null && currentUser.getId() == id) {
            setFlashMessage(req, "error", "Không thể xóa tài khoản của chính mình.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
            return;
        }

        User user = personnelDAO.findById(id).orElseThrow(NotFoundException::new);

        // Chỉ được xóa khi đã khóa
        if (!"INACTIVE".equals(user.getStatus())) {
            setFlashMessage(req, "error", "Chỉ có thể xóa nhân sự đang ở trạng thái bị khóa.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
            return;
        }

        int affected = personnelDAO.softDelete(id);
        if (affected == 0) {
            setFlashMessage(req, "error", "Xóa nhân sự thất bại. Vui lòng thử lại.");
            resp.sendRedirect(req.getContextPath() + BASE_PATH + "/" + id);
            return;
        }

        try {
            AuditLogHelper.log(auditLogDAO, req, "users", id,
                "DELETE_EMPLOYEE", user.getUsername(), null,
                currentUser != null ? currentUser.getId() : null);
        } catch (Exception ex) {
            logger.warn("AuditLog failed after personnel delete id={}", id, ex);
        }

        setFlashMessage(req, "success", "Đã xóa nhân sự '" + user.getFullName() + "' thành công.");
        resp.sendRedirect(req.getContextPath() + BASE_PATH);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private String getSubPath(HttpServletRequest req) {
        String uri = req.getRequestURI();
        String ctx = req.getContextPath();
        String subPath = uri.substring(ctx.length() + BASE_PATH.length());
        return subPath.isEmpty() ? "/" : subPath;
    }

    private int extractId(String path) {
        return Integer.parseInt(path.substring(1));
    }

    private int extractIdFromPrefix(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[1]);
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
    private String nullToEmpty(String s) { return s == null ? "" : s.trim(); }
    private int parseIntOrDefault(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    /**
     * Đóng gói lại các tham số từ request thành một Map đơn giản
     * để JSP có thể render lại giá trị đã nhập khi form bị lỗi.
     */
    private java.util.Map<String, String> buildDtoFromRequest(HttpServletRequest req) {
        java.util.Map<String, String> dto = new java.util.HashMap<>();
        String[] fields = { "fullName", "email", "phone", "role",
                            "identityNumber", "dob", "gender",
                            "permanentAddress", "facilityId" };
        for (String f : fields) {
            String v = req.getParameter(f);
            dto.put(f, v != null ? v.trim() : "");
        }
        return dto;
    }
}
