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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // If not first login, redirect to dashboard
        if (!currentUser.isFirstLogin()) {
            redirectToDashboard(currentUser, request, response);
            return;
        }

        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!PasswordValidator.isValid(newPassword)) {
            request.setAttribute("errorMessage", PasswordValidator.POLICY_MESSAGE);
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u x\u00E1c nh\u1EADn kh\u00F4ng kh\u1EDBp.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }

        Optional<User> userOpt = userDAO.findById(currentUser.getId());
        if (userOpt.isPresent() && PasswordUtil.verify(newPassword, userOpt.get().getPasswordHash())) {
            request.setAttribute("errorMessage", "Mật khẩu mới không được trùng với mật khẩu cũ.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }

        try {
            // Update password
            String hashedNewPassword = PasswordUtil.hash(newPassword);
            userDAO.updatePassword(currentUser.getId(), hashedNewPassword);

            // Update session status
            currentUser.setFirstLogin(false);
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.setAttribute("currentUser", currentUser);
            }

            // Redirect to dashboard
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
