package com.quanlyphongtro.model;

import java.time.LocalDateTime;

public class AuditLog {
    private Integer id;
    private String entityType;
    private Integer entityId;
    private String entityName; // tên hiển thị của entity (lookup khi cần)
    private String action;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private String comment;
    private Integer createdBy;
    private String createdByName; // joined username from users
    private LocalDateTime createdAt;

    public AuditLog() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Integer getEntityId() { return entityId; }
    public void setEntityId(Integer entityId) { this.entityId = entityId; }

    public String getEntityName() { return entityName; }
    public void setEntityName(String entityName) { this.entityName = entityName; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Backward compatibility for JSP fmt:formatDate
    public java.util.Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return java.sql.Timestamp.valueOf(createdAt);
    }
}
