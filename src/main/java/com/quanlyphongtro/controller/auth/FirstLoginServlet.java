package com.quanlyphongtro.controller.auth;
import com.quanlyphongtro.util.PasswordValidator;
import java.util.Optional;
import com.quanlyphongtro.model.User;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/first-login")
public class FirstLoginServlet extends BaseServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    // Hàm xử lý khi người dùng truy cập trang đổi mật khẩu lần đầu (GET request)
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        // Bắt buộc phải đăng nhập rồi mới được vào đây
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Nếu không phải là tài khoản đăng nhập lần đầu, đá thẳng về trang chủ (không cho phép ở lại trang này)
        if (!currentUser.isFirstLogin()) {
            redirectToDashboard(currentUser, request, response);
            return;
        }

        // Mở file giao diện đổi mật khẩu lên cho người dùng thao tác
        request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
    }

    @Override
    // Hàm xử lý khi người dùng điền form đổi mật khẩu và bấm Submit (POST request)
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Bước 0: Đảm bảo chỉ những người đăng nhập lần đầu mới được phép thao tác đổi mật khẩu
        if (!currentUser.isFirstLogin()) {
            redirectToDashboard(currentUser, request, response);
            return;
        }

        // Bước 1: Lấy Mật khẩu mới và Xác nhận mật khẩu từ form gửi lên
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Kiểm tra mật khẩu mới có đủ mạnh không (độ dài, ký tự đặc biệt...)
        if (!PasswordValidator.isValid(newPassword)) {
            request.setAttribute("errorMessage", PasswordValidator.POLICY_MESSAGE);
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra Mật khẩu mới và Xác nhận mật khẩu có khớp nhau không
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u x\u00E1c nh\u1EADn kh\u00F4ng kh\u1EDBp.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }

        // Bước 2: Truy vấn Database để lấy mật khẩu CŨ lên, kiểm tra xem Mật khẩu MỚI có bị TRÙNG với Mật khẩu CŨ không
        Optional<User> userOpt = userDAO.findById(currentUser.getId());
        if (userOpt.isPresent() && PasswordUtil.verify(newPassword, userOpt.get().getPasswordHash())) {
            request.setAttribute("errorMessage", "Mật khẩu mới không được trùng với mật khẩu cũ.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }

        try {
            // Bước 3: Băm (mã hóa) mật khẩu mới và lưu xuống Database qua tầng DAO
            String hashedNewPassword = PasswordUtil.hash(newPassword);
            userDAO.updatePassword(currentUser.getId(), hashedNewPassword);

            // Bước 4: Cập nhật lại Session - Đánh dấu tài khoản này ĐÃ đổi mật khẩu (không còn là first login nữa)
            currentUser.setFirstLogin(false);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("currentUser", currentUser);
            }

            // Bước 5: Đổi mật khẩu thành công, điều hướng người dùng thẳng vào trang chủ tương ứng với quyền
            redirectToDashboard(currentUser, request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
        }
    }

    private void redirectToDashboard(UserSessionDTO user, HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String redirect = switch (user.getRole()) {
            case "ADMIN"    -> req.getContextPath() + "/admin/dashboard";
            case "MANAGER"  -> req.getContextPath() + "/manager/dashboard";
            case "TENANT"   -> req.getContextPath() + "/tenant/dashboard";
            case "OPERATOR" -> req.getContextPath() + "/operator/dashboard";
            default         -> req.getContextPath() + "/login";
        };
        resp.sendRedirect(redirect);
    }
}
