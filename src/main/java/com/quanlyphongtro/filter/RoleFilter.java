package com.quanlyphongtro.filter;

import com.quanlyphongtro.dto.UserSessionDTO;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(filterName = "4RoleFilter", urlPatterns = "/*")
public class RoleFilter implements Filter {

    private static final String ADMIN_PREFIX    = "/admin";
    private static final String MANAGER_PREFIX  = "/manager";
    private static final String TENANT_PREFIX   = "/tenant";
    private static final String OPERATOR_PREFIX = "/operator";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        String path = req.getRequestURI().substring(req.getContextPath().length());

        if (!requiresRoleCheck(path)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = req.getSession(false);
        if (session == null) {
            chain.doFilter(request, response);
            return;
        }

        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (currentUser == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!hasRequiredRole(path, currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean requiresRoleCheck(String path) {
        return path.startsWith(ADMIN_PREFIX)
                || path.startsWith(MANAGER_PREFIX)
                || path.startsWith(TENANT_PREFIX)
                || path.startsWith(OPERATOR_PREFIX);
    }

    private boolean hasRequiredRole(String path, String role) {
        if (role == null) return false;

        if (path.startsWith(ADMIN_PREFIX)) {
            return "ADMIN".equals(role);
        }
        if (path.startsWith(MANAGER_PREFIX)) {
            return "ADMIN".equals(role) || "MANAGER".equals(role);
        }
        if (path.startsWith(TENANT_PREFIX)) {
            return "ADMIN".equals(role) || "MANAGER".equals(role) || "TENANT".equals(role);
        }
        if (path.startsWith(OPERATOR_PREFIX)) {
            return "ADMIN".equals(role) || "MANAGER".equals(role) || "OPERATOR".equals(role);
        }
        return true;
    }
}
