package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Notification;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(urlPatterns = {"/operator/notifications", "/operator/notifications/*"})
public class NotificationListServlet extends HttpServlet {

    private final NotificationDAO notificationDAO = new NotificationDAO();
    private final FacilityDAO facilityDAO = new FacilityDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            resp.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        UserSessionDTO user = (UserSessionDTO) session.getAttribute("currentUser");
        if (!"OPERATOR".equals(user.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Chỉ lấy thông báo của Hệ thống (target_type = 'ALL'), không lấy của riêng khu trọ
            int facilityId = 0;

            // Pagination
            int page = 1;
            int limit = 10;
            try {
                String pageStr = req.getParameter("page");
                if (pageStr != null && !pageStr.isBlank()) {
                    page = Integer.parseInt(pageStr);
                    if (page < 1) page = 1;
                }
            } catch (NumberFormatException ignored) {}

            List<Notification> notifications = notificationDAO.findNotificationsForOperator(facilityId, page, limit);
            int totalRecords = notificationDAO.countNotificationsForOperator(facilityId);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);
            if (totalPages == 0) totalPages = 1;

            req.setAttribute("notifications", notifications);
            req.setAttribute("currentPage", page);
            req.setAttribute("totalPages", totalPages);
            req.setAttribute("pageTitle", "Thông báo hệ thống");

            req.getRequestDispatcher("/WEB-INF/views/operator/notifications.jsp").forward(req, resp);
        } else {
            // Detail
            String idStr = pathInfo.substring(1);
            try {
                int id = Integer.parseInt(idStr);
                Optional<Notification> notifOpt = notificationDAO.findById(id);
                if (notifOpt.isPresent()) {
                    req.setAttribute("notification", notifOpt.get());
                    req.getRequestDispatcher("/WEB-INF/views/operator/notification_detail.jsp").forward(req, resp);
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                }
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }
}
