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
    maxFileSize = 1024 * 1024 * 5,       // 5 MB
    maxRequestSize = 1024 * 1024 * 10    // 10 MB
)
public class ProfileServlet extends BaseServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        Optional<User> userOpt = userDAO.findById(currentUser.getId());
        if (userOpt.isPresent()) {
            request.setAttribute("userProfile", userOpt.get());
        }

        request.getRequestDispatcher("/WEB-INF/views/common/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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

            if ("update_profile".equals(action)) {
                String fullName = request.getParameter("fullName");
                String phone = request.getParameter("phone");
                String identityNumber = request.getParameter("identityNumber");
                String dobStr = request.getParameter("dob");
                String gender = request.getParameter("gender");
                String permanentAddress = request.getParameter("permanentAddress");

                if (phone != null && !phone.trim().isEmpty()) {
                    if (!com.quanlyphongtro.util.ValidationUtil.isValidVnPhone(phone)) {
                        response.sendRedirect(request.getContextPath() + "/profile?error=invalid_phone");
                        return;
                    }
                }
                if (identityNumber != null && !identityNumber.trim().isEmpty()) {
                    if (!com.quanlyphongtro.util.ValidationUtil.isValidVnIdentity(identityNumber)) {
                        response.sendRedirect(request.getContextPath() + "/profile?error=invalid_identity");
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

                // Handle Avatar Upload
                Part filePart = request.getPart("avatar");
                if (filePart != null && filePart.getSize() > 0) {
                    String uploadPath = getServletContext().getRealPath("") + File.separator + "uploads" + File.separator + "avatars";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    String fileName = UUID.randomUUID().toString() + "_" + extractFileName(filePart);
                    filePart.write(uploadPath + File.separator + fileName);
                    user.setAvatarUrl("/uploads/avatars/" + fileName);
                }

                userDAO.updateProfile(user);

                // Update Session DTO
                currentUser.setFullName(user.getFullName());
                currentUser.setAvatarUrl(user.getAvatarUrl());
                currentUser.setInitials(UserSessionDTO.extractInitials(user.getFullName()));
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.setAttribute("currentUser", currentUser);
                }

                response.sendRedirect(request.getContextPath() + "/profile?success=profile");
                
            } else if ("change_password".equals(action)) {
                String currentPassword = request.getParameter("currentPassword");
                String newPassword = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                if (!newPassword.equals(confirmPassword)) {
                    response.sendRedirect(request.getContextPath() + "/profile?error=password_mismatch");
                    return;
                }

                if (!PasswordUtil.verify(currentPassword, user.getPasswordHash())) {
                    response.sendRedirect(request.getContextPath() + "/profile?error=invalid_password");
                    return;
                }

                String hashedNewPassword = PasswordUtil.hash(newPassword);
                userDAO.updatePassword(user.getId(), hashedNewPassword);

                if (currentUser.isFirstLogin()) {
                    currentUser.setFirstLogin(false);
                    HttpSession session = request.getSession(false);
                    if (session != null) {
                        session.setAttribute("currentUser", currentUser);
                    }
                }

                response.sendRedirect(request.getContextPath() + "/profile?success=password");
            } else {
                response.sendRedirect(request.getContextPath() + "/profile");
            }
        } catch (Throwable t) {
            t.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().write("<h3>Đã xảy ra lỗi hệ thống (500):</h3><pre>");
            t.printStackTrace(response.getWriter());
            response.getWriter().write("</pre>");
        }
    }

    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                String fileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                return fileName.replaceAll("[\\\\/:*?\"<>|]", "_"); // Sanitize file name
            }
        }
        return "";
    }
}
