package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 1, // 1 MB
    maxFileSize       = 1024 * 1024 * 5, // 5 MB
    maxRequestSize    = 1024 * 1024 * 10 // 10 MB
)
public class ProfileServlet extends BaseServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    // ── GET ──────────────────────────────────────────────────────────────
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Optional<User> userOpt = userDAO.findById(currentUser.getId());
        userOpt.ifPresent(u -> request.setAttribute("userProfile", u));

        request.getRequestDispatcher("/WEB-INF/views/common/profile.jsp").forward(request, response);
    }

    // ── POST ─────────────────────────────────────────────────────────────
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        try {
            String action = request.getParameter("action");

            Optional<User> userOpt = userDAO.findById(currentUser.getId());
            if (userOpt.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            User user = userOpt.get();

            // ── update_profile ───────────────────────────────────────────
            if ("update_profile".equals(action)) {

                String fullName        = request.getParameter("fullName");
                String phone           = request.getParameter("phone");
                String identityNumber  = request.getParameter("identityNumber");
                String dobStr          = request.getParameter("dob");
                String gender          = request.getParameter("gender");
                String permanentAddress = request.getParameter("permanentAddress");

                if (phone != null && !phone.trim().isEmpty()) {
                    if (!com.quanlyphongtro.util.ValidationUtil.isValidVnPhone(phone)) {
                        setFlashMessage(request, "error",
                            "So dien thoai khong hop le (chi chap nhan so dien thoai di dong Viet Nam gom 10 so).");
                        response.sendRedirect(request.getContextPath() + "/profile");
                        return;
                    }
                }
                if (identityNumber != null && !identityNumber.trim().isEmpty()) {
                    if (!com.quanlyphongtro.util.ValidationUtil.isValidVnIdentity(identityNumber)) {
                        setFlashMessage(request, "error",
                            "So CMND/CCCD khong hop le (phai gom 9 hoac 12 chu so).");
                        response.sendRedirect(request.getContextPath() + "/profile");
                        return;
                    }
                }

                user.setFullName(fullName);
                user.setPhone(phone);
                user.setIdentityNumber(identityNumber);
                if (dobStr != null && !dobStr.isBlank()) {
                    user.setDob(LocalDate.parse(dobStr));
                }
                user.setGender(gender);
                user.setPermanentAddress(permanentAddress);

                // Avatar upload
                Part filePart = request.getPart("avatar");
                if (filePart != null && filePart.getSize() > 0) {
                    String uploadPath = getServletContext().getRealPath("")
                            + File.separator + "uploads" + File.separator + "avatars";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    String fileName = UUID.randomUUID() + "_" + extractFileName(filePart);
                    filePart.write(uploadPath + File.separator + fileName);
                    user.setAvatarUrl("/uploads/avatars/" + fileName);
                }

                userDAO.updateProfile(user);

                // Sync session
                currentUser.setFullName(user.getFullName());
                currentUser.setAvatarUrl(user.getAvatarUrl());
                currentUser.setInitials(UserSessionDTO.extractInitials(user.getFullName()));
                HttpSession session = request.getSession(false);
                if (session != null) session.setAttribute("currentUser", currentUser);

                setFlashMessage(request, "success", "Cap nhat thong tin ho so thanh cong!");
                response.sendRedirect(request.getContextPath() + "/profile");

            // ── change_password ──────────────────────────────────────────
            } else if ("change_password".equals(action)) {

                String currentPassword = request.getParameter("currentPassword");
                String newPassword     = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                if (!com.quanlyphongtro.util.PasswordValidator.isValid(newPassword)) {
                    setFlashMessage(request, "error",
                        "Mat khau moi khong dat chuan bao mat (can it nhat 8 ky tu, co chu hoa, chu so va ky tu dac biet).");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    setFlashMessage(request, "error", "Xac nhan mat khau moi khong khop!");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }
                if (newPassword.equals(currentPassword)) {
                    setFlashMessage(request, "error", "Mat khau moi khong duoc trung voi mat khau cu.");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }
                if (!PasswordUtil.verify(currentPassword, user.getPasswordHash())) {
                    setFlashMessage(request, "error", "Mat khau hien tai khong chinh xac!");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }

                userDAO.updatePassword(user.getId(), PasswordUtil.hash(newPassword));

                if (currentUser.isFirstLogin()) {
                    currentUser.setFirstLogin(false);
                    HttpSession session = request.getSession(false);
                    if (session != null) session.setAttribute("currentUser", currentUser);
                }

                setFlashMessage(request, "success", "Doi mat khau thanh cong!");
                response.sendRedirect(request.getContextPath() + "/profile");

            } else {
                response.sendRedirect(request.getContextPath() + "/profile");
            }

        } catch (Throwable t) {
            logger.error("ProfileServlet error", t);
            setFlashMessage(request, "error", "Da xay ra loi he thong. Vui long thu lai.");
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }

    // ── helpers ──────────────────────────────────────────────────────────
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        for (String s : contentDisp.split(";")) {
            if (s.trim().startsWith("filename")) {
                String fileName = s.substring(s.indexOf('=') + 2, s.length() - 1);
                return fileName.replaceAll("[\\\\/:*?\"<>|]", "_");
            }
        }
        return "";
    }
}
