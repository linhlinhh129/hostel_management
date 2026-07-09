package com.quanlyphongtro.controller.api;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.util.EmailService;
import com.quanlyphongtro.util.RateLimitManager;
import com.quanlyphongtro.util.ResetTokenManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet("/api/v1/auth/forgot-password")
public class ForgotPasswordApiServlet extends HttpServlet {

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

        // 2. Extract email via simple regex
        Pattern pattern = Pattern.compile("\"email\"\\s*:\\s*\"([^\"]+)\"");
        Matcher matcher = pattern.matcher(body);
        String email = null;
        if (matcher.find()) {
            email = matcher.group(1).trim();
        }

        // 3. Validation
        if (email == null || email.isBlank() || !email.contains("@") || email.length() > 100) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"success\":false,\"error\":{\"code\":\"INVALID_EMAIL\",\"message\":\"Sai định dạng email\"}}");
            out.flush();
            return;
        }

        // 4. Rate Limit
        if (!RateLimitManager.isAllowed(email)) {
            // Even if rate limited, return success to prevent enumeration, or return 429?
            // The spec says: limit 3/hour. Let's return success but don't send email.
            out.print("{\"success\":true,\"message\":\"Nếu email của bạn có trong hệ thống, link khôi phục đã được gửi đi.\"}");
            out.flush();
            return;
        }

        // 5. DB Check
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            String token = ResetTokenManager.generateToken(user.getId());
            
            // Note: Since it's an API call, we need the frontend base URL to construct the reset link.
            // Assuming frontend runs on same domain.
            String resetLink = req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort() 
                             + req.getContextPath() + "/reset-password?token=" + token;

            EmailService.sendResetLink(email, resetLink);
        }

        // 6. Return 200 to prevent enumeration
        resp.setStatus(HttpServletResponse.SC_OK);
        out.print("{\"success\":true,\"message\":\"Nếu email của bạn có trong hệ thống, link khôi phục đã được gửi đi.\"}");
        out.flush();
    }
}
