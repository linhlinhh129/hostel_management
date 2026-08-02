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
    // Hàm khởi tạo, chạy 1 lần duy nhất khi Servlet được nạp vào bộ nhớ để chuẩn bị tầng Service
    public void init() throws ServletException {
        userService = new UserServiceImpl();
    }

    @Override
    // Hàm xử lý yêu cầu GET (khi người dùng gõ URL /login hoặc click link)
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        // Kiểm tra xem người dùng đã đăng nhập chưa, nếu rồi thì đẩy thẳng vào trang chủ (Dashboard)
        if (session != null && session.getAttribute("currentUser") != null) {
            redirectToDashboard((UserSessionDTO) session.getAttribute("currentUser"), req, resp);
            return;
        }
        // Nếu chưa đăng nhập, chuyển hướng mở file giao diện login.jsp lên
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    // Hàm xử lý yêu cầu POST (khi người dùng điền form và bấm nút Đăng nhập) 
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Bước 1: Lấy username và password từ form gửi lên
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // Bước 2: Kiểm tra dữ liệu đầu vào (không rỗng, độ dài hợp lệ)
        if (username == null || password == null || username.isBlank() || password.length() < 8
                || username.length() > 50 || password.length() > 50) {
            req.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không hợp lệ.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        // Bước 3: Kiểm tra xem user này có đang bị tạm khóa do nhập sai mật khẩu quá nhiều lần (Brute-force) hay không
        if (LoginAttemptTracker.isLocked(username.trim())) {
            req.setAttribute("errorMessage", ErrorMessageConstant.ACCOUNT_LOCKED);
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        try {
            // Bước 4: Gọi xuống tầng Service để xử lý logic kiểm tra tài khoản và mật khẩu
            Optional<UserSessionDTO> userOpt = userService.login(username, password);

            if (userOpt.isPresent()) {
                // Mật khẩu ĐÚNG -> Lấy thông tin user an toàn (DTO)
                UserSessionDTO user = userOpt.get();
                
                // Khởi tạo phiên làm việc (Session) mới và lưu thông tin vào đó
                HttpSession session = req.getSession(true);
                session.setAttribute("currentUser", user);
                session.setMaxInactiveInterval(30 * 60); // Đặt thời gian hết hạn session là 30 phút

                // Bước 5: Phân luồng điều hướng sau khi đăng nhập
                if (user.isFirstLogin()) {
                    // Nếu là tài khoản mới cấp, ép chuyển sang trang đổi mật khẩu lần đầu
                    resp.sendRedirect(req.getContextPath() + "/first-login");
                } else {
                    // Nếu bình thường thì cho vào trang chủ tương ứng với Role
                    redirectToDashboard(user, req, resp);
                }
            } else {
                // Mật khẩu hoặc tài khoản SAI -> Báo lỗi và bắt nhập lại
                req.setAttribute("errorMessage", ErrorMessageConstant.INVALID_CREDENTIALS);
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            }
        } catch (ForbiddenException e) {
            // Xử lý ngoại lệ khi tài khoản đã bị khóa cứng (bị cấm bởi Admin)
            req.setAttribute("errorMessage", "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ Admin.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
        }
    }

    // Hàm điều hướng người dùng tới trang chủ chính xác dựa theo Quyền (Role) của họ
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
