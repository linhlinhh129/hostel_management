package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
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
        // Validation
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
}
