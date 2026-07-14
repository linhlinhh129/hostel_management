package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.service.RequestService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class RequestServiceImpl implements RequestService {

    private static final Logger logger = LoggerFactory.getLogger(RequestServiceImpl.class);
    private final RequestDAO requestDAO = new RequestDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

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
        return requestDAO.updateRequestStatus(requestId, "ASSIGNED", "PENDING", operatorId, null);
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
    public boolean scheduleAppointment(int requestId, java.time.LocalDateTime appointSchedule) {
        return requestDAO.updateAppointmentSchedule(requestId, appointSchedule);
    }

    @Override
    public int countManagerTickets(int managerId, String type, String status, String keyword) {
        return requestDAO.countManagerTickets(managerId, type, status, keyword);
    }

    @Override
    public List<java.util.Map<String, Object>> getManagerTickets(int managerId, String type, String status, String keyword, int page, int pageSize) {
        return requestDAO.getManagerTickets(managerId, type, status, keyword, (page - 1) * pageSize, pageSize);
    }

    @Override
    public java.util.Map<String, Object> getManagerTicketDetail(int ticketId, int managerId) throws Exception {
        java.util.Map<String, Object> ticket = requestDAO.getManagerTicketDetail(ticketId);
        if (ticket == null) {
            return null;
        }

        int ownerManagerId = (Integer) ticket.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không quản lý cơ sở của yêu cầu này.");
        }

        // Generate history timeline logic
        java.util.List<java.util.Map<String, Object>> historyList = new java.util.ArrayList<>();
        String senderName = (String) ticket.get("senderName");
        String senderRole = (String) ticket.get("senderRole");
        String status = (String) ticket.get("status");
        String createdAt = (String) ticket.get("createdAt");
        String updatedAt = (String) ticket.get("updatedAt");
        String assignedOperatorName = (String) ticket.get("assignedOperatorName");
        String rejectionReason = (String) ticket.get("rejectionReason");
        String appointScheduleFormatted = (String) ticket.get("appointScheduleFormatted");
        String appointSchedule = (String) ticket.get("appointSchedule");

        if ("OPERATOR".equals(senderRole)) {
            java.util.Map<String, Object> h1 = new java.util.HashMap<>();
            h1.put("action", "Gửi yêu cầu");
            h1.put("performedAt", createdAt);
            h1.put("performedBy", senderName);
            h1.put("note", "Khởi tạo yêu cầu hỗ trợ hệ thống.");
            h1.put("sequence", 1);
            h1.put("performedAtRaw", ticket.get("createdAtRaw"));
            historyList.add(h1);

            if (!"NEW".equals(status) && !"PENDING".equals(status)) {
                java.util.Map<String, Object> h2 = new java.util.HashMap<>();
                h2.put("action", "Tiếp nhận yêu cầu");
                h2.put("performedAt", updatedAt);
                h2.put("performedBy", "Ban Quản lý");
                h2.put("note", "Đã tiếp nhận và đưa vào hàng chờ xử lý.");
                h2.put("sequence", 2);
                h2.put("performedAtRaw", ticket.get("updatedAtRaw"));
                historyList.add(h2);

                if ("ASSIGNED".equals(status) || "IN_PROGRESS".equals(status) || "DONE".equals(status)
                        || "RESOLVED".equals(status)) {
                    java.util.Map<String, Object> h3 = new java.util.HashMap<>();
                    h3.put("action", "Phân công xử lý");
                    h3.put("performedAt", updatedAt);
                    h3.put("performedBy", "Ban Quản lý");
                    h3.put("note", "Phân công cho nhân viên: " + assignedOperatorName);
                    h3.put("sequence", 3);
                    h3.put("performedAtRaw", ticket.get("updatedAtRaw"));
                    historyList.add(h3);
                }

                if ("DONE".equals(status) || "RESOLVED".equals(status)) {
                    java.util.Map<String, Object> h4 = new java.util.HashMap<>();
                    h4.put("action", "Hoàn thành yêu cầu");
                    h4.put("performedAt", updatedAt);
                    h4.put("performedBy", assignedOperatorName);
                    h4.put("note", "Đã sửa chữa / hoàn thành xử lý sự cố.");
                    h4.put("sequence", 4);
                    h4.put("performedAtRaw", ticket.get("updatedAtRaw"));
                    historyList.add(h4);
                }

                if ("REJECTED".equals(status)) {
                    java.util.Map<String, Object> h5 = new java.util.HashMap<>();
                    h5.put("action", "Từ chối yêu cầu");
                    h5.put("performedAt", updatedAt);
                    h5.put("performedBy", "Ban Quản lý");
                    h5.put("note", "Lý do: " + rejectionReason);
                    h5.put("sequence", 5);
                    h5.put("performedAtRaw", ticket.get("updatedAtRaw"));
                    historyList.add(h5);
                }
            }
        } else {
            java.util.Map<String, Object> h1 = new java.util.HashMap<>();
            h1.put("action", "Gửi yêu cầu");
            h1.put("performedAt", createdAt);
            h1.put("performedBy", senderName);
            h1.put("note", "Khởi tạo yêu cầu hỗ trợ hệ thống.");
            h1.put("sequence", 1);
            h1.put("performedAtRaw", ticket.get("createdAtRaw"));
            historyList.add(h1);

            if ("RECEIVED".equals(status)) {
                java.util.Map<String, Object> h2 = new java.util.HashMap<>();
                h2.put("action", "Tiếp nhận yêu cầu");
                h2.put("performedAt", updatedAt);
                h2.put("performedBy", "Ban Quản lý");
                h2.put("note", "Đã tiếp nhận yêu cầu.");
                h2.put("sequence", 2);
                h2.put("performedAtRaw", ticket.get("updatedAtRaw"));
                historyList.add(h2);
            } else if ("IN_PROGRESS".equals(status)) {
                java.util.Map<String, Object> h2 = new java.util.HashMap<>();
                h2.put("action", "Đang xử lý");
                h2.put("performedAt", updatedAt);
                h2.put("performedBy", "Ban Quản lý");
                if (appointScheduleFormatted != null && !appointScheduleFormatted.isEmpty()) {
                    h2.put("note", "Lịch hẹn xử lý: " + appointScheduleFormatted);
                } else {
                    h2.put("note", "Đang tiến hành xử lý yêu cầu.");
                }
                h2.put("sequence", 2);
                h2.put("performedAtRaw", ticket.get("updatedAtRaw"));
                historyList.add(h2);
            } else if ("DONE".equals(status) || "RESOLVED".equals(status)) {
                if (appointScheduleFormatted != null) {
                    java.util.Map<String, Object> h2 = new java.util.HashMap<>();
                    h2.put("action", "Đang xử lý");
                    h2.put("performedAt", appointSchedule);
                    h2.put("performedBy", "Ban Quản lý");
                    h2.put("note", "Lịch hẹn xử lý: " + appointScheduleFormatted);
                    h2.put("sequence", 2);
                    h2.put("performedAtRaw", ticket.get("appointScheduleRaw"));
                    historyList.add(h2);
                }
                java.util.Map<String, Object> h3 = new java.util.HashMap<>();
                h3.put("action", "Đã hoàn thành");
                h3.put("performedAt", updatedAt);
                h3.put("performedBy", "Ban Quản lý");
                h3.put("note", (rejectionReason != null && !rejectionReason.trim().isEmpty()) ? rejectionReason : "Yêu cầu đã được xử lý hoàn tất.");
                h3.put("sequence", 3);
                h3.put("performedAtRaw", ticket.get("updatedAtRaw"));
                historyList.add(h3);
            } else if ("REJECTED".equals(status)) {
                java.util.Map<String, Object> h2 = new java.util.HashMap<>();
                h2.put("action", "Từ chối yêu cầu");
                h2.put("performedAt", updatedAt);
                h2.put("performedBy", "Ban Quản lý");
                h2.put("note", "Lý do: " + ((rejectionReason != null && !rejectionReason.trim().isEmpty()) ? rejectionReason : "Không có lý do cụ thể."));
                h2.put("sequence", 2);
                h2.put("performedAtRaw", ticket.get("updatedAtRaw"));
                historyList.add(h2);
            } else if ("CANCELLED".equals(status)) {
                java.util.Map<String, Object> h2 = new java.util.HashMap<>();
                h2.put("action", "Đã hủy yêu cầu");
                h2.put("performedAt", updatedAt);
                h2.put("performedBy", senderName);
                h2.put("note", "Cư dân đã chủ động hủy yêu cầu.");
                h2.put("sequence", 2);
                h2.put("performedAtRaw", ticket.get("updatedAtRaw"));
                historyList.add(h2);
            }
        }

        // Load reschedule audit logs and merge into timeline
        java.util.List<java.util.Map<String, Object>> reschedules = requestDAO.getRescheduleHistory(ticketId);
        int rescheduleSeq = 100;
        for (java.util.Map<String, Object> r : reschedules) {
            java.util.Map<String, Object> hn = new java.util.HashMap<>();
            hn.put("action", "Thay đổi lịch hẹn");
            hn.put("performedAt", r.get("createdAt"));
            hn.put("performedAtRaw", r.get("createdAtRaw"));
            hn.put("performedBy", r.get("performerName"));
            hn.put("note", "Lịch mới: " + r.get("newValue") + " (Lịch cũ: " + r.get("oldValue") + "). Lý do: " + r.get("comment"));
            hn.put("sequence", rescheduleSeq++);
            historyList.add(hn);
        }

        // Sort historyList chronologically using raw timestamps and stable sequence numbers
        java.util.Collections.sort(historyList, (a, b) -> {
            Object rawA = a.get("performedAtRaw");
            Object rawB = b.get("performedAtRaw");

            java.sql.Timestamp tsA = null;
            java.sql.Timestamp tsB = null;

            if (rawA instanceof java.sql.Timestamp) tsA = (java.sql.Timestamp) rawA;
            else if (rawA instanceof java.time.LocalDateTime) tsA = java.sql.Timestamp.valueOf((java.time.LocalDateTime) rawA);

            if (rawB instanceof java.sql.Timestamp) tsB = (java.sql.Timestamp) rawB;
            else if (rawB instanceof java.time.LocalDateTime) tsB = java.sql.Timestamp.valueOf((java.time.LocalDateTime) rawB);

            if (tsA != null && tsB != null) {
                int cmp = tsA.compareTo(tsB);
                if (cmp != 0) {
                    return cmp;
                }
            }

            Integer seqA = (Integer) a.get("sequence");
            Integer seqB = (Integer) b.get("sequence");
            if (seqA != null && seqB != null) {
                return seqA.compareTo(seqB);
            }
            return 0;
        });

        ticket.put("history", historyList);
        return ticket;
    }

    private String getTicketStatus(int ticketId) {
        java.util.Map<String, Object> ticket = requestDAO.getManagerTicketDetail(ticketId);
        if (ticket == null) {
            return null;
        }
        return (String) ticket.get("status");
    }

    private boolean isClosedStatus(String status) {
        return "REJECTED".equals(status) || "RESOLVED".equals(status) || "DONE".equals(status) || "CANCELLED".equals(status);
    }

    @Override
    public boolean receiveTicket(int ticketId) {
        String status = getTicketStatus(ticketId);
        if (status == null || isClosedStatus(status)) {
            return false;
        }
        if (!"NEW".equals(status) && !"PENDING".equals(status)) {
            return false;
        }
        return requestDAO.receiveTicket(ticketId);
    }

    @Override
    public boolean rejectTicket(int ticketId, String reason) {
        String status = getTicketStatus(ticketId);
        if (status == null || isClosedStatus(status)) {
            return false;
        }
        return requestDAO.rejectTicket(ticketId, reason);
    }

    @Override
    public boolean scheduleTicket(int ticketId, java.time.LocalDateTime scheduleTime) {
        String status = getTicketStatus(ticketId);
        if (status == null || isClosedStatus(status)) {
            return false;
        }
        return requestDAO.scheduleTicket(ticketId, scheduleTime);
    }

    @Override
    public boolean completeTicket(int ticketId, String notes, String attachmentUrls2) {
        String status = getTicketStatus(ticketId);
        if (status == null || isClosedStatus(status)) {
            return false;
        }
        return requestDAO.completeTicket(ticketId, notes, attachmentUrls2);
    }

    @Override
    public boolean rescheduleTicket(int ticketId, java.time.LocalDateTime newTime, String reason, int managerId, String ipAddress) throws Exception {
        java.util.Map<String, Object> ticket = requestDAO.getManagerTicketDetail(ticketId);
        if (ticket == null) {
            return false;
        }

        int ownerManagerId = (Integer) ticket.get("managerId");
        if (ownerManagerId != managerId) {
            throw new java.nio.file.AccessDeniedException("Bạn không quản lý cơ sở của yêu cầu này.");
        }

        String status = (String) ticket.get("status");
        if (!"IN_PROGRESS".equals(status)) {
            throw new IllegalStateException("Chỉ được dời lịch hẹn khi yêu cầu ở trạng thái Đang xử lý.");
        }

        String oldTimeStr = (String) ticket.get("appointSchedule");
        if (oldTimeStr == null || oldTimeStr.trim().isEmpty()) {
            oldTimeStr = "Chưa có";
        }

        String newTimeStr = newTime.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        try {
            boolean success = requestDAO.rescheduleTicket(ticketId, newTime);
            if (success) {
                // Write reschedule trace to audit log
                auditLogDAO.logWithComment(
                    "requests",
                    ticketId,
                    "RESCHEDULE",
                    oldTimeStr,
                    newTimeStr,
                    ipAddress,
                    managerId,
                    reason
                );
                return true;
            }
        } catch (Exception e) {
            logger.error("Failed to reschedule ticket id={}", ticketId, e);
            throw e;
        }
        return false;
    }
}
