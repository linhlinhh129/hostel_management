package com.quanlyphongtro.model;

import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Contract {
    private int contractId;
    private String code;
    private int roomId;
    private Integer tenantId; // Changed to Integer to allow null initially if needed, based on user's input

    private String tenantFullName;
    private LocalDate tenantDob;
    private String tenantPermanentAddress;
    private String tenantIdentityNumber;
    private LocalDate tenantIdentityIssueDate;
    private String tenantIdentityIssuePlace;
    private String tenantPhone;

    private String amountInWords;

    private LocalDate signedDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private String status;

    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Snapshot price fields (frozen upon contract signing)
    private java.math.BigDecimal roomFee;
    private java.math.BigDecimal depositAmount;
    private java.math.BigDecimal electricityPrice;
    private java.math.BigDecimal waterPrice;
    private java.math.BigDecimal internetFee;
    private java.math.BigDecimal serviceFee;

    // Additional fields for convenience
    private Room room;
    private User tenant;

    // Getters and Setters

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public Integer getTenantId() {
        return tenantId;
    }

    public void setTenantId(Integer tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantFullName() {
        return tenantFullName;
    }

    public void setTenantFullName(String tenantFullName) {
        this.tenantFullName = tenantFullName;
    }

    public LocalDate getTenantDob() {
        return tenantDob;
    }

    public void setTenantDob(LocalDate tenantDob) {
        this.tenantDob = tenantDob;
    }

    public String getTenantPermanentAddress() {
        return tenantPermanentAddress;
    }

    public void setTenantPermanentAddress(String tenantPermanentAddress) {
        this.tenantPermanentAddress = tenantPermanentAddress;
    }

    public String getTenantIdentityNumber() {
        return tenantIdentityNumber;
    }

    public void setTenantIdentityNumber(String tenantIdentityNumber) {
        this.tenantIdentityNumber = tenantIdentityNumber;
    }

    public LocalDate getTenantIdentityIssueDate() {
        return tenantIdentityIssueDate;
    }

    public void setTenantIdentityIssueDate(LocalDate tenantIdentityIssueDate) {
        this.tenantIdentityIssueDate = tenantIdentityIssueDate;
    }

    public String getTenantIdentityIssuePlace() {
        return tenantIdentityIssuePlace;
    }

    public void setTenantIdentityIssuePlace(String tenantIdentityIssuePlace) {
        this.tenantIdentityIssuePlace = tenantIdentityIssuePlace;
    }

    public String getTenantPhone() {
        return tenantPhone;
    }

    public void setTenantPhone(String tenantPhone) {
        this.tenantPhone = tenantPhone;
    }

    public String getAmountInWords() {
        return amountInWords;
    }

    public void setAmountInWords(String amountInWords) {
        this.amountInWords = amountInWords;
    }

    public LocalDate getSignedDate() {
        return signedDate;
    }

    public void setSignedDate(LocalDate signedDate) {
        this.signedDate = signedDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getTenant() {
        return tenant;
    }

    public void setTenant(User tenant) {
        this.tenant = tenant;
    }

    private Facility facility;
    private User manager;

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    public User getManager() {
        return manager;
    }

    public void setManager(User manager) {
        this.manager = manager;
    }

    public String getSignedDay() {
        return signedDate != null ? String.format("%02d", signedDate.getDayOfMonth()) : "...";
    }

    public String getSignedMonth() {
        return signedDate != null ? String.format("%02d", signedDate.getMonthValue()) : "...";
    }

    public String getSignedYear() {
        return signedDate != null ? String.valueOf(signedDate.getYear()) : "......";
    }

    public String getFormattedEndDate() {
        return endDate != null ? endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public java.math.BigDecimal getRoomFee() {
        return roomFee;
    }

    public void setRoomFee(java.math.BigDecimal roomFee) {
        this.roomFee = roomFee;
    }

    public java.math.BigDecimal getDepositAmount() {
        return depositAmount;
    }

    public void setDepositAmount(java.math.BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }

    public java.math.BigDecimal getElectricityPrice() {
        return electricityPrice;
    }

    public void setElectricityPrice(java.math.BigDecimal electricityPrice) {
        this.electricityPrice = electricityPrice;
    }

    public java.math.BigDecimal getWaterPrice() {
        return waterPrice;
    }

    public void setWaterPrice(java.math.BigDecimal waterPrice) {
        this.waterPrice = waterPrice;
    }

    public java.math.BigDecimal getInternetFee() {
        return internetFee;
    }

    public void setInternetFee(java.math.BigDecimal internetFee) {
        this.internetFee = internetFee;
    }

    public java.math.BigDecimal getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(java.math.BigDecimal serviceFee) {
        this.serviceFee = serviceFee;
    }

    public String getFormattedStartDate() {
        return startDate != null ? startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    public boolean isExpired() {
        return "ACTIVE".equals(status) && endDate != null && endDate.isBefore(LocalDate.now());
    }

    public boolean getExpired() {
        return isExpired();
    }

    public boolean isExpiringSoon() {
        if (!"ACTIVE".equals(status) || endDate == null) {
            return false;
        }
        LocalDate now = LocalDate.now();
        return !endDate.isBefore(now) && !endDate.isAfter(now.plusDays(30));
    }

    public boolean getExpiringSoon() {
        return isExpiringSoon();
    }

    public long getDaysUntilExpiry() {
        if (endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), endDate);
    }

    public long getDaysOverdue() {
        if (endDate == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(endDate, LocalDate.now());
    }
}
