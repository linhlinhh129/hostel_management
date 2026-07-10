package com.quanlyphongtro.filter;

import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.service.FacilityService;
import com.quanlyphongtro.service.impl.FacilityServiceImpl;
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

    // Service injected once at init — stateless, safe to share
    private FacilityService facilityService;

    @Override
    public void init(FilterConfig filterConfig) {
        this.facilityService = new FacilityServiceImpl();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  req  = (HttpServletRequest)  request;
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

        // ── Facility-status guard for MANAGER / OPERATOR ──────────────────
        // Block write operations when the facility they belong to is INACTIVE
        if (isManagerOrOperator(currentUser.getRole())
                && (path.startsWith(MANAGER_PREFIX) || path.startsWith(OPERATOR_PREFIX))) {
            try {
                Facility facility = "OPERATOR".equals(currentUser.getRole())
                        ? facilityService.findByOperatorId(currentUser.getId())
                        : facilityService.findByManagerId(currentUser.getId());

                if (facility != null) {
                    String facilityStatus = facility.getStatus();
                    req.setAttribute("currentFacilityStatus", facilityStatus);

                    if ("INACTIVE".equals(facilityStatus) && isWriteMethod(req.getMethod())) {
                        session.setAttribute("flashType",    "error");
                        session.setAttribute("flashMessage",
                                "Cơ sở đã bị vô hiệu hoá. Bạn chỉ có quyền xem, không thể thực hiện thao tác thêm/sửa.");
                        String referer = req.getHeader("Referer");
                        resp.sendRedirect(referer != null
                                ? referer
                                : req.getContextPath() + "/" + currentUser.getRole().toLowerCase() + "/dashboard");
                        return;
                    }
                }
            } catch (Exception e) {
                req.getServletContext().log("RoleFilter: failed to check facility status", e);
                // Non-fatal — let the request continue; downstream handlers will deal with it
            }
        }

        // ── Role-based access control ─────────────────────────────────────
        if (!hasRequiredRole(path, currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    // ── Private helpers ───────────────────────────────────────────────────

    private boolean requiresRoleCheck(String path) {
        return path.startsWith(ADMIN_PREFIX)
                || path.startsWith(MANAGER_PREFIX)
                || path.startsWith(TENANT_PREFIX)
                || path.startsWith(OPERATOR_PREFIX);
    }

    private boolean hasRequiredRole(String path, String role) {
        if (role == null) return false;
        if (path.startsWith(ADMIN_PREFIX))    return "ADMIN".equals(role);
        if (path.startsWith(MANAGER_PREFIX))  return "ADMIN".equals(role) || "MANAGER".equals(role);
        if (path.startsWith(OPERATOR_PREFIX)) return "ADMIN".equals(role) || "MANAGER".equals(role) || "OPERATOR".equals(role);
        if (path.startsWith(TENANT_PREFIX))   return "ADMIN".equals(role) || "MANAGER".equals(role) || "TENANT".equals(role);
        return true;
    }

    private boolean isManagerOrOperator(String role) {
        return "MANAGER".equals(role) || "OPERATOR".equals(role);
    }

    private boolean isWriteMethod(String method) {
        return "POST".equalsIgnoreCase(method)
                || "PUT".equalsIgnoreCase(method)
                || "DELETE".equalsIgnoreCase(method);
    }
}
