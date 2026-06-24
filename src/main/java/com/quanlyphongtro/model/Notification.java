package com.quanlyphongtro.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notification {
    private Integer id;
    private String code;
    private String title;
    private String content;
    private String targetType;   // ALL / FACILITY / ROOM
    private Integer facilityId;
    private Integer roomId;
    private String status;       // DRAFT / SENT
    private Integer createdBy;
    private String createdByName; // joined from users
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime sentAt;
    private LocalDateTime deletedAt;

    // Transient fields for View
    private boolean unread;
    private String summary; // Shortened content

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public Notification() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Integer getFacilityId() { return facilityId; }
    public void setFacilityId(Integer facilityId) { this.facilityId = facilityId; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isUnread() { return unread; }
    public void setUnread(boolean unread) { this.unread = unread; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    // Helpers for View
    public String getCreatedDateLabel() {
        return createdAt != null ? createdAt.format(DATE_TIME_FORMATTER) : "N/A";
    }

    public void generateSummary() {
        if (this.content == null) {
            this.summary = "";
        } else if (this.content.length() > 100) {
            this.summary = this.content.substring(0, 100) + "...";
        } else {
            this.summary = this.content;
        }
    }

    /**
     * Alias for targetType — JSP uses recipientType.
     */
    public String getRecipientType() { return targetType; }
    public void setRecipientType(String recipientType) { this.targetType = recipientType; }

    /**
     * Returns facilityId or roomId depending on targetType.
     */
    public Integer getRecipientId() {
        if ("FACILITY".equals(targetType)) return facilityId;
        if ("ROOM".equals(targetType)) return roomId;
        return null;
    }
}
