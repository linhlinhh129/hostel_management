package com.quanlyphongtro.service;

import com.quanlyphongtro.model.Request;
import java.util.List;
import java.util.Optional;

public interface RequestService {
    // Tenant methods
    List<Request> getRequestsBySenderId(int senderId);
    Optional<Request> getRequestById(int id, int senderId);
    boolean createRequest(Request request);
    int countPendingRequests(int senderId);

    // Operator methods
    Request getRequestDetail(int requestId);
    boolean acceptRequest(int requestId, int operatorId);
    boolean rejectRequest(int requestId, int operatorId, String reason);
    boolean completeRequest(int requestId, String notes, String attachmentUrls2);
    boolean scheduleAppointment(int requestId, java.time.LocalDateTime appointSchedule);

    // Manager methods
    int countManagerTickets(int managerId, String type, String status, String keyword);
    List<java.util.Map<String, Object>> getManagerTickets(int managerId, String type, String status, String keyword, int page, int pageSize);
    java.util.Map<String, Object> getManagerTicketDetail(int ticketId, int managerId) throws Exception;
    boolean receiveTicket(int ticketId);
    boolean rejectTicket(int ticketId, String reason);
    boolean scheduleTicket(int ticketId, java.time.LocalDateTime scheduleTime);
    boolean completeTicket(int ticketId, String notes, String attachmentUrls2);
    boolean rescheduleTicket(int ticketId, java.time.LocalDateTime newTime, String reason, int managerId, String ipAddress) throws Exception;
}
