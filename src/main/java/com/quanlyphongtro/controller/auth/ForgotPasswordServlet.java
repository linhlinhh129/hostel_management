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
    // Hàm xử lý yêu cầu GET: Mở giao diện trang "Quên mật khẩu"
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
    }

    @Override
    // Hàm xử lý yêu cầu POST (Cách cổ điển khi Form submit không dùng Javascript Fetch API)
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Bước 1: Lấy email người dùng nhập
        String email = req.getParameter("email");

        // Kiểm tra tính hợp lệ của email
        if (email == null || email.isBlank() || email.length() > 100) {
            req.setAttribute("errorMessage", "Vui lòng nhập địa chỉ email hợp lệ.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        email = email.trim();

        // Bước 2: Kiểm tra giới hạn số lần gửi (Rate Limit) để chống spam (tối đa 3 lần/giờ)
        if (!RateLimitManager.isAllowed(email)) {
            req.setAttribute("errorMessage", "Bạn đã vượt quá số lần yêu cầu (tối đa 3 lần/giờ). Vui lòng thử lại sau.");
            req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
            return;
        }

        // Bước 3: Tìm xem email có tồn tại trong Database không
        Optional<User> userOpt = userDAO.findByEmail(email);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Sinh mã token bảo mật ngẫu nhiên cho user này
            String token = ResetTokenManager.generateToken(user.getId());
            
            // Xây dựng đường link khôi phục mật khẩu đính kèm token
            String resetLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() 
                             + req.getContextPath() + "/reset-password?token=" + token;

            // Gửi email thực sự tới hòm thư người dùng
            EmailService.sendResetLink(email, resetLink);
        }

        // Bước 4: Luôn báo thành công (dù email có tồn tại hay không) để bảo mật thông tin, không cho hacker biết email nào có trong hệ thống
        req.setAttribute("emailSent", true);
        req.setAttribute("submittedEmail", email);
        req.getRequestDispatcher("/WEB-INF/views/auth/forgot-password.jsp").forward(req, resp);
    }
}
