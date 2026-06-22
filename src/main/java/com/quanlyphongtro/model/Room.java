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
    private String status;
    private Integer tenantId;
    private BigDecimal depositAmount;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private BigDecimal roomFee;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Room() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isDeleted() { return deletedAt != null; }
}
