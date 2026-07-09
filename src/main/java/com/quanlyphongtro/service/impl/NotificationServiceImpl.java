package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    @Override
    public List<Notification> getNotificationsForTenant(int roomId, int facilityId, int page, int pageSize) {
        return notificationDAO.findForTenant(roomId, facilityId, null, page, pageSize);
    }

    @Override
    public List<Notification> getNotificationsForTenant(int roomId, int facilityId, String keyword, int page, int pageSize) {
        return notificationDAO.findForTenant(roomId, facilityId, keyword, page, pageSize);
    }

    @Override
    public int countNotificationsForTenant(int roomId, int facilityId) {
        return notificationDAO.countForTenant(roomId, facilityId, null);
    }

    @Override
    public int countNotificationsForTenant(int roomId, int facilityId, String keyword) {
        return notificationDAO.countForTenant(roomId, facilityId, keyword);
    }

    @Override
    public Optional<Notification> getNotificationById(int id, int roomId, int facilityId) {
        return notificationDAO.findByIdForTenant(id, roomId, facilityId);
    }

    @Override
    public int countUnreadNotifications(int roomId, int facilityId, LocalDateTime lastReadTime) {
        return notificationDAO.countUnreadForTenant(roomId, facilityId, lastReadTime);
    }

    @Override
    public int countManagerNotifications(int managerId, String tab, String type, Integer filterFacilityId, String keyword) {
        return notificationDAO.countManagerNotifications(managerId, tab, type, filterFacilityId, keyword);
    }

    @Override
    public List<Map<String, Object>> getManagerNotifications(int managerId, String tab, String type, Integer filterFacilityId, String keyword, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return notificationDAO.getManagerNotifications(managerId, tab, type, filterFacilityId, keyword, offset, pageSize);
    }

    @Override
    public List<Map<String, Object>> getAssignedFacilitiesForManager(int managerId) {
        return notificationDAO.getAssignedFacilitiesForManager(managerId);
    }

    @Override
    public List<Map<String, Object>> getReportedIncorrectInvoices(int managerId, Integer filterFacilityId, String keyword) {
        return notificationDAO.getReportedIncorrectInvoices(managerId, filterFacilityId, keyword);
    }

    @Override
    public List<Map<String, Object>> getAssignedRoomsForManager(int managerId) {
        return notificationDAO.getAssignedRoomsForManager(managerId);
    }

    @Override
    public boolean sendNotification(String title, String content, String recipientType, Integer recipientId, Integer facilityIdForRoom, int managerId) throws Exception {
        Integer facilityId = null;
        Integer roomId = null;

        if ("FACILITY".equals(recipientType)) {
            if (recipientId == null) {
                throw new IllegalArgumentException("Vui lòng chọn cơ sở nhận thông báo.");
            }
            facilityId = recipientId;
            // Verify manager manages this facility
            if (!notificationDAO.verifyFacilityManager(facilityId, managerId)) {
                throw new java.nio.file.AccessDeniedException("Bạn không có quyền gửi thông báo đến cơ sở này.");
            }
        } else if ("ROOM".equals(recipientType)) {
            if (recipientId == null) {
                throw new IllegalArgumentException("Vui lòng chọn phòng nhận thông báo.");
            }
            roomId = recipientId;
            // Verify room exists and belongs to a facility managed by this manager
            Integer facId = notificationDAO.verifyRoomManagerAndGetFacilityId(roomId, managerId);
            if (facId == null) {
                throw new java.nio.file.AccessDeniedException("Bạn không có quyền gửi thông báo đến phòng này.");
            }
        } else {
            throw new IllegalArgumentException("Đối tượng nhận không hợp lệ.");
        }

        // Generate unique code
        String code = notificationDAO.generateCode(recipientType);
        return notificationDAO.insertNotificationAndGetId(code, title, content, recipientType, facilityId, roomId, managerId) > 0;
    }

    @Override
    public Map<String, Object> getNotificationDetail(int notificationId, int managerId) throws Exception {
        Map<String, Object> notification = notificationDAO.getNotificationDetail(notificationId);
        if (notification == null) {
            return null;
        }

        int creatorId = (Integer) notification.get("created_by");
        Integer targetFacilityManagerId = (Integer) notification.get("target_facility_manager_id");
        Integer targetRoomFacilityManagerId = (Integer) notification.get("target_room_facility_manager_id");
        String targetType = (String) notification.get("recipientType");

        boolean hasAccess = (creatorId == managerId) 
                         || (targetFacilityManagerId != null && targetFacilityManagerId == managerId)
                         || (targetRoomFacilityManagerId != null && targetRoomFacilityManagerId == managerId)
                         || "ALL".equals(targetType);

        if (!hasAccess) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền xem thông báo này.");
        }

        return notification;
    }

    @Override
    public boolean reportIncorrectInvoice(int invoiceId, int managerId) throws Exception {
        Map<String, Object> verify = notificationDAO.getInvoiceVerifyDetails(invoiceId);
        if (verify == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn.");
        }

        int ownerManagerId = (Integer) verify.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền báo cáo hóa đơn này.");
        }

        String invoiceStatus = (String) verify.get("status");
        if ("PAID".equals(invoiceStatus)) {
            throw new IllegalStateException("Không thể báo cáo sai số cho hóa đơn đã thanh toán.");
        }

        int meterId = (Integer) verify.get("meterId");
        if (meterId <= 0) {
            throw new IllegalArgumentException("Hóa đơn không liên kết với chỉ số điện nước hợp lệ.");
        }

        return notificationDAO.updateMeterReadingStatus(meterId, "INCORRECT");
    }

    @Override
    public Map<String, Object> getInvoiceDetailsForSendOperator(int invoiceId, int managerId) throws Exception {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendOperator(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn.");
        }

        int ownerManagerId = (Integer) invoice.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền thực hiện thao tác này.");
        }

        return invoice;
    }

    @Override
    public List<Map<String, Object>> getActiveOperatorsForFacility(int facilityId) {
        return notificationDAO.getActiveOperatorsForFacility(facilityId);
    }

    @Override
    public boolean sendOperatorRequest(int invoiceId, int operatorId, String title, String content, int managerId) throws Exception {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendOperator(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn.");
        }

        int ownerManagerId = (Integer) invoice.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền thực hiện thao tác này.");
        }

        int meterId = (Integer) invoice.get("meter_id");

        // Create unique request code: REQ-UTL-XXXX
        String reqCode = "REQ-UTL-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return notificationDAO.sendOperatorRequestTransaction(reqCode, managerId, title, content, operatorId, meterId);
    }

    @Override
    public Map<String, Object> getInvoiceDetailsForSendDebt(int invoiceId, int managerId) throws Exception {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendDebt(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn.");
        }

        int ownerManagerId = (Integer) invoice.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền thực hiện thao tác này.");
        }

        return invoice;
    }

    @Override
    public boolean sendDebtReminder(int invoiceId, String title, String content, int managerId) throws Exception {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendDebt(invoiceId);
        if (invoice == null) {
            throw new IllegalArgumentException("Không tìm thấy hóa đơn.");
        }

        int ownerManagerId = (Integer) invoice.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không có quyền thực hiện thao tác này.");
        }

        int roomId = (Integer) invoice.get("roomId");

        // Generate unique code: NTF-DEBT-XXXX
        String code = notificationDAO.generateCode("DEBT");

        return notificationDAO.sendDebtReminder(code, title, content, roomId, managerId);
    }
}
