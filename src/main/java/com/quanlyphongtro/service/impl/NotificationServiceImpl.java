package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
}
