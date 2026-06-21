package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet(name = "TenantProfileServlet", urlPatterns = "/tenant/profile")
public class TenantProfileServlet extends BaseServlet {

    private final TenantService tenantService = new TenantServiceImpl();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserSessionDTO currentUser = getCurrentUser(req);
            Optional<User> userOpt = tenantService.getTenantProfile(currentUser.getId());
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                // Simple DTO mapping for JSP
                req.setAttribute("profile", new ProfileViewDTO(user, tenantService));
            }
            req.getRequestDispatcher("/WEB-INF/views/tenant/profile/index.jsp").forward(req, resp);
        } catch (Exception e) {
            forwardError(req, resp, "/WEB-INF/views/tenant/profile/index.jsp", e);
        }
    }

    public static class ProfileViewDTO {
        private final User user;
        private final Room room;
        
        public ProfileViewDTO(User user, TenantService ts) {
            this.user = user;
            this.room = ts.getTenantRoom(user.getId()).orElse(null);
        }

        public String getFullName() { return user.getFullName(); }
        public String getCode() { return user.getUsername(); } // Using username as ID/Code representation
        public String getRoomCode() { return room != null ? room.getCode() : "N/A"; }
        public String getDobLabel() { return user.getDob() != null ? user.getDob().format(DATE_FORMATTER) : "N/A"; }
        public String getPhone() { return user.getPhone(); }
        public String getIdCardNumber() { return user.getIdentityNumber(); }
        public String getEmail() { return user.getEmail(); }
        public String getContractStartDateLabel() { return (room != null && room.getContractStartDate() != null) ? room.getContractStartDate().format(DATE_FORMATTER) : "N/A"; }
        public String getContractEndDateLabel() { return (room != null && room.getContractEndDate() != null) ? room.getContractEndDate().format(DATE_FORMATTER) : "N/A"; }
    }
}
