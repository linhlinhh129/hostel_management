package com.quanlyphongtro.controller.auth;
import com.quanlyphongtro.exception.ForbiddenException;

import com.quanlyphongtro.constant.ErrorMessageConstant;
import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.UserService;
import com.quanlyphongtro.service.impl.UserServiceImpl;
import com.quanlyphongtro.util.LoginAttemptTracker;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "LoginServlet", urlPatterns = "/login")
public class LoginServlet extends BaseServlet {

    private UserService userService;

    @Override
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // Bước 1: Kiểm tra xem user đã có session đăng nhập chưa.
        // Nếu đã đăng nhập, tự động chuyển hướng (redirect) vào thẳng Dashboard dựa trên Role của user.
        if (session != null && session.getAttribute("currentUser") != null) {
            redirectToDashboard((UserSessionDTO) session.getAttribute("currentUser"), req, resp);
            return;
        }
        // Nếu chưa đăng nhập, hiển thị form đăng nhập (trả về file giao diện login.jsp)
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    // Bước 2: Nhận dữ liệu user submit từ Form Đăng nhập
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Bước 3: Validation cơ bản. 
        // Kiểm tra rỗng, kiểm tra độ dài mật khẩu (>= 8) và độ dài tối đa (50).
        if (username == null || password == null || username.isBlank() || password.length() < 8
                || username.length() > 50 || password.length() > 50) {
            req.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không hợp lệ.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        // Bước 4: Kiểm tra chống tấn công dò mật khẩu (Brute-force).
        // Nếu user này đang bị khóa tạm thời do nhập sai quá nhiều lần trước đó.
        if (LoginAttemptTracker.isLocked(username.trim())) {
            req.setAttribute("errorMessage", ErrorMessageConstant.ACCOUNT_LOCKED);
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        try {
            // Bước 5: Đẩy dữ liệu xuống tầng Service để xử lý logic (Kiểm tra DB, mã hóa pass, v.v...)
            Optional<UserSessionDTO> userOpt = userService.login(username, password);

            if (userOpt.isPresent()) {
                // Bước 6: Đăng nhập thành công, thiết lập Session cho user.
                UserSessionDTO user = userOpt.get();
                HttpSession session = req.getSession(true);
                session.setAttribute("currentUser", user);
                session.setMaxInactiveInterval(30 * 60); // Timeout 30 phút

                // Kiểm tra xem đây có phải là đăng nhập lần đầu không (bắt buộc đổi pass)
                if (user.isFirstLogin()) {
                    resp.sendRedirect(req.getContextPath() + "/first-login");
                } else {
                    // Bước 7: Redirect vào đúng Dashboard theo Role của user
                    redirectToDashboard(user, req, resp);
                }
            } else {
                // Đăng nhập thất bại (sai pass hoặc username không tồn tại)
                req.setAttribute("errorMessage", ErrorMessageConstant.INVALID_CREDENTIALS);
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            }
        } catch (ForbiddenException e) {
            // Tài khoản đã bị Admin khóa vĩnh viễn (status inactive/locked)
            req.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
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
