package com.quanlyphongtro.model;

import java.time.LocalDateTime;

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
