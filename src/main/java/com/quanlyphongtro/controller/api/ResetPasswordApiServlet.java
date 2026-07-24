package com.quanlyphongtro.controller.api;
import com.quanlyphongtro.util.PasswordValidator;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.util.PasswordUtil;
import com.quanlyphongtro.util.ResetTokenManager;
import com.quanlyphongtro.util.SessionRegistry;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/api/v1/auth/reset-password")
public class ResetPasswordApiServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        // 1. Read JSON Body
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String body = sb.toString();

        // 2. Extract token and newPassword
        Pattern tokenPattern = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"");
        Matcher tokenMatcher = tokenPattern.matcher(body);
        String token = null;
        if (tokenMatcher.find()) {
            token = tokenMatcher.group(1).trim();
        }

        Pattern passPattern = Pattern.compile("\"newPassword\"\\s*:\\s*\"([^\"]+)\"");
        Matcher passMatcher = passPattern.matcher(body);
        String newPassword = null;
        if (passMatcher.find()) {
            newPassword = passMatcher.group(1).trim();
        }

        // 3. Validate
        if (token == null || token.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"error\":{\"code\":\"INVALID_TOKEN\",\"message\":\"Token không hợp lệ hoặc đã hết hạn\"}}");
            out.flush();
            return;
        }

        if (!PasswordValidator.isValid(newPassword)) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"error\":{\"code\":\"INVALID_PASSWORD\",\"message\":\"" + PasswordValidator.POLICY_MESSAGE + "\"}}");
            out.flush();
            return;
        }

        // 4. Verify token
        Integer userId = ResetTokenManager.verifyToken(token);
        if (userId == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"error\":{\"code\":\"INVALID_TOKEN\",\"message\":\"Token không hợp lệ hoặc đã hết hạn\"}}");
            out.flush();
            return;
        }

        // 5. Update Password
        String hashedNewPassword = PasswordUtil.hash(newPassword);
        userDAO.updatePassword(userId, hashedNewPassword);

        // 6. Cleanup
        ResetTokenManager.invalidateToken(token);
        SessionRegistry.invalidateAllSessions(userId);

        // 7. Success
        resp.setStatus(HttpServletResponse.SC_OK);
        out.print("{\"success\":true,\"message\":\"Cập nhật mật khẩu thành công\"}");
        out.flush();
    }
}
