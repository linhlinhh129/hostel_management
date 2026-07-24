package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.NotificationService;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class NotificationServiceImpl implements NotificationService {

    private final NotificationDAO notificationDAO = new NotificationDAO();

    // ── Tenant scope ─────────────────────────────────────────────────────

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

    // ── Admin scope ───────────────────────────────────────────────────────

    @Override
    public PageDTO<Notification> getAdminNotifications(String keyword, int page, int pageSize) {
        int total = notificationDAO.count(keyword);
        List<Notification> items = notificationDAO.findAll(keyword, page, pageSize);
        return new PageDTO<>(items, page, pageSize, total);
    }

    @Override
    public Optional<Notification> getAdminNotificationById(int id) {
        return notificationDAO.findById(id);
    }

    @Override
    public void createAdminNotification(String title, String content, Integer createdBy)
            throws ValidationException {
        if (title == null || title.isBlank())
            throw new ValidationException("Tiêu đề không được để trống.");
        if (title.length() > 255)
            throw new ValidationException("Tiêu đề không được vượt quá 255 ký tự.");
        if (content == null || content.isBlank())
            throw new ValidationException("Nội dung không được để trống.");
        if (content.length() > 1000)
            throw new ValidationException("Nội dung không được vượt quá 1000 ký tự.");

        String code = notificationDAO.generateCode("ALL");

        Notification n = new Notification();
        n.setCode(code);
        n.setTitle(title.trim());
        n.setContent(content.trim());
        n.setTargetType("ALL");
        n.setStatus("SENT");
        n.setCreatedBy(createdBy);

        notificationDAO.insert(n);
    }

    // ── Manager scope ─────────────────────────────────────────────────────

    @Override
    public int countManagerNotifications(Integer managerId, String tab, String type,
                                         Integer filterFacilityId, String keyword) {
        return notificationDAO.countManagerNotifications(managerId, tab, type, filterFacilityId, keyword);
    }

    @Override
    public List<Map<String, Object>> getManagerNotifications(Integer managerId, String tab, String type,
                                                             Integer filterFacilityId, String keyword,
                                                             int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return notificationDAO.getManagerNotifications(managerId, tab, type, filterFacilityId, keyword, offset, pageSize);
    }

    @Override
    public List<Map<String, Object>> getAssignedFacilitiesForManager(Integer managerId) {
        return notificationDAO.getAssignedFacilitiesForManager(managerId);
    }

    @Override
    public List<Map<String, Object>> getAssignedRoomsForManager(Integer managerId) {
        return notificationDAO.getAssignedRoomsForManager(managerId);
    }

    @Override
    public List<Map<String, Object>> getReportedIncorrectInvoices(Integer managerId,
                                                                   Integer filterFacilityId,
                                                                   String keyword) {
        return notificationDAO.getReportedIncorrectInvoices(managerId, filterFacilityId, keyword);
    }

    @Override
    public boolean sendNotification(String title, String content, String recipientType,
                                    Integer recipientId, Integer facilityIdForRoom,
                                    Integer createdBy) throws AccessDeniedException {
        if (title == null || title.isBlank() || content == null || content.isBlank()
                || recipientType == null) {
            throw new IllegalArgumentException("Tiêu đề, nội dung và loại đối tượng nhận không được để trống.");
        }

        Integer facilityId = null;
        Integer roomId = null;
        String targetType;

        switch (recipientType.toUpperCase()) {
            case "FACILITY":
                if (recipientId == null)
                    throw new IllegalArgumentException("Vui lòng chọn cơ sở nhận thông báo.");
                if (!notificationDAO.verifyFacilityManager(recipientId, createdBy))
                    throw new AccessDeniedException("Bạn không có quyền gửi thông báo đến cơ sở này.");
                facilityId = recipientId;
                targetType = "FACILITY";
                break;
            case "ROOM":
                if (recipientId == null)
                    throw new IllegalArgumentException("Vui lòng chọn phòng nhận thông báo.");
                // Bước 1: kiểm tra phòng có thuộc cơ sở manager quản lý không (bỏ điều kiện tenant)
                Integer verifiedFacilityId = notificationDAO.verifyRoomManagerAndGetFacilityId(recipientId, createdBy);
                if (verifiedFacilityId == null) {
                    // Phân biệt: phòng không thuộc cơ sở vs phòng không có tenant
                    Integer facilityIdOfRoom = notificationDAO.getRoomFacilityId(recipientId);
                    if (facilityIdOfRoom == null) {
                        throw new IllegalArgumentException("Phòng không tồn tại.");
                    }
                    // Phòng tồn tại nhưng không có tenant → verifyRoomManagerAndGetFacilityId trả null
                    // Thử lại không có điều kiện tenant để phân biệt lỗi quyền vs lỗi tenant
                    if (!notificationDAO.verifyFacilityManager(facilityIdOfRoom, createdBy)) {
                        throw new AccessDeniedException("Bạn không có quyền gửi thông báo đến phòng này.");
                    }
                    throw new IllegalArgumentException("Phòng này chưa có người thuê. Không thể gửi thông báo.");
                }
                roomId = recipientId;
                facilityId = (facilityIdForRoom != null) ? facilityIdForRoom : verifiedFacilityId;
                targetType = "ROOM";
                break;
            default:
                throw new IllegalArgumentException("Loại đối tượng nhận không hợp lệ: " + recipientType);
        }

        String code = notificationDAO.generateCode(targetType);  // generateCode maps FACILITY→FAC, ROOM→ROOM
        // CHECK CONSTRAINT: khi target_type='ROOM' thì facility_id phải NULL; khi 'FACILITY' thì room_id phải NULL
        Integer insertFacilityId = "ROOM".equals(targetType) ? null : facilityId;
        Integer insertRoomId = "FACILITY".equals(targetType) ? null : roomId;
        int id = notificationDAO.insertNotificationAndGetId(code, title, content, targetType,
                insertFacilityId, insertRoomId, createdBy);
        return id > 0;
    }

    @Override
    public Map<String, Object> getNotificationDetail(int notificationId, Integer managerId)
            throws AccessDeniedException {
        Map<String, Object> notification = notificationDAO.getNotificationDetail(notificationId);
        if (notification == null) return null;

        // Kiểm tra quyền: manager chỉ được xem thông báo do mình tạo
        // hoặc thông báo gửi đến cơ sở/phòng mà mình phụ trách
        Object createdBy = notification.get("created_by");
        Object targetFacMgr = notification.get("target_facility_manager_id");
        Object targetRoomFacMgr = notification.get("target_room_facility_manager_id");

        boolean isCreator = managerId.equals(createdBy);
        boolean isTargetManager = managerId.equals(targetFacMgr) || managerId.equals(targetRoomFacMgr);
        // ALL type: accessible to manager (sent by admin)
        String recipientType = (String) notification.get("recipientType");
        boolean isAllType = "ALL".equals(recipientType);

        if (!isCreator && !isTargetManager && !isAllType) {
            throw new AccessDeniedException("Bạn không có quyền xem thông báo này.");
        }
        return notification;
    }

    @Override
    public boolean reportIncorrectInvoice(int invoiceId, Integer managerId)
            throws AccessDeniedException {
        Map<String, Object> invoice = notificationDAO.getInvoiceVerifyDetails(invoiceId);
        if (invoice == null)
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");

        Object invManagerId = invoice.get("managerId");
        if (!managerId.equals(invManagerId))
            throw new AccessDeniedException("Bạn không có quyền báo cáo hóa đơn này.");

        String status = (String) invoice.get("status");
        if ("PAID".equals(status))
            throw new IllegalStateException("Không thể báo cáo hóa đơn đã thanh toán.");

        int meterId = (int) invoice.get("meterId");
        return notificationDAO.updateMeterReadingStatus(meterId, "REPORTED");
    }

    @Override
    public Map<String, Object> getInvoiceDetailsForSendOperator(int invoiceId, Integer managerId)
            throws AccessDeniedException {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendOperator(invoiceId);
        if (invoice == null)
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");

        Object invManagerId = invoice.get("managerId");
        if (!managerId.equals(invManagerId))
            throw new AccessDeniedException("Bạn không có quyền truy cập hóa đơn này.");

        return invoice;
    }

    @Override
    public List<Map<String, Object>> getActiveOperatorsForFacility(Integer facilityId) {
        return notificationDAO.getActiveOperatorsForFacility(facilityId);
    }

    @Override
    public boolean sendOperatorRequest(int invoiceId, int operatorId, String title, String content,
                                       Integer managerId) throws AccessDeniedException {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendOperator(invoiceId);
        if (invoice == null)
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");

        Object invManagerId = invoice.get("managerId");
        if (!managerId.equals(invManagerId))
            throw new AccessDeniedException("Bạn không có quyền gửi yêu cầu cho hóa đơn này.");

        // Lấy meterId từ bảng invoices qua verify details
        Map<String, Object> verify = notificationDAO.getInvoiceVerifyDetails(invoiceId);
        if (verify == null)
            throw new IllegalArgumentException("Không tìm thấy thông tin chỉ số điện nước.");

        int meterId = (int) verify.get("meterId");

        // Sinh code request
        String reqCode = "REQ-UTL-" + System.currentTimeMillis() % 100000;
        return notificationDAO.sendOperatorRequestTransaction(reqCode, managerId, title, content, operatorId, meterId);
    }

    @Override
    public Map<String, Object> getInvoiceDetailsForSendDebt(int invoiceId, Integer managerId)
            throws AccessDeniedException {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendDebt(invoiceId);
        if (invoice == null)
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");

        Object invManagerId = invoice.get("managerId");
        if (!managerId.equals(invManagerId))
            throw new AccessDeniedException("Bạn không có quyền truy cập hóa đơn này.");

        return invoice;
    }

    @Override
    public boolean sendDebtReminder(int invoiceId, String title, String content, Integer managerId)
            throws AccessDeniedException {
        Map<String, Object> invoice = notificationDAO.getInvoiceDetailsForSendDebt(invoiceId);
        if (invoice == null)
            throw new IllegalArgumentException("Hóa đơn không tồn tại.");

        Object invManagerId = invoice.get("managerId");
        if (!managerId.equals(invManagerId))
            throw new AccessDeniedException("Bạn không có quyền gửi nhắc nợ cho hóa đơn này.");

        Integer roomId = (Integer) invoice.get("roomId");
        if (roomId == null)
            throw new IllegalArgumentException("Phòng không hợp lệ.");

        String code = notificationDAO.generateCode("DEBT");
        return notificationDAO.sendDebtReminder(code, title, content, roomId, managerId);
    }
}
