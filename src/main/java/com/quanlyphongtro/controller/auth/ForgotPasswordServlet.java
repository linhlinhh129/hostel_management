package com.quanlyphongtro.controller.auth;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.EmailService;
import com.quanlyphongtro.util.ResetTokenManager;
import com.quanlyphongtro.util.RateLimitManager;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/forgot-password")
public class ForgotPasswordServlet extends BaseServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");

        if (email == null || email.isBlank()) {
            req.setAttribute("errorMessage", "Vui lòng nhập địa chỉ email hợp lệ.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        email = email.trim();

        if (!RateLimitManager.isAllowed(email)) {
            req.setAttribute("errorMessage", "Bạn đã vượt quá số lần yêu cầu (tối đa 3 lần/giờ). Vui lòng thử lại sau.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        Optional<User> userOpt = userDAO.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Sinh token
            String token = ResetTokenManager.generateToken(user.getId());
            
            // Xây dựng link
            String resetLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() 
                             + req.getContextPath() + "/reset-password?token=" + token;

            // Gửi email
            EmailService.sendResetLink(email, resetLink);
        }

        req.setAttribute("emailSent", true);
        req.setAttribute("submittedEmail", email);
        req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
    }
}
