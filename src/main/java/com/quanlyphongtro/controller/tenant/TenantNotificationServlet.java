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
                String pageParam = req.getParameter("page");
                int page = 1;
                if (pageParam != null && !pageParam.isEmpty()) {
                    try { page = Integer.parseInt(pageParam); } catch (NumberFormatException ignored) {}
                }
                int pageSize = 10;
                
                int totalItems = notificationService.countNotificationsForTenant(roomId, facilityId);
                int totalPages = (int) Math.ceil((double) totalItems / pageSize);
                if (totalPages == 0) totalPages = 1;
                if (page > totalPages) page = totalPages;
                if (page < 1) page = 1;

                List<Notification> notifications = notificationService.getNotificationsForTenant(roomId, facilityId, page, pageSize);
                
                java.time.LocalDateTime lastRead = currentUser.getLastReadNotificationTime();
                if (lastRead == null) lastRead = java.time.LocalDateTime.now().minusDays(7); // default 7 days ago if null

                for (Notification n : notifications) {
                    n.setUnread(n.getSentAt() != null && n.getSentAt().isAfter(lastRead));
                }
                
                if (page == 1) {
                    currentUser.setLastReadNotificationTime(java.time.LocalDateTime.now());
                    req.getSession().setAttribute("currentUser", currentUser);
                }
                
                req.setAttribute("notifications", notifications);
                req.setAttribute("currentPage", page);
                req.setAttribute("totalPages", totalPages);
                
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
