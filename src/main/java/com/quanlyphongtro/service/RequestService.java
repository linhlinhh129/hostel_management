package com.quanlyphongtro.service;
import java.time.LocalDateTime;
import java.util.Map;

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
    // Lấy thông tin chi tiết một yêu cầu sự cố
    Request getRequestDetail(int requestId);
    // Nhân viên vận hành tiếp nhận xử lý yêu cầu sự cố
    boolean acceptRequest(int requestId, int operatorId);
    // Nhân viên vận hành từ chối tiếp nhận yêu cầu sự cố
    boolean rejectRequest(int requestId, int operatorId, String reason);
    // Báo cáo hoàn thành xử lý sự cố (đính kèm ghi chú và ảnh minh chứng)
    boolean completeRequest(int requestId, String notes, String attachmentUrls2);
    // Đặt lịch hẹn xử lý sự cố
    boolean scheduleAppointment(int requestId, LocalDateTime appointSchedule);
    // Đặt lịch hẹn xử lý sự cố kèm ID nhân viên vận hành
    boolean scheduleAppointment(int requestId, LocalDateTime appointSchedule, int operatorId);

    // Manager methods
    int countManagerTickets(int managerId, String type, String status, String keyword);
    List<Map<String, Object>> getManagerTickets(int managerId, String type, String status, String keyword, int page, int pageSize);
    Map<String, Object> getManagerTicketDetail(int ticketId, int managerId) throws Exception;
    boolean receiveTicket(int ticketId);
    boolean rejectTicket(int ticketId, String reason);
    boolean scheduleTicket(int ticketId, LocalDateTime scheduleTime);
    boolean completeTicket(int ticketId, String notes, String attachmentUrls2);
    boolean rescheduleTicket(int ticketId, LocalDateTime newTime, String reason, int managerId, String ipAddress) throws Exception;
}
