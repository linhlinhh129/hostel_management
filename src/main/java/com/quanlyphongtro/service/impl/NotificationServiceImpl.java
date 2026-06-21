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
    public List<Notification> getNotificationsForTenant(int roomId, int facilityId) {
        return notificationDAO.findForTenant(roomId, facilityId);
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
