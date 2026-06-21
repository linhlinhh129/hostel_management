package com.quanlyphongtro.controller.auth;

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
        if (session != null && session.getAttribute("currentUser") != null) {
            redirectToDashboard((UserSessionDTO) session.getAttribute("currentUser"), req, resp);
            return;
        }
        req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (username == null || password == null || username.isBlank() || password.length() < 7) {
            req.setAttribute("errorMessage", "Tên đăng nhập hoặc mật khẩu không hợp lệ.");
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        if (LoginAttemptTracker.isLocked(username.trim())) {
            req.setAttribute("errorMessage", ErrorMessageConstant.ACCOUNT_LOCKED);
            req.setAttribute("username", username);
            req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            return;
        }

        try {
            Optional<UserSessionDTO> userOpt = userService.login(username, password);

            if (userOpt.isPresent()) {
                UserSessionDTO user = userOpt.get();
                HttpSession session = req.getSession(true);
                session.setAttribute("currentUser", user);
                session.setMaxInactiveInterval(30 * 60);

                if (user.isFirstLogin()) {
                    resp.sendRedirect(req.getContextPath() + "/first-login");
                } else {
                    redirectToDashboard(user, req, resp);
                }
            } else {
                req.setAttribute("errorMessage", ErrorMessageConstant.INVALID_CREDENTIALS);
                req.setAttribute("username", username);
                req.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(req, resp);
            }
        } catch (com.quanlyphongtro.exception.ForbiddenException e) {
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
