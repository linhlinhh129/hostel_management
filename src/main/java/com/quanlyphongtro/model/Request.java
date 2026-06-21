package com.quanlyphongtro.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Request {
    private int requestId;
    private String code;
    private int senderId;
    private String category;
    private String title;
    private String content;
    private String status;
    private String attachmentUrls1;
    private String attachmentUrls2;
    private Integer assignedStaffId;
    private String rejectionReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    // View-specific additional fields (for JOINs)
    private String senderName;
    private String roomCode;
    private String facilityName;

    public Request() {}

    public int getRequestId() { return requestId; }
    public void setRequestId(int requestId) { this.requestId = requestId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getSenderId() { return senderId; }
    public void setSenderId(int senderId) { this.senderId = senderId; }

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

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public Timestamp getDeletedAt() { return deletedAt; }
    public void setDeletedAt(Timestamp deletedAt) { this.deletedAt = deletedAt; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

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
}
