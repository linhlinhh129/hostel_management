package com.quanlyphongtro.service;
import com.quanlyphongtro.exception.ValidationException;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.model.Notification;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface NotificationService {

    // ── Tenant scope ─────────────────────────────────────────────────────
    List<Notification> getNotificationsForTenant(int roomId, int facilityId, int page, int pageSize);
    List<Notification> getNotificationsForTenant(int roomId, int facilityId, String keyword, int page, int pageSize);
    int countNotificationsForTenant(int roomId, int facilityId);
    int countNotificationsForTenant(int roomId, int facilityId, String keyword);
    Optional<Notification> getNotificationById(int id, int roomId, int facilityId);
    int countUnreadNotifications(int roomId, int facilityId, LocalDateTime lastReadTime);

    // ── Admin scope ───────────────────────────────────────────────────────
    /** Lấy danh sách thông báo (target_type = ALL) phân trang cho Admin. */
    PageDTO<Notification> getAdminNotifications(String keyword, int page, int pageSize);

    /** Lấy chi tiết thông báo theo id cho Admin. */
    Optional<Notification> getAdminNotificationById(int id);

    /**
     * Tạo thông báo mới và gửi ngay (status = SENT).
     * @throws ValidationException nếu dữ liệu không hợp lệ.
     */
    void createAdminNotification(String title, String content, Integer createdBy)
            throws ValidationException;

    // ── Manager scope ─────────────────────────────────────────────────────

    /** Đếm thông báo cho Manager theo tab/type/filter. */
    int countManagerNotifications(Integer managerId, String tab, String type,
                                  Integer filterFacilityId, String keyword);

    /** Lấy danh sách thông báo cho Manager có phân trang. */
    List<Map<String, Object>> getManagerNotifications(Integer managerId, String tab, String type,
                                                      Integer filterFacilityId, String keyword,
                                                      int page, int pageSize);

    /** Lấy danh sách cơ sở mà manager phụ trách (để render filter). */
    List<Map<String, Object>> getAssignedFacilitiesForManager(Integer managerId);

    /** Lấy danh sách phòng (có tenant) thuộc cơ sở manager phụ trách. */
    List<Map<String, Object>> getAssignedRoomsForManager(Integer managerId);

    /** Lấy danh sách hóa đơn bị báo sai chỉ số điện nước. */
    List<Map<String, Object>> getReportedIncorrectInvoices(Integer managerId,
                                                           Integer filterFacilityId, String keyword);

    /**
     * Gửi thông báo mới (FACILITY / ROOM / ALL) từ Manager.
     *
     * @throws AccessDeniedException  nếu manager không có quyền gửi đến đối tượng đó
     * @throws IllegalArgumentException nếu recipientType hoặc recipientId không hợp lệ
     */
    boolean sendNotification(String title, String content, String recipientType,
                             Integer recipientId, Integer facilityIdForRoom,
                             Integer createdBy) throws AccessDeniedException;

    /**
     * Lấy chi tiết thông báo cho Manager.
     *
     * @throws AccessDeniedException nếu manager không có quyền xem thông báo này
     */
    Map<String, Object> getNotificationDetail(int notificationId, Integer managerId)
            throws AccessDeniedException;

    /**
     * Báo cáo hóa đơn bị sai chỉ số điện nước (đổi meter_reading.status = REPORTED).
     *
     * @throws AccessDeniedException  nếu manager không phụ trách cơ sở chứa hóa đơn
     * @throws IllegalStateException  nếu hóa đơn đã ở trạng thái không thể báo cáo
     * @throws IllegalArgumentException nếu invoiceId không tồn tại
     */
    boolean reportIncorrectInvoice(int invoiceId, Integer managerId)
            throws AccessDeniedException;

    /** Lấy thông tin hóa đơn để chuẩn bị form gửi yêu cầu sửa cho Operator. */
    Map<String, Object> getInvoiceDetailsForSendOperator(int invoiceId, Integer managerId)
            throws AccessDeniedException;

    /** Lấy danh sách Operator đang ACTIVE thuộc cơ sở. */
    List<Map<String, Object>> getActiveOperatorsForFacility(Integer facilityId);

    /**
     * Gửi yêu cầu sửa chỉ số điện nước cho Operator (tạo request + cập nhật meter_reading).
     *
     * @throws AccessDeniedException  nếu manager không có quyền
     * @throws IllegalArgumentException nếu invoiceId/operatorId không hợp lệ
     */
    boolean sendOperatorRequest(int invoiceId, int operatorId, String title, String content,
                                Integer managerId) throws AccessDeniedException;

    /** Lấy thông tin hóa đơn quá hạn để chuẩn bị form nhắc nợ. */
    Map<String, Object> getInvoiceDetailsForSendDebt(int invoiceId, Integer managerId)
            throws AccessDeniedException;

    /**
     * Gửi thông báo nhắc nợ đến phòng có hóa đơn quá hạn.
     *
     * @throws AccessDeniedException  nếu manager không phụ trách cơ sở đó
     * @throws IllegalArgumentException nếu invoiceId không hợp lệ
     */
    boolean sendDebtReminder(int invoiceId, String title, String content, Integer managerId)
            throws AccessDeniedException;
}
