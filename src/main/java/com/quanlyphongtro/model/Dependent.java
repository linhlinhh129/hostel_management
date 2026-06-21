package com.quanlyphongtro.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Dependent {
    private Integer id;
    private Integer tenantId;
    private String fullName;
    private LocalDate dob;
    private String gender;
    private String relationship;
    private String phone;
    private String identityNumber;
    private String permanentAddress;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Dependent() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDob() { return dob; }
    public void setDob(LocalDate dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getRelationship() { return relationship; }
    public void setRelationship(String relationship) { this.relationship = relationship; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getIdentityNumber() { return identityNumber; }
    public void setIdentityNumber(String identityNumber) { this.identityNumber = identityNumber; }

    public String getPermanentAddress() { return permanentAddress; }
    public void setPermanentAddress(String permanentAddress) { this.permanentAddress = permanentAddress; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    // Helper for View
    public String getDobLabel() {
        return dob != null ? dob.format(DATE_FORMATTER) : "N/A";
    }

    public String getMaskedIdentityNumber() {
        if (identityNumber == null || identityNumber.length() < 6) return identityNumber;
        return identityNumber.substring(0, 3) + "******" + identityNumber.substring(identityNumber.length() - 3);
    }
}
