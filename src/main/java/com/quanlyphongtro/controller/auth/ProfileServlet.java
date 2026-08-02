package com.quanlyphongtro.controller.auth;
import com.quanlyphongtro.util.ValidationUtil;
import com.quanlyphongtro.util.PasswordValidator;

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
    // Hàm xử lý khi người dùng truy cập trang /profile để xem hồ sơ
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Bước 1: Kiểm tra xem người dùng đã đăng nhập chưa
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Bước 2: Lấy thông tin mới nhất của người dùng từ Database
        Optional<User> userOpt = userDAO.findById(currentUser.getId());
        userOpt.ifPresent(u -> request.setAttribute("userProfile", u));

        // Bước 3: Mở file giao diện profile.jsp và truyền dữ liệu sang để hiển thị
        request.getRequestDispatcher("/WEB-INF/views/common/profile.jsp").forward(request, response);
    }

    // ── POST ─────────────────────────────────────────────────────────────
    @Override
    // Hàm xử lý khi người dùng bấm Submit ở 1 trong 2 form (Cập nhật thông tin HOẶC Đổi mật khẩu)
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Kiểm tra đăng nhập
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        try {
            // Lấy biến action để xem người dùng đang gửi Form nào lên
            String action = request.getParameter("action");

            // Lấy thông tin người dùng từ DB để chuẩn bị cập nhật
            Optional<User> userOpt = userDAO.findById(currentUser.getId());
            if (userOpt.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }
            User user = userOpt.get();

            // ── LUỒNG 1: XỬ LÝ FORM CẬP NHẬT THÔNG TIN CÁ NHÂN ───────────────────────────────────────────
            if ("update_profile".equals(action)) {

                // Lấy các dữ liệu từ form gửi lên
                String fullName        = request.getParameter("fullName");
                String phone           = request.getParameter("phone");
                String identityNumber  = request.getParameter("identityNumber");
                String dobStr          = request.getParameter("dob");
                String gender          = request.getParameter("gender");
                String permanentAddress = request.getParameter("permanentAddress");

                if (phone != null && !phone.trim().isEmpty()) {
                    if (!ValidationUtil.isValidVnPhone(phone)) {
                        setFlashMessage(request, "error",
                            "Số điện thoại không hợp lệ (chỉ chấp nhận số điện thoại di động Việt Nam gồm 10 số).");
                        response.sendRedirect(request.getContextPath() + "/profile");
                        return;
                    }
                }
                if (identityNumber != null && !identityNumber.trim().isEmpty()) {
                    if (!ValidationUtil.isValidVnIdentity(identityNumber)) {
                        setFlashMessage(request, "error",
                            "Số CCCD không hợp lệ (phải gồm 12 chữ số).");
                        response.sendRedirect(request.getContextPath() + "/profile");
                        return;
                    }
                }

                user.setFullName(fullName);
                user.setPhone(phone);
                user.setIdentityNumber(identityNumber);
                if (dobStr != null && !dobStr.isBlank()) {
                    try {
                        LocalDate dob = LocalDate.parse(dobStr.trim());
                        if (!ValidationUtil.isAtLeast18YearsOld(dob)) {
                            setFlashMessage(request, "error", "Bạn phải từ 18 tuổi trở lên.");
                            response.sendRedirect(request.getContextPath() + "/profile");
                            return;
                        }
                        user.setDob(dob);
                    } catch (Exception e) {
                        setFlashMessage(request, "error", "Ngày sinh không hợp lệ.");
                        response.sendRedirect(request.getContextPath() + "/profile");
                        return;
                    }
                }
                user.setGender(gender);
                user.setPermanentAddress(permanentAddress);

                // --- XỬ LÝ UPLOAD ẢNH ĐẠI DIỆN ---
                Part filePart = request.getPart("avatar");
                if (filePart != null && filePart.getSize() > 0) {
                    // Xác định thư mục lưu ảnh trên Server
                    String uploadPath = getServletContext().getRealPath("")
                            + File.separator + "uploads" + File.separator + "avatars";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    // Tạo tên file ngẫu nhiên bằng UUID để chống trùng tên
                    String fileName = UUID.randomUUID() + "_" + extractFileName(filePart);
                    filePart.write(uploadPath + File.separator + fileName);
                    user.setAvatarUrl("/uploads/avatars/" + fileName);
                }

                // Cập nhật thông tin xuống Database
                userDAO.updateProfile(user);

                // --- ĐỒNG BỘ LẠI SESSION (Để góc phải trên cùng cập nhật tên và ảnh ngay lập tức) ---
                currentUser.setFullName(user.getFullName());
                currentUser.setAvatarUrl(user.getAvatarUrl());
                currentUser.setInitials(UserSessionDTO.extractInitials(user.getFullName()));
                HttpSession session = request.getSession(false);
                if (session != null) session.setAttribute("currentUser", currentUser);

                setFlashMessage(request, "success", "Cập nhật thông tin hồ sơ thành công!");
                response.sendRedirect(request.getContextPath() + "/profile");

            // ── LUỒNG 2: XỬ LÝ FORM ĐỔI MẬT KHẨU ──────────────────────────────────────────
            } else if ("change_password".equals(action)) {

                // Lấy mật khẩu cũ và mới từ form
                String currentPassword = request.getParameter("currentPassword");
                String newPassword     = request.getParameter("newPassword");
                String confirmPassword = request.getParameter("confirmPassword");

                // Kiểm tra mật khẩu mới có đủ mạnh không
                if (!PasswordValidator.isValid(newPassword)) {
                    setFlashMessage(request, "error",
                        "Mật khẩu mới không đạt chuẩn bảo mật (cần ít nhất 8 ký tự, có chữ hoa, chữ số và ký tự đặc biệt).");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }
                // Kiểm tra 2 ô mật khẩu mới có khớp nhau không
                if (!newPassword.equals(confirmPassword)) {
                    setFlashMessage(request, "error", "Xác nhận mật khẩu mới không khớp!");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }
                // Kiểm tra xem có trùng mật khẩu cũ không
                if (newPassword.equals(currentPassword)) {
                    setFlashMessage(request, "error", "Mật khẩu mới không được trùng với mật khẩu cũ.");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }
                // Quan trọng nhất: Kiểm tra mật khẩu HIỆN TẠI (cũ) nhập vào có đúng không
                if (!PasswordUtil.verify(currentPassword, user.getPasswordHash())) {
                    setFlashMessage(request, "error", "Mật khẩu hiện tại không chính xác!");
                    response.sendRedirect(request.getContextPath() + "/profile");
                    return;
                }

                // Nếu mọi thứ hợp lệ -> Băm mật khẩu mới và lưu xuống Database
                userDAO.updatePassword(user.getId(), PasswordUtil.hash(newPassword));

                // Cập nhật session (đánh dấu đã đổi mật khẩu nếu là đăng nhập lần đầu)
                if (currentUser.isFirstLogin()) {
                    currentUser.setFirstLogin(false);
                    HttpSession session = request.getSession(false);
                    if (session != null) session.setAttribute("currentUser", currentUser);
                }

                // Báo thành công và tải lại trang
                setFlashMessage(request, "success", "Đổi mật khẩu thành công!");
                response.sendRedirect(request.getContextPath() + "/profile");

            } else {
                response.sendRedirect(request.getContextPath() + "/profile");
            }

        } catch (Throwable t) {
            logger.error("ProfileServlet error", t);
            setFlashMessage(request, "error", "Đã xảy ra lỗi hệ thống. Vui lòng thử lại.");
            response.sendRedirect(request.getContextPath() + "/profile");
        }
    }
    //validate đuôi dẫn ảnh
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
