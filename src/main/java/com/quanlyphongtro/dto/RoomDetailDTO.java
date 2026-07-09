package com.quanlyphongtro.dto;

import java.math.BigDecimal;

public class RoomDetailDTO {
    private int id;
    private int facilityId;
    private String facilityCode;
    private String facilityName;
    private int managerId;
    private String code;
    private double area;
    private String status;
    private String createdAt;
    private String updatedAt;
    private Integer tenantId;
    private String tenantName;
    private String tenantCode;
    private String tenantPhone;
    private Integer activeContractId;
    private String floor;
    private String roomNumber;
    private BigDecimal roomFee;
    private java.util.Date createdAtAsDate;
    private java.util.Date updatedAtAsDate;

    public RoomDetailDTO() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFacilityId() {
        return facilityId;
    }

    public void setFacilityId(int facilityId) {
        this.facilityId = facilityId;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public Integer getActiveContractId() {
        return activeContractId;
    }

    public void setActiveContractId(Integer activeContractId) {
        this.activeContractId = activeContractId;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public BigDecimal getRoomFee() {
        return roomFee;
    }

    public void setRoomFee(BigDecimal roomFee) {
        this.roomFee = roomFee;
    }

    public java.util.Date getCreatedAtAsDate() {
        return createdAtAsDate;
    }

    public void setCreatedAtAsDate(java.util.Date createdAtAsDate) {
        this.createdAtAsDate = createdAtAsDate;
    }

    public java.util.Date getUpdatedAtAsDate() {
        return updatedAtAsDate;
    }

    public void setUpdatedAtAsDate(java.util.Date updatedAtAsDate) {
        this.updatedAtAsDate = updatedAtAsDate;
    }
}
