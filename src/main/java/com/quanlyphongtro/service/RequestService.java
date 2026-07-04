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
}
