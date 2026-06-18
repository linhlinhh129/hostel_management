package com.quanlyphongtro.filter;

import com.quanlyphongtro.constant.ErrorMessageConstant;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Arrays;
import java.util.List;

@WebFilter(filterName = "2CsrfFilter", urlPatterns = "/*")
public class CsrfFilter implements Filter {

    private static final String CSRF_SESSION_KEY = "csrfToken";
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
            "/login", "/logout", "/forgot-password", "/reset-password", "/assets"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        HttpSession session = req.getSession(true);
        String token = (String) session.getAttribute(CSRF_SESSION_KEY);
        if (token == null) {
            token = generateToken();
            session.setAttribute(CSRF_SESSION_KEY, token);
        }
        req.setAttribute(CSRF_SESSION_KEY, token);

        if ("POST".equalsIgnoreCase(req.getMethod()) && requiresCsrf(path)) {
            String submitted = req.getParameter(CSRF_SESSION_KEY);
            if (submitted == null || !submitted.equals(token)) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, ErrorMessageConstant.CSRF_INVALID);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean requiresCsrf(String path) {
        if (path.equals("/") || path.equals("/index.jsp")) {
            return false;
        }
        for (String p : PUBLIC_PATHS) {
            if (path.equals(p) || path.startsWith(p + "/")) {
                return false;
            }
        }
        return true;
    }

    private String generateToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
