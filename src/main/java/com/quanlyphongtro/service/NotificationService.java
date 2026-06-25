package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Notification;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NotificationService {
    List<Notification> getNotificationsForTenant(int roomId, int facilityId, int page, int pageSize);
    List<Notification> getNotificationsForTenant(int roomId, int facilityId, String keyword, int page, int pageSize);
    int countNotificationsForTenant(int roomId, int facilityId);
    int countNotificationsForTenant(int roomId, int facilityId, String keyword);
    Optional<Notification> getNotificationById(int id, int roomId, int facilityId);
    int countUnreadNotifications(int roomId, int facilityId, LocalDateTime lastReadTime);
}
