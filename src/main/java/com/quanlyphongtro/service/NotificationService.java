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

    // Manager notification methods
    int countManagerNotifications(int managerId, String tab, String type, Integer filterFacilityId, String keyword);
    List<java.util.Map<String, Object>> getManagerNotifications(int managerId, String tab, String type, Integer filterFacilityId, String keyword, int page, int pageSize);
    List<java.util.Map<String, Object>> getAssignedFacilitiesForManager(int managerId);
    List<java.util.Map<String, Object>> getReportedIncorrectInvoices(int managerId, Integer filterFacilityId, String keyword);
    List<java.util.Map<String, Object>> getAssignedRoomsForManager(int managerId);
    boolean sendNotification(String title, String content, String recipientType, Integer recipientId, Integer facilityIdForRoom, int managerId) throws Exception;
    java.util.Map<String, Object> getNotificationDetail(int notificationId, int managerId) throws Exception;
    boolean reportIncorrectInvoice(int invoiceId, int managerId) throws Exception;
    java.util.Map<String, Object> getInvoiceDetailsForSendOperator(int invoiceId, int managerId) throws Exception;
    List<java.util.Map<String, Object>> getActiveOperatorsForFacility(int facilityId);
    boolean sendOperatorRequest(int invoiceId, int operatorId, String title, String content, int managerId) throws Exception;
    java.util.Map<String, Object> getInvoiceDetailsForSendDebt(int invoiceId, int managerId) throws Exception;
    boolean sendDebtReminder(int invoiceId, String title, String content, int managerId) throws Exception;
}
