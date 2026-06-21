package com.quanlyphongtro.model;

import com.quanlyphongtro.constant.StatusConstant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Request {
    private Integer id;
    private String code;
    private Integer senderId;
    private String category;
    private String title;
    private String content;
    private String status;
    private String attachmentUrls1;
    private String attachmentUrls2;
    private Integer assignedStaffId;
    private String rejectionReason;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Transient fields
    private String assignedTo; // Staff name

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public Request() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getSenderId() { return senderId; }
    public void setSenderId(Integer senderId) { this.senderId = senderId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAttachmentUrls1() { return attachmentUrls1; }
    public void setAttachmentUrls1(String attachmentUrls1) { this.attachmentUrls1 = attachmentUrls1; }

    public String getAttachmentUrls2() { return attachmentUrls2; }
    public void setAttachmentUrls2(String attachmentUrls2) { this.attachmentUrls2 = attachmentUrls2; }

    public Integer getAssignedStaffId() { return assignedStaffId; }
    public void setAssignedStaffId(Integer assignedStaffId) { this.assignedStaffId = assignedStaffId; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    // Helpers for View
    public String getCreatedDateLabel() {
        return createdAt != null ? createdAt.format(DATE_TIME_FORMATTER) : "N/A";
    }

    public String getTypeLabel() {
        if (category == null) return "Khác";
        return switch (category) {
            case "ELECTRIC" -> "⚡ Điện";
            case "WATER" -> "💧 Nước";
            case "INTERNET" -> "🌐 Internet";
            case "INFRASTRUCTURE" -> "🏗 Cơ sở vật chất";
            default -> "📌 Khác";
        };
    }

    public String getStatusBadgeClass() {
        if (StatusConstant.REQUEST_DONE.equals(status)) return "badge-success";
        if (StatusConstant.REQUEST_REJECTED.equals(status) || StatusConstant.REQUEST_CANCELLED.equals(status)) return "badge-danger";
        if (StatusConstant.REQUEST_IN_PROGRESS.equals(status)) return "badge-warning";
        if (StatusConstant.REQUEST_ASSIGNED.equals(status)) return "badge-info";
        return "badge-info"; // Pending
    }

    public String getStatusLabel() {
        if (status == null) return "Mới tạo";
        return switch (status) {
            case StatusConstant.REQUEST_PENDING -> "Mới tạo";
            case StatusConstant.REQUEST_ASSIGNED -> "Đã tiếp nhận";
            case StatusConstant.REQUEST_IN_PROGRESS -> "Đang xử lý";
            case StatusConstant.REQUEST_DONE -> "Hoàn thành";
            case StatusConstant.REQUEST_REJECTED -> "Từ chối";
            case StatusConstant.REQUEST_CANCELLED -> "Đã hủy";
            default -> status;
        };
    }
}
