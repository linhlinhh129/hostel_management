package com.quanlyphongtro.service;

import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.model.Notification;
import java.time.LocalDateTime;
import java.util.List;
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
     * @throws com.quanlyphongtro.exception.ValidationException nếu dữ liệu không hợp lệ.
     */
    void createAdminNotification(String title, String content, Integer createdBy)
            throws com.quanlyphongtro.exception.ValidationException;
}
