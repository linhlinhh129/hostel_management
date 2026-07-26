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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // If not first login, shouldn't be here
        if (!currentUser.isFirstLogin()) {
            redirectToDashboard(currentUser, request, response);
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
    }

    @Override
    // Bước Đổi mật khẩu bắt buộc cho người dùng đăng nhập lần đầu
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Nếu không phải lần đầu đăng nhập thì đẩy về Dashboard (ngăn chặn truy cập trái phép)
        if (!currentUser.isFirstLogin()) {
            redirectToDashboard(currentUser, request, response);
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // Xác minh policy của mật khẩu (độ dài, chữ hoa, số, ký tự đặc biệt)
        if (!PasswordValidator.isValid(newPassword)) {
            request.setAttribute("errorMessage", PasswordValidator.POLICY_MESSAGE);
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        
        // Kiểm tra mật khẩu xác nhận có khớp không
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u x\u00E1c nh\u1EADn kh\u00F4ng kh\u1EDBp.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }

        Optional<User> userOpt = userDAO.findById(currentUser.getId());
        // Kiểm tra xem mật khẩu mới có bị trùng với mật khẩu cũ không
        if (userOpt.isPresent() && PasswordUtil.verify(newPassword, userOpt.get().getPasswordHash())) {
            request.setAttribute("errorMessage", "Mật khẩu mới không được trùng với mật khẩu cũ.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }

        try {
            // Update password trong Database (mã hóa hash trước khi lưu)
            String hashedNewPassword = PasswordUtil.hash(newPassword);
            userDAO.updatePassword(currentUser.getId(), hashedNewPassword);

            // Cập nhật trạng thái session để user không bị điều hướng vào trang này nữa
            currentUser.setFirstLogin(false);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("currentUser", currentUser);
            }

            // Đổi pass thành công, cho phép truy cập vào Dashboard tương ứng với role
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
