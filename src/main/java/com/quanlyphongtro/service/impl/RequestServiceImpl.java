package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.service.RequestService;

import java.util.List;
import java.util.Optional;

public class RequestServiceImpl implements RequestService {

    private final RequestDAO requestDAO = new RequestDAO();

    @Override
    public List<Request> getRequestsBySenderId(int senderId) {
        return requestDAO.findBySenderId(senderId);
    }

    @Override
    public Optional<Request> getRequestById(int id, int senderId) {
        return requestDAO.findByIdAndSenderId(id, senderId);
    }

    @Override
    public boolean createRequest(Request request) {
        return requestDAO.insert(request);
    }

    @Override
    public int countPendingRequests(int senderId) {
        return requestDAO.countPendingBySenderId(senderId);
    }

    @Override
    public Request getRequestDetail(int requestId) {
        return requestDAO.getRequestById(requestId);
    }

    @Override
    public boolean acceptRequest(int requestId, int operatorId) {
        // Only accept if status is currently PENDING
        return requestDAO.updateRequestStatus(requestId, "IN_PROGRESS", "PENDING", operatorId, null);
    }

    @Override
    public boolean rejectRequest(int requestId, int operatorId, String reason) {
        // Reject request, keeping staff ID but updating status to REJECTED and adding reason
        return requestDAO.updateRequestStatus(requestId, "REJECTED", "PENDING", operatorId, reason);
    }

    @Override
    public boolean completeRequest(int requestId, String notes, String attachmentUrls2) {
        return requestDAO.completeRequest(requestId, notes, attachmentUrls2);
    }

    @Override
    public boolean scheduleAppointmentText(int requestId, String appointmentDateStr) {
        return requestDAO.updateAppointmentDateText(requestId, appointmentDateStr);
    }
}
