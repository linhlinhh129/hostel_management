package com.quanlyphongtro.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Facility {
    private Integer facilityId;
    private String code;
    private String name;
    private String address;
    private Integer floorCount;
    private Integer roomsPerFloor;
    private String status;
    private Integer managerId;
    private BigDecimal electricityPrice;
    private BigDecimal waterPrice;
    private BigDecimal internetFee;
    private BigDecimal serviceFee;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public Facility() {}

    public Integer getFacilityId() { return facilityId; }
    public void setFacilityId(Integer facilityId) { this.facilityId = facilityId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public Integer getFloorCount() { return floorCount; }
    public void setFloorCount(Integer floorCount) { this.floorCount = floorCount; }

    public Integer getRoomsPerFloor() { return roomsPerFloor; }
    public void setRoomsPerFloor(Integer roomsPerFloor) { this.roomsPerFloor = roomsPerFloor; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getManagerId() { return managerId; }
    public void setManagerId(Integer managerId) { this.managerId = managerId; }

    public BigDecimal getElectricityPrice() { return electricityPrice; }
    public void setElectricityPrice(BigDecimal electricityPrice) { this.electricityPrice = electricityPrice; }

    public BigDecimal getWaterPrice() { return waterPrice; }
    public void setWaterPrice(BigDecimal waterPrice) { this.waterPrice = waterPrice; }

    public BigDecimal getInternetFee() { return internetFee; }
    public void setInternetFee(BigDecimal internetFee) { this.internetFee = internetFee; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isDeleted() { return deletedAt != null; }
}
