package com.quanlyphongtro.model;

import com.quanlyphongtro.constant.StatusConstant;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Room {
    private Integer id;
    private Integer facilityId;
    private String code;
    private BigDecimal area;
    private String status;           // AVAILABLE, OCCUPIED, MAINTENANCE, RESERVED, INACTIVE
    private Integer tenantId;
    private BigDecimal depositAmount;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal roomFee;
    private String tenantName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Room() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
    // Alias for backwards compatibility
    public Integer getRoomId() { return id; }
    public void setRoomId(Integer roomId) { this.id = roomId; }

    public Integer getFacilityId() { return facilityId; }
    public void setFacilityId(Integer facilityId) { this.facilityId = facilityId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public BigDecimal getDepositAmount() { return depositAmount; }
    public void setDepositAmount(BigDecimal depositAmount) { this.depositAmount = depositAmount; }

    public LocalDate getContractStartDate() { return contractStartDate; }
    public void setContractStartDate(LocalDate contractStartDate) { this.contractStartDate = contractStartDate; }

    public LocalDate getContractEndDate() { return contractEndDate; }
    public void setContractEndDate(LocalDate contractEndDate) { this.contractEndDate = contractEndDate; }

    public BigDecimal getRoomFee() { return roomFee; }
    public void setRoomFee(BigDecimal roomFee) { this.roomFee = roomFee; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isDeleted() { return deletedAt != null; }

    // Backward compatibility for JSP fmt:formatDate
    public java.util.Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return java.sql.Timestamp.valueOf(createdAt);
    }

    public java.util.Date getUpdatedAtAsDate() {
        if (updatedAt == null) return null;
        return java.sql.Timestamp.valueOf(updatedAt);
    }

    /** Trích số tầng từ mã phòng — 2 ký tự trước 2 ký tự cuối. VD: HL0103 → "01" */
    public String getFloorLabel() {
        if (code == null || code.length() < 4) return "";
        return code.substring(code.length() - 4, code.length() - 2);
    }

    /** Trích số phòng trong tầng từ mã phòng — 2 ký tự cuối. VD: HL0103 → "03" */
    public String getRoomLabel() {
        if (code == null || code.length() < 2) return "";
        return code.substring(code.length() - 2);
    }
}
