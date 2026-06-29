package com.quanlyphongtro.model;

import com.quanlyphongtro.constant.StatusConstant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    // View-specific additional fields (for JOINs)
    private String senderName;
    private String roomCode;
    private String facilityName;

    // Transient fields
    private String assignedTo; // Staff name

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public Request() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    // Backward compatibility for JSP
    public Integer getRequestId() { return id; }
    public void setRequestId(Integer requestId) { this.id = requestId; }

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

    // Backward compatibility for JSP fmt:formatDate
    public java.util.Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return java.sql.Timestamp.valueOf(createdAt);
    }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    // Helper method to get all images as list
    public List<String> getImages() {
        List<String> images = new ArrayList<>();
        if (attachmentUrls1 != null && !attachmentUrls1.trim().isEmpty()) {
            for (String url : attachmentUrls1.split(",")) {
                if (!url.trim().isEmpty()) images.add(url.trim());
            }
        }
        if (attachmentUrls2 != null && !attachmentUrls2.trim().isEmpty()) {
            for (String url : attachmentUrls2.split(",")) {
                if (!url.trim().isEmpty()) images.add(url.trim());
            }
        }
        return images;
    }

    public String getDisplayLocation() {
        if (roomCode != null && !roomCode.isEmpty() && facilityName != null && !facilityName.isEmpty()) {
            return "<span style=\"font-weight: 500;\">P." + roomCode + "</span> <span style=\"color: var(--color-steel); font-size: 12px;\">(" + facilityName + ")</span>";
        }
        
        // Cố gắng parse từ title của operator: [Mức độ] Sự cố ... tại Khu vực chung (Tên cơ sở)
        if (title != null && title.contains(" tại ")) {
            int idx = title.lastIndexOf(" tại ");
            if (idx > 0) {
                String locPart = title.substring(idx + 5);
                if (locPart.contains(" (") && locPart.endsWith(")")) {
                    String loc = locPart.substring(0, locPart.lastIndexOf(" ("));
                    String fac = locPart.substring(locPart.lastIndexOf(" (") + 2, locPart.length() - 1);
                    return "<span style=\"font-weight: 500;\">" + loc + "</span> <span style=\"color: var(--color-steel); font-size: 12px;\">(" + fac + ")</span>";
                }
                return "<span style=\"font-weight: 500;\">" + locPart + "</span>";
            }
        }
        return "<span style=\"font-weight: 500; color: var(--color-steel);\">Không xác định</span>";
    }

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
