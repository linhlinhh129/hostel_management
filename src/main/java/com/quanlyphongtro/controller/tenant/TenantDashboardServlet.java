package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.InvoiceService;
import com.quanlyphongtro.service.NotificationService;
import com.quanlyphongtro.service.RequestService;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.InvoiceServiceImpl;
import com.quanlyphongtro.service.impl.NotificationServiceImpl;
import com.quanlyphongtro.service.impl.RequestServiceImpl;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "TenantDashboardServlet", urlPatterns = "/tenant/dashboard")
public class TenantDashboardServlet extends BaseServlet {

    private final TenantService tenantService = new TenantServiceImpl();
    private final InvoiceService invoiceService = new InvoiceServiceImpl();
    private final NotificationService notificationService = new NotificationServiceImpl();
    private final RequestService requestService = new RequestServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            UserSessionDTO currentUser = getCurrentUser(req);
            
            Optional<Room> roomOpt = tenantService.getTenantRoom(currentUser.getId());
            if (roomOpt.isEmpty()) {
                req.setAttribute("roomCode", "Chưa xếp phòng");
                req.setAttribute("facilityName", "N/A");
                req.getRequestDispatcher("/WEB-INF/views/tenant/dashboard.jsp").forward(req, resp);
                return;
            }

            Room room = roomOpt.get();
            Optional<Facility> facilityOpt = tenantService.getFacilityByRoomId(room.getId());
            
            req.setAttribute("roomCode", room.getCode());
            req.setAttribute("facilityName", facilityOpt.map(Facility::getName).orElse("N/A"));

            // KPI
            BigDecimal unpaidAmount = invoiceService.getUnpaidTotal(room.getId());
            req.setAttribute("unpaidAmount", unpaidAmount);

            Optional<Invoice> currentInvoiceOpt = invoiceService.getCurrentInvoice(room.getId());
            if (currentInvoiceOpt.isPresent()) {
                req.setAttribute("currentInvoice", currentInvoiceOpt.get());
                req.setAttribute("dueDateLabel", currentInvoiceOpt.get().getDueDateLabel());
            }

            int facilityId = facilityOpt.map(Facility::getId).orElse(0);
            
            int unreadCount = notificationService.countUnreadNotifications(room.getId(), facilityId, null);
            req.setAttribute("unreadNotifications", unreadCount);

            int pendingTickets = requestService.countPendingRequests(currentUser.getId());
            req.setAttribute("pendingTickets", pendingTickets);

            // Latest notifications
            List<Notification> notifications = notificationService.getNotificationsForTenant(room.getId(), facilityId);
            if (notifications.size() > 3) {
                notifications = notifications.subList(0, 3);
            }
            req.setAttribute("latestNotifications", notifications);

            req.getRequestDispatcher("/WEB-INF/views/tenant/dashboard.jsp").forward(req, resp);
        } catch (Exception e) {
            forwardError(req, resp, "/WEB-INF/views/tenant/dashboard.jsp", e);
        }
    }
}
