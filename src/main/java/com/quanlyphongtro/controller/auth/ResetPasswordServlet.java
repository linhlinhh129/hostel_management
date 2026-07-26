package com.quanlyphongtro.controller.auth;
import com.quanlyphongtro.util.PasswordValidator;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.util.ResetTokenManager;
import com.quanlyphongtro.util.SessionRegistry;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/reset-password")
public class ResetPasswordServlet extends BaseServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    // Bước 3: User click vào đường link trong Email, hệ thống chuyển hướng tới trang Reset Password
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        // Nếu không có token, đẩy ngược về trang quên mật khẩu
        if (token == null || token.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        // Kiểm tra token có hợp lệ không trước khi render form (có bị hết hạn chưa, có tồn tại không)
        Integer userId = ResetTokenManager.verifyToken(token);
        if (userId == null) {
            req.setAttribute("errorMessage", "Đường dẫn khôi phục không hợp lệ hoặc đã hết hạn.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        req.setAttribute("resetToken", token);
        req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
    }

    @Override
    // Bước 4: User nhập mật khẩu mới và submit form Đổi mật khẩu
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (token == null || token.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        // Xác minh token lần cuối trước khi thực sự đổi pass
        Integer userId = ResetTokenManager.verifyToken(token);
        if (userId == null) {
            req.setAttribute("errorMessage", "Đường dẫn khôi phục không hợp lệ hoặc đã hết hạn.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        // Xác minh pass mới và pass nhập lại phải khớp
        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Xác nhận mật khẩu không khớp.");
            req.setAttribute("resetToken", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        // Xác minh độ phức tạp mật khẩu mới (chữ hoa, chữ thường, số, ký tự đặc biệt)
        if (!PasswordValidator.isValid(newPassword)) {
            req.setAttribute("errorMessage", PasswordValidator.POLICY_MESSAGE);
            req.setAttribute("resetToken", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        // Cập nhật mật khẩu trong DB (đã băm - hash)
        String hashedNewPassword = PasswordUtil.hash(newPassword);
        userDAO.updatePassword(userId, hashedNewPassword);

        // Hủy token để token này không thể được sử dụng lại
        ResetTokenManager.invalidateToken(token);

        // Thu hồi toàn bộ phiên đăng nhập hiện tại của người dùng này trên mọi thiết bị
        SessionRegistry.invalidateAllSessions(userId);

        // Đổi pass thành công, redirect báo thành công và yêu cầu đăng nhập lại
        resp.sendRedirect(req.getContextPath() + "/login?success=reset");
    }
}
