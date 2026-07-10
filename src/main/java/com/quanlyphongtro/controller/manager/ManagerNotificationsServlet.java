package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.util.AuditLogHelper;
import com.quanlyphongtro.service.NotificationService;
import com.quanlyphongtro.service.impl.NotificationServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ManagerNotificationsServlet", urlPatterns = {
        "/manager/notifications",
        "/manager/notifications/*"
})
public class ManagerNotificationsServlet extends BaseServlet {

    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final NotificationService notificationService = new NotificationServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || "/".equals(pathInfo)) {
            String action = req.getParameter("action");
            if ("report-incorrect".equals(action)) {
                handleReportIncorrect(req, resp);
            } else {
                handleList(req, resp);
            }
        } else if ("/create".equals(pathInfo)) {
            handleCreateForm(req, resp);
        } else if ("/send-operator".equals(pathInfo)) {
            handleSendOperatorForm(req, resp);
        } else if ("/send-debt-reminder".equals(pathInfo)) {
            handleSendDebtReminderForm(req, resp);
        } else {
            String idStr = pathInfo.substring(1);
            try {
                int notificationId = Integer.parseInt(idStr);
                handleDetail(notificationId, req, resp);
            } catch (NumberFormatException e) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if ("/create".equals(pathInfo)) {
            handleCreateSubmit(req, resp);
        } else if ("/send-operator".equals(pathInfo)) {
            handleSendOperatorSubmit(req, resp);
        } else if ("/send-debt-reminder".equals(pathInfo)) {
            handleSendDebtReminderSubmit(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String keyword = req.getParameter("keyword");
        String facilityIdStr = req.getParameter("facilityId");
        Integer filterFacilityId = null;
        if (facilityIdStr != null && !facilityIdStr.trim().isEmpty()) {
            try {
                filterFacilityId = Integer.parseInt(facilityIdStr.trim());
            } catch (NumberFormatException e) {
                // ignore
            }
        }

        int page = 1;
        String pageStr = req.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (Exception e) {
                page = 1;
            }
        }
        int pageSize = 10;

        String tab = req.getParameter("tab");
        if (tab == null || tab.trim().isEmpty()) {
            tab = "general";
        }

        String type = req.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "received";
        }

        int totalCount = notificationService.countManagerNotifications(currentUser.getId(), tab, type, filterFacilityId, keyword);
        List<Map<String, Object>> notifications = notificationService.getManagerNotifications(currentUser.getId(), tab, type, filterFacilityId, keyword, page, pageSize);

        List<Map<String, Object>> assignedFacilities = notificationService.getAssignedFacilitiesForManager(currentUser.getId());
        List<Map<String, Object>> incorrectInvoices = notificationService.getReportedIncorrectInvoices(currentUser.getId(), filterFacilityId, keyword);

        int totalPages = totalCount > 0 ? (int) Math.ceil((double) totalCount / pageSize) : 1;

        Map<String, Object> pageObj = new HashMap<>();
        pageObj.put("items", notifications);
        pageObj.put("total", totalCount);
        pageObj.put("page", page);
        pageObj.put("totalPages", totalPages);

        req.setAttribute("page", pageObj);
        req.setAttribute("keyword", keyword);
        req.setAttribute("assignedFacilities", assignedFacilities);
        req.setAttribute("filterFacilityId", filterFacilityId);
        req.setAttribute("tab", tab);
        req.setAttribute("type", type);
        req.setAttribute("incorrectInvoices", incorrectInvoices);

        req.getRequestDispatcher("/WEB-INF/views/manager/notifications/list.jsp").forward(req, resp);
    }

    private void handleCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (req.getAttribute("dto") == null) {
            String pRecipientType = req.getParameter("recipientType");
            String pRoomIdStr = req.getParameter("roomId");
            String pFacilityIdStr = req.getParameter("facilityId");
            String pTitle = req.getParameter("title");
            String pContent = req.getParameter("content");
            boolean pIsDebtReminder = "true".equals(req.getParameter("isDebtReminder")) || pRoomIdStr != null;

            Integer pRecipientId = null;
            Integer pFacilityId = null;

            if (pRoomIdStr != null && !pRoomIdStr.trim().isEmpty()) {
                try {
                    pRecipientId = Integer.parseInt(pRoomIdStr.trim());
                    pRecipientType = "ROOM";
                    
                    // Call service to resolve facility for room
                    com.quanlyphongtro.dao.NotificationDAO ndao = new com.quanlyphongtro.dao.NotificationDAO();
                    pFacilityId = ndao.getRoomFacilityId(pRecipientId);
                } catch (Exception e) {
                    logger.error("Failed to resolve facility for room", e);
                }
            } else if (pFacilityIdStr != null && !pFacilityIdStr.trim().isEmpty()) {
                try {
                    pRecipientId = Integer.parseInt(pFacilityIdStr.trim());
                    pRecipientType = "FACILITY";
                    pFacilityId = pRecipientId;
                } catch (Exception e) {
                    // ignore
                }
            }

            if (pRecipientType != null || pTitle != null || pContent != null || pIsDebtReminder) {
                req.setAttribute("dto", buildDto(pTitle, pContent, pRecipientType, pRecipientId, pFacilityId, pIsDebtReminder));
            }
        }

        List<Map<String, Object>> assignedFacilities = notificationService.getAssignedFacilitiesForManager(currentUser.getId());
        List<Map<String, Object>> assignedRooms = notificationService.getAssignedRoomsForManager(currentUser.getId());

        req.setAttribute("assignedFacilities", assignedFacilities);
        req.setAttribute("assignedRooms", assignedRooms);
        req.getRequestDispatcher("/WEB-INF/views/manager/notifications/create.jsp").forward(req, resp);
    }

    private void handleCreateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String title = req.getParameter("title");
        String content = req.getParameter("content");
        String recipientType = req.getParameter("recipientType");
        String recipientIdStr = req.getParameter("recipientId");
        String facilityIdForRoomStr = req.getParameter("facilityId_for_room");
        boolean isDebtReminder = "true".equals(req.getParameter("isDebtReminder"));

        Integer facilityIdForRoom = null;
        if (facilityIdForRoomStr != null && !facilityIdForRoomStr.trim().isEmpty()) {
            try {
                facilityIdForRoom = Integer.parseInt(facilityIdForRoomStr.trim());
            } catch (Exception e) {
                // ignore
            }
        }

        if (title == null || content == null || recipientType == null || title.trim().isEmpty() || content.trim().isEmpty()) {
            req.setAttribute("errorMessage", "Vui lòng điền đầy đủ các trường bắt buộc.");
            Integer rId = null;
            try {
                if (recipientIdStr != null) rId = Integer.parseInt(recipientIdStr.trim());
            } catch (Exception e) {}
            req.setAttribute("dto", buildDto(title, content, recipientType, rId, facilityIdForRoom, isDebtReminder));
            handleCreateForm(req, resp);
            return;
        }

        Integer recipientId = null;
        if (recipientIdStr != null && !recipientIdStr.trim().isEmpty()) {
            try {
                recipientId = Integer.parseInt(recipientIdStr.trim());
            } catch (NumberFormatException e) {
                req.setAttribute("errorMessage", "Đối tượng nhận không hợp lệ.");
                req.setAttribute("dto", buildDto(title, content, recipientType, null, null));
                handleCreateForm(req, resp);
                return;
            }
        }

        try {
            boolean success = notificationService.sendNotification(title, content, recipientType, recipientId, facilityIdForRoom, currentUser.getId());
            if (success) {
                // To get generated notification ID for audit log, we can select latest notification by this manager.
                // However, since AuditLogHelper.log doesn't require a strict non-null audit ID if not available, we can skip or use dummy ID.
                // Let's pass a dummy -1 or retrieve the latest. Let's just log with a general indicator.
                try {
                    AuditLogHelper.log(auditLogDAO, req, "notifications", 0, "CREATE", null, title.trim(), currentUser.getId());
                } catch (Exception ex) {
                    logger.warn("AuditLog failed after create notification", ex);
                }
                setFlashMessage(req, "success", "Gửi thông báo thành công!");
            } else {
                req.setAttribute("errorMessage", "Không thể gửi thông báo.");
                req.setAttribute("dto", buildDto(title, content, recipientType, recipientId, facilityIdForRoom, isDebtReminder));
                handleCreateForm(req, resp);
                return;
            }
        } catch (java.nio.file.AccessDeniedException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("dto", buildDto(title, content, recipientType, recipientId, facilityIdForRoom, isDebtReminder));
            handleCreateForm(req, resp);
            return;
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.setAttribute("dto", buildDto(title, content, recipientType, recipientId, facilityIdForRoom, isDebtReminder));
            handleCreateForm(req, resp);
            return;
        } catch (Exception e) {
            logger.error("Failed to send notification", e);
            req.setAttribute("errorMessage", "Lỗi gửi thông báo: " + e.getMessage());
            req.setAttribute("dto", buildDto(title, content, recipientType, recipientId, facilityIdForRoom, isDebtReminder));
            handleCreateForm(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=" + (isDebtReminder ? "payment-reminder" : "general&type=sent"));
    }

    private Map<String, Object> buildDto(String title, String content, String recipientType, Integer recipientId, Integer facilityId) {
        return buildDto(title, content, recipientType, recipientId, facilityId, false);
    }

    private Map<String, Object> buildDto(String title, String content, String recipientType, Integer recipientId, Integer facilityId, boolean isDebtReminder) {
        Map<String, Object> dto = new HashMap<>();
        dto.put("title", title);
        dto.put("content", content);
        dto.put("recipientType", recipientType);
        dto.put("recipientId", recipientId);
        dto.put("facilityId", facilityId);
        dto.put("isDebtReminder", isDebtReminder);
        return dto;
    }

    private void handleDetail(int notificationId, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Map<String, Object> notification = null;
        try {
            notification = notificationService.getNotificationDetail(notificationId, currentUser.getId());
        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("Failed to query notification detail", e);
        }

        if (notification == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        req.setAttribute("notification", notification);
        req.getRequestDispatcher("/WEB-INF/views/manager/notifications/detail.jsp").forward(req, resp);
    }

    private void handleReportIncorrect(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        if (invoiceIdStr == null || invoiceIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã hóa đơn.");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            
            // Get invoice code for success message
            Map<String, Object> verify = notificationService.getInvoiceDetailsForSendOperator(invoiceId, currentUser.getId());
            String invoiceCode = (String) verify.get("code");

            boolean success = notificationService.reportIncorrectInvoice(invoiceId, currentUser.getId());
            if (success) {
                setFlashMessage(req, "success", "Đã báo cáo sai số điện nước cho hóa đơn " + invoiceCode + ". Vui lòng gửi thông báo cho Operator.");
                resp.sendRedirect(req.getContextPath() + "/manager/notifications/send-operator?invoiceId=" + invoiceId);
            } else {
                setFlashMessage(req, "danger", "Không thể báo cáo hóa đơn.");
                resp.sendRedirect(req.getContextPath() + "/manager/notifications");
            }
        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalStateException e) {
            setFlashMessage(req, "danger", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/manager/notifications");
        } catch (IllegalArgumentException e) {
            setFlashMessage(req, "danger", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/manager/notifications");
        } catch (Exception e) {
            logger.error("Failed to report incorrect invoice", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSendOperatorForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        if (invoiceIdStr == null || invoiceIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã hóa đơn.");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            Map<String, Object> invoice = notificationService.getInvoiceDetailsForSendOperator(invoiceId, currentUser.getId());
            List<Map<String, Object>> operators = notificationService.getActiveOperatorsForFacility((Integer) invoice.get("facilityId"));

            String defaultTitle = "Báo cáo sai số điện nước - Phòng " + invoice.get("roomCode");
            String defaultContent = "Kính gửi nhân viên vận hành,\n\nHóa đơn kỳ " + invoice.get("billingPeriod") + 
                    " của phòng " + invoice.get("roomCode") + " thuộc cơ sở " + invoice.get("facilityName") + 
                    " được phát hiện bị nhập sai chỉ số điện nước.\n\nThông tin hiện tại:\n" +
                    "- Chỉ số điện: " + invoice.get("electric") + " kWh\n" +
                    "- Chỉ số nước: " + invoice.get("water") + " m3\n\n" +
                    "Vui lòng kiểm tra thực tế, xác minh lại hình ảnh và cập nhật chỉ số chính xác.";

            req.setAttribute("invoice", invoice);
            req.setAttribute("operators", operators);
            req.setAttribute("defaultTitle", defaultTitle);
            req.setAttribute("defaultContent", defaultContent);

            req.getRequestDispatcher("/WEB-INF/views/manager/notifications/send_operator.jsp").forward(req, resp);

        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to prepare send operator form", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSendOperatorSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        String operatorIdStr = req.getParameter("operatorId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        if (invoiceIdStr == null || operatorIdStr == null || title == null || content == null || 
                invoiceIdStr.trim().isEmpty() || operatorIdStr.trim().isEmpty() || title.trim().isEmpty() || content.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng nhập đầy đủ tất cả các trường.");
            resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=incorrect-utility");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            int operatorId = Integer.parseInt(operatorIdStr.trim());

            boolean success = notificationService.sendOperatorRequest(invoiceId, operatorId, title, content, currentUser.getId());
            if (success) {
                setFlashMessage(req, "success", "Đã gửi thông báo yêu cầu sửa chỉ số điện nước cho Operator thành công!");
            } else {
                setFlashMessage(req, "danger", "Gửi thông báo thất bại.");
            }
            resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=incorrect-utility");

        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to send operator request", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSendDebtReminderForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        if (invoiceIdStr == null || invoiceIdStr.trim().isEmpty()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Thiếu mã hóa đơn.");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());
            Map<String, Object> invoice = notificationService.getInvoiceDetailsForSendDebt(invoiceId, currentUser.getId());

            String defaultTitle = "Nhắc đóng tiền phòng quá hạn - Phòng " + invoice.get("roomCode");
            String defaultContent = "Kính gửi thành viên phòng " + invoice.get("roomCode") + ",\n\n" +
                    "Hóa đơn tháng " + invoice.get("billingPeriod") + " của phòng bạn đã quá hạn thanh toán.\n" +
                    "Chi tiết khoản nợ:\n" +
                    "- Số tiền cần đóng: " + String.format("%,.0f", invoice.get("totalAmount")) + " đ\n" +
                    "- Hạn thanh toán: " + invoice.get("dueDateLabel") + "\n" +
                    "- Số ngày quá hạn: " + invoice.get("overdueDays") + " ngày\n\n" +
                    "Vui lòng thanh toán sớm nhất có thể để tránh phát sinh thêm phí phạt quá hạn hoặc các gián đoạn dịch vụ.\n" +
                    "Xin cảm ơn!";

            req.setAttribute("invoice", invoice);
            req.setAttribute("defaultTitle", defaultTitle);
            req.setAttribute("defaultContent", defaultContent);

            req.getRequestDispatcher("/WEB-INF/views/manager/notifications/send_debt_reminder.jsp").forward(req, resp);

        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to prepare send debt reminder form", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleSendDebtReminderSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        String invoiceIdStr = req.getParameter("invoiceId");
        String title = req.getParameter("title");
        String content = req.getParameter("content");

        if (invoiceIdStr == null || title == null || content == null || 
                invoiceIdStr.trim().isEmpty() || title.trim().isEmpty() || content.trim().isEmpty()) {
            setFlashMessage(req, "danger", "Vui lòng điền đầy đủ tiêu đề và nội dung nhắc nợ.");
            resp.sendRedirect(req.getContextPath() + "/manager/debts");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(invoiceIdStr.trim());

            boolean success = notificationService.sendDebtReminder(invoiceId, title, content, currentUser.getId());
            if (success) {
                setFlashMessage(req, "success", "Gửi nhắc nhở thanh toán thành công!");
            } else {
                setFlashMessage(req, "danger", "Gửi nhắc nhở thanh toán thất bại.");
            }
            resp.sendRedirect(req.getContextPath() + "/manager/notifications?tab=payment-reminder");

        } catch (java.nio.file.AccessDeniedException e) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
        } catch (IllegalArgumentException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to send debt reminder", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
