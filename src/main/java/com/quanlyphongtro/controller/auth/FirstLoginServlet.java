package com.quanlyphongtro.controller.auth;

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

        // ── Validate độ mạnh mật khẩu ───────────────────────────────────
        if (newPassword == null || newPassword.length() < 8) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u ph\u1EA3i c\u00F3 \u00EDt nh\u1EA5t 8 k\u00FD t\u1EF1.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        if (!newPassword.matches(".*[A-Z].*")) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u ph\u1EA3i c\u00F3 \u00EDt nh\u1EA5t 1 ch\u1EEF in hoa.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        if (!newPassword.matches(".*[a-z].*")) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u ph\u1EA3i c\u00F3 \u00EDt nh\u1EA5t 1 ch\u1EEF in th\u01B0\u1EDDng.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        if (!newPassword.matches(".*[0-9].*")) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u ph\u1EA3i c\u00F3 \u00EDt nh\u1EA5t 1 ch\u1EEF s\u1ED1.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        if (!newPassword.matches(".*[@#$%!^&*()_+\\-=\\[\\]{};':\"\\\\ |,.<>/?`~].*")) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u ph\u1EA3i c\u00F3 \u00EDt nh\u1EA5t 1 k\u00FD t\u1EF1 \u0111\u1EB7c bi\u1EC7t.");
            request.getRequestDispatcher("/WEB-INF/views/auth/first_login.jsp").forward(request, response);
            return;
        }
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "M\u1EADt kh\u1EA9u x\u00E1c nh\u1EADn kh\u00F4ng kh\u1EDBp.");
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
