package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;

public class RequestService {
    private RequestDAO requestDAO;

    public RequestService() {
        this.requestDAO = new RequestDAO();
    }

    public Request getRequestDetail(int requestId) {
        return requestDAO.getRequestById(requestId);
    }

    public boolean acceptRequest(int requestId, int operatorId) {
        // Only accept if status is currently PENDING
        return requestDAO.updateRequestStatus(requestId, "IN_PROGRESS", "PENDING", operatorId, null);
    }

    public boolean rejectRequest(int requestId, int operatorId, String reason) {
        // Reject request, keeping staff ID but updating status to REJECTED and adding reason
        return requestDAO.updateRequestStatus(requestId, "REJECTED", "PENDING", operatorId, reason);
    }

    public boolean completeRequest(int requestId, String notes, String attachmentUrls2) {
        return requestDAO.completeRequest(requestId, notes, attachmentUrls2);
    }

    public boolean scheduleAppointmentText(int requestId, String appointmentDateStr) {
        return requestDAO.updateAppointmentDateText(requestId, appointmentDateStr);
    }
}
