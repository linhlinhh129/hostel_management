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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        if (token == null || token.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        // Kiểm tra token có hợp lệ không trước khi render form
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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getParameter("token");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");

        if (token == null || token.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/forgot-password");
            return;
        }

        Integer userId = ResetTokenManager.verifyToken(token);
        if (userId == null) {
            req.setAttribute("errorMessage", "Đường dẫn khôi phục không hợp lệ hoặc đã hết hạn.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            req.setAttribute("errorMessage", "Xác nhận mật khẩu không khớp.");
            req.setAttribute("resetToken", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        if (!PasswordValidator.isValid(newPassword)) {
            req.setAttribute("errorMessage", PasswordValidator.POLICY_MESSAGE);
            req.setAttribute("resetToken", token);
            req.getRequestDispatcher("/WEB-INF/views/auth/reset-password.jsp").forward(req, resp);
            return;
        }

        // Cập nhật mật khẩu
        String hashedNewPassword = PasswordUtil.hash(newPassword);
        userDAO.updatePassword(userId, hashedNewPassword);

        // Hủy token
        ResetTokenManager.invalidateToken(token);

        // Thu hồi phiên đăng nhập
        SessionRegistry.invalidateAllSessions(userId);

        // Redirect báo thành công
        resp.sendRedirect(req.getContextPath() + "/login?success=reset");
    }
}
