package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.NotificationService;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.NotificationServiceImpl;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TenantNotificationServlet", urlPatterns = {"/tenant/notifications", "/tenant/notifications/*"})
public class TenantNotificationServlet extends BaseServlet {

    private final TenantService tenantService = new TenantServiceImpl();
    private final NotificationService notificationService = new NotificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        UserSessionDTO currentUser = getCurrentUser(req);

        try {
            Optional<Room> roomOpt = tenantService.getTenantRoom(currentUser.getId());
            int roomId = roomOpt.map(Room::getId).orElse(0);
            int facilityId = roomOpt.flatMap(r -> tenantService.getFacilityByRoomId(r.getId())).map(Facility::getId).orElse(0);

            if (pathInfo == null || pathInfo.equals("/")) {
                // List
                List<Notification> notifications = notificationService.getNotificationsForTenant(roomId, facilityId);
                req.setAttribute("notifications", notifications);
                req.getRequestDispatcher("/WEB-INF/views/tenant/notifications/list.jsp").forward(req, resp);
            } else {
                // Detail
                String idStr = pathInfo.substring(1);
                try {
                    int id = Integer.parseInt(idStr);
                    Optional<Notification> notifOpt = notificationService.getNotificationById(id, roomId, facilityId);
                    if (notifOpt.isPresent()) {
                        req.setAttribute("notification", notifOpt.get());
                        req.getRequestDispatcher("/WEB-INF/views/tenant/notifications/detail.jsp").forward(req, resp);
                    } else {
                        resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    }
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
                }
            }
        } catch (Exception e) {
            handleException(req, resp, e);
        }
    }
}
